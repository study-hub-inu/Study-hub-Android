package kr.co.gamja.study_hub.feature.mypage.engagedStudy

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kr.co.gamja.study_hub.data.model.ContentX
import kr.co.gamja.study_hub.data.model.GetBookmarkResponse
import kr.co.gamja.study_hub.data.model.ParticipatedStudyRequestDto
import kr.co.gamja.study_hub.data.repository.AuthRetrofitManager
import kr.co.gamja.study_hub.data.repository.StudyHubApi
import retrofit2.Response

class EngagedStudyViewModel(studyHubApi: StudyHubApi) : ViewModel() {
    private val tag = this.javaClass.simpleName

    // 리스트 개수
    private val _listSize = MutableLiveData<Int>()
    val listSize: LiveData<Int> get() = _listSize

    // paging 초기화
    private val _reloadTrigger = MutableStateFlow(Unit)

    fun setReloadTrigger() {
        _reloadTrigger.value = Unit
    }

    // 리스트 있는지 여부
    var isList = MutableLiveData<Boolean>(true)

    val engagedStudyFlow: Flow<PagingData<ContentX>> = _reloadTrigger.flatMapLatest {
        Pager(
            PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                initialLoadSize = 10
            )
        ) {
            EngagedStudyPagingSource(studyHubApi)
        }.flow.cachedIn(viewModelScope)
    }



    // 참여한 스터디 개수 세기
    fun updateEngagedListSize() {
        viewModelScope.launch {
            val response = AuthRetrofitManager.api.participatingMyStudy(0, 10)
            try {
                if (response.isSuccessful) {
                    val result = response.body()
                    _listSize.value = result?.participateStudyData?.numberOfElements
                    Log.d(" 사용자가 참여한 스터디 개수", _listSize.value.toString() +" result" +result?.totalCount.toString())
                }
            } catch (e: Exception) {
                Log.e(tag, "사용자가 참여한 스터디api에서 개수 조회 실패 code" + response.code().toString())
            }
        }
    }
}