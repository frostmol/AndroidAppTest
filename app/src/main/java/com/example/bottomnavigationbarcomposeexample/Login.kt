package com.example.bottomnavigationbarcomposeexample

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bottomnavigationbarcomposeexample.NavigationItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
fun LoginPage(navController: NavController) {
    /*Box(modifier = Modifier.fillMaxSize()) {
        ClickableText(
            text = AnnotatedString("Signup Here"),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(20.dp),
            onClick = { navController.navigate("signup") },
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily.Default,
                textDecoration = TextDecoration.Underline,
                color = Purple700
            )
        )
    }*/
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val username = remember {
            mutableStateOf(TextFieldValue())
        }
        val password = remember {
            mutableStateOf(TextFieldValue())
        }

        Text(
            text = "Login",
            style = TextStyle(fontSize = 40.sp, fontFamily = FontFamily.Cursive)
        )

        Spacer(modifier = Modifier.height(15.dp))

        TextField(
            label = { Text(text = "Username") },
            value = username.value,
            onValueChange = { username.value = it }
        )

        Spacer(modifier = Modifier.height(15.dp))

        TextField(
            label = { Text(text = "Password") },
            value = password.value,
            onValueChange = { password.value = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(15.dp))

        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            Button(
                onClick = {
                    GlobalScope.launch(Dispatchers.Main) {
                        val authManager = AuthManager()
                        val result = authManager.signInWithEmailAndPassword(
                            email = username.value.text,
                            password = password.value.text
                        )

                        when (result) {
                            is AuthResult.Success -> {
                                // Выполните дополнительные действия при успешной аутентификации
                                result.user?.let { user ->
                                    Log.d("UserInfo", "Current user UID: ${user.uid}")
                                    Log.d("UserInfo", "Email: ${user.email}")
                                    Log.d("UserInfo", "Display Name: ${user.displayName}")

                                    // Получаем дополнительные сведения о пользователе

                                }
                                navController.navigate(NavigationItem.Profile.route)
                            }
                            is AuthResult.Error -> {
                                // Отобразите сообщение об ошибке
                                // Например, showMessage(result.errorMessage)
                            }
                        }
                    }
                },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Login")
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        /*ClickableText(
            text = AnnotatedString("Forgot Password?"),
            onClick = { navController.navigate("forgot-password") },
            style = TextStyle(
                fontSize = 15.sp,
                fontFamily = FontFamily.Default
            )
        )*/
    }
}

