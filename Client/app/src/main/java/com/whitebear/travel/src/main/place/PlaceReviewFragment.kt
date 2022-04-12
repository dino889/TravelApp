package com.whitebear.travel.src.main.place

import android.app.Application
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.whitebear.travel.R
import com.whitebear.travel.config.ApplicationClass
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentPlaceReviewBinding
import com.whitebear.travel.src.dto.Message
import com.whitebear.travel.src.dto.Place
import com.whitebear.travel.src.dto.PlaceReview
import com.whitebear.travel.src.main.MainActivity
import com.whitebear.travel.src.network.service.PlaceService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response

private const val TAG = "PlaceReviewFragment"
class PlaceReviewFragment : BaseFragment<FragmentPlaceReviewBinding>(FragmentPlaceReviewBinding::bind,R.layout.fragment_place_review) {
    var placeId = 0
    private lateinit var mainActivity:MainActivity
    private lateinit var reviewAdapter: PlaceReviewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            placeId = it.getInt("placeId")
            Log.d(TAG, "onCreateInfo: $placeId")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = mainViewModel
        Log.d(TAG, "onViewCreated: $placeId")
        runBlocking {
            mainViewModel.getPlaceReview(placeId)
        }
        setListener()
    }
    fun setListener(){
        setButtons()
        initAdapter()
        initData()
    }
    fun initAdapter(){
        mainViewModel.placeReviews.observe(viewLifecycleOwner, {
            reviewAdapter = PlaceReviewAdapter(requireContext())
            reviewAdapter.list = it
            binding.fragmentPlaceReviewRv.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
                adapter = reviewAdapter
                adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }
            reviewAdapter.setItemClickListener(object: PlaceReviewAdapter.ItemClickListener{
                override fun onClick(view: View, position: Int, id: Int) {
                    showReviewWriteDialog(2)
                }

            })

        })
    }
    fun initData(){

    }
    fun setButtons(){
        binding.fragmentPlaceReviewWrite.setOnClickListener {
            showReviewWriteDialog(1)
        }
    }
    fun showReviewWriteDialog(flag:Int){
        var dialog = Dialog(requireContext())
        var dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_review_write,null)
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        if(flag== 1){
            dialogView.findViewById<AppCompatButton>(R.id.fragment_placeReview_writeSuccess).setOnClickListener{
                var content = dialogView.findViewById<EditText>(R.id.fragment_placeReview_writeContent).text
                var rating = dialogView.findViewById<RatingBar>(R.id.fragment_placeReview_writeRating).rating
                Log.d(TAG, "showReviewWriteDialog: $rating $content")
                var review = PlaceReview(
                    content = content.toString(),
                    "",
                    0,
                    placeId,
                    rating.toInt(),
                    "",
                    ApplicationClass.sharedPreferencesUtil.getUser().id,
                    null
                )
                Log.d(TAG, "showReviewWriteDialog: $review")
                insertReview(review)
                dialog.dismiss()
            }
        }

    }
    fun insertReview(review:PlaceReview){
        Log.d(TAG, "insertReview: $review")
        var response : Response<Message>
        runBlocking {
            response = PlaceService().insertPlaceReview(review)
        }

        val res = response.body()
        Log.d(TAG, "insertReview: ${response.code()}")
        if(response.code() == 200 || response.code() == 201){
            if(res!=null){
                if(res.isSuccess){
                    showCustomToast("성공하였습니다.")
                    runBlocking {
                        mainViewModel.getPlaceReview(placeId)
                    }
                }
            }
        }

    }
    companion object {

        @JvmStatic
        fun newInstance(key:String,value:Int) =
            PlaceReviewFragment().apply {
                arguments = Bundle().apply {
                    this.putInt(key,value)
                }
            }
    }
}