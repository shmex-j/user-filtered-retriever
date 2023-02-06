package model

import com.fasterxml.jackson.annotation.JsonProperty

data class TagDto(
    @JsonProperty("name") val name : String,
    @JsonProperty("user_id") val userId : Long
)
