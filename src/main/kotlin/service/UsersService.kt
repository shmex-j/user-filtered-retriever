package service

import model.StackOverflowUser

interface UsersService {
    fun retrieveUsers(startPage : Long, lastPage : Long?) : List<StackOverflowUser>
}