package com.whitebear.travel.src.dto

import androidx.room.*
import androidx.room.RoomDatabase

@Entity(tableName="fcm")
data class Noti (
    @PrimaryKey
    @ColumnInfo(name = "userId") val userId:Int,
    @ColumnInfo(name="eventCheck") val eventChecked:Boolean,
    @ColumnInfo(name="infoCheck") val infoChecked:Boolean
        )
@Dao
interface FcmDao {
    @Query("SELECT * FROM fcm WHERE userId IN (:userId)")
    fun getFcmCheck(userId:Int) : Noti

    @Insert
    fun insertChecked(noti:Noti)

    @Query("UPDATE fcm SET eventCheck = :eventCheck WHERE userId = :userId")
    fun updateEventChecked(eventCheck:Boolean, userId:Int)

    @Query("UPDATE fcm SET infoCheck = :infoCheck WHERE userId = :userId")
    fun updateInfoChecked(infoCheck:Boolean, userId:Int)

}
