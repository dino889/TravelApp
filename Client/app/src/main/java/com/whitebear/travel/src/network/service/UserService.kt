package com.whitebear.travel.src.network.service

import com.whitebear.travel.src.dto.User
import com.whitebear.travel.util.RetrofitUtil

class UserService {

    suspend fun getUsers() = RetrofitUtil.userService.getUsers()

    suspend fun insertUser(user:User) = RetrofitUtil.userService.insertUser(user)

    suspend fun updateUser(user:User)= RetrofitUtil.userService.updateUser(user)

    suspend fun login(user:User) = RetrofitUtil.userService.login(user)

    suspend fun deleteUser() = RetrofitUtil.userService.deleteUser()

    suspend fun doubleCheckEmail(email:String) = RetrofitUtil.userService.doubleCheckEmail(email)

}