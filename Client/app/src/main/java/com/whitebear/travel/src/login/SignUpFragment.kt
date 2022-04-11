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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


class SignUpFragment : BaseFragment<FragmentSignUpBinding>(FragmentSignUpBinding::bind, R.layout.fragment_sign_up) {
//    private lateinit var loginActivity: LoginActivity
//    private lateinit var editTextSubscription: Disposable
//
//    private var isEmailPossible = false
//    private lateinit var certCode: String
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        loginActivity = context as LoginActivity
//        loginActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        dupChkEmailClickEvent()
//        joinBtnClickEvent()
//        initDomain()
//
//        loginActivity.runOnUiThread(kotlinx.coroutines.Runnable {
//            inputObservable()
//        })
//    }
//
//    // 이메일 중복 확인 버튼 클릭 이벤트
//    private fun dupChkEmailClickEvent() {
//        binding. fragmentJoinBtnDupChkEmail.setOnClickListener {
//
//
////            else {
////                showCustomToast("중복된 이메일입니다.")
////            }
//        }
//
//    }
//
//
//
//    /**
//     * email 중복 체크
//     * @return 중복된 이메일이 없으면 true 반환
//     */
//    private fun existEmailChk(email: String?) : Boolean {
//        var existEmailRes = Message()
//        runBlocking {
//            if(email != null) {
//                existEmailRes = mainViewModel.existsChkUserEmail(email)
//            }
//        }
//
//        if(existEmailRes.data["type"] == "false" && existEmailRes.message == "중복된 이메일 없음") {
//            return true
//        } else if(existEmailRes.message == "이미 존재하는 이메일") {
//            val socialType = existEmailRes.data["type"]
//            if(socialType == "none") {
//                binding.joinFragmentClCertNum.visibility = View.GONE
//                showCustomToast("이미 존재하는 이메일입니다.")
//                return false
//            } else {
//                Snackbar.make(requireView(), "이미 가입하신 적이 있으시네요! \n$socialType (으)로 로그인 해주세요╰(*°▽°*)╯", Snackbar.LENGTH_LONG).show()
//                (requireActivity() as LoginActivity).onBackPressed()
//                return false
//            }
//
//        } else {
//            showCustomToast("서버 통신에 실패했습니다.")
//            Log.d(TAG, "existEmailChk: ${existEmailRes.message}")
//            binding.joinFragmentClCertNum.visibility = View.GONE
//            return false
//        }
//    }
//
//
//    // join 버튼 클릭 이벤트
//    private fun joinBtnClickEvent() {
//        binding.fragmentJoinBtnJoin.setOnClickListener {
//            val nickname = binding.fragmentJoinEtNick.text.toString()
//            val password = binding.fragmentJoinEtPw.text.toString()
////            val email = "${binding.fragmentJoinEtEmail.text}@${binding.fragmentJoinEtDomain.text}"
//            val email = validatedEmail()
//            val user = isAvailable(nickname, password, email, "none")
//            if(user != null) {
//                val joinRes = join(email!!, nickname, password, "none")
//
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
//            } else {
//                showCustomToast("입력 값을 다시 확인해 주세요.")
//            }
//
//        }
//    }
//
//    // 회원가입
//    private fun join(email: String, nickname: String, password: String, socialType: String) : Message {
//        var result = Message()
//        val encPw = loginActivity.sha256(password)
//
//        runBlocking {
//            result = mainViewModel.join(User(email, nickname, encPw, "", socialType))
//        }
//        return result
//    }
//
//    /**
//     * 필수 데이터 email 유효성 검사 및 중복 확인, pw, nickname 유효성 통과 여부 확인
//     * @return 가입 가능한 상태이면 user 객체를 반환
//     */
//    private fun isAvailable(nickname: String, password: String, email: String?, socialType: String) : User? {
//        if(validatedNickname(nickname) && validatedPw(password) && email != null && isEmailPossible) {
//            return User(email, nickname, password, "default.png", socialType)
//        } else {
//            return null
//        }
//    }
//
//
//    /**
//     * 각 EditText 쿼리 디바운스 적용
//     */
//    private fun inputObservable() {
//
//
//        binding.fragmentJoinEtPw.setQueryDebounce {
//            validatedPw(it)
//        }
//
//        binding.fragmentJoinEtNick.setQueryDebounce {
//            validatedNickname(it)
//        }
//
//        binding.fragmentJoinEtEmail.setQueryDebounce {
//            validatedEmail()
//        }
//
//        binding.fragmentJoinEtDomain.setQueryDebounce {
//            validatedEmail()
//        }
//    }
//
//    /**
//     * 입력된 nickname 길이 및 빈 칸 체크
//     * @return 통과 시 true 반환
//     */
//    private fun validatedNickname(nickname: String) : Boolean{
//        if(nickname.trim().isEmpty()){
//            binding.fragmentJoinTilNick.error = "Required Field"
//            binding.fragmentJoinEtNick.requestFocus()
//            return false
//        } else if(nickname.length >= 25) {
//            binding.fragmentJoinTilNick.error = "Nickname 길이를 25자 이하로 설정해 주세요."
//            binding.fragmentJoinEtNick.requestFocus()
//            return false
//        }
//        else {
//            binding.fragmentJoinTilNick.error = null
//            return true
//        }
//    }
//
//    /**
//     * 입력된 password 유효성 검사
//     * @return 유효성 검사 통과 시 true 반환
//     */
//    private fun validatedPw(pw: String) : Boolean {
//        if(pw.trim().isEmpty()) {   // 값이 비어있으면
//            binding.fragmentJoinTilPw.error = "Required Field"
//            binding.fragmentJoinEtPw.requestFocus()
//            return false
//        } else if(!Pattern.matches("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[\$@!%*#?&]).{8,25}.\$", pw)) {
//            binding.fragmentJoinTilPw.error = "비밀번호 형식을 확인해주세요.(영어, 숫자, 특수문자 포함 8 ~ 25)"
//            binding.fragmentJoinEtPw.requestFocus()
//            return false
//        }
//        else {
//            binding.fragmentJoinTilPw.isErrorEnabled = false
//            return true
//        }
//    }
//
//    /**
//     * email 입력 데이터 검사
//     * @return email 형식이면 email(String), 아니면 null
//     */
//    private fun validatedEmail() : String? {
//        val inputEmail = binding.fragmentJoinEtEmail.text.toString()
//        val inputDomain = binding.fragmentJoinEtDomain.text.toString()
//
//        val email = "$inputEmail@$inputDomain"
//
//        if(inputDomain.trim().isEmpty()) {
//            binding.fragmentJoinTilDomain.error = "Required Domain Field"
//            return null
//        }
//        if(inputEmail.trim().isEmpty()) {
//            binding.fragmentJoinTilEmail.error = "Required Email Field"
//            return null
//        }
//        else if(!Pattern.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z].{2,25}\$", email)) {
//            binding.fragmentJoinTilEmail.error = "이메일 형식을 확인해주세요."
//            return null
//        }
//        else {
//            binding.fragmentJoinTilEmail.isErrorEnabled = false
//            binding.fragmentJoinTilDomain.isErrorEnabled = false
//            return email
//        }
//    }
//
//    /**
//     * email domain list set Adapter
//     */
//    private fun initDomain() {
//        // 자동완성으로 보여줄 내용들
//        val domains = arrayOf("gmail.com", "naver.com", "nate.com", "daum.net", "kakao.com", "icloud.com", "outlook.com", "hotmail.com", "outlook.kr")
//
//        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, domains)
//        binding.fragmentJoinEtDomain.setAdapter(adapter)
//    }
//
//
//    /**
//     * EditText에 쿼리 디바운싱 함수
//     */
//    private fun EditText.setQueryDebounce(queryFunction: (String) -> Unit): Disposable {
//        val editTextChangeObservable = this.textChanges()
//        editTextSubscription =
//            editTextChangeObservable
//                // 마지막 글자 입력 0.5초 후에 onNext 이벤트로 데이터 발행
//                .debounce(500, TimeUnit.MILLISECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                // 구독을 통해 이벤트 응답 처리
//                .subscribeBy(
//                    onNext = {
//                        Log.d(TAG, "onNext : $it")
//                        queryFunction(it.toString())
//                    },
//                    onComplete = {
//                        Log.d(TAG, "onComplete")
//                    },
//                    onError = {
//                        Log.i(TAG, "onError : $it")
//                    }
//                )
//        return editTextSubscription  // Disposable 반환
//    }
//
//    private fun showConfirmDialog() {
//        AlertDialog.Builder(requireContext())
//            .setTitle("이메일 사용 확인")
//            .setMessage("현재 이메일을 사용하시겠습니까?")
//            .setPositiveButton("확인") { dialog, which ->
//                binding.fragmentJoinTilEmail.isEnabled = false
//                binding.fragmentJoinTilDomain.isEnabled = false
//                binding.fragmentJoinBtnDupChkEmail.visibility = View.GONE
//                binding.fragmentJoinBtnAuthOk.visibility = View.VISIBLE
//                binding.joinFragmentClCertNum.visibility = View.VISIBLE
//                certBtnClickEvent()
//                dialog.dismiss()
//            }
//            .setNegativeButton("취소") {
//                    dialog, which -> dialog.dismiss()
//            }
////            .setNeutralButton("neutral", object : DialogInterface.OnClickListener {
////                override fun onClick(dialog: DialogInterface, which: Int) {
////                    Log.d("MyTag", "neutral")
////                }
////            })
//            .create()
//            .show()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        if (!editTextSubscription.isDisposed()) {
//            editTextSubscription.dispose()
//        }
//    }
}