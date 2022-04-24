package com.whitebear.travel.src.login

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.WindowManager
import androidx.activity.viewModels
import com.kakao.sdk.common.util.Utility
import com.whitebear.travel.R
import com.whitebear.travel.config.ApplicationClass
import com.whitebear.travel.config.BaseActivity
import com.whitebear.travel.databinding.ActivityLoginBinding
import com.whitebear.travel.src.main.MainActivity
import com.whitebear.travel.src.network.viewmodel.MainViewModel
import kotlinx.coroutines.runBlocking
import java.security.DigestException
import java.security.MessageDigest

class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {
    val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        val userId = ApplicationClass.sharedPreferencesUtil.getAutoLogin()

        //로그인 상태 확인. id가 있다면 로그인 된 상태 -> 가장 첫 화면은 홈 화면의 Fragment로 지정
        if (userId != null || userId != -1){
            var isPossible = 0
            runBlocking {
                isPossible = mainViewModel.getUserInfo(userId, true)
            }
            if(isPossible == 1) {
                openFragment(1)
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.login_frame_layout, SignInFragment())
                    .commit()
            }

        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.login_frame_layout, SignInFragment())
                .commit()
        }


//        kakao 플랫폼 키 해시 등록
        val keyHash = Utility.getKeyHash(this)
        Log.d("kakaoKeyHash", "onCreate: $keyHash")
    }

    fun openFragment(int:Int){
        val transaction = supportFragmentManager.beginTransaction()
        when(int){
            1->{
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            2->{
                transaction.replace(R.id.login_frame_layout, SignInFragment())
                    .addToBackStack(null)
            }
            3->{
                transaction.replace(R.id.login_frame_layout, SignUpFragment())
                    .addToBackStack(null)
            }
            4-> {
                transaction.replace(R.id.login_frame_layout, ResetPasswordFragment())
                    .addToBackStack(null)
            }
        }
        transaction.commit()
    }


    fun sha256(pw: String) : String {
        val hash: ByteArray
        try {
            val md = MessageDigest.getInstance("SHA-256")
            md.update(pw.toByteArray())
            hash = md.digest()
        } catch (e: CloneNotSupportedException) {
            throw DigestException("couldn't make digest of partial content");
        }

        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }
}