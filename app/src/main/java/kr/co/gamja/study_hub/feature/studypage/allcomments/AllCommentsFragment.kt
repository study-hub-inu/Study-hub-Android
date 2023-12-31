package kr.co.gamja.study_hub.feature.studypage.allcomments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kr.co.gamja.study_hub.R
import kr.co.gamja.study_hub.data.repository.AuthRetrofitManager
import kr.co.gamja.study_hub.data.repository.CallBackListener
import kr.co.gamja.study_hub.data.repository.OnCommentClickListener
import kr.co.gamja.study_hub.data.repository.StudyHubApi
import kr.co.gamja.study_hub.databinding.FragmentAllCommentsBinding
import kr.co.gamja.study_hub.feature.studypage.allcomments.bottomsheet.CommentBottomSheetFragment
import kr.co.gamja.study_hub.global.CustomSnackBar

class AllCommentsFragment : Fragment() {
    private lateinit var binding: FragmentAllCommentsBinding
    private lateinit var viewModel: AllCommentsViewModel
    private lateinit var adapter: AllCommentsAdapter
    private var nowCommentId: Int = -1 // 현재 보고 있는 댓글 id
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_all_comments, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory = AllCommentViewModelFactory(AuthRetrofitManager.api)
        viewModel = ViewModelProvider(requireActivity(), factory)[AllCommentsViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        getValue() // postId viewModel에 저장

        // 툴바 설정
        val toolbar = binding.commentToolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = ""

        binding.iconBack.setOnClickListener {
            findNavController().navigateUp() // 뒤로 가기
        }

        adapter = AllCommentsAdapter(requireContext())
        binding.recycleComments.adapter = adapter
        binding.recycleComments.layoutManager = LinearLayoutManager(requireContext())

        observeData() // 페이징 데이터 업뎃

        // 댓글 관찰 -> 버튼 활성화 여부 결정
        viewModel.comment.observe(viewLifecycleOwner) { comment ->
            val isButtonEnabled = comment.isNotEmpty()
            binding.btnCommentOk.isEnabled = isButtonEnabled
        }

        // 댓글 등록하기 버튼 누를 시
        binding.btnCommentOk.setOnClickListener {
            viewModel.setUserComment(object : CallBackListener {
                override fun isSuccess(result: Boolean) {
                    if (result) {
                        CustomSnackBar.make(
                            binding.layoutRelative,
                            getString(R.string.setComment), binding.viewDivideRecycler, false
                        ).show()
                        // 키보드 내리기
                        hideKeyboardForResend()
                        // editText에 입력된 글 지우기
                        viewModel.comment.value = ""
                        // paging refresh
                        adapter.refresh()
                        // 댓 개수 업뎃
                        viewModel.getCommentsList(viewModel.postId.value!!)
                    }
                }
            })
        }
        adapter.setOnItemClickListener(object : OnCommentClickListener {
            override fun getCommentValue(whatItem: Int, itemValue: Int, comment: String) {
                when (whatItem) {
                    1 -> {
                        nowCommentId = itemValue // 댓 id 저장
                        getModal(comment) // 수정 삭제 모달로 이동
                    }
                }
            }
        })
        // bottomsheet에서 삭제 눌러서 왔을 경우
        viewModel.isDelete.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.deleteComment(postId = nowCommentId, object : CallBackListener {
                    override fun isSuccess(result: Boolean) {
                        if (result) {
                            adapter.refresh() // 리사이클러뷰 업뎃
                            viewModel.setDelete(false) // 삭제 후 false로 원상복귀
                            viewModel.getCommentsList(viewModel.postId.value!!) // 댓 개수 없뎃
                        }
                    }
                })
            }
        }
        // bottomsheet에서 수정 눌러서 왔을 경우
        viewModel.isModify.observe(viewLifecycleOwner) {
            if (it) {
                // todo("수정 시 연결 ")
            }
        }

    }

    private fun getValue() {
        val receiveBundle = arguments
        if (receiveBundle != null) {
            val value = receiveBundle.getInt("postId")
            viewModel.setPostId(value) // postId 설정
            viewModel.getCommentsList(value) // 댓 개수 가져오기
            Log.d(tag, " value: $value")
        } else Log.e(tag, "receiveBundle is Null")
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allContentsFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    private fun hideKeyboardForResend() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusView = requireActivity().currentFocus
        if (currentFocusView != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocusView.windowToken, 0)
        }
    }

    // 삭제 수정 모달싯트로
    private fun getModal(comment: String) {
        val modal = CommentBottomSheetFragment()
        val bundle = Bundle()
        bundle.putString("comment", comment)
        modal.arguments = bundle
        modal.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerBottomSheetDialogTheme)
        modal.show(parentFragmentManager, modal.tag)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 댓글 다는 editText값 초기화
        viewModel.initComment()
    }
}

class AllCommentViewModelFactory(private val studyHubApi: StudyHubApi) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AllCommentsViewModel::class.java)) {
            return AllCommentsViewModel(studyHubApi) as T
        }
        throw IllegalArgumentException("AllCommentViewModel 모름")
    }
}