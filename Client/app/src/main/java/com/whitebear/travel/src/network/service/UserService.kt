package com.whitebear.travel.src.network.service

import com.navercorp.nid.profile.NidProfileCallback
import com.whitebear.travel.src.dto.User
import com.whitebear.travel.util.RetrofitUtil

class UserService {

    suspend fun selectUser(userId: Int) = RetrofitUtil.userService.selectUser(userId)

    suspend fun insertUser(user:User) = RetrofitUtil.userService.insertUser(user.email, user.password, user.username, user.nickname, user.social_type)

    suspend fun updateUser(userId: Int, user:User)= RetrofitUtil.userService.updateUser(userId, user)

    suspend fun login(email: String, password: String) = RetrofitUtil.userService.login(email, password)

    suspend fun deleteUser(userId: Int) = RetrofitUtil.userService.deleteUser(userId)

    suspend fun doubleCheckEmail(email:String) = RetrofitUtil.userService.doubleCheckEmail(email)

//    fun getNaverProfile(callback: NidProfileCallback) {
//        RetrofitUtil.userService.callProfileApi(callback)
//    }
}