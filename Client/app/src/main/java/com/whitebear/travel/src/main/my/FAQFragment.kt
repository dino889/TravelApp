package com.whitebear.travel.src.main.my

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.whitebear.travel.R
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentFAQBinding
import com.whitebear.travel.src.dto.FAQ
import com.whitebear.travel.src.main.MainActivity

class FAQFragment : BaseFragment<FragmentFAQBinding>(FragmentFAQBinding::bind, R.layout.fragment_f_a_q) {
    private lateinit var mainActivity : MainActivity

    private lateinit var faqList: MutableList<FAQ>
    private lateinit var faqCategoryRecyclerviewAdapter: FAQCategoryRecyclerviewAdapter
    private lateinit var faqRecyclerviewAdapter: FAQRecyclerviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.hideBottomNav(true)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        mainActivity.hideBottomNav(true)

        setList()
        initFaqCategoryAdapter()
        initFaqAdapter()
        backBtnClickEvent()
    }

    private fun setList() {
        faqList = mutableListOf()

        faqList.add(FAQ("회원정보", "아이디를 찾고 싶어요.", "아이디가 기억나지 않을 경우, \n고객센터로 문의해 주세요!"))
        faqList.add(FAQ("회원정보", "비밀번호를 잊었어요/변경하고 싶어요.", "비밀번호를 분실하셨거나 변경이 필요한 경우, \n[로그인 페이지] - [forgot Password] 버튼을 통해 비밀번호를 재설정 하실 수 있습니다."))
        faqList.add(FAQ("회원정보", "계정 탈퇴를 하고 싶어요.", "Travel 회원 탈퇴를 원하시는군요😥\n회원 탈퇴는, [마이페이지] - [설정] - 화면 하단의 [회원 탈퇴]를 통해 진행하실 수 있습니다!\n\n탈퇴 시엔 저장하셨던 즐겨찾기 및 찜한 장소, 경로 등 등록 기록은 모두 삭제되어 복구가 어려우니, 이 점 이용에 참고 부탁드립니다."))
        faqList.add(FAQ("회원정보", "소셜 로그인 연동을 해지하고 싶어요.", "소셜 로그인 해지의 경우 [마이페이지] - [앱 문의]를 통해 Travel 고객센터로 문의 부탁드립니다."))
        faqList.add(FAQ("앱 기능", "앱에 기능 추가를 요청하고 싶어요.", "Travel 앱에 대한 건의사항이 있을 경우, [마이페이지] - [앱 문의]로 자세한 내용을 보내주세요.\n건의사항을 통해 더욱 나은 Travel 서비스로 개선할 수 있으니 언제든지 자유롭게 의견을 말씀해주세요!"))
        faqList.add(FAQ("앱 기능", "좋지 않은 댓글을 받았습니다. 어떻게 해야하나요?", ""))
        faqList.add(FAQ("앱 기능", "제휴 및 광고 문의는 어떻게 하나요?", ""))
        faqList.add(FAQ("앱 오류", "찜한 장소가 갑자기 없어졌어요.", "찜한 장소들이 갑자기 사라진 경우,\n[마이페이지] - [앱 문의]를 통해 Travel 고객센터로 아래의 정보를 전달해주세요!\n\n1) Travel Email(마이페이지에서 확인 가능)\n2) 자세한 현상 설명"))
        faqList.add(FAQ("앱 오류", "모바일 앱이 정상 동작하지 않아요.", "아이디가 기억나지 않을 경우, \n고객센터로 문의해 주세요!"))
        faqList.add(FAQ("환경설정", "푸시 알림 설정은 어떻게 하나요?", "  "))
        faqList.add(FAQ("환경설정", "상담가능시간과 유선번호는 어떻게 되나요?", ""))
    }

    private fun initFaqCategoryAdapter() {
        faqCategoryRecyclerviewAdapter = FAQCategoryRecyclerviewAdapter()
        faqCategoryRecyclerviewAdapter.categoryList = listOf<String>("전체", "회원정보", "앱 기능", "앱 오류", "환경설정")

        binding.faqFragmentRvFaqCategory.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = faqCategoryRecyclerviewAdapter
            adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        faqCategoryRecyclerviewAdapter.setItemClickListener(object : FAQCategoryRecyclerviewAdapter.ItemClickListener {

            override fun onClick(view: View, position: Int) {
                // 카테고리별로 faq list 필터링
                showCustomToast(position.toString())
            }
        })

    }

    private fun initFaqAdapter() {

        faqRecyclerviewAdapter = FAQRecyclerviewAdapter()
        faqRecyclerviewAdapter.faqList = faqList

        binding.faqFragmentRvFaqList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = faqRecyclerviewAdapter
            adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        faqRecyclerviewAdapter.setItemClickListener(object : FAQRecyclerviewAdapter.ItemClickListener {

            override fun onClick(view: View, contentView: TextView, position: Int) {
                val arrow = view as ImageButton
                if(contentView.visibility == View.GONE) { // content가 숨겨져 있는 경우
                    arrow.setImageResource(R.drawable.ic_up_arrow)
                    contentView.visibility = View.VISIBLE
                } else if(contentView.visibility == View.VISIBLE) {
                    arrow.setImageResource(R.drawable.ic_arrow_down)
                    contentView.visibility = View.GONE
                }

            }
        })

    }

    private fun backBtnClickEvent() {
        binding.faqFragmentIvBack.setOnClickListener {
            this.findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainActivity.hideBottomNav(false)
    }

    override fun onPause() {
        super.onPause()
        mainActivity.hideBottomNav(true)
    }

    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNav(true)
    }
}