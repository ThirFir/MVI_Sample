package com.sample.mvisample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun MyPageScreen(
    modifier: Modifier = Modifier,
    viewModel: MyPageViewModel = viewModel(
        factory = MyPageViewModelFactory(UserRepository())
    )
) {

    val state = viewModel.collectAsState().value

    Column(
        modifier = modifier
    ) {
        MyPageScreenContent(
            modifier = modifier.fillMaxSize(),
            myPageUiState = state,
            onRandomNicknameButtonClicked = viewModel::getRandomNickname
        )
    }
}

@Composable
fun MyPageScreenContent(
    myPageUiState: MyPageUiState,
    modifier: Modifier = Modifier,
    onRandomNicknameButtonClicked: () -> Unit = { }
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when(myPageUiState) {
            is MyPageUiState.Success -> {
                TextField(
                    value = myPageUiState.userInfo.account,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Account") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = myPageUiState.userInfo.nickname,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Nickname") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onRandomNicknameButtonClicked
                ) {
                    Text("랜덤 닉네임 설정")
                }
            }

            MyPageUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("로딩 중", fontSize = 30.sp, modifier = Modifier.background(Color.Cyan))
                }
            }

            MyPageUiState.LoadFailed -> {
                // TODO : 에러 화면
            }
        }
    }
}