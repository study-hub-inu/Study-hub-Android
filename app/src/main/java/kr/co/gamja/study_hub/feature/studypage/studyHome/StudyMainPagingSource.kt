package kr.co.gamja.study_hub.feature.studypage.studyHome

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kr.co.gamja.study_hub.data.model.ContentX
import kr.co.gamja.study_hub.data.repository.StudyHubApi
import retrofit2.HttpException

class StudyMainPagingSource(private val studyHubApi :StudyHubApi, private val isHot:Boolean) : PagingSource<Int, ContentX>(){
    override fun getRefreshKey(state: PagingState<Int, ContentX>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ContentX> {
        return try {
            val currentPageNumber = params.key ?: 0
            val response =
                studyHubApi.getStudyPostAll(isHot, currentPageNumber, params.loadSize, null,false)
            val responseData = response.body()?.content ?: emptyList()
            val nextPageNumber =
                if (response.body()?.last == true) {
                    null
                } else {
                    currentPageNumber + 1
                }
            LoadResult.Page(
                data = responseData,
                prevKey = if (currentPageNumber > 0) currentPageNumber - 1 else null,
                nextKey = nextPageNumber
            )
        } catch (e: Exception) {
            LoadResult.Error(e) // 오류 paging에 전달
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }

    }
}