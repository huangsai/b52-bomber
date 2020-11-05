package com.mobile.app.bomber.tik.home

import android.Manifest.permission
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.LiveData
import com.mobile.app.bomber.common.base.tool.AppUtil
import com.mobile.app.bomber.runner.base.PrefsManager
import com.mobile.guava.android.mvvm.AndroidX
import timber.log.Timber
import java.io.IOException
import java.util.*

object LocationLiveData : LiveData<Location>(), LocationListener {

    private val manager by lazy {
        AndroidX.myApp.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    val value: Location? = PrefsManager.getLocation()

    @RequiresPermission(anyOf = [permission.ACCESS_COARSE_LOCATION, permission.ACCESS_FINE_LOCATION])
    override fun onInactive() {
        super.onInactive()
        manager.removeUpdates(this)
    }

    @RequiresPermission(anyOf = [permission.ACCESS_COARSE_LOCATION, permission.ACCESS_FINE_LOCATION])
    override fun onActive() {
        super.onActive()
        val providers = manager.allProviders
        val provider: String = when {
            providers.contains(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
            providers.contains(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
            providers.contains(LocationManager.PASSIVE_PROVIDER) -> LocationManager.PASSIVE_PROVIDER
            else -> ""
        }
        if (provider.isEmpty()) {
            Timber.tag("LocationLiveData").d("没有可用的位置提供器")
        } else {
            if (
                    AndroidX.myApp.checkCallingOrSelfPermission(permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED &&
                    AndroidX.myApp.checkCallingOrSelfPermission(permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
            ) {
                manager.requestLocationUpdates(provider, 3600_000L, 5_000.00f, this)
            }
        }
    }

    fun getAddressByLocation(): String {
        var locationShowStr = "无法获取地理信息"
        val location = value
        if (location != null) {
            val lat: Double = location.latitude //维度
            val lng: Double = location.longitude //经度
            val gc = Geocoder(AndroidX.myApp, Locale.getDefault())
            // 取得地址相关的一些信息\经度、纬度
            var addresses: List<Address>? = null
            try {
                addresses = gc.getFromLocation(lat, lng, 1)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (!addresses.isNullOrEmpty()) {
                Timber.tag("LocationLiveData").d("address: %s", addresses.toString());
                val address = addresses[0]
                locationShowStr = address.locality
            }
        }
        return locationShowStr
    }

    override fun onLocationChanged(location: Location) {
        location?.let {
            PrefsManager.setLocation(it)
            postValue(it)
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        Timber.tag("LocationLiveData").d("onStatusChanged")
    }

    override fun onProviderEnabled(provider: String) {
        Timber.tag("LocationLiveData").d("onProviderEnabled")
    }

    override fun onProviderDisabled(provider: String) {
        Timber.tag("LocationLiveData").d("onProviderDisabled")
    }

    fun lookupLocation(owner: LifecycleOwner,context: Context) {
        if (value == null && !AppUtil.isGpsAble(context)) {
            AppUtil.openGPS(context)
        }
        LocationLiveData.observe(owner, Observer { location: Location? ->
            if (location != null) {
                Timber.tag("LocationLiveData").d(
                        "(%s,%s)",
                        location.latitude,
                        location.longitude
                )
                LocationLiveData.removeObservers(owner)
            }
        })
    }
}