package com.mobile.app.bomber.tik

import com.mobile.app.bomber.data.DataLib
import com.mobile.app.bomber.runner.RunnerLib
import com.mobile.app.bomber.tik.dagger.DaggerTikComponent
import com.mobile.app.bomber.tik.dagger.TikComponent

object TikLib {

    val component: TikComponent by lazy {
        DaggerTikComponent.factory().create(
                DataLib.component,
                RunnerLib.component
        )
    }
}