package com.whitebear.travel.src.main.my

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.rx
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.whitebear.travel.R
import com.whitebear.travel.config.ApplicationClass
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentSettingBinding
import com.whitebear.travel.src.dto.FcmDao
import com.whitebear.travel.src.dto.Noti

import com.whitebear.travel.src.login.LoginActivity
import com.whitebear.travel.src.main.MainActivity
import com.whitebear.travel.src.network.service.UserService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import kotlin.properties.Delegates

/**
 * @author Jiwoo Choi
 * @since 04/02/22
 */
class SettingFragment : BaseFragment<FragmentSettingBinding>(FragmentSettingBinding::bind, R.layout.fragment_setting) {
    private val TAG = "SettingFragment"
    private lateinit var mainActivity : MainActivity
//    val db = Room.databaseBuilder(requireContext(), AppDatabase::class.java, "fcm").build()
//    val notiDao = db.fcmDao()
    // firebase authenticationg
    var mGoogleSignInClient: GoogleSignInClient? = null
    lateinit var notiDao:FcmDao
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.hideBottomNav(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notiDao = mainActivity.notiDB?.fcmDao()!!
        var eventCheck = false
        var infoCheck = false
        var job = CoroutineScope(Dispatchers.IO).launch {
            var fcm = notiDao.getFcmCheck(ApplicationClass.sharedPreferencesUtil.getUser().id)
            eventCheck = fcm.eventChecked
            infoCheck = fcm.infoChecked
        }
        runBlocking {
            job.join()
        }

        Log.d(TAG, "onViewCreated: $eventCheck  $infoCheck")
        binding.fragmentSettingInfoSwitch.setChecked(infoCheck)
        binding.fragmentSettingEventSwitch.setChecked(eventCheck)

        initSnsInstance()
        initListener()
    }

