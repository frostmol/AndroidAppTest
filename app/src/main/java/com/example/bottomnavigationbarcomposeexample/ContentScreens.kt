package com.example.bottomnavigationbarcomposeexample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.colorPrimaryDark))
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "Home View",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 25.sp
        )


    }
}
/*
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}*/

@Composable
fun BooksScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.colorPrimaryDark))
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "Books View",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 25.sp
        )
    }
}

/*@Preview(showBackground = true)
@Composable
fun BooksScreenPreview() {
    BooksScreen()
}*/

@Composable
fun SubjectDetailsList(subjects: List<SubjectDetails>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Предметы:",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        subjects.forEach { subject ->
            Text(
                text = "Предмет: ${subject.subjectName}, Преподаватель: ${subject.teacherName}",
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
    }
}

@Composable
fun ProfileScreen(authViewModel: AuthViewModel = viewModel()) {
    val userDetails: StudentDetails? = authViewModel.currentUserDetails.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.colorPrimaryDark))
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .padding(20.dp)
                    .background(Color.Gray, shape = CircleShape)
            )
            Column(
                modifier = Modifier
                    .padding(30.dp)
            ) {
                Text(
                    userDetails?.fullName ?: "",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(5.dp)
                )
                Text(
                    "Группа: ${userDetails?.group}",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(5.dp)
                )

                // Display the subject details
                userDetails?.subjects?.let { SubjectDetailsList(it) }
            }
        }
    }
}












/*@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {

    ProfileScreen(userUid = "dummyUserId")
}*/




