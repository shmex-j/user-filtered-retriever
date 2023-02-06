package interceptors

import com.google.common.util.concurrent.RateLimiter
import okhttp3.Interceptor
import okhttp3.Response

class ApiRateInterceptor : Interceptor {
    private val rateLimiter = RateLimiter.create(25.0)

    override fun intercept(chain: Interceptor.Chain): Response {
        rateLimiter.acquire()
        return chain.proceed(chain.request())
    }
}