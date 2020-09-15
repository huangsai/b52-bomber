package com.mobile.app.bomber.tik.dagger

import com.mobile.app.bomber.data.DataComponent
import com.mobile.app.bomber.runner.dagger.RunnerComponent
import com.mobile.guava.android.mvvm.dagger.FeatureScope
import com.mobile.guava.android.mvvm.dagger.ViewModelFactoryComponent
import dagger.Component

@Component(
        modules = [TikBinder::class],
        dependencies = [DataComponent::class, RunnerComponent::class]
)
@FeatureScope
interface TikComponent : ViewModelFactoryComponent {

    @Component.Factory
    interface Factory {

        fun create(
                dataComponent: DataComponent,
                runnerComponent: RunnerComponent
        ): TikComponent
    }
}