package presentation.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import domain.RequestState
import domain.ToDoTask
import presentation.components.ErrorScreen
import presentation.components.LoadingScreen
import presentation.components.TaskView
import presentation.screen.task.TaskScreen

class HomeScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        Scaffold(topBar = {
            CenterAlignedTopAppBar(title = { Text(text = "Home") })
        }, floatingActionButton = {
            FloatingActionButton(
                onClick = { }, shape = RoundedCornerShape(size = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit, contentDescription = "Edit Icon"
                )
            }
        }) { padding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(top = 24.dp).padding(
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding()
                )
            ) {
                DisplayTask(
                    modifier = Modifier.weight(1f),
                    tasks = RequestState.Idle,
                    onSelect = { selectedTask -> },
                    onFavorite = { task, isFavorite -> },
                    onComplete = { task, completed -> }
                )
                Spacer(modifier = Modifier.height(24.dp))
                DisplayTask(
                    modifier = Modifier.weight(1f),
                    tasks = RequestState.Idle,
                    showActive = false,
                    onComplete = { task, completed ->},
                    onDelete = {task ->}
                )


            }
        }
    }
}

@Composable
fun DisplayTask(
    modifier: Modifier = Modifier,
    tasks: RequestState<List<ToDoTask>>,
    showActive: Boolean = true,
    onSelect: ((ToDoTask) -> Unit)? = null,
    onFavorite: ((ToDoTask, Boolean) -> Unit)? = null,
    onComplete: (ToDoTask, Boolean) -> Unit,
    onDelete: ((ToDoTask) -> Unit)? = null,
) {
    var showDialog by remember { mutableStateOf(false) }
    var taskToDelete: ToDoTask? by remember { mutableStateOf(null) }

    if (showDialog) {
        AlertDialog(title = {
            Text(text = "Delete", fontSize = MaterialTheme.typography.titleLarge.fontSize)
        }, text = {
            Text(
                text = "Are you sure you want to remove '${taskToDelete!!.title}' task?",
                fontSize = MaterialTheme.typography.bodyMedium.fontSize
            )
        }, confirmButton = {
            Button(onClick = {
                onDelete?.invoke(taskToDelete!!)
                showDialog = false
                taskToDelete = null
            }) {
                Text(text = "Yes")
            }
        }, dismissButton = {
            TextButton(onClick = {
                taskToDelete = null
                showDialog = false
            }) {
                Text(text = "Cancel")
            }
        }, onDismissRequest = {
            taskToDelete = null
            showDialog = false
        })
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = if (showActive) "Active Tasks" else "Completed Tasks",
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(12.dp))
        tasks.DisplayResult(
            onLoading = { LoadingScreen() },
            onError = { ErrorScreen(message = it) },
            onSuccess = {
                if (it.isNotEmpty()) {
                    LazyColumn(modifier = Modifier.padding(horizontal = 24.dp)) {
                        items(
                            items = it,
                            key = { task -> task._id }
                        ) { task ->
                            TaskView(
                                showActive = showActive,
                                task = task,
                                onSelect = { onSelect?.invoke(it) },
                                onComplete = { selectedTask, completed ->
                                    onComplete(
                                        selectedTask,
                                        completed
                                    )
                                },
                                onFavorite = { selectedTask, favorite ->
                                    onFavorite?.invoke(
                                        selectedTask,
                                        favorite
                                    )
                                },
                                onDelete = { selectedTask ->
                                    taskToDelete = selectedTask
                                    showDialog = true
                                }
                            )
                        }
                    }

                } else {
                    ErrorScreen()
                }
            }
        )
    }
}
