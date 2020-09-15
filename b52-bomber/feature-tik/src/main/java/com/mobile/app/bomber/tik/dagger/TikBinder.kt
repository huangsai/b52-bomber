package com.mobile.app.bomber.tik.dagger

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import com.mobile.app.bomber.tik.category.CategoryViewModel
import com.mobile.app.bomber.tik.home.HomeViewModel
import com.mobile.app.bomber.tik.login.LoginViewModel
import com.mobile.app.bomber.tik.message.MsgViewModel
import com.mobile.app.bomber.tik.mine.MeViewModel
import com.mobile.app.bomber.tik.search.SearchViewModel
import com.mobile.app.bomber.tik.video.VideoViewModel
import com.mobile.guava.android.mvvm.dagger.FeatureScopeViewModelFactoryBinder
import com.mobile.guava.android.mvvm.dagger.ViewModelKey

@Module(includes = [FeatureScopeViewModelFactoryBinder::class])
abstract class TikBinder {

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(it: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(it: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MeViewModel::class)
    abstract fun bindMeViewModel(it: MeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CategoryViewModel::class)
    abstract fun bindCategoryViewModel(it: CategoryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VideoViewModel::class)
    abstract fun bindVideoViewModel(it: VideoViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MsgViewModel::class)
    abstract fun bindMsgViewModel(it: MsgViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindSearchViewModel(it: SearchViewModel): ViewModel
}