package com.whitebear.travel.src.network.service

import com.whitebear.travel.src.dto.User
import com.whitebear.travel.util.RetrofitUtil

class UserService {

    suspend fun selectUser(userId: Int) = RetrofitUtil.userService.selectUser(userId)

    suspend fun insertUser(user:User) = RetrofitUtil.userService.insertUser(user)

    suspend fun updateUser(userId: Int, user:User)= RetrofitUtil.userService.updateUser(userId, user)

    suspend fun login(email: String, password: String) = RetrofitUtil.userService.login(email, password)

    suspend fun deleteUser(userId: Int) = RetrofitUtil.userService.deleteUser(userId)

    suspend fun doubleCheckEmail(email:String) = RetrofitUtil.userService.doubleCheckEmail(email)

}