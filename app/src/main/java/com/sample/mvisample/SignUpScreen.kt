package com.sample.mvisample

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect


@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = viewModel(
        factory = SignUpViewModelFactory(
            userRepository = UserRepository()
        )
    ),
    onNavigateToMain: () -> Unit = {}
){

    val state = viewModel.collectAsState().value

    Column(modifier = modifier) {

        SignUpScreenContent(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 60.dp),
            signUpState = state,
            onAccountChanged = viewModel::onAccountChanged,
            onPasswordChanged = viewModel::onPasswordChanged,
            onNicknameChanged = viewModel::onNicknameChanged,
            onSignUpClicked = viewModel::trySignUp,
            onCheckDuplicateClicked = viewModel::checkAccountAlreadyExist
        )
    }

    val context = LocalContext.current
    viewModel.collectSideEffect {
        when(it) {
            SignUpSideEffect.NavigateToMain -> {
                onNavigateToMain()
            }

            SignUpSideEffect.ShowSnackbar -> {
                Toast.makeText(context, "회원가입 실패 스낵바", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun SignUpScreenContent(
    modifier: Modifier = Modifier,
    signUpState: SignUpState,
    onAccountChanged: (String) -> Unit = {},
    onPasswordChanged: (String) -> Unit = {},
    onNicknameChanged: (String) -> Unit = {},
    onSignUpClicked: () -> Unit = {},
    onCheckDuplicateClicked: () -> Unit = {},
) {

    val isSignUpButtonEnabled by remember(signUpState) {
        derivedStateOf {
            signUpState.account.isNotEmpty() && signUpState.password.isNotEmpty() && signUpState.nickname.isNotEmpty()
        }
    }

    Column(
        modifier = modifier,
    ) {
        if (signUpState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("로딩 중", fontSize = 30.sp, modifier = Modifier.background(Color.Cyan))
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = signUpState.account,
                    onValueChange = onAccountChanged,
                    label = { Text("Account") },
                )
                Button(
                    onClick = onCheckDuplicateClicked,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(start = 8.dp),
                ) {
                    Text("중복확인")
                }
            }
            when (signUpState.accountUiState) {
                AccountUiState.Empty -> {
                    Text(text = "아이디를 입력해주세요",)
                }

                AccountUiState.InvalidFormat -> {
                    Text(text = "아이디 형식을 확인해주세요",)
                }

                AccountUiState.AlreadyExist -> {
                    Text(text = "이미 존재하는 아이디입니다",)
                }

                AccountUiState.Available -> {
                    Text(text = "사용 가능한 아이디입니다",)
                }

                AccountUiState.Idle -> Unit
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = signUpState.password,
                onValueChange = onPasswordChanged,
                label = { Text("Password") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = signUpState.nickname,
                onValueChange = onNicknameChanged,
                label = { Text("Nickname") }
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                enabled = isSignUpButtonEnabled,
                onClick = onSignUpClicked,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("회원가입")
            }
        }
    }
}