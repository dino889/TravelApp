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

    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainActivity.hideBottomNav(false)
    }
}