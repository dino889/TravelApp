package com.whitebear.travel.src.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.rx
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthBehavior
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import com.whitebear.travel.R
import com.whitebear.travel.config.ApplicationClass
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentSignInBinding
import com.whitebear.travel.src.dto.NidProfile
import com.whitebear.travel.src.dto.User
import com.whitebear.travel.src.network.service.UserService
import com.whitebear.travel.util.CommonUtils
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import java.lang.reflect.Type

class SignInFragment : BaseFragment<FragmentSignInBinding>(FragmentSignInBinding::bind, R.layout.fragment_sign_in) {
    private val TAG = "SignInFragment"

    private lateinit var loginActivity: LoginActivity

    // google 로그인
    private lateinit var mAuth: FirebaseAuth
    var mGoogleSignInClient: GoogleSignInClient? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginActivity = context as LoginActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()

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

        // kakao Login
        kakaoLoginBtnClickEvent()

        // google Login
        googleLoginBtnClickEvent()

        // naver login SDK 초기화
        NaverIdLoginSDK.apply {
            showDevelopersLog(true)
            initialize(requireContext(), getString(R.string.naver_client_id), getString(R.string.naver_client_secret), getString(R.string.naver_client_name))
            isShowMarketLink = true
            isShowBottomTab = true
        }

        naverLoginBtnClickEvent()
    }

    private fun login(email: String, password: String){
        var result : HashMap<String, Any>

        runBlocking {
            result = mainViewModel.login(email, loginActivity.sha256(password))
        }

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
            showCustomToast("로그인에 실패했습니다. 다시 시도해 주세요.")
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

        val msg = existEmailRes["message"] as String

        if(existEmailRes["isSuccess"] == true && msg.contains("there is no email")) {   // 중복되는 이메일 없음.
            snsLoginJoin(user)
            return true
        } else if(existEmailRes["isSuccess"] == true && msg.contains("exist email") ) { // 이미 존재하는 이메일
            login(user.email, user.password)
            return false
        } else {
            showCustomToast("서버 통신에 실패했습니다.")
            Log.d(TAG, "existEmailChk: ${existEmailRes["message"]}")
            return false
        }
    }

    /**
     * 새로운 회원이라면 회원가입 진행
     */
    private fun snsLoginJoin(user: User) {
        val realPw = user.password

        val encPw = loginActivity.sha256(user.password)
        user.password = encPw

        var response : Response<HashMap<String, Any>>

        runBlocking {
            response = UserService().insertUser(user)
//            mainViewModel.join(User(email = email, password = loginActivity.sha256(password), nickname = nickname, username = username, social_type = socialType))
        }
        if(response.code() == 200 || response.code() == 500 || response.code() == 201) {
            val res = response.body()
            if (res != null) {
                Log.d(TAG, "join: $res")
                if(res["isSuccess"] == true && res["message"] == "create user successful") {
                    login(user.email, realPw)
                } else if(res["isSuccess"] == false) {
                    showCustomToast("회원가입에 실패했습니다. 다시 시도해 주세요.")
                }
//                else {
//                    Log.d(TAG, "join: $res")
//                    showCustomToast("회원가입에 실패했습니다. 다시 시도해 주세요.")
//                }
            }
        }
    }

    /**
     * sns Login - Google 로그인
     */

    // firebase auth 인증 초기화
    private fun initAuth() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_login_key))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        mAuth = FirebaseAuth.getInstance()

    }

    private fun googleLoginBtnClickEvent() {
        binding.signInFragmentGoogleBtn.setOnClickListener {
            initAuth()
            signIn()
        }
    }

    // 구글 로그인 창을 띄우는 작업
    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        requestActivity.launch(signInIntent)
    }

    // 구글 인증 결과 획득 후 동작 처리
    private val requestActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data = it.data

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    // 구글 인증 결과 성공 여부에 따른 처리
    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    if(user != null) {
                        // email nickname pw photo
                        val email = user.email.toString()
                        val nickname = user.displayName.toString()
                        val uid = user.uid
                        val newUser = User(email = email, password = uid, username = nickname, nickname = nickname, "google")

                        Log.d(TAG, "firebaseAuthWithGoogle: $newUser")
                        existEmailChk(newUser)
                    }
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * sns Login - Kakao
     */
    private fun kakaoLoginBtnClickEvent() {
        val disposables = CompositeDisposable()
        binding.signInFragmentKakaoBtn.setOnClickListener {
            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
                UserApiClient.rx.loginWithKakaoTalk(requireContext())
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorResumeNext { error ->
                        // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                        // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            Single.error(error)
                        } else {
                            // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                            UserApiClient.rx.loginWithKakaoAccount(requireContext())
                        }
                    }.observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ token ->
                        Log.i(TAG, "로그인 성공 ${token.accessToken}")
                        kakaoLogin()
                    }, { error ->
                        Log.e(TAG, "로그인 실패", error)
                    }).addTo(disposables)
            } else {
                UserApiClient.rx.loginWithKakaoAccount(requireContext())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ token ->
                        Log.i(TAG, "로그인 성공 ${token.accessToken}")
                        kakaoLogin()
                    }, { error ->
                        Log.e(TAG, "로그인 실패", error)
                    }).addTo(disposables)
            }
        }
    }

    private fun kakaoLogin() {
        val disposables = CompositeDisposable()
        // 사용자 정보 요청 (기본)
        UserApiClient.rx.me()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ user ->
//                Log.i(TAG, "사용자 정보 요청 성공" +
//                        "\n회원번호: ${user.id}" +  // pw
//                        "\n이메일: ${user.kakaoAccount?.email}" +  // id, email
//                        "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +  // nickname
//                        "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}")   // image

                val email = user.kakaoAccount?.email.toString()
                val uid = user.id.toString()
                val nickname = user.kakaoAccount?.profile?.nickname.toString()
//                val image = user.kakaoAccount?.profile?.thumbnailImageUrl.toString()

                val newUser = User(email = email, password = uid, username = nickname, nickname = nickname, "kakao")
                existEmailChk(newUser)

            }, { error ->
                Log.e(TAG, "사용자 정보 요청 실패", error)
            })
            .addTo(disposables)
    }




    /**
     * #S06P12D109-14
     * sns Login - Naver
     */

    private fun naverLoginBtnClickEvent() {
        binding.signInFragmentNaverBtn.setOnClickListener {
//            NaverIdLoginSDK.authenticate(requireContext(), oAuthLoginCallback)
            NaverIdLoginSDK.behavior = NidOAuthBehavior.DEFAULT
            NaverIdLoginSDK.authenticate(requireContext(), object : OAuthLoginCallback {
                override fun onSuccess() {
                    // 성공하면 사용자 프로필 불러오는 api 호출
                    NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
                        override fun onSuccess(profileResponse: NidProfileResponse) {

                            val type: Type = object : TypeToken<NidProfile>() {}.type
                            val user = CommonUtils.parseDto<NidProfile>(profileResponse.profile!!, type)

                            val email = user.email
                            val uid = user.id
                            val nickname = user.nickname
                            val username = user.name

                            val newUser = User(email = email, password = uid, username = username, nickname = nickname, "naver")
                            existEmailChk(newUser)
                        }

                        override fun onFailure(httpStatus: Int, message: String) {
                            val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                            val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                            showCustomToast("errorCode:$errorCode, errorDesc:$errorDescription")
                        }

                        override fun onError(errorCode: Int, message: String) {
                            onFailure(errorCode, message)
                        }
                    })
                }

                override fun onFailure(httpStatus: Int, message: String) {
                    val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                    val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                    showCustomToast("errorCode:$errorCode, errorDesc:$errorDescription")
                }

                override fun onError(errorCode: Int, message: String) {
                    onFailure(errorCode, message)
                }
            })
        }
    }


    // ---------------------------------------------------------------------------------------------
