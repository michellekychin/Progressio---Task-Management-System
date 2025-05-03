package com.example.progressiomobileapp

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.progressiomobileapp.data.Task
import com.example.progressiomobileapp.ui.theme.components.TaskItem
import java.text.SimpleDateFormat
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.text.style.TextAlign
import java.text.DateFormat
import java.util.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.example.progressiomobileapp.HomepageUserActivity


//@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskItem(task: Task, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = task.title, color = Color.Black, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Due Date: ${task.dueDate ?: "Not Available"}",
                color = Color.Black,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainCalendarScreen(taskViewModel: TaskViewModel) {
    val tasks by taskViewModel.allTasks.collectAsState(initial = emptyList())
    var selectedDate by remember { mutableStateOf<Calendar?>(null) }
    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }

    val context = LocalContext.current

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Calendar", color = Color.Black) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            CalendarView(
                currentMonth = currentMonth,
                onDateSelected = { date -> selectedDate = date },
                onPreviousMonth = {
                    currentMonth = (currentMonth.clone() as Calendar).apply {
                        add(Calendar.MONTH, -1)
                    }
                    selectedDate = null
                },
                onNextMonth = {
                    currentMonth = (currentMonth.clone() as Calendar).apply {
                        add(Calendar.MONTH, 1)
                    }
                    selectedDate = null
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            selectedDate?.let { date ->
                val dateTasks = tasks.filter { task ->
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    val dueDateParsed = task.dueDate?.let { dateFormat.parse(it) }
                    val cal = Calendar.getInstance().apply { time = dueDateParsed ?: Date() }
                    cal.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                            cal.get(Calendar.MONTH) == date.get(Calendar.MONTH) &&
                            cal.get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH)
                }

                if (dateTasks.isNotEmpty()) {
                    Text(
                        "Tasks on ${DateFormat.getDateInstance(DateFormat.MEDIUM).format(date.time)}",
                        color = Color.Black
                    )
                    LazyColumn {
                        items(dateTasks) { task ->
                            TaskItem(task) {
                                val intent = Intent(context, TaskDetailActivity::class.java)
                                intent.putExtra("TASK_ID", task.taskId)
                                context.startActivity(intent)
                            }
                        }
                    }
                } else {
                    Text("No tasks on this date.", color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun CalendarView(
    currentMonth: Calendar,
    onDateSelected: (Calendar) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val daysInWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    val calendar = currentMonth.clone() as Calendar
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
    val maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    Column {
        Text(
            "Current Month: ${SimpleDateFormat("MMMM yyyy").format(currentMonth.time)}",
            color = Color.Black,
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onPreviousMonth,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
            ) {
                Text("Previous Month")
            }
            Button(
                onClick = onNextMonth,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
            ) {
                Text("Next Month")
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            daysInWeek.forEach {
                Text(
                    text = it,
                    color = Color.Black,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth(),
            userScrollEnabled = false
        ) {
            items(firstDayOfWeek) {
                Box(modifier = Modifier
                    .aspectRatio(1f)
                    .padding(4.dp))
            }

            items(maxDays) { index ->
                val day = index + 1
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(4.dp)
                        .clickable {
                            val selectedDate = currentMonth.clone() as Calendar
                            selectedDate.set(Calendar.DAY_OF_MONTH, day)
                            onDateSelected(selectedDate)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "$day", color = Color.Black)
                }
            }
        }
    }
}