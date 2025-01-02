package com.sample.mvisample

import androidx.core.text.isDigitsOnly

class UserRepository {

    fun trySignUp(account: String, password: String, nickname: String): Result<SignUpResult> {
        return runCatching {
            SignUpResult.Success
        }
    }

    fun checkAccountAlreadyExist(account: String): Result<AccountUiState> {
        return runCatching {
            when {
                account.isEmpty() -> AccountUiState.Empty
                account.isDigitsOnly() -> AccountUiState.InvalidFormat
                account == "aaa" -> AccountUiState.AlreadyExist
                else -> AccountUiState.Available
            }
        }
    }

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