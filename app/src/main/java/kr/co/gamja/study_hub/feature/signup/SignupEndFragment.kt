package kr.co.gamja.study_hub.feature.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import kr.co.gamja.study_hub.R
import kr.co.gamja.study_hub.databinding.FragmentSignupEndBinding


class SignupEndFragment : Fragment() {
    private lateinit var binding : FragmentSignupEndBinding
    lateinit var navOptions: NavOptions
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_signup_end,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnStart.setOnClickListener{
            findNavController().navigate(R.id.action_create05EndFragment_to_login)
        }

        // 안드로이드 폰 자체 뒤로 가기 누를 시
        val pressedCallBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                navOptions = NavOptions.Builder() // 해당 framgent까지 백스택에서 제거
                    .setPopUpTo(R.id.signupEndFragment, true)
                    .build()

                findNavController().navigate(R.id.action_create05EndFragment_to_login, null, navOptions)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, pressedCallBack)

    }


}