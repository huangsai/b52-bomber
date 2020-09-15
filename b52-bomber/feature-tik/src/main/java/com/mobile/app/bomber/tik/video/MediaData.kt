package com.mobile.app.bomber.tik.video

import java.io.Serializable

class MediaData(
    val path: String,
    val type: String,
    var width: Int,
    var height: Int
) : Serializable {
  var duration: Int = 0
}