package com.whitebear.travel.src.login

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding3.widget.textChanges
import com.whitebear.travel.R
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentSignUpBinding
import com.whitebear.travel.src.dto.User
import com.whitebear.travel.src.network.service.UserService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


class SignUpFragment : BaseFragment<FragmentSignUpBinding>(FragmentSignUpBinding::bind, R.layout.fragment_sign_up) {
    private val TAG = "SignUpFragment"
    private lateinit var loginActivity: LoginActivity
    private lateinit var editTextSubscription: Disposable

    private var isEmailPossible = false
    private lateinit var certCode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginActivity = context as LoginActivity
        loginActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        joinBtnClickEvent()
        initDomain()

        loginActivity.runOnUiThread(kotlinx.coroutines.Runnable {
            inputObservable()
        })
    }


    /**
     * email 중복 체크
     * @return 중복된 이메일이 없으면 true 반환
     */
    /**
     * email 중복 체크
     * @return 중복된 이메일이 없으면 true 반환
     */
    private fun existEmailChk(email: String) : Boolean {
        var existEmailRes : HashMap<String, Any>
        runBlocking {
            existEmailRes = mainViewModel.existsChkUserEmail(email)
        }

        val msg = existEmailRes["message"] as String

        if(existEmailRes["isSuccess"] == true && msg.contains("there is no email")) {   // 중복되는 이메일 없음.
            binding.signUpFragmentTilEmail.isErrorEnabled = false
            binding.signUpFragmentTilDomain.isErrorEnabled = false
            return true
        } else if(existEmailRes["isSuccess"] == true && msg.contains("exist email") ) { // 이미 존재하는 이메일
            binding.signUpFragmentTilEmail.error = "중복된 이메일입니다. 다시 입력해 주세요."
//            if(socialType == "none") {
//                binding.joinFragmentClCertNum.visibility = View.GONE
//                showCustomToast("이미 존재하는 이메일입니다.")
//                return false
//            } else {
//                Snackbar.make(requireView(), "이미 가입하신 적이 있으시네요! \n$socialType (으)로 로그인 해주세요╰(*°▽°*)╯", Snackbar.LENGTH_LONG).show()
//                (requireActivity() as LoginActivity).onBackPressed()
//                return false
//            }
            return false
        } else {
            showCustomToast("서버 통신에 실패했습니다.")
            Log.d(TAG, "existEmailChk: ${existEmailRes["message"]}")
            return false
        }
    }


    // join 버튼 클릭 이벤트
    private fun joinBtnClickEvent() {
        binding.signUpFragmentBtnJoin.setOnClickListener {
            val nickname = binding.signUpFragmentTietNickname.text.toString()
            val username = binding.signUpFragmentTietUserName.text.toString()
            val password = binding.signUpFragmentTietPw.text.toString()

            val email = validatedEmail()
            if(existEmailChk(email!!) == false) {
                showCustomToast("중복된 이메일입니다. 다른 이메일을 사용해 주세요.")
                binding.signUpFragmentEtEmail.requestFocus()
            } else {

                val user = isAvailable(nickname, username, password, email, "none")
                if(user != null) {
                    join(email!!, nickname, username, password, "none")

    //                if(joinRes.data["isSignup"] == true && joinRes.message == "회원가입 성공") {
    //                    showCustomToast("회원가입이 완료되었습니다. 다시 로그인 해주세요.")
    //                    (requireActivity() as LoginActivity).onBackPressed()
    //                } else if(joinRes.data["isSignup"] == false && joinRes.message == "회원가입 실패") {
    //                    showCustomToast("회원가입에 실패했습니다. 다시 시도해 주세요.")
    //                } else if(joinRes.data["isExist"] == false && joinRes.message == "회원가입 실패") {
    //                    showCustomToast("이미 존재하는 이메일입니다. 다시 인증해 주세요.")
    //                } else {
    //                    showCustomToast("서버 통신에 실패했습니다.")
    //                    Log.d(TAG, "joinBtnClickEvent: ${joinRes.message}")
    //                }
                } else {
                    showCustomToast("입력 값을 다시 확인해 주세요.")
                }

            }
        }

    }

    // 회원가입
    private fun join(email: String, nickname: String, username: String, password: String, socialType: String) {

        var response : Response<HashMap<String, Any>>

        runBlocking {
            response = UserService().insertUser(User(email = email, password = loginActivity.sha256(password), nickname = nickname, username = username, social_type = socialType))
            Log.d(TAG, "join: $response")
//            mainViewModel.join(User(email = email, password = loginActivity.sha256(password), nickname = nickname, username = username, social_type = socialType))
        }
        if(response.code() == 200 || response.code() == 500 || response.code() == 201) {
            val res = response.body()
            if (res != null) {
                Log.d(TAG, "join: $res")
                if(res["isSuccess"] == true && res["message"] == "create user successful") {
                    showCustomToast("회원가입이 완료되었습니다. 다시 로그인 해주세요.")
                    (requireActivity() as LoginActivity).onBackPressed()
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
     * 필수 데이터 email 유효성 검사 및 중복 확인, pw, nickname 유효성 통과 여부 확인
     * @return 가입 가능한 상태이면 user 객체를 반환
     */
    private fun isAvailable(nickname: String, username: String, password: String, email: String?, socialType: String) : User? {

        if(validatedNickname(nickname) && validatedUsername(username) && validatedPw(password) && email != null) {
            return User(email = email, password = password, nickname = nickname, username = username, social_type = socialType)
        } else {
            return null
        }
    }


    /**
     * 각 EditText 쿼리 디바운스 적용
     */
    private fun inputObservable() {

        binding.signUpFragmentTietNickname.setQueryDebounce {
            validatedNickname(it)
        }

        binding.signUpFragmentTietUserName.setQueryDebounce {
            validatedUsername(it)
        }

        binding.signUpFragmentTietPw.setQueryDebounce {
            validatedPw(it)
        }

        binding.signUpFragmentEtEmail.setQueryDebounce {
            validatedEmail()
        }

        binding.signUpFragmentEtDomain.setQueryDebounce {
            validatedEmail()
        }
    }

    /**
     * 입력된 nickname 길이 및 빈 칸 체크
     * @return 통과 시 true 반환
     */
    private fun validatedNickname(nickname: String) : Boolean{
        if(nickname.trim().isEmpty()){
            binding.signUpFragmentTilNickname.error = "Required Field"
            binding.signUpFragmentTietNickname.requestFocus()
            return false
        } else if(nickname.length >= 25) {
            binding.signUpFragmentTilNickname.error = "Nickname 길이를 25자 이하로 설정해 주세요."
            binding.signUpFragmentTietNickname.requestFocus()
            return false
        }
        else {
            binding.signUpFragmentTilNickname.error = null
            return true
        }
    }

    /**
     * 입력된 username 길이 및 빈 칸 체크
     * @return 통과 시 true 반환
     */
    private fun validatedUsername(username: String) : Boolean{
        if(username.trim().isEmpty()){
            binding.signUpFragmentTilUserName.error = "Required Field"
            binding.signUpFragmentTietUserName.requestFocus()
            return false
        } else if(username.length >= 25) {
            binding.signUpFragmentTilUserName.error = "UserName 길이를 25자 이하로 설정해 주세요."
            binding.signUpFragmentTietUserName.requestFocus()
            return false
        }
        else {
            binding.signUpFragmentTilUserName.error = null
            return true
        }
    }

    /**
     * 입력된 password 유효성 검사
     * @return 유효성 검사 통과 시 true 반환
     */
    private fun validatedPw(pw: String) : Boolean {
        if(pw.trim().isEmpty()) {   // 값이 비어있으면
            binding.signUpFragmentTilPW.error = "Required Field"
            binding.signUpFragmentTietPw.requestFocus()
            return false
        } else if(!Pattern.matches("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[\$@!%*#?&]).{8,25}.\$", pw)) {
            binding.signUpFragmentTilPW.error = "비밀번호 형식을 확인해주세요.(영어, 숫자, 특수문자 포함 8 ~ 25)"
            binding.signUpFragmentTietPw.requestFocus()
            return false
        }
        else {
            binding.signUpFragmentTilPW.isErrorEnabled = false
            return true
        }
    }

    /**
     * email 입력 데이터 검사
     * @return email 형식이면 email(String), 아니면 null
     */
    private fun validatedEmail() : String? {
        val inputEmail = binding.signUpFragmentEtEmail.text.toString()
        val inputDomain = binding.signUpFragmentEtDomain.text.toString()

        val email = "$inputEmail@$inputDomain"

        if(inputDomain.trim().isEmpty()) {
            binding.signUpFragmentTilDomain.error = "Required Domain Field"
            return null
        }
        if(inputEmail.trim().isEmpty()) {
            binding.signUpFragmentTilEmail.error = "Required Email Field"
            return null
        }
        else if(!Pattern.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z].{2,25}\$", email)) {
            binding.signUpFragmentTilEmail.error = "이메일 형식을 확인해주세요."
            return null
        }
        else {
            binding.signUpFragmentTilEmail.isErrorEnabled = false
            binding.signUpFragmentTilDomain.isErrorEnabled = false
            existEmailChk(email)
            return email
        }
    }

    /**
     * email domain list set Adapter
     */
    private fun initDomain() {
        // 자동완성으로 보여줄 내용들
        val domains = arrayOf("gmail.com", "naver.com", "nate.com", "daum.net", "kakao.com", "icloud.com", "outlook.com", "hotmail.com", "outlook.kr")

        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, domains)
        binding.signUpFragmentEtDomain.setAdapter(adapter)
    }


    /**
     * EditText에 쿼리 디바운싱 함수
     */
    private fun EditText.setQueryDebounce(queryFunction: (String) -> Unit): Disposable {
        val editTextChangeObservable = this.textChanges()
        editTextSubscription =
            editTextChangeObservable
                // 마지막 글자 입력 0.5초 후에 onNext 이벤트로 데이터 발행
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                // 구독을 통해 이벤트 응답 처리
                .subscribeBy(
                    onNext = {
                        Log.d(TAG, "onNext : $it")
                        queryFunction(it.toString())
                    },
                    onComplete = {
                        Log.d(TAG, "onComplete")
                    },
                    onError = {
                        Log.i(TAG, "onError : $it")
                    }
                )
        return editTextSubscription  // Disposable 반환
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!editTextSubscription.isDisposed()) {
            editTextSubscription.dispose()
        }
    }
}