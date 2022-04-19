package com.whitebear.travel.src.dto.covid

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "response")
data class Covid(
    @Element(name = "header")
    val header: Header,
    @Element(name = "body")
    val body: Body
)