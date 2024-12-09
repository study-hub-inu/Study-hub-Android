package kr.co.gamja.study_hub.feature.signup.password

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import kr.co.gamja.study_hub.R
import kr.co.gamja.study_hub.databinding.FragmentPasswordBinding
import kr.co.gamja.study_hub.feature.signup.major.User
import kr.co.gamja.study_hub.global.CustomDialog
import kr.co.gamja.study_hub.global.ExtensionFragment.Companion.hideKeyboard
import kr.co.gamja.study_hub.global.OnDialogClickListener
import kotlin.properties.Delegates


class PasswordFragment : Fragment() {
    private lateinit var binding: FragmentPasswordBinding
    private val viewModel: PasswordViewModel by viewModels()
    private var grayColor by Delegates.notNull<Int>() // Gray_80 : 이메일 정상
    private lateinit var grayStateList: ColorStateList
    private var redColor by Delegates.notNull<Int>() // R_50 : 오류
    private lateinit var redStateList: ColorStateList
    private var greenColor by Delegates.notNull<Int>() // Green_10 : 인증코드 정상
    private lateinit var greenStateList: ColorStateList


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_password, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        grayColor = ContextCompat.getColor(requireContext(), R.color.G_80)
        redColor = ContextCompat.getColor(requireContext(), R.color.R_50)
        greenColor = ContextCompat.getColor(requireContext(), R.color.GN_10)
        grayStateList = ColorStateList.valueOf(grayColor)
        redStateList = ColorStateList.valueOf(redColor)
        greenStateList = ColorStateList.valueOf(greenColor)

        setupPasswordText()
        setupRePasswordText()

        // 에딧텍스트 자판 내리기
        binding.root.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    true
                }
                MotionEvent.ACTION_UP -> {
                    this.hideKeyboard()
                    v.performClick()
                    true
                }
                else -> false
            }
        }
        // 툴바 설정
        val toolbar = binding.createPassToolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = ""

        binding.iconBack.setOnClickListener {
          isPressedBackBtn()
        }
        val pressedCallBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                isPressedBackBtn()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, pressedCallBack)

        binding.txtPageNumber.text = getString(R.string.txt_pagenumber, 3)

        binding.viewPassword.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // 비밀번호가 안보이고 있는데 보이게
                if (binding.viewPassword.getTag().toString() == "0") {
                    binding.viewPassword.setTag("1")
                    binding.editPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    binding.viewPassword.setImageResource(R.drawable.img_toggle_eye)
                } else { // 비밀번호가 보일때 안보이게
                    binding.viewPassword.setTag("0")
                    binding.editPassword.inputType =
                        InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
                    binding.viewPassword.setImageResource(R.drawable.img_toggle_closedeye)
                }
                binding.editPassword.setSelection(binding.editPassword.text.toString().length)
            }
        })
        binding.viewRePassword.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // 비밀번호가 안보이고 있는데 보이게
                if (binding.viewRePassword.getTag().toString() == "0") {
                    binding.viewRePassword.setTag("1")
                    binding.editRePassword.inputType =
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    binding.viewRePassword.setImageResource(R.drawable.img_toggle_eye)
                } else { // 비밀번호가 보일때 안보이게
                    binding.viewRePassword.setTag("0")
                    binding.editRePassword.inputType =
                        InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
                    binding.viewRePassword.setImageResource(R.drawable.img_toggle_closedeye)
                }
                binding.editRePassword.setSelection(binding.editRePassword.text.toString().length)
            }
        })

        binding.btnNext.setOnClickListener {
            User.password = viewModel.password.value.toString()
            findNavController().navigate(
                R.id.action_createAccountFragment02_to_createAccountFragment03,
                null
            )
        }

    }


    private fun setupPasswordText() {
        binding.editPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0 != null) {
                    val filterText = p0.toString().replace("\\s{1,}".toRegex(), "")
                    val watcher = this
                    if (filterText != p0.toString()) {
                        // 공백 입력 시: 공백 제거 후 텍스트 설정
                        binding.editPassword.apply {
                            removeTextChangedListener(watcher) // 무한 루프 방지
                            setText(filterText) // 공백 제거된 텍스트 설정
                            setSelection(filterText.length) // 커서 끝으로 이동
                            addTextChangedListener(watcher) // TextWatcher 다시 추가
                        }
                        viewModel.setPassword(filterText) // ViewModel 동기화
                    } else if (filterText != viewModel.password.value) {
                        // 공백 입력이 없을 시: ViewModel만 업데이트
                        viewModel.setPassword(filterText)
                    }


                }
            }
        })

//        binding.editPassword.doOnTextChanged { text, _, _, _ ->
//            val filterText = text.toString().replace("\\s{1,}".toRegex(), "")
//            if (filterText != viewModel.password.value) {
//                Log.e("패스워드 에러", filterText)
//                viewModel.setPassword(filterText)
//                binding.editPassword.setSelection(filterText.length)
//            }
//        }


//        viewModel.password.observe(viewLifecycleOwner) {
//            if (it != binding.editPassword.text.toString())
//                binding.editPassword.setText(it)
//        }


        viewModel.validPassword.observe(viewLifecycleOwner) {
            if (it) {

                binding.editPassword.backgroundTintList = greenStateList
                binding.errorPassword.apply {
                    text = getString(R.string.txterror_passwordOk)
                    setTextColor(greenColor)
                    isVisible = true
                }
            } else {

                binding.editPassword.backgroundTintList = redStateList
                binding.errorPassword.apply {
                    text = getString(R.string.txterror_password)
                    setTextColor(redColor)
                    isVisible = true
                }
            }
        }
    }

    private fun setupRePasswordText() {
        binding.editRePassword.doOnTextChanged { text, _, _, _ ->
            if (text.toString() != viewModel.rePassword.value) {
                viewModel.setRePassword(text.toString())
            }
            if (binding.editPassword.backgroundTintList == greenStateList) {

                binding.editPassword.backgroundTintList = grayStateList
                binding.errorPassword.isVisible = false
            }
        }
        viewModel.rePassword.observe(viewLifecycleOwner) {
            if (it != binding.editRePassword.text.toString())
                binding.editRePassword.setText(it)
        }
        viewModel.validRePassword.observe(viewLifecycleOwner) {
            if (it) {
                binding.editRePassword.backgroundTintList = greenStateList
                binding.errorRePassword.apply {
                    text = getString(R.string.error_samePassword)
                    setTextColor(greenColor)
                    isVisible = true
                }
                viewModel.setEnableNextBtn(true)
            } else {
                binding.editRePassword.backgroundTintList = redStateList
                binding.errorRePassword.apply {
                    text = getString(R.string.error_notSamePassword)
                    setTextColor(redColor)
                    isVisible = true
                }
                viewModel.setEnableNextBtn(false)
            }
        }
    }

    fun isPressedBackBtn() {
            val head: String =requireContext().resources.getString(R.string.q_alterSignUp)
        val sub: String =requireContext().resources.getString(R.string.q_alterSignUpSub)
            val no = requireContext().resources.getString(R.string.btn_no)
            val yes = requireContext().resources.getString(R.string.btn_yes)
            val dialog = CustomDialog(requireContext() ,head,sub, no, yes)
            dialog.showDialog()
            dialog.setOnClickListener(object : OnDialogClickListener {
                override fun onclickResult() {
                    viewModel.setPassword("") // 초기화
                    viewModel.setRePassword("")
                    findNavController().navigate(R.id.action_global_loginFragment)
                }
            })

    }

}