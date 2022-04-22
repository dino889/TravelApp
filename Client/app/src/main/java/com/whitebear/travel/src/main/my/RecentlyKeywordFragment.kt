package com.whitebear.travel.src.main.my

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.whitebear.travel.R
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentRecentlyKeywordBinding
import com.whitebear.travel.src.dto.Keyword
import com.whitebear.travel.src.main.MainActivity

class RecentlyKeywordFragment : BaseFragment<FragmentRecentlyKeywordBinding>(FragmentRecentlyKeywordBinding::bind, R.layout.fragment_recently_keyword) {
    private lateinit var mainActivity : MainActivity
    private lateinit var recentlyKeywordAdapter: RecentlyKeywordAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerviewAdapter()
    }

    private fun initRecyclerviewAdapter() {
        recentlyKeywordAdapter = RecentlyKeywordAdapter()
        mainViewModel.liveKeywords.observe(viewLifecycleOwner) {
            recentlyKeywordAdapter.list = it.toList() as MutableList<Keyword>
        }

        binding.myPostFragmentRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recentlyKeywordAdapter
            adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

    }
}