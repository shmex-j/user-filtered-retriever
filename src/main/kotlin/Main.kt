import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import interceptors.ApiCacheInterceptor
import interceptors.ApiRateInterceptor
import mapper.StackOverflowUserMapper
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import service.StackOverflowService
import service.impl.TagPredicate
import service.impl.UserPrimaryPredicate
import service.impl.UserServiceImpl
import java.io.File

const val BASE_URL = "https://api.stackexchange.com"
const val CACHE_SIZE = (1024 * 1024 * 1024).toLong()
const val CACHE_PATH = "cache"

fun main() {
    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(JacksonConverterFactory.create(jacksonMapper))
        .build()
    val stackOverflowService: StackOverflowService = retrofit.create(StackOverflowService::class.java)
    val usersService = UserServiceImpl(
        stackOverflowService,
        StackOverflowUserMapper(),
        UserPrimaryPredicate(),
        TagPredicate()
    )
    println("Enter the starting page to search (leave blank to search from the beginning):")
    val startPageString = readln()
    val startPage = minOf(startPageString.toLongOrNull() ?: 1, 1)
    println("Enter the search last page (leave blank to search to the end)\n" +
            "WARNING! Large search ranges can result in API quota exhaustion or lengthy searches")
    val lastPageString = readln()
    val lastPage = lastPageString.toLongOrNull()
    if ((lastPage ?: Long.MAX_VALUE) < startPage) {
        throw RuntimeException("Last page must be further start page")
    }
    println("Retrieving users data. Please wait...")
    val users = usersService.retrieveUsers(startPage, lastPage)
    println("Retrieved users:\n$users")
}

private val okHttpClient: OkHttpClient by lazy {
    OkHttpClient.Builder()
        .cache(Cache(File(CACHE_PATH), CACHE_SIZE))
        .addNetworkInterceptor(ApiCacheInterceptor())
        .addNetworkInterceptor(ApiRateInterceptor())
        .build()
}

private val jacksonMapper: ObjectMapper by lazy {
    jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
}