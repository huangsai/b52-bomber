package com.mobile.app.bomber.tik.home

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mobile.app.bomber.common.base.tool.SingleClick
import com.mobile.app.bomber.runner.base.PrefsManager
import com.mobile.app.bomber.tik.R
import com.mobile.app.bomber.tik.base.AppRouterUtils
import com.mobile.app.bomber.tik.base.requireLogin
import com.mobile.app.bomber.tik.category.CategoryActivity
import com.mobile.app.bomber.tik.databinding.FragmentHomeBinding
import com.mobile.app.bomber.tik.login.LoginActivity
import com.mobile.app.bomber.tik.search.SearchActivity
import com.mobile.app.bomber.tik.video.VideoRecordActivity
import com.mobile.ext.net.HttpInputDialogFragment
import com.mobile.ext.net.OnDialogDismissListener
import com.mobile.ext.permission.PermissionCallback
import com.mobile.ext.permission.PermissionRequest
import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.guava.android.mvvm.newStartActivity
import com.mobile.guava.android.mvvm.showDialogFragment
import com.mobile.guava.android.ui.view.recyclerview.enforceSingleScrollDirection
import com.mobile.guava.android.ui.view.viewpager.recyclerView
import com.mobile.guava.jvm.Guava
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation

class HomeFragment : TopMainFragment(), View.OnClickListener, View.OnLongClickListener, TabLayout.OnTabSelectedListener {

    private val tabTitles = arrayOf("关注", "推荐")

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var mediator: TabLayoutMediator
    private lateinit var adapter: MyAdapter

    private var balloon: Balloon? = null

