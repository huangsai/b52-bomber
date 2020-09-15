package com.mobile.app.bomber.runner.dagger

import android.app.Application
import android.content.Context
import com.mobile.guava.android.mvvm.dagger.AppScope
import dagger.Binds
import dagger.Module

@Module
abstract class RunnerBinder {

    @Binds
    @AppScope
    abstract fun provideContext(it: Application): Context
}