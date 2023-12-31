package kr.co.gamja.study_hub.feature.mypage.withdrawal

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kr.co.gamja.study_hub.data.repository.AuthRetrofitManager
import kr.co.gamja.study_hub.data.repository.CallBackListener
import kr.co.gamja.study_hub.feature.login.PASSWORD

class ConfirmingWithdrawalViewModel : ViewModel() {
    val tag = this.javaClass.simpleName

    // 통신- 비번 값
    val currentPassword = MutableLiveData<String>()

    // 버튼 클릭 enable
    private val _enableBtn = MutableLiveData<Boolean>()
    val enableBtn: LiveData<Boolean> get() = _enableBtn

    // 비번 입력이 되면 버튼 enable
    fun updateEnableBtn() {
        _enableBtn.value = currentPassword.value.toString().matches(PASSWORD.toRegex())
    }

    // 회원탈퇴 api
    fun clickWithdrawal(params: CallBackListener) {
        viewModelScope.launch {
            try {
                val response = AuthRetrofitManager.api.deleteUser()
                if (response.isSuccessful) {
                    params.isSuccess(true)
                } else params.isSuccess(false)
            } catch (e: Exception) {
                Log.e(tag, "회원탈퇴 Excep: ${e.message}")
            }
        }
    }
}