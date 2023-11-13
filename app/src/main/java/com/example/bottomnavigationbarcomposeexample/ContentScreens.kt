package com.example.bottomnavigationbarcomposeexample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

@Preview(showBackground = true)
@Composable
fun BooksScreenPreview() {
    BooksScreen()
}

@Composable
fun ProfileScreen() {


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.colorPrimaryDark))
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
        ) {
            // Верхняя часть: Фото в круге
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .padding(20.dp)
                    .background(Color.Gray, shape = CircleShape)

            ) {
                // Здесь можно использовать фактическую логику для загрузки фото
                // Например: Image(...)
            }
            Column(
                modifier = Modifier
                    .padding(30.dp)
            ) {
                Text("Иван Иванов", fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(5.dp)
                )
                Text("Группа: А123", fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(5.dp)
                )
            }

        }


        // Верхняя часть данных пользователя: ФИО и группа обучения


        Spacer(modifier = Modifier.height(16.dp))

        // Таблица с предметами и учителями
        Table(
            subjects = listOf("Математика", "Физика", "Информатика"),
            teachers = listOf("Иванова", "Петров", "Сидоров")
        )



        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun Table(
    subjects: List<String>,
    teachers: List<String>
) {
    // Отобразить таблицу с предметами и учителями
    // Можно использовать LazyColumn, LazyRow или другие компоненты Jetpack Compose
    // для динамического отображения данных в зависимости от их количества
    // Например:
    LazyColumn {
        items(subjects.size) { index ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(text = subjects[index], fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = teachers[index])
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}