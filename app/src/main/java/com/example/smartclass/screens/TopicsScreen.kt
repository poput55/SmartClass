package com.example.smartclass.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smartclass.ui.components.*
import com.example.smartclass.viewmodel.AlgebraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicsScreen(
    viewModel: AlgebraViewModel,
    onTopicSelected: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val topics = viewModel.getTopicsForCurrentGrade()

    Scaffold(
        topBar = {
            AlgebraTopBar(
                title = "Темы класса ${viewModel.selectedGrade}",
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Grade Selector
            GradeSelector(
                selectedGrade = viewModel.selectedGrade,
                onGradeSelected = { viewModel.selectGrade(it) }
            )

            // Topics List
            if (topics.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(
                        text = "Для этого класса нет доступных тем",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(topics) { topic ->
                        TopicCard(
                            title = topic.title,
                            description = topic.description,
                            grade = topic.grade,
                            isCompleted = topic.id in viewModel.completedTopics,
                            iconName = topic.iconName,
                            onClick = {
                                viewModel.selectTopic(topic)
                                onTopicSelected()
                            }
                        )
                    }
                }
            }
        }
    }
}
