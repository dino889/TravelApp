package com.whitebear.travel.src.dto

import androidx.room.*
import retrofit2.http.DELETE

@Entity(tableName = "Nav")
data class Navigator (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="navId") val idx:Int,
    @ColumnInfo(name ="userId") val userId : Int,
    @ColumnInfo(name ="placeId") val placeId : Int,
    @ColumnInfo(name ="placeName") val placeName : String,
    @ColumnInfo(name="placeLat") val placeLat:Double,
    @ColumnInfo(name="placeLng") val placeLng:Double,
    @ColumnInfo(name="placeAddr") val placeAddr:String,
    @ColumnInfo(name="placeContent") val placeContent:String,
    @ColumnInfo(name="placeImg") val placeImg:String
    )

@Dao
interface NavDao {
    @Insert
    fun insertNav(nav:Navigator)

    @Query("SELECT * FROM Nav WHERE userId IN (:userId)")
    fun getNav(userId:Int) : List<Navigator>

    @Query("DELETE FROM Nav WHERE navId = :navId")
    fun removeNav(navId:Int)
}