package com.mobile.app.bomber.auth.dagger

import com.mobile.app.bomber.data.DataComponent
import com.mobile.app.bomber.runner.dagger.RunnerComponent
import com.mobile.guava.android.mvvm.dagger.FeatureScope
import com.mobile.guava.android.mvvm.dagger.ViewModelFactoryComponent
import dagger.Component

@Component(
        modules = [AuthBinder::class],
        dependencies = [DataComponent::class, RunnerComponent::class]
)
@FeatureScope
interface AuthComponent : ViewModelFactoryComponent {

    @Component.Factory
    interface Factory {

        fun create(
                dataComponent: DataComponent,
                runnerComponent: RunnerComponent
        ): AuthComponent
    }
}