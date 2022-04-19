package com.whitebear.travel.src.dto.covid

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name= "items")
data class Items(
    @Element(name = "item")
    val item: List<Item>
)