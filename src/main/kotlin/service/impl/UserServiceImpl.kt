package service.impl

import ApiCallException
import mapper.StackOverflowUserMapper
import model.ApiResponse
import model.StackOverflowUser
import model.StackOverflowUserDto
import model.TagDto
import service.StackOverflowService
import service.UsersService
import java.util.function.Predicate

class UserServiceImpl(
    private val stackOverflowService: StackOverflowService,
    private val userMapper: StackOverflowUserMapper,
    private val userPredicate: Predicate<StackOverflowUserDto>,
    private val tagPredicate: Predicate<List<TagDto>>
) : UsersService {

    private val userFilter = "!BTeB3PnWxp6SxkntR9QeFyDf-5_0b2"
    private val userOptions = mapOf(
        "pagesize" to "100",
        "filter" to userFilter,
        "order" to "desc",
        "min" to "223",
        "sort" to "reputation",
        "site" to "stackoverflow"
    )

    private val tagFilter = "!6UoxY2(G1*FIY"
    private val tagOptions = mapOf(
        "pagesize" to "100",
        "filter" to tagFilter,
        "order" to "desc",
        "sort" to "popular",
        "site" to "stackoverflow"
    )

    override fun retrieveUsers(): List<StackOverflowUser> {
        return recursiveRetrieveUsers(ArrayList(), 1)
    }

    private tailrec fun recursiveRetrieveUsers(users: MutableList<StackOverflowUser>, page : Long)
            : List<StackOverflowUser> {
        println("Page: $page")
        val call = stackOverflowService.getUsers(page, userOptions)
        val response = call.execute()
        if (!response.isSuccessful || response.body() == null) {
            ApiCallException("Some request was unsuccessful: ${response.errorBody()}. " +
                    "Not all users was retrieved").printStackTrace()
            return users
        }

        val responseBody : ApiResponse<StackOverflowUserDto> = response.body()!!
        val dtos : List<StackOverflowUserDto> = responseBody.items
            .filter(userPredicate::test)
        if (dtos.isNotEmpty()) {
            addUsers(dtos, users)
        }

        if (responseBody.quotaRemaining < 1 && responseBody.hasMore) {
            ApiCallException("Out of API quota. Not all users was retrieved").printStackTrace()
            return users
        }
        return if (responseBody.hasMore) recursiveRetrieveUsers(users, page + 1) else users
    }

    private fun addUsers(dtos : List<StackOverflowUserDto>, users: MutableList<StackOverflowUser>) {
        val userIds = dtos.map(StackOverflowUserDto::userId).joinToString(";")
        val tags: List<TagDto> = try {
            recursiveRetrieveTags(ArrayList(), userIds, 1)
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

    private tailrec fun recursiveRetrieveTags(tags : MutableList<TagDto>, userIds : String, page: Long) : List<TagDto> {
        val call = stackOverflowService.getUsersTags(userIds, page, tagOptions)
        val response = call.execute()
        if (!response.isSuccessful || response.body() == null) {
            throw ApiCallException("Some request was unsuccessful: ${response.errorBody()}. " +
                    "Not all tags was retrieved")
        }

        val responseBody = response.body()!!
        tags.addAll(responseBody.items)

        if (responseBody.quotaRemaining < 1 && responseBody.hasMore) {
            throw ApiCallException("Out of API quota. Not all tags was retrieved")
        }
        return if (responseBody.hasMore) recursiveRetrieveTags(tags, userIds, page + 1) else tags
    }
}