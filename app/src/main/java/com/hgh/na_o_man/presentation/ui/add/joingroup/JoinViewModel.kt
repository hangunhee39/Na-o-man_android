package com.hgh.na_o_man.presentation.ui.add.joingroup

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.hgh.na_o_man.R
import com.hgh.na_o_man.data.dto.share_group.request.GroupJoinRequestDto
import com.hgh.na_o_man.di.util.remote.onException
import com.hgh.na_o_man.di.util.remote.onFail
import com.hgh.na_o_man.di.util.remote.onSuccess
import com.hgh.na_o_man.domain.usecase.share_group.JoinGroupUsecase
import com.hgh.na_o_man.domain.usecase.share_group.JoinInviteUsecase
import com.hgh.na_o_man.presentation.base.BaseViewModel
import com.hgh.na_o_man.presentation.ui.add.JoinScreenRoute
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class JoinViewModel @Inject constructor(
    private val joinGroupUsecase: JoinGroupUsecase,
    private val joinInviteUsecase: JoinInviteUsecase,
) : BaseViewModel<JoinContract.JoinViewState, JoinContract.JoinSideEffect, JoinContract.JoinEvent>(
    JoinContract.JoinViewState() // 초기 상태 설정
) {

    init {
        Log.d("ViewModel", "JoinViewModel initialized")
    }

    override fun handleEvents(event: JoinContract.JoinEvent) {
        when (event) {
            is JoinContract.JoinEvent.ValidateUrl -> {
                validateUrl(event.inviteCode)// inviteCode 전달
            }

            is JoinContract.JoinEvent.onFind -> {
                // 다시 찾기 이벤트 처리
                findAnotherGroup()
            }

            is JoinContract.JoinEvent.onCorrect -> {
                // 맞아요 이벤트 처리
                sendEffect({ JoinContract.JoinSideEffect.NavigateToNextScreen })
                confirmGroup()
            }

            is JoinContract.JoinEvent.onProfileSelected -> {
                // 프로필 선택 이벤트 처리
//                updateState({ copy(profileId = event.profileId.toLong()) })
                sendEffect({ JoinContract.JoinSideEffect.NavigateToNextScreen })
            }

            else -> {}
        }
    }

    private fun validateUrl(inviteCode: String) = viewModelScope.launch {
        try {
            joinInviteUsecase(inviteCode).collect { result ->
                result.onSuccess { response ->
                    updateState {
                        copy(
                            isUrlValid = true,
                            shareGroupId = viewState.value.shareGroupId,
                            members = response.profileInfoList.map {
                                Member(it.name, R.drawable.ic_add_group_avatar_94)
                            },
                            groupName = response.name,
                            inviteCode = inviteCode
                        )
                    }
                    sendEffect({ JoinContract.JoinSideEffect._ShowToast("URL 검증에 성공했습니다.") })
                }.onFail { error ->
                    Log.e("ValidateUrl", "서버와 연결을 실패했습니다. 오류: $error")
                    sendEffect({ JoinContract.JoinSideEffect._ShowToast("URL 검증에 실패했습니다.") })
                }.onException { e ->
                    Log.e("ValidateUrl", "Exception: $e")
                    sendEffect({ JoinContract.JoinSideEffect._ShowToast("서버와 연결을 실패했습니다. 오류: ${e.message}") })
                }
            }
        } catch (e: Exception) {
            Log.e("ValidateUrl", "Exception: $e")
            sendEffect({ JoinContract.JoinSideEffect._ShowToast("알 수 없는 오류가 발생했습니다.") })
        }
    }



    private fun fetchGroupInfo(profileId: Long, shareGroupId: Int) = viewModelScope.launch {
        try {
            // GroupJoinRequestDto 생성
            val requestDto = GroupJoinRequestDto(profileId, shareGroupId)

            joinGroupUsecase(requestDto).collect { result ->
                result.onSuccess { response ->
                    // 성공적으로 그룹 정보를 가져온 경우
                    updateState {
                        copy(
                            shareGroupId = viewState.value.shareGroupId, // 가져온 그룹 ID 업데이트
                            profileId = viewState.value.profileId
                        )
                    }
                    sendEffect ({
                        JoinContract.JoinSideEffect._ShowToast("그룹 정보를 성공적으로 가져왔습니다.")
                        JoinContract.JoinSideEffect.NavigateToNextScreen
                    })
                }.onFail { error ->
                    // 그룹 정보가 유효하지 않은 경우 처리
                    Log.e("FetchGroupInfo", "유효하지 않은 그룹 정보: $error")
                    updateState {
                        copy(
                            isUrlValid = false,
                            members = emptyList() // 멤버 리스트 비우기
                        )
                    }
                    sendEffect ({ JoinContract.JoinSideEffect._ShowToast("유효하지 않은 그룹입니다.") })
                }.onException { e ->
                    // 예외 발생 시 로그 출력 및 메시지 표시
                    Log.e("FetchGroupInfo", "Exception: $e")
                    sendEffect ({ JoinContract.JoinSideEffect._ShowToast("서버와 연결을 실패했습니다. 오류: ${e.message}") })
                }
            }
        } catch (e: Exception) {
            // 전체 예외 처리
            Log.e("FetchGroupInfo", "Exception: $e")
            sendEffect ({ JoinContract.JoinSideEffect._ShowToast("알 수 없는 오류가 발생했습니다.") })
        }
    }

    private fun confirmGroup() {
    }

    private fun findAnotherGroup() {

    }

    fun getGroupName(): String = viewState.value.groupName

}


