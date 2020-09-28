package com.mobile.app.bomber.tik.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.mobile.app.bomber.data.db.entities.DbTikSearchKey
import kotlinx.coroutines.Dispatchers
import com.mobile.app.bomber.common.base.MyBaseViewModel
import com.mobile.app.bomber.data.http.entities.ApiVideo
import com.mobile.app.bomber.data.http.entities.Pager
import com.mobile.guava.jvm.domain.Source
import kotlinx.coroutines.flow.flow
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


    fun search(key: String, page: Pager): LiveData<Source<List<ApiVideo.Video>>> {
        return flow { emit(tikSearchRepository.searchTikVideoList(key, page)) }
                .asLiveData(Dispatchers.IO)

//        return liveData(Dispatchers.IO) {
//            emit(tikSearchRepository.searchTikVideoList(key, page))
//        }
    }

    fun getHotKeyTop(): LiveData<Source<List<String>>> {
        return flow { emit(tikSearchRepository.getHotKeyTopN()) }
                .asLiveData(Dispatchers.IO)
//        return liveData(Dispatchers.IO) {
//            emit(tikSearchRepository.searchTikVideoList(key, page))
//        }
    }

    suspend fun search(key: String) {
    }
}