    private fun initListener() {
        faqBtnClickEvent()
        fcmBtnClickEvent()
        backBtnClickEvent()
        logoutBtnClickEvent()
        withdrawalBtnClickEvent()
    }
    private fun fcmBtnClickEvent(){
        binding.fragmentSettingEventSwitch.setOnCheckedChangeListener { p0, p1 ->
            if (p1) {
                FirebaseMessaging.getInstance().subscribeToTopic("event")
                val r = Runnable {
                    notiDao?.updateEventChecked(
                        true,
                        ApplicationClass.sharedPreferencesUtil.getUser().id
                    )
                }
                val thread = Thread(r)
                thread.start()

            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("event")
                val r = Runnable {
                    notiDao?.updateEventChecked(
                        false,
                        ApplicationClass.sharedPreferencesUtil.getUser().id
                    )
                }
                val thread = Thread(r)
                thread.start()
            }
        }
        binding.fragmentSettingInfoSwitch.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                if(p1){
                    val r = Runnable {
                        notiDao?.updateInfoChecked(true, ApplicationClass.sharedPreferencesUtil.getUser().id)
                    }
                    val thread = Thread(r)
                    thread.start()

                    FirebaseMessaging.getInstance().subscribeToTopic("info")
                }else{
                    val r = Runnable {
                        notiDao?.updateInfoChecked(false, ApplicationClass.sharedPreferencesUtil.getUser().id)
                    }
                    val thread = Thread(r)
                    thread.start()

                    FirebaseMessaging.getInstance().unsubscribeFromTopic("info")
                }
            }

        })
    }
    private fun faqBtnClickEvent() {
        binding.settingFragmentTvFAQ.setOnClickListener {
            this@SettingFragment.findNavController().navigate(R.id.action_settingFragment_to_faqFragment)
        }
    }
    
    private fun backBtnClickEvent() {
        binding.settingFragmentIvBack.setOnClickListener {
            this.findNavController().popBackStack()
        }
    }

    private fun initSnsInstance() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_login_key))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        // naver login SDK 초기화
        NaverIdLoginSDK.apply {
            showDevelopersLog(true)
            initialize(requireContext(), getString(R.string.naver_client_id), getString(R.string.naver_client_secret), getString(R.string.naver_client_name))
            isShowMarketLink = true
            isShowBottomTab = true
        }
    }

    private fun logoutBtnClickEvent() {
        binding.settingFragmentTvLogout.setOnClickListener {
            logout()
        }
    }

    private fun withdrawalBtnClickEvent() {
        binding.settingFragmentTvWithdrawal.setOnClickListener {
            showDeleteUserDialog()
        }
    }


    /**
     * @author Jiwoo
     * 로그아웃
     */
    private fun logout() {
        mainViewModel.loginUserInfo.observe(viewLifecycleOwner) {
            val type = it.social_type
            if (type == "google") {
                // google, facebook Logout
                FirebaseAuth.getInstance().signOut()
            } else if (type == "kakao") {
                // kakao Logout
                val disposables = CompositeDisposable()

                UserApiClient.rx.logout()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제 됨")
                    }, { error ->
                        Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제 됨", error)
                    }).addTo(disposables)
            } else if(type == "naver") {
                NaverIdLoginSDK.logout()
            }
        }

        ApplicationClass.sharedPreferencesUtil.deleteUser()
        ApplicationClass.sharedPreferencesUtil.deleteUserCookie()
        ApplicationClass.sharedPreferencesUtil.deleteAutoLogin()

        //화면이동
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    /**
     * @author Jiwoo
     * 사용자 회원탈퇴 다이얼로그
     */
    private fun showDeleteUserDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("회원 탈퇴")
            .setMessage("정말 탈퇴하시겠습니까?")
            .setPositiveButton("YES", DialogInterface.OnClickListener{ dialogInterface, id ->
                withdrawal()
            })
            .setNeutralButton("NO", null)
            .create()

        builder.show()
    }

    /**
     * 회원 탈퇴
     * @author Jiwoo
     */
    private fun withdrawal() {
        // 탈퇴기능구현
        mainViewModel.loginUserInfo.observe(viewLifecycleOwner) {
            val type = it.social_type
            if (type == "kakao") {
                val disposables = CompositeDisposable()
                // 연결 끊기
                UserApiClient.rx.unlink()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        Log.i(TAG, "kakao 연결 끊기 성공. SDK에서 토큰 삭제 됨")
                    }, { error ->
                        Log.e(TAG, "kakao 연결 끊기 실패", error)
                    }).addTo(disposables)
            } else if (type == "google") {
                FirebaseAuth.getInstance().currentUser?.delete()!!.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //로그아웃처리
                        FirebaseAuth.getInstance().signOut()
                        mGoogleSignInClient?.signOut()
                        Log.i(TAG, "firebase auth 로그인 연결 끊기 성공")
                    } else {
                        Log.i(TAG, "firebase auth 로그인 user 삭제 실패")
                    }
                }
            } else if(type == "naver") {
                NidOAuthLogin().callRefreshAccessTokenApi(requireContext(), object : OAuthLoginCallback {
                    override fun onSuccess() {
                        Log.i(TAG, "onSuccess: naver token 갱신 성공")

                        NidOAuthLogin().callDeleteTokenApi(requireContext(), object : OAuthLoginCallback {
                            override fun onSuccess() {
                                Log.i(TAG, "naver 연결 끊기 성공. SDK에서 토큰 삭제 됨")
                            }

                            override fun onFailure(httpStatus: Int, message: String) {
                                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                                Log.i(TAG, "delete token errorCode:$errorCode, errorDesc:$errorDescription")
                            }

                            override fun onError(errorCode: Int, message: String) {
                                onFailure(errorCode, message)
                            }
                        })
                    }

                    override fun onFailure(httpStatus: Int, message: String) {
                        val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                        val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                        Log.i(TAG, "refresh token errorCode:$errorCode, errorDesc:$errorDescription")
                    }

                    override fun onError(errorCode: Int, message: String) {
                        onFailure(errorCode, message)
                    }

                })

            }

            var res: Response<HashMap<String, Any>>
            runBlocking {
                res = UserService().deleteUser(ApplicationClass.sharedPreferencesUtil.getUser().id)
            }
            if (res.code() == 200 || res.code() == 500) {
                val rbody = res.body()
                if (rbody != null) {
                    if (rbody["isSuccess"] == true) {
                        showCustomToast("회원 탈퇴가 완료되었습니다.")
                        logout()
                    } else if (rbody["isSuccess"] == false) {
                        showCustomToast("회원 탈퇴 실패")
                    }
                } else {
                    showCustomToast("서버 통신 실패")
                    Log.d(TAG, "withdrawal: ${res.message()}")
                }
            }
            ApplicationClass.sharedPreferencesUtil.deleteUser()
            ApplicationClass.sharedPreferencesUtil.deleteUserCookie()
            ApplicationClass.sharedPreferencesUtil.deleteAutoLogin()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mainActivity.hideBottomNav(false)
    }
    override fun onPause() {
        super.onPause()
        mainActivity.hideBottomNav(true)
    }

    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNav(true)
    }

}