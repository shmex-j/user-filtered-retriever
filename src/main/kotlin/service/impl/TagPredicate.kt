package service.impl

import model.TagDto
import java.util.function.Predicate

class TagPredicate : Predicate<List<TagDto>> {
    private val whiteList = listOf("java", ".net", "docker", "c#")

    override fun test(tags: List<TagDto>): Boolean {
        return tags.map(TagDto::name).any(whiteList::contains)
    }
}