package com.mobile.app.bomber.auth

import android.os.Bundle
import androidx.activity.viewModels
import com.mobile.app.bomber.auth.databinding.AuthActivityAuthBinding
import com.mobile.app.bomber.common.base.MyBaseActivity

class AuthActivity : MyBaseActivity() {

    val viewModel: AuthViewModel by viewModels {
        AuthX.component.viewModelFactory()
    }

    private lateinit var binding: AuthActivityAuthBinding

    var fragment: NopeAuthFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AuthActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fragment = LoginFragment().also {
            supportFragmentManager.beginTransaction()
                    .add(R.id.layout_auth_activity, it, it.javaClass.simpleName)
                    .disallowAddToBackStack()
                    .commit()
        }
    }
}