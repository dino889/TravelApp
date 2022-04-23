package com.whitebear.travel.src.main

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
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
import android.location.LocationManager
import android.widget.Toast
import android.content.DialogInterface

import androidx.core.app.ActivityCompat.startActivityForResult

import android.content.Intent
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.whitebear.travel.src.dto.Noti
import com.whitebear.travel.src.network.api.FCMApi
import com.whitebear.travel.src.network.service.UserService
import com.whitebear.travel.util.NavDB
import com.whitebear.travel.util.NotiDB
import retrofit2.Response
import kotlin.collections.HashMap


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
    var hour = ""
    var addr = ""
    private var today2Type = ""
    private val GPS_ENABLE_REQUEST_CODE = 2001
    //Room DB
    var notiDB : NotiDB ?= null
    var navDB: NavDB?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //db저장
        notiDB = NotiDB.getInstance(this)
        navDB = NavDB.getInstance(this)

        var notiDao = notiDB?.fcmDao()
        val r = java.lang.Runnable {
            if(notiDao?.getFcmCheck(ApplicationClass.sharedPreferencesUtil.getUser().id) == null){
                notiDao?.insertChecked(Noti(ApplicationClass.sharedPreferencesUtil.getUser().id,false,false))
            }
        }
        val thread = Thread(r)
        thread.start()
        
        initNavigation()
        setInstance()
        if(checkPermissionForLocation(this)) {
            startLocationUpdates()
        }
        initFcm()
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
        mLocationRequest =  LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        //FusedLocationProviderClient의 인스턴스를 생성.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        // 기기의 위치에 관한 정기 업데이트를 요청하는 메서드 실행
        // 지정한 루퍼 스레드(Looper.myLooper())에서 콜백(mLocationCallback)으로 위치 업데이트를 요청
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper()!!)

    }

    // 시스템으로 부터 위치 정보를 콜백으로 받음
    private val mLocationCallback = object : LocationCallback() {
        @RequiresApi(Build.VERSION_CODES.O)
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
        Log.d(TAG, "onLocationChanged: ${location.latitude} / ${location.longitude}")
        getToday()
        //lat=35.8988, long=128.599
        runBlocking {
            mainViewModel.getWeather("JSON",10,1,today.toInt(),hour,"${location.latitude.toInt()}","${location.longitude.toInt()}")
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

    fun getToday() : String {
        var current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val formattering = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val hourFormatt = DateTimeFormatter.ofPattern("HH")
        val formatted = current.format(formatter)
        val formatted2 = current.format(formattering)
        val formatted3 = current.format(hourFormatt).toInt()
        today = formatted
        today2Type = formatted2
        if(formatted3.toInt() < 2){
            today = formatter.format(current.minusDays(1))
            hour = "2300"
        }else if(formatted3 < 5){
            hour = "0200"
        }else if(formatted3 < 8){
            hour = "0500"
        }else if(formatted3 < 11){
            hour = "0800"
        }else if(formatted3 < 14){
            hour = "1100"
        }else if(formatted3 < 17){
            hour = "1400"
        }else if(formatted3 < 20){
            hour = "1700"
        }else if(formatted3 < 23){
            hour = "2000"
        }else{
            hour = "2300"
        }
        mainViewModel.setHour(hour)
        mainViewModel.setToday(today.toInt())
        return today
    }

    fun checkRunTimePermission() {
        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            this@MainActivity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
            // 3.  위치 값을 가져올 수 있음
        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    LOCATION[0]
                )
            ) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(this@MainActivity, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG)
                    .show()
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(
                    this@MainActivity, LOCATION,
                    LOCATION_CODE
                )
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(
                    this@MainActivity, LOCATION,
                    LOCATION_CODE
                )
            }
        }
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    fun showDialogForLocationServiceSetting() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage(
            """
            앱을 사용하기 위해서는 위치 서비스가 필요합니다.
            위치 설정을 수정하시겠습니까?
            """.trimIndent()
        )
        builder.setCancelable(true)
        builder.setPositiveButton("설정", DialogInterface.OnClickListener { dialog, id ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
        })
        builder.setNegativeButton("취소",
            DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        builder.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GPS_ENABLE_REQUEST_CODE ->                 //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음")
                        checkRunTimePermission()
                        return
                    }
                }
        }
    }

    fun checkLocationServicesStatus(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }

    /**
     * FCM 토큰 수신 및 채널 생성
     */
    private fun initFcm() {
        // FCM 토큰 수신
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "FCM 토큰 얻기에 실패하였습니다.", task.exception)
                return@OnCompleteListener
            }
            // token log 남기기
            Log.d(TAG, "token: ${task.result?:"task.result is null"}")
            uploadToken(task.result!!, ApplicationClass.sharedPreferencesUtil.getUser().id)
        })

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(channel_id, "whitebear")
        }
    }

    /**
     * Fcm Notification 수신을 위한 채널 추가
     */
    private fun createNotificationChannel(id: String, name: String) {
        val importance = NotificationManager.IMPORTANCE_DEFAULT // or IMPORTANCE_HIGH
        val channel = NotificationChannel(id, name, importance)

        val notificationManager: NotificationManager
                = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        const val channel_id = "whitebear_channel"
        fun uploadToken(token:String, userId: Int) {

            var response : Response<HashMap<String, Any>>
            runBlocking {
                response = UserService().updateUserToken(ApplicationClass.sharedPreferencesUtil.getUser().id, token)
            }
            if(response.code() == 200) {
                val res = response.body()
                if(res != null) {
                    if(res["isSuccess"] == true) {
                        Log.d(TAG, "uploadToken: $token")
                    } else {
                        Log.d(TAG, "uploadToken: ${res["message"]}")
                    }
                }
            } else {
                Log.e(TAG, "uploadToken: 토큰 정보 등록 중 통신 오류")
            }
        }
    }
}