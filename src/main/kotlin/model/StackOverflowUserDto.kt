package model

import com.fasterxml.jackson.annotation.JsonProperty

data class StackOverflowUserDto (
    @JsonProperty("user_id") val userId: Long,
    @JsonProperty("display_name") val displayName: String,
    @JsonProperty("location") val location: String?,
    @JsonProperty("answer_count") val answerCount: Long,
    @JsonProperty("question_count") val questionCount: Long,
    @JsonProperty("link") val link: String,
    @JsonProperty("profile_image") val profileImage: String
)
