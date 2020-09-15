package com.mobile.app.bomber.runner.dagger

import android.app.Application
import android.content.Context
import com.mobile.app.bomber.data.DataComponent
import com.mobile.guava.android.mvvm.dagger.AppScope
import dagger.BindsInstance
import dagger.Component

@Component(
        modules = [RunnerBinder::class],
        dependencies = [DataComponent::class]
)
@AppScope
interface RunnerComponent {

    fun context(): Context

    @Component.Factory
    interface Factory {

        fun create(
                dataComponent: DataComponent,
                @BindsInstance app: Application
        ): RunnerComponent
    }
}