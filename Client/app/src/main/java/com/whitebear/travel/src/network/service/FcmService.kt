package com.whitebear.travel.src.network.service

import com.whitebear.travel.src.dto.Message
import com.whitebear.travel.util.RetrofitUtil
import retrofit2.Response

class FcmService {

    suspend fun sendToMsgToUser(userId: Int, title: String, body: String, type: String) = RetrofitUtil.fcmService.sendMsgToUser(userId, title, body, type)
}