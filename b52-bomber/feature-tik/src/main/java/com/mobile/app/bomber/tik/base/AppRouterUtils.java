package com.mobile.app.bomber.tik.base;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.mobile.app.bomber.tik.TikX;

public class AppRouterUtils {

    @NonNull
    @MainThread
    public static <T extends ViewModel> T viewModels(ViewModelStoreOwner owner, Class<T> modelClass) {
        ViewModelProvider provider = new ViewModelProvider(owner, viewModelFactory());
        return provider.get(modelClass);
    }

    @NonNull
    public static ViewModelProvider.Factory viewModelFactory() {
        return TikX.INSTANCE.getComponent().viewModelFactory();
    }
}
