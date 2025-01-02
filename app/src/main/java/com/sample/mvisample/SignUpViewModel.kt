package com.sample.mvisample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import org.orbitmvi.orbit.viewmodel.container
import kotlin.reflect.KClass

data class SignUpState(
    val account: String = "",
    val password: String = "",
    val nickname: String = "",
    val accountUiState: AccountUiState = AccountUiState.Idle,
    val isLoading: Boolean = true
)

sealed interface AccountUiState {
    data object Idle: AccountUiState
    data object InvalidFormat: AccountUiState
    data object Empty: AccountUiState
    data object AlreadyExist: AccountUiState
    data object Available: AccountUiState
}

sealed interface SignUpSideEffect {
    data object NavigateToMain: SignUpSideEffect
    data object ShowSnackbar: SignUpSideEffect
}

sealed interface SignUpResult {
    data object Success: SignUpResult
}

class SignUpViewModel(
    private val userRepository: UserRepository
) : BaseContainerHost<SignUpState, SignUpSideEffect>() {

    override val container = container<SignUpState, SignUpSideEffect>(SignUpState()) {
        delay(1000)
        reduce {
            state.copy(isLoading = false)
        }
    }

    fun checkAccountAlreadyExist() = intent {
        userRepository.checkAccountAlreadyExist(state.account).onSuccess {
            reduce {
                state.copy(accountUiState = it)
            }
        }.onFailure {
            // 에러 처리
            postSideEffect(SignUpSideEffect.ShowSnackbar)
        }
    }

    fun onAccountChanged(account: String) = intent {
        reduce {
            state.copy(account = account, accountUiState = AccountUiState.Idle)
        }
    }

    fun onPasswordChanged(password: String) = intent {
        reduce {
            state.copy(password = password)
        }
    }

    fun onNicknameChanged(nickname: String) = intent {
        reduce {
            state.copy(nickname = nickname)
        }
    }

    fun trySignUp() = intent {
        val account = state.account
        val password = state.password
        val nickname = state.nickname

        userRepository.trySignUp(account, password, nickname).onSuccess {
            postSideEffect(SignUpSideEffect.NavigateToMain)
        }.onFailure {
            // 에러 처리
            postSideEffect(SignUpSideEffect.ShowSnackbar)
        }
    }
}

class SignUpViewModelFactory(
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        return SignUpViewModel(userRepository) as T
    }
}
