package com.whitebear.travel.util

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.whitebear.travel.src.dto.NavDao
import com.whitebear.travel.src.dto.Navigator

@Database(entities = [Navigator::class], version = 2)
abstract class NavDB : RoomDatabase(){
    abstract fun navDao(): NavDao

    companion object{
        private var INSTANCE : NavDB? = null

        fun getInstance(context: Context) : NavDB? {
            if(INSTANCE == null){
                synchronized(NavDB::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                    NavDB::class.java,"Nav")
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