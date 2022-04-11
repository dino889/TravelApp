package com.whitebear.travel.src.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.whitebear.travel.R
import com.whitebear.travel.config.ApplicationClass
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentSignInBinding
import com.whitebear.travel.src.dto.User
import com.whitebear.travel.src.network.service.UserService
import com.whitebear.travel.util.CommonUtils
import kotlinx.coroutines.runBlocking
import java.lang.reflect.Type

class SignInFragment : BaseFragment<FragmentSignInBinding>(FragmentSignInBinding::bind, R.layout.fragment_sign_in) {
    private val TAG = "SignInFragment"

    private lateinit var loginActivity: LoginActivity

//    // google 로그인
//    private lateinit var mAuth: FirebaseAuth
//    var mGoogleSignInClient: GoogleSignInClient? = null
//
//    // naver 로그인
//    lateinit var mOAuthLoginInstance : OAuthLogin
//
//    // kakao 로그인
////    private var disposables = CompositeDisposable()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginActivity = context as LoginActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()

//        googleLoginBtnClickEvent()
//        kakaoLoginBtnClickEvent()
//        facebookLoginBtnClickEvent()
    }

    private fun initListener() {

        // SignUp now
        binding.signInFragmentSignUpBtn.setOnClickListener {
            loginActivity.openFragment(3)
        }

        // Forgot password
        binding.signInFragmentLostPwTv.setOnClickListener {
            loginActivity.openFragment(4)
        }

        // login
        binding.signInFragmentLoginBtn.setOnClickListener {
            login(binding.signInFragmentEmailEt.text.toString(), binding.signInFragmentPwEt.text.toString())
        }
    }

    private fun login(email: String, password: String){
        var result : HashMap<String, Any>

        runBlocking {
            result = mainViewModel.login(email, loginActivity.sha256(password))
            Log.d(TAG, "login: ${loginActivity.sha256(password)}")
        }
        Log.d(TAG, "login: $email, $password, ${loginActivity.sha256("test")}")
        if(result["data"] != null && result["message"] == "login success") {
            val loginUser = result["data"]

            val type: Type = object : TypeToken<User>() {}.type
            val user = CommonUtils.parseDto<User>(loginUser!!, type)

            ApplicationClass.sharedPreferencesUtil.addUser(User(user.id, user.token))

            if(binding.signInFragmentAutoLoginCb.isChecked) {   // 자동 로그인이 체크되어 있으면
                ApplicationClass.sharedPreferencesUtil.setAutoLogin(user.id)
            }

            showCustomToast("로그인 되었습니다.")
            loginActivity.openFragment(1)

        } else if(result["isSuccess"] == false) {
            showCustomToast("ID와 PW를 확인해 주세요.")

        } else if(email.isEmpty() || password.isEmpty()){
            showCustomToast("E-MAIN, PW를 입력해 주세요")
        } else {
            showCustomToast("서버 통신에 실패했습니다.")
            Log.d(TAG, "loginBtnClickEvent: ${result["data"]} ${result["message"]}")
        }
    }

    /**
     * email 중복 체크
     * @return 중복된 이메일이 없으면 true 반환
     */
    private fun existEmailChk(user: User) : Boolean {
        var existEmailRes : HashMap<String, Any>
        runBlocking {
            existEmailRes = mainViewModel.existsChkUserEmail(user.email)
        }

        if(existEmailRes["isSuccess"] == false) {   // 중복되는 이메일 없음.
//            snsLoginJoin(user)
            return true
        } else if(existEmailRes["isSuccess"] == true) { // 이미 존재하는 이메일
            login(user.email, user.password)
            return false
        } else {
            showCustomToast("서버 통신에 실패했습니다.")
            Log.d(TAG, "existEmailChk: ${existEmailRes["message"]}")
            return false
        }
    }


}