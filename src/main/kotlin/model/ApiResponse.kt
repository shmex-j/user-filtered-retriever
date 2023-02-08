package model

import com.fasterxml.jackson.annotation.JsonProperty

data class ApiResponse<T> (
    @JsonProperty("items") val items: List<T>,
    @JsonProperty("page") val page: Long,
    @JsonProperty("has_more") val hasMore: Boolean,
    @JsonProperty("quota_remaining") val quotaRemaining: Long,
    @JsonProperty("backoff") val backoff: Long?
)
