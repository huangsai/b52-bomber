package com.mobile.app.bomber.auth.dagger

import androidx.lifecycle.ViewModel
import com.mobile.app.bomber.auth.AuthViewModel
import com.mobile.guava.android.mvvm.dagger.FeatureScopeViewModelFactoryBinder
import com.mobile.guava.android.mvvm.dagger.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [FeatureScopeViewModelFactoryBinder::class])
abstract class AuthBinder {

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(it: AuthViewModel): ViewModel
}