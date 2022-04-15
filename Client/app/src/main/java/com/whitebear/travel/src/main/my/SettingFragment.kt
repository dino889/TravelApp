package com.whitebear.travel.src.main.my

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.rx
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.whitebear.travel.R
import com.whitebear.travel.config.ApplicationClass
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentSettingBinding
import com.whitebear.travel.src.login.LoginActivity
import com.whitebear.travel.src.main.MainActivity
import com.whitebear.travel.src.network.service.UserService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.runBlocking
import retrofit2.Response

/**
 * @author Jiwoo Choi
 * @since 04/02/22
 */
class SettingFragment : BaseFragment<FragmentSettingBinding>(FragmentSettingBinding::bind, R.layout.fragment_setting) {
    private val TAG = "SettingFragment"
    private lateinit var mainActivity : MainActivity
    // firebase authenticationg
    var mGoogleSignInClient: GoogleSignInClient? = null

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
        initSnsInstance()
        initListener()
    }

    private fun initListener() {
        faqBtnClickEvent()
        backBtnClickEvent()
        logoutBtnClickEvent()
//        withdrawalBtnClickEvent()
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

//    private fun withdrawalBtnClickEvent() {
//        binding.settingFragmentTvWithdrawal.setOnClickListener {
//            showDeleteUserDialog()
//        }
//    }


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




    override fun onDestroyView() {
        super.onDestroyView()
        mainActivity.hideBottomNav(false)
    }


}