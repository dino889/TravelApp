package com.whitebear.travel.src.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding3.widget.textChanges
import com.whitebear.travel.R
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentResetPasswordBinding
import com.whitebear.travel.src.dto.User
import com.whitebear.travel.src.network.service.UserService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class ResetPasswordFragment : BaseFragment<FragmentResetPasswordBinding>(FragmentResetPasswordBinding::bind, R.layout.fragment_reset_password) {
    private val TAG = "ResetPasswordF"
    private lateinit var loginActivity: LoginActivity
    private lateinit var editTextSubscription: Disposable
    private var isVerificationSuccess = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginActivity = context as LoginActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDomain()

        getAuthNumClickEvent()
//        changePwBtnClickEvent()

        loginActivity.runOnUiThread(kotlinx.coroutines.Runnable {
            inputObservable()
        })
    }

    /**
     * email domain list set Adapter
     */
    private fun initDomain() {
        // 자동완성으로 보여줄 내용들
        val domains = arrayOf("gmail.com", "naver.com", "nate.com", "daum.net", "kakao.com", "icloud.com", "outlook.com", "hotmail.com", "outlook.kr")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, domains)
        binding.resetPwFragmentActvDomain.setAdapter(adapter)
    }


    // 인증번호 받기 버튼 클릭 이벤트
    private fun getAuthNumClickEvent() {
        binding.resetPwFragmentBtnGetAuthNum.setOnClickListener {
            // 1. 이메일 형식 확인
            // 2. 1이 true이면 이메일 중복 여부 확인
            // 3. 2를 통해 사용자가 입력한 이메일이 존재한다면 해당 이메일로 인증번호 발송
//            val res = existEmailChk(validatedEmail())
//            if(res == true) {
////                sendCodeToEmail()
//            }
        }
    }

//    // 인증번호 확인 버튼 클릭 이벤트
//    private fun okBtnClickEvent(code: String) {
//        Log.d(TAG, "okBtnClickEvent: $code")
//        binding.resetPwFragmentBtnConfirm.setOnClickListener {
//            if(code == binding.resetPwFragmentEtCertNum.text.toString()) {
//                showCustomToast("이메일 인증에 성공했습니다")
//                binding.resetPwFragmentBtnGetAuthNum.isEnabled = false
//                binding.resetPwFragmentTilCertNum.isEnabled = false
//
//                binding.resetPwFragmentTilPw1.visibility = View.VISIBLE
//                binding.resetPwFragmentTilPw2.visibility = View.VISIBLE
//                isVerificationSuccess = true
//            } else {
//                showCustomToast("이메일 인증에 실패했습니다")
//                binding.resetPwFragmentBtnGetAuthNum.isEnabled = true
//                binding.resetPwFragmentTilCertNum.isEnabled = true
//
//                binding.resetPwFragmentTilPw1.visibility = View.INVISIBLE
//                binding.resetPwFragmentTilPw2.visibility = View.INVISIBLE
//                isVerificationSuccess = false
//            }
//        }
//    }

//    // 비밀번호 변경 버튼 클릭 이벤트
//    private fun changePwBtnClickEvent() {
//        binding.resetPwFragmentBtnChangePw.setOnClickListener {
//            val pw1 = binding.resetPwFragmentEtPw1.text.toString()
//            val pw2 = binding.resetPwFragmentEtPw2.text.toString()
//            val email = validatedEmail()
//
//            if(pw1 == pw2 && isVerificationSuccess == true) {
//                val encPw = loginActivity.sha256(pw2)
//                val user = User(password = encPw, email = email!!)
//                var result : Response<HashMap<String, Any>>
//                runBlocking {
//                    result = UserService().updateUser()
//                }
//                if(result.code() == 200 || result.code() == 500) {
//                    val rbody = result.body()
//                    if(rbody != null) {
//                        if(rbody.data["isSuccess"] == true && rbody.message == "비밀번호 초기화 성공") {
//                            showCustomToast("비밀번호가 성공적으로 변경되었습니다. 새로운 비밀번호로 로그인 해주세요.")
//                            (requireActivity() as LoginActivity).onBackPressed()
//                        } else if(rbody.data["isSuccess"] == false) {
//                            showCustomToast("비밀번호 초기화 실패")
//                        }
//                        if(rbody.success == false) {
//                            showCustomToast("서버 통신 실패")
//                            Log.d(TAG, "changePwBtnClickEvent: ${rbody.message}")
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private fun sendCodeToEmail() {
//        var codeRes = Message()
//
//        runBlocking {
//            codeRes = mainViewModel.sendCodeToEmail(validatedEmail()!!)
//        }
//        if(codeRes.data["code"] != null && codeRes.message == "이메일 인증 요청 성공") {
//            showCustomToast("입력하신 이메일로 인증번호가 전송되었습니다.")
//            okBtnClickEvent(codeRes.data["code"] as String)
//
//        } else if(codeRes.data["code"] == null && codeRes.message == "이메일 인증 요청 실패") {
//
//            showCustomToast(codeRes.message)    // 이메일 존재O, but 이메일 인증 요청 실패
//
//        } else {
//            showCustomToast("서버 통신에 실패했습니다.")
//            Log.d(TAG, "certBtnClickEvent: ${codeRes.message}")
//        }
//    }
//

    /**
     * email 입력 데이터 검사
     * @return email 형식이면 email(String), 아니면 null
     */
    private fun validatedEmail() : String? {
        val inputEmail = binding.resetPwFragmentEtEmail.text.toString()
        val inputDomain = binding.resetPwFragmentActvDomain.text.toString()

        val email = "$inputEmail@$inputDomain"

        if(inputDomain.trim().isEmpty()) {
            binding.resetPwFragmentTilDomain.error = "Required Domain Field"
            return null
        }
        if(inputEmail.trim().isEmpty()) {
            binding.resetPwFragmentTilEmail.error = "Required Email Field"
            return null
        }
        else if(!Pattern.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z].{2,25}\$", email)) {
            binding.resetPwFragmentTilEmail.error = "이메일 형식을 확인해주세요."
            return null
        }
        else {
            binding.resetPwFragmentTilEmail.isErrorEnabled = false
            binding.resetPwFragmentTilDomain.isErrorEnabled = false
            return email
        }
    }


