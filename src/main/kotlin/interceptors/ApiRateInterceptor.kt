package interceptors

import com.google.common.util.concurrent.RateLimiter
import okhttp3.Interceptor
import okhttp3.Response

const val MAX_REQ_PER_SEC = 25.0

class ApiRateInterceptor : Interceptor {
    private val rateLimiter = RateLimiter.create(MAX_REQ_PER_SEC)

    override fun intercept(chain: Interceptor.Chain): Response {
        rateLimiter.acquire(1)
        return chain.proceed(chain.request())
    }
}
