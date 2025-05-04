package com.example.progressiomobileapp

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
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
import com.example.progressiomobileapp.UserRoleManager
import com.example.progressiomobileapp.TaskDetailActivity
import com.example.progressiomobileapp.data.dao.AdminDao
import com.example.progressiomobileapp.data.dao.TaskDao
import com.example.progressiomobileapp.data.dao.UserDao
import androidx.compose.ui.res.stringResource




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
                text = stringResource(R.string.due_date, task.dueDate ?: stringResource(R.string.not_available)),
                color = Color.Black,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainCalendarScreen(
    userId: Int,
    isAdmin: Boolean,
    adminDao: AdminDao,
    taskViewModel: TaskViewModel
) {

    val tasks by taskViewModel.getTasksForUser(userId, isAdmin).collectAsState(initial = emptyList())

    LaunchedEffect(tasks) {
        Log.d("MainCalendarScreen", "Tasks loaded: ${tasks.size}")
    }


    var selectedDate by remember { mutableStateOf<Calendar?>(null) }
    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }

    val context = LocalContext.current
    val activity = (LocalContext.current as? android.app.Activity)

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.calendar), color = Color.Black) },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                navigationIcon = {
                    IconButton(onClick = { activity?.finish() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back)
                            , tint = Color.Black)
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
                        text = stringResource(
                            R.string.tasks_on_date,
                            DateFormat.getDateInstance(DateFormat.MEDIUM).format(date.time)
                        ),
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
                    Text(stringResource(R.string.no_tasks_on_date), color = Color.Black)

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
            stringResource(R.string.current_month, SimpleDateFormat("MMMM yyyy").format(currentMonth.time)),
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
                Text(stringResource(R.string.previous_month))

            }
            Button(
                onClick = onNextMonth,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
            ) {
                Text(stringResource(R.string.next_month))

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