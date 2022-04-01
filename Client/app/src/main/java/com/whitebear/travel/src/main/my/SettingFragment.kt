package com.whitebear.travel.src.main.my

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.whitebear.travel.R
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentSettingBinding
import com.whitebear.travel.src.main.MainActivity

/**
 * @author Jiwoo Choi
 * @since 04/02/22
 */
class SettingFragment : BaseFragment<FragmentSettingBinding>(FragmentSettingBinding::bind, R.layout.fragment_setting) {
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
        faqBtnClickEvent()
        backBtnClickEvent()
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

    override fun onDestroyView() {
        super.onDestroyView()
        mainActivity.hideBottomNav(false)
    }


}