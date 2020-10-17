package com.mobile.app.bomber.data.repository

import com.mobile.guava.jvm.domain.SourceException

val sourceException403 = SourceException("快速登录已失效", 5)

fun Int.errorMsg(): String {
    return when (this) {
        1 -> "系统错误"
        2 -> "已经注册"
        3 -> "数据库错误"
        5 -> "快速登录已失效"
        6 -> "昵称太长"
        11 -> "验证码错误"
        12 -> "手机号错误"
        13 -> "请求参数出现错误"
        14 -> "未查询到任何信息"
        15 -> "结果不存在"
        else -> "ERROR"
    }
}

fun Int.toSourceException(): SourceException {
    return if (this == 5) {
        sourceException403
    } else {
        SourceException(this.errorMsg(), this)
    }
}

fun Throwable.is403(): Boolean {
    if (this is SourceException) {
        return this == sourceException403 || this.code == 5
    }
    return false
}