package com.whitebear.travel.util

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomMasterTable
import com.whitebear.travel.src.dto.FcmDao
import com.whitebear.travel.src.dto.Noti
@Database(entities = [Noti::class], version = 1)
abstract class NotiDB :RoomDatabase(){
    abstract fun fcmDao(): FcmDao

    companion object{
        private var INSTANCE: NotiDB? = null

        fun getInstance(context: Context) : NotiDB? {
            if(INSTANCE == null){
                synchronized(NotiDB::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                    NotiDB::class.java, "fcm")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }
        fun destroyInstance(){
            INSTANCE = null
        }
    }

}

