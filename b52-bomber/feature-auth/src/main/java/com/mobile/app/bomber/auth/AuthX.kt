package com.mobile.app.bomber.auth

import com.mobile.app.bomber.auth.dagger.AuthComponent
import com.mobile.app.bomber.auth.dagger.DaggerAuthComponent
import com.mobile.app.bomber.data.DataX
import com.mobile.app.bomber.runner.RunnerX

object AuthX {

    val component: AuthComponent by lazy {
        DaggerAuthComponent.factory().create(
                DataX.component,
                RunnerX.component
        )
    }
}