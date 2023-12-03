package com.anushka.uilayerdemo1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anushka.uilayerdemo1.ui.theme.UILayerDemo1Theme
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UILayerDemo1Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    counterViewModel: CounterViewModel = viewModel()
) {
    //CounterViewModel內的總管state傳過來
    val screenState = counterViewModel.screenState.value
    //建立compose用的state
    val scaffoldState = rememberScaffoldState()
    //取用uiEventFlow 作為snackbar通知的根據
    //因為不需要每次都執行，只有第一次執行才需要，因此使用launchEffect(key = true)
    LaunchedEffect(key1 = true) {
        counterViewModel.uiEventFlow.collectLatest { event ->
            when (event) {//從UIEvent取用
                is UIEvent.ShowMessage -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
            }
        }

    }
    Scaffold(scaffoldState = scaffoldState) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(50.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                modifier = modifier.fillMaxWidth(),
                text = screenState.displayingResult,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Color.DarkGray
            )
            OutlinedTextField(
                value = screenState.inputValue,
                onValueChange = {//event處理使用者輸入值
                    counterViewModel.onEvent(CounterEvent.ValueEntered(it))
                },
                modifier = modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                textStyle = TextStyle(
                    color = Color.LightGray,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                ),
                label = { Text(text = "New Count") }
            )
            //count button需根據state決定visibility
            if (screenState.isCountButtonVisible) {
                Button(
                    onClick = {
                        counterViewModel.onEvent(CounterEvent.CounterButtonClicked)
                    },
                    modifier = modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Count",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            //Button reset
            Button(
                onClick = {
                    counterViewModel.onEvent(CounterEvent.ResetButtonClicked)
                },
                modifier = modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Reset",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

    }

}