//    /**
//     * email 중복 체크
//     * @return 중복된 이메일이 없으면 true 반환
//     */
//    private fun existEmailChk(email: String?) : Boolean {
//        var existEmailRes : HashMap<String, Any> = hashMapOf()
//        runBlocking {
//            if(email != null) {
//                existEmailRes = mainViewModel.existsChkUserEmail(email)
//            }
//        }
//
//        if(existEmailRes["isSuccess"] == false) {  // 중복되는 이메일 없음.
//            binding.resetPwFragmentClCertNum.visibility = View.INVISIBLE
//            showCustomToast("존재하는 이메일이 없습니다. 입력 값을 다시 확인해 주세요.")
//            return false
//        } else if(existEmailRes["isSuccess"] == true) {  // 이미 존재하는 이메일
//            val socialType = existEmailRes["data"]
//            if(socialType == "none") {
//                binding.resetPwFragmentClCertNum.visibility = View.VISIBLE  // 인증번호 입력 보이도록하기
//                return true
//            } else {
//                Snackbar.make(requireView(), "소셜 로그인 회원이시네요! \n$socialType (으)로 로그인 해주세요╰(*°▽°*)╯", Snackbar.LENGTH_LONG).show()
//                (requireActivity() as LoginActivity).onBackPressed()
//                return false
//            }
//        } else {
//            showCustomToast("서버 통신에 실패했습니다.")
//            Log.d(TAG, "existEmailChk: ${existEmailRes["message"]}")
//            binding.resetPwFragmentClCertNum.visibility = View.INVISIBLE
//            return false
//        }
//    }

    /**
     * 각 EditText 쿼리 디바운스 적용
     */
    private fun inputObservable() {

        binding.resetPwFragmentEtEmail.setQueryDebounce {
            validatedEmail()
        }

        binding.resetPwFragmentActvDomain.setQueryDebounce {
            validatedEmail()
        }

        binding.resetPwFragmentEtPw1.setQueryDebounce {
            validatedPw(it)
        }

        binding.resetPwFragmentEtPw2.setQueryDebounce {
            chkSamePw(it)
        }
    }

    /**
     * 입력된 password 유효성 검사
     * @return 유효성 검사 통과 시 true 반환
     */
    private fun validatedPw(pw: String) : Boolean {
        if (pw.trim().isEmpty()) {   // 값이 비어있으면
            binding.resetPwFragmentTilPw1.error = "Required Field"
            binding.resetPwFragmentEtPw1.requestFocus()
            return false
        } else if (!Pattern.matches("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[\$@!%*#?&]).{8,25}.\$", pw)) {
            binding.resetPwFragmentTilPw1.error = "비밀번호 형식을 확인해주세요.(영어, 숫자, 특수문자 포함 8 ~ 25)"
            binding.resetPwFragmentEtPw1.requestFocus()
            return false
        } else {
            binding.resetPwFragmentTilPw1.isErrorEnabled = false
            return true
        }
    }

    private fun chkSamePw(pw2: String) : Boolean {
        val pw1 = binding.resetPwFragmentEtPw1.text.toString()

        if (pw2.trim().isEmpty()) {   // 값이 비어있으면
            binding.resetPwFragmentTilPw2.error = "Required Field"
            binding.resetPwFragmentEtPw2.requestFocus()
            return false
        } else if (!Pattern.matches("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[\$@!%*#?&]).{8,25}.\$", pw2)) {
            binding.resetPwFragmentTilPw2.error = "비밀번호 형식을 확인해주세요.(영어, 숫자, 특수문자 포함 8 ~ 25)"
            binding.resetPwFragmentEtPw2.requestFocus()
            return false
        } else if(pw1 == pw2) {
            binding.resetPwFragmentTilPw2.isErrorEnabled = false
            binding.resetPwFragmentBtnChangePw.visibility = View.VISIBLE
            return true
        } else {
            binding.resetPwFragmentTilPw2.error = "비밀번호가 일치하지 않습니다."
            binding.resetPwFragmentEtPw2.requestFocus()
            return false
        }
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
