package kr.co.gamja.study_hub.feature.mypage.participant

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kr.co.gamja.study_hub.R
import kr.co.gamja.study_hub.data.repository.CallBackListener
import kr.co.gamja.study_hub.databinding.FragmentBottomSheetListDialogBinding
import kr.co.gamja.study_hub.feature.mypage.participant.participation.ParticipationViewModel
import kr.co.gamja.study_hub.feature.mypage.participant.waiting.WaitingViewModel

class BottomSheet() : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetListDialogBinding
    private val viewModel: WaitingViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //dialog height 수정
        val bottomSheetDialog = BottomSheetDialog(requireContext(), theme)
        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet =
                (dialog as BottomSheetDialog).findViewById<FrameLayout>(R.id.design_bottom_sheet)
                    ?: throw NullPointerException("Bottom Dialog is NULL")
            val behavior = BottomSheetBehavior.from(bottomSheet)

            // 화면 높이의 40%로 설정
            val layoutParams = bottomSheet.layoutParams
            layoutParams?.height = (resources.displayMetrics.heightPixels * 0.4).toInt()
            bottomSheet.layoutParams = layoutParams

            // 바텀 시트 상태 및 동작 설정
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
            behavior.isHideable = true
        }
        return bottomSheetDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetListDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //선택 사유
        var selectedReason = ""

        binding.apply {

            //초기 거절 비활성화
            btnRefusal.isEnabled = false

            //거절 선택 가능
            isChecked = isChecked

            //radio group 내 button의 check 상태 변경 listener
            rgSelect.setOnCheckedChangeListener { group, checkedId ->
                selectedReason = when (checkedId) {
                    R.id.chb1 -> chb1.text.toString()
                    R.id.chb2 -> chb2.text.toString()
                    R.id.chb3 -> chb3.text.toString()
                    else -> chb4.text.toString()
                }
                isChecked = true
                btnRefusal.isEnabled = true
            }

            val userId = arguments?.getInt("userId") ?: -1
            val studyId = arguments?.getInt("studyId") ?: -1
            val page = arguments?.getInt("page") ?: -1

            //거절 버튼
            btnRefusal.setOnClickListener {
                //tvR4 텍스트 메세지라면? 거절 사유 작성 화면으로 navigation
                if (selectedReason == chb4.text.toString()) {
                    val bundle = Bundle()
                    bundle.putInt("userId", userId)
                    bundle.putInt("studyId", studyId)
                    arguments = bundle
                    //RefusalFragment로 이동
                    findNavController().navigate(
                        R.id.action_participantFragment_to_refusalReasonFragment,
                        arguments
                    )
                    viewModel.fetchData(studyId)
                    dismiss()
                } else {
                    if (userId != -1 && studyId != -1 && page != -1) {
                        viewModel.reject(
                            rejectReason = selectedReason,
                            studyId = studyId,
                            userId = userId,
                            object : CallBackListener {
                                override fun isSuccess(result: Boolean) {
                                    if (result) {
                                        //dialog 띄우기
                                        val customToast = CustomToast()
                                        val bundle = Bundle()
                                        bundle.putInt("studyId", studyId)
                                        bundle.putInt("page", page)
                                        arguments = bundle
                                        //toast 띄우기
                                        customToast.show(
                                            requireActivity().supportFragmentManager,
                                            "Toast"
                                        )
                                        viewModel.fetchData(studyId) // todo
                                        dismiss()
                                    }
                                }
                            }
                        )
                    }
                }
            }

            //bottom sheet dialog 닫기
            btnClose.setOnClickListener {
                dismiss()
            }
        }
    }
}