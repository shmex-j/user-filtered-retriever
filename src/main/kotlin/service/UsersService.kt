package service

import model.StackOverflowUser
import model.StackOverflowUserDto

interface UsersService {
    fun retrieveUsers() : List<StackOverflowUser>
}