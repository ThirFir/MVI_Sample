package com.sample.mvisample

import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.viewmodel.container

class MyPageViewModel(
    private val myPageRepository: MyPageRepository
): BaseContainerHost<MyPageUiState, Nothing>() {

    override val container: Container<MyPageUiState, Nothing> = container(MyPageUiState.Loading) {
        myPageRepository.fetchUserInfo().onSuccess {
            reduce {
                MyPageUiState.Success(it)
            }
        }.onFailure {
            reduce {
                MyPageUiState.LoadFailed
            }
        }
    }

    @OptIn(OrbitExperimental::class)
    fun getRandomNickname() = intent {
        runOn<MyPageUiState.Success> {
            myPageRepository.getRandomNickname()
                .onSuccess { reduce { MyPageUiState.Success(state.userInfo.copy(nickname = it)) }
            }.onFailure { reduce { MyPageUiState.LoadFailed } }
            // OR
            myPageRepository.getRandomNickname().reduceResult(
                syntax = this@intent,
                onSuccess = { MyPageUiState.Success(state.userInfo.copy(nickname = it)) },
                onFailure = { MyPageUiState.LoadFailed }
            )
            myPageRepository.getRandomNickname().reduceOnSuccess(
                syntax = this@intent
            ) {
                MyPageUiState.Success(state.userInfo.copy(nickname = it))
            }
        }
    }
}


sealed interface MyPageUiState {
    data class Success(val userInfo: UserInfoState): MyPageUiState
    data object Loading: MyPageUiState
    data object LoadFailed: MyPageUiState
}

data class UserInfoState(
    val account: String,
    val nickname: String
)

class MyPageRepository() {
    fun fetchUserInfo(): Result<UserInfoState> {
        return runCatching {
            UserInfoState("account1", "nickname1")
        }
    }

    fun getRandomNickname(): Result<String> {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return runCatching {
            (1..8)
                .map { allowedChars.random() }
                .joinToString("")
        }
    }
}