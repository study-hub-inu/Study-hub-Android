package kr.co.gamja.study_hub.feature.signup.password

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.gamja.study_hub.feature.login.PASSWORD

class PasswordViewModel : ViewModel() {
    private val tag = this.javaClass.simpleName

    // 패스워드
    private val _password = MutableLiveData<String>()
    val password: LiveData<String> get() = _password

    // 패스워드 형식 확인
    private val _validPassword = MutableLiveData<Boolean>()
    val validPassword: LiveData<Boolean> get() = _validPassword

    fun setPassword(newPassword: String) {
        _password.value = newPassword
        _validPassword.value = newPassword.matches(PASSWORD.toRegex())

        Log.e("비번 viewModel", _validPassword.value.toString())
        _validRePassword.value = password.value.toString() == rePassword.value.toString() && !rePassword.value.toString().isNullOrBlank() && validPassword.value ==true

    }

    // re패스워드
    private val _rePassword = MutableLiveData<String>()
    val rePassword: LiveData<String> get() = _rePassword

    // re패스워드 형식 확인
    private val _validRePassword = MutableLiveData<Boolean>()
    val validRePassword: LiveData<Boolean> get() = _validRePassword

    fun setRePassword(newRePassword: String) {
        _rePassword.value = newRePassword
        _validRePassword.value = (password.value.toString() == rePassword.value.toString())
    }


    // 다음 버튼 활성화
    private val _nextBtn = MutableLiveData<Boolean>(false)
    val nextBtn: LiveData<Boolean> get()=_nextBtn

    fun setEnableNextBtn( newValue : Boolean){
        _nextBtn.value= newValue
    }


}