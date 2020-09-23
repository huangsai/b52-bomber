package com.mobile.app.bomber.tik

import com.mobile.app.bomber.data.DataX
import com.mobile.app.bomber.runner.RunnerX
import com.mobile.app.bomber.tik.dagger.DaggerTikComponent
import com.mobile.app.bomber.tik.dagger.TikComponent

object TikX {

    val component: TikComponent by lazy {
        DaggerTikComponent.factory().create(
                DataX.component,
                RunnerX.component
        )
    }
}