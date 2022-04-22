package com.whitebear.travel.src.network.service

import com.whitebear.travel.util.RetrofitUtil

class NotificationService {

    suspend fun selectNotiByUser(userId: Int) = RetrofitUtil.notificationService.selectNotificationByUser(userId)

    suspend fun deleteNotiById(id: Int) = RetrofitUtil.notificationService.deleteNotification(id)

}