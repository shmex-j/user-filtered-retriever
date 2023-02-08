package mapper

import model.StackOverflowUser
import model.StackOverflowUserDto
import model.TagDto

class StackOverflowUserMapper {
    fun fromDtos(userDto : StackOverflowUserDto, tags : List<TagDto>) : StackOverflowUser {
        val tagString = tags.map(TagDto::name).joinToString(", ")
        return StackOverflowUser(
            userDto.displayName,
            userDto.location ?: "",
            userDto.answerCount,
            userDto.questionCount,
            tagString,
            userDto.link,
            userDto.profileImage
        )
    }
}
