package com.whitebear.travel.src.network.service

import com.navercorp.nid.profile.NidProfileCallback
import com.whitebear.travel.src.dto.User
import com.whitebear.travel.util.RetrofitUtil

class UserService {

    suspend fun selectUser(userId: Int) = RetrofitUtil.userService.selectUser(userId)

    suspend fun insertUser(user:User) = RetrofitUtil.userService.insertUser(user.email, user.password, user.username, user.nickname, user.social_type)

//    suspend fun updateUser(userId: Int, user:User)= RetrofitUtil.userService.updateUser(userId, user)
    suspend fun updateUser(userId: Int, user:User) = RetrofitUtil.userService.updateUser(userId, user.nickname, user.username)

    suspend fun login(email: String, password: String) = RetrofitUtil.userService.login(email, password)

    suspend fun deleteUser(userId: Int) = RetrofitUtil.userService.deleteUser(userId)

    suspend fun doubleCheckEmail(email:String) = RetrofitUtil.userService.doubleCheckEmail(email)

    suspend fun updateUserPw(userId: Int, password: String) = RetrofitUtil.userService.updateUserPw(userId, password)

    suspend fun selectUserByEmailUsername(email: String, username: String) = RetrofitUtil.userService.selectUserByEmailUsername(email, username)

}