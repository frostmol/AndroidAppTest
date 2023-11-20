package com.example.bottomnavigationbarcomposeexample

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
@Composable
fun AssignmentContainer(itemName: String, additionalInfo: String) {
    val expanded = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(4.dp, colorResource(id = R.color.blue))
            .background(Color.Transparent)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = itemName,
            style = MaterialTheme.typography.h6,
            color = Color.Black,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (expanded.value) {
            Text(
                text = additionalInfo, // Используем дополнительную информацию, переданную в параметрах
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { expanded.value = false },
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(16.dp)
            ) {
                Text(text = "Свернуть")
            }
        } else {
            Button(
                onClick = { expanded.value = true },
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(16.dp)
            ) {
                Text(text = "Подробнее")
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}


@Composable
fun BooksScreen(authViewModel: AuthViewModel = viewModel()) {
    val userDetails: StudentDetails? = authViewModel.currentUserDetails.value

    userDetails?.let { details ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            val assignmentsBySubject = details.studentAssignments.groupBy { it.subjectName }

            items(assignmentsBySubject.entries.toList()) { entry ->
                val (subject, assignments) = entry

                AssignmentContainer(
                    itemName = subject,
                    additionalInfo = buildAdditionalInfo(assignments)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// Function to build additional info based on tasks for a subject
@Composable
fun buildAdditionalInfo(assignments: List<StudentAssignment>): String {
    val tasksInfo = buildString {
        append("Задания:")
        assignments.forEach { assignment ->
            append("\n- ${assignment.taskDescription}, Срок сдачи: ${assignment.deadline}")
        }
    }
    return tasksInfo
}

/*@Preview(showBackground = true)
@Composable
fun BooksScreenPreview() {
    BooksScreen()
}*/

@Composable
fun SubjectDetailsList(subjects: List<SubjectDetails>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Программа обучения",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(subjects) { subject ->
            Text(
                text = "${subject.subjectName}, Преподаватель: ${subject.teacherName}",
                fontSize = 14.sp,
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
                        .align(Alignment.Start)
                )
                Text(
                    "Группа: ${userDetails?.group}",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.Start)
                )
            }
        }

        // Display the subject details as a table
        userDetails?.subjects?.let { subjects ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = "Дисциплина",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Преподаватель",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                items(subjects) { subject ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    ) {
                        Text(
                            text = subject.subjectName,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = subject.teacherName,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}














/*@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {

    ProfileScreen(userUid = "dummyUserId")
}*/




