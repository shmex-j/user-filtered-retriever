package service.impl

import ApiCallException
import mapper.StackOverflowUserMapper
import model.ApiResponse
import model.StackOverflowUser
import model.StackOverflowUserDto
import model.TagDto
import service.StackOverflowService
import service.UsersService
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import java.util.function.Predicate

class UserServiceImpl(
    private val stackOverflowService: StackOverflowService,
    private val userMapper: StackOverflowUserMapper,
    private val userPredicate: Predicate<StackOverflowUserDto>,
    private val tagPredicate: Predicate<List<TagDto>>
) : UsersService {
    private val apiKey = "sDFeTdZyAWjTmlRHguV0qQ(("

    private val userFilter = "!BTeB3PnWxp6SxkntR9QeFyDf-5_0b2"
    private val userOptions = mapOf(
        "key" to apiKey,
        "pagesize" to "100",
        "filter" to userFilter,
        "order" to "desc",
        "min" to "223",
        "sort" to "reputation",
        "site" to "stackoverflow"
    )

    private val tagFilter = "!6UoxY2(G1*FIY"
    private val tagOptions = mapOf(
        "key" to apiKey,
        "pagesize" to "100",
        "filter" to tagFilter,
        "order" to "desc",
        "sort" to "popular",
        "site" to "stackoverflow"
    )

    override fun retrieveUsers(startPage : Long, lastPage : Long?): List<StackOverflowUser> {
        return recursiveRetrieveUsers(ArrayList(), startPage, lastPage, LocalDateTime.now())
    }

    private tailrec fun recursiveRetrieveUsers(users: MutableList<StackOverflowUser>,
                                               page : Long,
                                               lastPage: Long?,
                                               backoffExpiration: LocalDateTime)
            : List<StackOverflowUser> {
        if (lastPage != null && lastPage < page) {
            return users
        }

        val call = stackOverflowService.getUsers(page, userOptions)
        waitForBackoffExpiration(backoffExpiration)
        val response = call.execute()
        if (!response.isSuccessful || response.body() == null) {
            ApiCallException("Some request was unsuccessful: ${response.errorBody()}. " +
                    "Not all users was retrieved").printStackTrace()
            return users
        }

        val responseBody : ApiResponse<StackOverflowUserDto> = response.body()!!
        val dtos = responseBody.items.filter(userPredicate::test)
        if (dtos.isNotEmpty()) {
            addUsers(dtos, users)
        }

        if (responseBody.hasMore) {
            if (responseBody.quotaRemaining < 1) {
                ApiCallException("Out of API quota. Not all users was retrieved").printStackTrace()
                return users
            }
            return recursiveRetrieveUsers(users,
                page + 1, lastPage,
                LocalDateTime.now()
                    .plusSeconds(responseBody.backoff ?: 0))
        }
        return users
    }

    private fun waitForBackoffExpiration(expiration : LocalDateTime) {
        if (LocalDateTime.now().isBefore(expiration)) {
            val waitSeconds = Duration.between(LocalDateTime.now(), expiration).seconds
            println("Waiting for the end of backoff: $waitSeconds seconds.")
            Thread.sleep(TimeUnit.SECONDS.toMillis(waitSeconds))
        }
    }

    private fun addUsers(dtos : List<StackOverflowUserDto>, users: MutableList<StackOverflowUser>) {
        val userIds = dtos.map(StackOverflowUserDto::userId).joinToString(";")
        val tags: List<TagDto> = try {
            recursiveRetrieveTags(ArrayList(), userIds, 1, LocalDateTime.now())
        } catch (e: ApiCallException) {
            e.printStackTrace()
            return
        }
        val userTagMap =
            dtos.associateWith { user -> tags.filter {
                    tag -> tag.userId == user.userId
            } }
        userTagMap.filter { tagPredicate.test(it.value) }
            .forEach { (user, tags) ->
                run {
                    users.add(userMapper.fromDtos(user, tags))
                }
            }
    }

    private tailrec fun recursiveRetrieveTags(tags : MutableList<TagDto>,
                                              userIds : String,
                                              page: Long,
                                              backoffExpiration: LocalDateTime) : List<TagDto> {
        val call = stackOverflowService.getUsersTags(userIds, page, tagOptions)
        waitForBackoffExpiration(backoffExpiration)
        val response = call.execute()
        if (!response.isSuccessful || response.body() == null) {
            throw ApiCallException(
                "Some request was unsuccessful: $response. " +
                        "Not all tags was retrieved"
            )
        }

        val responseBody = response.body()!!
        tags.addAll(responseBody.items)

        if (responseBody.quotaRemaining < 1 && responseBody.hasMore) {
            throw ApiCallException("Out of API quota. Not all tags was retrieved")
        }
        return if (responseBody.hasMore) recursiveRetrieveTags(tags, userIds,
            page + 1, LocalDateTime.now().plusSeconds(responseBody.backoff ?: 0)) else tags
    }
}
