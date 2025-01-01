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
}