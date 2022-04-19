package com.whitebear.travel.src.dto.covid

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml
data class Item(
    @PropertyElement(name = "createDt") // 등록 날짜
    var createDt: String,
    @PropertyElement(name = "deathCnt") // 사망자 수
    var deathCnt: Int,
    @PropertyElement(name="decideCnt")  // 확진자 수
    var decideCnt: Int,
    @PropertyElement(name="stateDt")    // 기준 날짜
    var stateDt: String
)