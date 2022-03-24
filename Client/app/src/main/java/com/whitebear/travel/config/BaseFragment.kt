package com.whitebear.travel.config

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.whitebear.travel.util.LoadingDialog
import io.reactivex.disposables.CompositeDisposable
//
//abstract class BaseFragment <B : ViewBinding>(
//    private val bind: (View) -> B,
//    @LayoutRes layoutResId: Int
//) : Fragment(layoutResId) {
//    private var _binding: B? = null
//    lateinit var mLoadingDialog: LoadingDialog
//    //    val viewModel: MainViewModel by activityViewModels()
////    lateinit var con:ViewGroup
//    protected val binding get() = _binding!!
//    private val compositeDisposable = CompositeDisposable()
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = bind(super.onCreateView(inflater, container, savedInstanceState)!!)
//        return binding.root
//    }
//
//    override fun onDestroyView() {
//        _binding = null
//        super.onDestroyView()
//    }
//
//    fun showCustomToast(message: String) {
//        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
//    }
//
//    fun showLoadingDialog(context: Context) {
//        mLoadingDialog = LoadingDialog(context)
//        mLoadingDialog.show()
//    }
//
//    fun dismissLoadingDialog() {
//        if (mLoadingDialog.isShowing) {
//            mLoadingDialog.dismiss()
//        }
//    }
//
//}