//    val mOAuthLoginHandler: OAuthLoginHandler = object : OAuthLoginHandler() {
//        override fun run(success: Boolean) {
//            if (success) {
//                val accessToken: String = mOAuthLoginInstance.getAccessToken(requireContext())
//                Log.d(TAG, "run: $accessToken")
//                RequestApiTask(requireContext(), mOAuthLoginInstance).execute()
//            } else {
//                val errorCode: String = mOAuthLoginInstance.getLastErrorCode(requireContext()).code
//                val errorDesc = mOAuthLoginInstance.getLastErrorDesc(requireContext())
//                Log.d(TAG, "run: errorCode:" + errorCode + ", errorDesc:" + errorDesc)
//            }
//        }
//    }
//
//
//    inner class RequestApiTask(private val mContext: Context, private val mOAuthLoginModule: OAuthLogin) :
//        AsyncTask<Void?, Void?, String>() {
//        override fun onPreExecute() {}
//
//        override fun onPostExecute(content: String) {
//            try {
//                val loginResult = JSONObject(content)
//                if (loginResult.getString("resultcode") == "00") {
//                    val response = loginResult.getJSONObject("response")
//                    val id = response.getString("email")
//                    val pw = response.getString("id")   // 사용자 식별 정보
//                    val nickname = response.getString("nickname")
//                    val mobile = response.getString("mobile")
//                    val gender = response.getString("gender")
//                    val birthYear = response.getString("birthyear")
//                    val birthDay = response.getString("birthday")
//                    var image = response.getString("profile_image")
//                    image = image.replace("\\", "")
//                    val newUser = User(id, pw, nickname, mobile, id, "$birthYear-$birthDay", gender, "naver", image)
//                    UserService().isUsedId(id, isUsedIdCallback(newUser))
//                }
//            } catch (e: JSONException) {
//                e.printStackTrace()
//            }
//        }
//
//        override fun doInBackground(vararg params: Void?): String {
//            val url = "https://openapi.naver.com/v1/nid/me"
//            val at = mOAuthLoginModule.getAccessToken(mContext)
//            return mOAuthLoginModule.requestApi(mContext, at, url)
//        }
//    }


}