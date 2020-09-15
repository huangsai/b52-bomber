package com.mobile.app.bomber.movie.player

import androidx.lifecycle.ViewModel
import com.mobile.app.bomber.data.http.entities.ApiComment
import javax.inject.Inject

class PlayerViewModel @Inject constructor() : ViewModel() {

    fun requestComment(): List<ApiComment.Comment> {
        val children = List<ApiComment.Comment>(10) {
            createComment(it.toLong(), emptyList())
        }
        return listOf(
                createComment(100, children),
                createComment(101, children),
                createComment(102, children),
                createComment(103, children),
                createComment(104, children),
                createComment(105, children),
                createComment(106, children),
                createComment(107, children),
                createComment(108, emptyList())
        )
    }

    private fun createComment(id: Long, children: List<ApiComment.Comment>): ApiComment.Comment {
        return ApiComment.Comment(
                1,
                1,
                id,
                3,
                System.currentTimeMillis(),
                "username",
                "username",
                "Image Filter is an Android Libary that lets you to Filtering any image. You can report any issue on issues page. Note: If you speak Arabic, you can submit issues with Arabic language and I will check them",
                1,
                1,
                "targetUsername",
                "targetUsername",
                true,
                emptyList(),
                children
        )
    }
}