    private var currentPosition = -1
    private var ignoreOnPageChangeCallback = false
    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            if (ignoreOnPageChangeCallback) {
                ignoreOnPageChangeCallback = false
                return
            }
            if (position == 0) {
                requireActivity().requireLogin(ActivityResultCallback {
                    if (it.resultCode == Activity.RESULT_OK) {
                        currentPosition = position
                    } else {
                        ignoreOnPageChangeCallback = true
                        binding.viewPager.setCurrentItem(currentPosition, false)
                    }
                })
            } else {
                currentPosition = position
            }
        }
    }

    val model: HomeViewModel by viewModels { AppRouterUtils.viewModelFactory() }
    var currentFragment: TopHomeFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt("position")
        }
        adapter = MyAdapter(this)
        requestPermission()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.imgSearch.setOnClickListener(this)
        binding.imgCamera.setOnClickListener(this)
        if (Guava.isDebug) {
            binding.imgCamera.setOnLongClickListener(this)
        }
        binding.imgDown.setOnClickListener(this)
        binding.viewPager.recyclerView.enforceSingleScrollDirection()
        binding.viewPager.offscreenPageLimit = 2
        binding.viewPager.adapter = adapter
        binding.lineFull.setOnClickListener(this)
        currentPosition = 1
        binding.viewPager.setCurrentItem(currentPosition, false)

        binding.viewPager.registerOnPageChangeCallback(onPageChangeCallback)
        mediator = TabLayoutMediator(binding.layoutTab, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }
        mediator.attach()
        binding.layoutTab.addOnTabSelectedListener(this)

        return binding.root
    }

    //弹框菜单框（分类、同城）
    private fun showPopupOptions(anchor: View) {
        balloon = Balloon.Builder(AndroidX.myApp)
                .setLayout(R.layout.tik_home_pop_menu)
                .setArrowVisible(false)
                .setBackgroundColor(Color.TRANSPARENT)
                .setBalloonAnimation(BalloonAnimation.FADE)
                .setLifecycleOwner(this)
                .setMarginRight(0)
                .setMarginBottom(24)
                .setMarginTop(30)
                .setCornerRadius(0f)
                .setAutoDismissDuration(2000)
                .build()
                .apply {
                    with(getContentView()) {
                        findViewById<View>(R.id.txt_category).setOnClickListener {
                            newStartActivity(CategoryActivity::class.java)
                        }
                        findViewById<View>(R.id.txt_nearby).setOnClickListener {
                            requestPermissionLocation()

                        }
                    }
                    show(anchor)
                }
    }

    // 请求定位权限
    private fun requestPermission() {
        PermissionRequest.request(this, 101, arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                object : PermissionCallback {
                    override fun passPermissions() {
                        LocationLiveData.lookupLocation(this@HomeFragment, requireContext())
                    }
                })

    }

    private fun requestPermissionLocation() {
        PermissionRequest.request(this, 102, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                object : PermissionCallback {
                    override fun passPermissions() {
                        newStartActivity(NearByActivity::class.java)
                    }
                })
    }

    // 请求相机权限
    private fun requestPermissionCamera() {
        PermissionRequest.request(this, 103, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO),
                object : PermissionCallback {
                    override fun passPermissions() {
                        newStartActivity(VideoRecordActivity::class.java)
                    }
                })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        when (requestCode) {
            101 -> {
                var hasPhoneStatePermission = true
                var hasLocationPermission = true
                for (i in permissions.indices) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        //判断是否勾选禁止后不再询问
                        val key = permissions[i]
                        if (key == Manifest.permission.READ_PHONE_STATE) {
                            hasPhoneStatePermission = false
                        } else {
                            hasLocationPermission = false
                        }
                    }
                }
                if (hasLocationPermission) {
                    LocationLiveData.lookupLocation(this, requireContext())
                    if (!hasPhoneStatePermission) {
                        PermissionRequest.alertPermission(R.string.alert_msg_permission_phone_state, activity)
                    }
                } else {
                    if (!hasPhoneStatePermission) {
                        PermissionRequest.alertPermission(R.string.alert_msg_permission_location_and_phone, activity)
                    } else {
                        PermissionRequest.alertPermission(R.string.alert_msg_permission_location, activity)
                    }
                }
            }
            102 -> {
                if (grantResults.isNotEmpty() &&
                        grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    LocationLiveData.lookupLocation(this, requireContext())
                    newStartActivity(NearByActivity::class.java)
                } else {
                    PermissionRequest.alertPermission(R.string.alert_msg_permission_location, requireActivity())
                }
                return
            }
            103 -> {
                var hasCameraPermission = true
                var hasWritePermission = true
                var hasRecordPermission = true
                for (i in permissions.indices) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        //判断是否勾选禁止后不再询问
                        val key = permissions[i]
                        if (key == Manifest.permission.CAMERA) {
                            hasCameraPermission = false
                        } else if (key == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                            hasWritePermission = false
                        } else if (key == Manifest.permission.RECORD_AUDIO) {
                            hasRecordPermission = false
                        }
                    }
                }
                if (hasCameraPermission && hasWritePermission && hasRecordPermission) {
                    newStartActivity(VideoRecordActivity::class.java)
                } else {
                    PermissionRequest.alertPermission(R.string.alert_msg_permission_camera_storage_record, activity)
                }
            }
        }
    }

    @SingleClick
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.img_search -> newStartActivity(SearchActivity::class.java)
            R.id.img_camera -> {
                if (PrefsManager.isLogin()) {
                    requestPermissionCamera()
                } else {
                    newStartActivity(LoginActivity::class.java)
                }
            }
            R.id.img_down -> {
                showPopupOptions(v)
            }
            R.id.line_full -> {
                showPopupOptions(v)
            }
        }
    }

    //处理自动服务器ip地址 切换管理
    override fun onLongClick(p0: View?): Boolean {
        val dialogFragment = HttpInputDialogFragment.newInstance(
                object : OnDialogDismissListener {
                    override fun onDismiss(vararg args: String) {
                        PrefsManager.setHttpAddress(args[0])
                        PrefsManager.setHttpAddressUpload(args[1])
                    }
                })
        this.showDialogFragment(dialogFragment)
        return true
    }

    fun selectFragment(position: Int) {
        binding.viewPager.setCurrentItem(position, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.viewPager.adapter = null
        binding.viewPager.unregisterOnPageChangeCallback(onPageChangeCallback)
        _binding = null
    }

    companion object {

        @JvmStatic
        fun newInstance(position: Int): HomeFragment = HomeFragment().apply {
            arguments = Bundle().apply {
                putInt("position", position)
            }
        }
    }

    class MyAdapter(fragment: HomeFragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> FollowingFragment.newInstance(position)
                else -> RecommendFragment.newInstance(position)
            }
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        tab?.setCustomView(null)
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        val textView = TextView(activity)
        val selectedSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 18f, resources.displayMetrics)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, selectedSize)
        textView.text = tab?.text
        textView.setTextColor(Color.WHITE)
        textView.getPaint().setFakeBoldText(true);
        tab?.customView = textView
    }

}