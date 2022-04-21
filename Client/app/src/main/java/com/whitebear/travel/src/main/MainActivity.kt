package com.whitebear.travel.src.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.location.*
import com.whitebear.travel.R
import com.whitebear.travel.config.ApplicationClass
import com.whitebear.travel.config.BaseActivity
import com.whitebear.travel.databinding.ActivityMainBinding
import com.whitebear.travel.src.network.viewmodel.MainViewModel
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

private const val TAG = "MainActivity"
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate){
    lateinit var mainViewModel : MainViewModel

    // 현재 위치 locationManager
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 변수
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    private lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장하는

    // 위치 권한
    private val LOCATION = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    private val LOCATION_CODE = 100
    private var today = ""
    private var hour = ""
    private var addr = ""
    private var today2Type = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initNavigation()
        setInstance()
//        if(mainViewModel.userLoc == null) {
//            val userLastLoc = ApplicationClass.sharedPreferencesUtil.getUserLoc()
//            if(userLastLoc != null) {
//                runBlocking {
//                    mainViewModel.getWeather("JSON",10,1,today.toInt(),1400,"${userLastLoc.latitude.toInt()}","${userLastLoc.longitude.toInt()}")
//                }
//            }
//        } else if(mainViewModel.userLoc != null) {
//            runBlocking {
//                mainViewModel.getWeather("JSON",10,1,today.toInt(),1400,"${mainViewModel.userLoc!!.latitude.toInt()}","${mainViewModel.userLoc!!.longitude.toInt()}")
//            }
//        }
        if(checkPermissionForLocation(this)) {
            startLocationUpdates()
        }
    }
    private fun setInstance(){
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
//        getMeasure()

    }

    private fun initNavigation(){
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.activity_main_navHost) as NavHostFragment

        // 네비게이션 컨트롤러
        val navController = navHostFragment.navController

        // 바인딩
        NavigationUI.setupWithNavController(binding.activityMainBottomNav, navController)
    }



    /**
     * bottom Nav hide & show
     * hide - true
     * show - false
     */
    fun hideBottomNav(state: Boolean) {
        if(state) {
            binding.activityMainBottomNav.visibility = View.GONE
        } else {
            binding.activityMainBottomNav.visibility = View.VISIBLE
        }
    }

    /**
     * 위치 권한
     */
    fun checkPermissionForLocation(context: Context): Boolean {
        // Android 6.0 Marshmallow 이상에서는 위치 권한에 추가 런타임 권한이 필요
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                // 권한이 없으므로 권한 요청 알림 보내기
                ActivityCompat.requestPermissions(this, LOCATION, LOCATION_CODE)
                false
            }
        } else {
            true
        }
    }

    fun checkPermission(permissions: Array<out String>, type: Int): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, permissions, type)
                    return false
                }
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {

            LOCATION_CODE -> {
                for(grant in grantResults) {
                    if(grant != PackageManager.PERMISSION_GRANTED) {
                        showCustomToast("위치 권한을 승인해 주세요.")
                        Log.d(TAG, "onRequestPermissionsResult: ")
                    } else if(grant == PackageManager.PERMISSION_GRANTED) {
                        startLocationUpdates()
                    }
                }
            }
        }
    }

    fun startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates: ")
        mLocationRequest =  LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        //FusedLocationProviderClient의 인스턴스를 생성.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        Log.d(TAG, "startLocationUpdates: 2?")
        // 기기의 위치에 관한 정기 업데이트를 요청하는 메서드 실행
        // 지정한 루퍼 스레드(Looper.myLooper())에서 콜백(mLocationCallback)으로 위치 업데이트를 요청
        runBlocking {
            mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper()!!)
        }

    }
    // 시스템으로 부터 위치 정보를 콜백으로 받음

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation
            onLocationChanged(locationResult.lastLocation)
            Log.d(TAG, "onLocationResult: ")
            // 시스템에서 받은 location 정보를 onLocationChanged()에 전달
        }
    }

    // 시스템으로 부터 받은 위치정보를 화면에 갱신해주는 메소드
    fun onLocationChanged(location: Location) {
        mLastLocation = location
        mainViewModel.setUserLoc(location, getAddress(location))
        Log.d(TAG, "onLocationChanged: ${location.latitude}")
        getToday()
        //lat=35.8988, long=128.599
        runBlocking {
            mainViewModel.getWeather("JSON",10,1,today.toInt(),"0200","${location.latitude.toInt()}","${location.longitude.toInt()}")
        }
    }

    fun getAddress(position: Location) : String {
        val geoCoder = Geocoder(this, Locale.getDefault())
        val address = geoCoder.getFromLocation(position.latitude, position.longitude, 1).first()
            .getAddressLine(0)
        addr = address
        Log.d(TAG, "Address, $address")
        return address
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getToday() : String {
        var current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val formattering = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val hourFormatt = DateTimeFormatter.ofPattern("HH")
        val formatted = current.format(formatter)
        val formatted2 = current.format(formattering)
//        val formatted2 = current.format(hourFormatt)
        today = formatted
        today2Type = formatted2
        mainViewModel.setToday(today.toInt())
        return today
    }

}