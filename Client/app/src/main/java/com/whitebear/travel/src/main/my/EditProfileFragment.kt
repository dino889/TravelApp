package com.whitebear.travel.src.main.my

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import com.whitebear.travel.R
import com.whitebear.travel.config.ApplicationClass
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentEditProfileBinding
import com.whitebear.travel.src.dto.User
import com.whitebear.travel.src.main.MainActivity
import com.whitebear.travel.src.network.service.UserService
import kotlinx.coroutines.runBlocking
import retrofit2.Response


class EditProfileFragment : BaseFragment<FragmentEditProfileBinding>(FragmentEditProfileBinding::bind, R.layout.fragment_edit_profile) {
    private val TAG = "EditProfileFragment"
    private lateinit var mainActivity : MainActivity

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
        val loginUser = mainViewModel.loginUserInfo.value
        binding.user = loginUser

        initListener()
    }

    private fun initListener() {
        backBtnClickEvent()
        updateConfirmBtnClickEvent()
    }

    private fun backBtnClickEvent() {
        binding.editProfileFragmentIvBack.setOnClickListener {
            this.findNavController().popBackStack()
        }
    }


    /**
     * 회원정보 수정 완료 버튼 클릭 이벤트
     */
    private fun updateConfirmBtnClickEvent() {
        binding.editProfileFragmentBtnConfirm.setOnClickListener {
            val userId = ApplicationClass.sharedPreferencesUtil.getUser().id

            val updateUser = User(id = userId, nickname = binding.editProfileFragmentEtNick.text.toString(), username = binding.editProfileFragmentEtUserName.text.toString())

            updateUser(updateUser)
        }
    }

    /**
     * 사용자 정보 업데이트 서버 통신
     */
    private fun updateUser(user: User) {
        var response : Response<HashMap<String, Any>>
        runBlocking {
            response = UserService().updateUser(user.id, user)
        }

        if(response.code() == 200 || response.code() == 201 || response.code() == 500) {
            val body = response.body()
            if(body != null) {
                if(body["isSuccess"] == true) {
                    showCustomToast("회원 정보가 정상적으로 변경되었습니다.")
                    runBlocking {
                        mainViewModel.getUserInfo(user.id, true)
                    }
                    (requireActivity() as MainActivity).onBackPressed()

                } else if(body["isSuccess"] == false) {
                    showCustomToast("회원 정보 수정 실패")
                }
            } else {
                showCustomToast("서버 통신 실패")
                Log.d(TAG, "updateUser: ${response}")
                Log.d(TAG, "updateUser: ${response.message()}")
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainActivity.hideBottomNav(false)
    }
}