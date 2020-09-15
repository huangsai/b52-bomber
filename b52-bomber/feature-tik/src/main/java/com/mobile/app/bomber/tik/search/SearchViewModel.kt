package com.mobile.app.bomber.tik.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.mobile.app.bomber.data.db.entities.DbTikSearchKey
import kotlinx.coroutines.Dispatchers
import com.mobile.app.bomber.common.base.MyBaseViewModel
import javax.inject.Inject

class SearchViewModel @Inject constructor() : MyBaseViewModel() {

    fun getKeys(): LiveData<List<DbTikSearchKey>> {
        return tikSearchRepository.getKeys()
                .asLiveData(Dispatchers.IO)
    }

    fun addKey(key: String): LiveData<DbTikSearchKey> {
        return liveData(Dispatchers.IO) {
            emit(tikSearchRepository.addKey(key))
        }
    }

    fun clearKeys(): LiveData<Int> {
        return liveData(Dispatchers.IO) {
            emit(tikSearchRepository.clearKeys())
        }
    }

    fun deleteKey(obj: DbTikSearchKey): LiveData<Int> {
        return liveData(Dispatchers.IO) {
            emit(tikSearchRepository.deleteKey(obj))
        }
    }

    suspend fun search(key: String) {
    }
}