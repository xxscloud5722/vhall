package com.xxscloud.sdk.vhall

import java.math.BigDecimal
import java.util.*

/**
 * @author 橘猫.
 */
data class Live(
    var subject: String = "",
    var introduction: String? = null,
    var startTime: Date = Date(),
    var webinarType: Int = 3,
    var imgUrl: String? = null,
    var isPrivate: Boolean? = null,
    var isOpen: Boolean? = null,
    var hideWatch: Boolean? = null,
    var isAdiWatchDoc: Boolean? = null,
    var hideAppointment: Boolean? = null,
    var hidePv: Boolean? = null,
    var webinarCurrNum: Int? = null,
    var isCapacity: Boolean? = null,
    var num: Int? = null,
    var webinarShowType: Int? = null,
    var fee: BigDecimal? = null,
    var password: String? = null,
    var verify: Int? = null
)