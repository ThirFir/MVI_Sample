package com.sample.mvisample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.viewmodel.container
import kotlin.reflect.KClass

class MyPageViewModel(
    private val userRepository: UserRepository
): BaseContainerHost<MyPageUiState, Nothing>() {

    override val container: Container<MyPageUiState, Nothing> = container(MyPageUiState.Loading) {
        delay(1000)
        userRepository.fetchUserInfo().reduceResult(
            syntax = this,
            onSuccess = { MyPageUiState.Success(it) },
            onFailure = { MyPageUiState.LoadFailed }
        )
    }

    @OptIn(OrbitExperimental::class)
    fun getRandomNickname() = intent {
        runOn<MyPageUiState.Success> {
//            userRepository.getRandomNickname()
//                .onSuccess { reduce { MyPageUiState.Success(state.userInfo.copy(nickname = it)) }
//            }.onFailure { reduce { MyPageUiState.LoadFailed } }
            // OR
            userRepository.getRandomNickname().reduceResult(
                syntax = this@intent,
                onSuccess = { MyPageUiState.Success(state.userInfo.copy(nickname = it)) },
                onFailure = { MyPageUiState.LoadFailed }
            )
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


class MyPageViewModelFactory(
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        return MyPageViewModel(userRepository) as T
    }
}