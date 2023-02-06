package service.impl

import model.StackOverflowUserDto
import java.util.function.Predicate

class UserPrimaryPredicate : Predicate<StackOverflowUserDto> {
    override fun test(user: StackOverflowUserDto): Boolean {
        if (user.location == null) {
            return false
        }
        return (user.location.contains("moldova", true)
                || user.location.contains("romania", true))
                && user.questionCount >= 1
    }
}