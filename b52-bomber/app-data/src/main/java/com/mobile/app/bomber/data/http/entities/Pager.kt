package com.mobile.app.bomber.data.http.entities

class Pager(val pageSize: Int = 10) {

    var requestPage = 1
        private set

    var totalPage = 1
        private set

    var isBusy = false

    val isReachedTheEnd: Boolean
        get() = requestPage > totalPage

    val isAvailable: Boolean
        get() = !isBusy && requestPage <= totalPage

    fun reset() {
        requestPage = 1
        totalPage = 1
        isBusy = false
    }

    fun nextPage(_totalPage: Int, forceOverrideTotalPage: Boolean = false) {
        if (forceOverrideTotalPage) {
            totalPage = _totalPage
        } else {
            if (requestPage == 1) {
                totalPage = _totalPage
            }
        }
        isBusy = false
        requestPage++
    }

    fun isFirstPage(logicFirstPage: Int): Boolean = logicFirstPage == requestPage
}