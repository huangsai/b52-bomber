package com.mobile.app.bomber.data.test

import androidx.annotation.VisibleForTesting
import com.mobile.app.bomber.data.DataComponent
import com.mobile.app.bomber.data.DataModule
import com.mobile.app.bomber.data.db.AppDatabase
import com.mobile.app.bomber.data.files.AppPrefsManager
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@VisibleForTesting
@Component(modules = [DataModule::class])
@Singleton
interface TestComponent : DataComponent {

    @Component.Factory
    interface Factory {

        fun create(
                @BindsInstance appDatabase: AppDatabase,
                @BindsInstance appPrefsManager: AppPrefsManager
        ): TestComponent
    }
}