package model

data class StackOverflowUser(
    val username: String,
    val location: String,
    val answerCount: Long,
    val questionsCount: Long,
    val tags: String,
    val linkToProfile: String,
    val linkToAvatar: String
) {
    override fun toString(): String {
        return """
            User: 
                username: $username,
                location: $location,
                answerCount: $answerCount,
                questionsCount: $questionsCount,
                tags: $tags,
                linkToProfile: $linkToProfile,
                linkToAvatar: $linkToAvatar${System.lineSeparator()}
        """.trimIndent()
    }
}
