package com.example.smartclass.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartclass.data.AlgebraTopics
import com.example.smartclass.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlgebraTopBar(
    title: String,
    onNavigateBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            if (onNavigateBack != null) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

@Composable
fun GradeSelector(
    selectedGrade: Int,
    onGradeSelected: (Int) -> Unit
) {
    val scroll = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .horizontalScroll(scroll),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        listOf(7, 8, 9).forEach { grade ->
            GradeChip(
                grade = grade,
                isSelected = selectedGrade == grade,
                onClick = { onGradeSelected(grade) }
            )
        }
    }
}
@Preview(
    name = "GradeSelector",
    showBackground = true,
    device = Devices.PIXEL_4
)
@Composable
fun GradeSelectorPreview() {
    SmartClassTheme {
        GradeSelector(
            selectedGrade = 8,
            onGradeSelected = {}
        )
    }
}

@Composable
fun GradeChip(
    grade: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val gradeColor = when (grade) {
        7 -> Grade7Color
        8 -> Grade8Color
        else -> Grade9Color
    }

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) gradeColor else gradeColor.copy(alpha = 0.2f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = "Класс $grade",
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            color = if (isSelected) Color.White else gradeColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(
    name = "GradChip",
    showBackground = true,
    device = Devices.PIXEL_4
)
@Composable
fun GradeChipView()
{
    SmartClassTheme {
        GradeChip(
            grade = 7,
            isSelected = true,
            onClick = {}
        )
    }
}

@Preview(
    name = "SelectorNoActive",
    showBackground = true,
    device = Devices.PIXEL_4
)
@Composable
fun GradeChipViewUn()
{
    SmartClassTheme {
        GradeChip(
            grade = 8,
            isSelected = false,
            onClick = {}
        )
    }
}

@Composable
fun TopicCard(
    title: String,
    description: String,
    grade: Int,
    iconName: String,
    isCompleted: Boolean = false,
    onClick: () -> Unit
) {
    val gradeColor = when (grade) {
        7 -> Grade7Color
        8 -> Grade8Color
        else -> Grade9Color
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(gradeColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getIconByName(iconName),
                    contentDescription = null,
                    tint = gradeColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    tint = SuccessGreen,
                    modifier = Modifier.size(28.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Open",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun MenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(gradientColors)
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}

@Composable
fun FormulaCard(
    name: String,
    formula: String,
    description: String,
    category: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f, fill = false),
                    maxLines = 2,
                    softWrap = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PrimaryBlue.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = category,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ) {
                Text(
                    text = formula,
                    modifier = Modifier
                        .padding(16.dp)
                        .wrapContentWidth(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    softWrap = true,
                    lineHeight = MaterialTheme.typography.titleMedium.lineHeight * 1.4
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun AnswerOption(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean?,
    showResult: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        showResult && isCorrect == true -> SuccessGreen.copy(alpha = 0.2f)
        showResult && isSelected && isCorrect == false -> ErrorRed.copy(alpha = 0.2f)
        isSelected -> PrimaryBlue.copy(alpha = 0.2f)
        else -> MaterialTheme.colorScheme.surface
    }

    val borderColor = when {
        showResult && isCorrect == true -> SuccessGreen
        showResult && isSelected && isCorrect == false -> ErrorRed
        isSelected -> PrimaryBlue
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(enabled = !showResult, onClick = onClick),
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            if (showResult) {
                Icon(
                    imageVector = if (isCorrect == true) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    contentDescription = null,
                    tint = if (isCorrect == true) SuccessGreen else ErrorRed
                )
            }
        }
    }
}

@Composable
fun ProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = SuccessGreen,
            trackColor = SuccessGreen.copy(alpha = 0.2f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${(progress * 100).toInt()}% Complete",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun StepCard(
    stepNumber: Int,
    content: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(28.dp),
            shape = CircleShape,
            color = PrimaryBlue
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = stepNumber.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}
@Preview(name = "TopicCard - Linear Equations", showBackground = true)
@Composable
fun TopicCardLinearPreview() {
    SmartClassTheme {
        TopicCard(
            title = "Линейные уравнения",
            description = "Научитесь решать уравнения с одной переменной",
            grade = 7,
            isCompleted = false,
            iconName = "calculate",
            onClick = {}
        )
    }
}

@Preview(name = "TopicCard - Completed", showBackground = true)
@Composable
fun TopicCardCompletedPreview() {
    SmartClassTheme {
        TopicCard(
            title = "Квадратные уравнения",
            description = "Решение уравнений с x²",
            grade = 8,
            isCompleted = true,
            iconName = "square",
            onClick = {}
        )
    }
}

@Preview(name = "TopicCard - Grade 9", showBackground = true)
@Composable
fun TopicCardGrade9Preview() {
    SmartClassTheme {
        TopicCard(
            title = "Функции",
            description = "Понимание математических функций",
            grade = 9,
            isCompleted = false,
            iconName = "show_chart",
            onClick = {}
        )
    }
}

@Preview(name = "TopicCard - Dark Theme", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TopicCardDarkPreview() {
    SmartClassTheme {
        TopicCard(
            title = "Координатная плоскость",
            description = "Работа с координатами и построение графиков",
            grade = 7,
            isCompleted = false,
            iconName = "grid_4x4",
            onClick = {}
        )
    }
}

// Preview для разных иконок
@Composable
@Preview(name = "All Icons Demo", showBackground = true)
fun TopicCardIconsPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        listOf(
            "calculate" to "Линейные уравнения",
            "edit" to "Алгебраические выражения",
            "auto_graph" to "Многочлены",
            "percent" to "Дроби и проценты",
            "grid_4x4" to "Координатная плоскость",
            "square" to "Квадратные уравнения",
            "grid_on" to "Системы уравнений",
            "show_chart" to "Функции"
        ).forEach { (icon, title) ->
            TopicCard(
                title = title,
                description = "Описание темы для демонстрации иконки",
                grade = 7,
                isCompleted = false,
                iconName = icon,
                onClick = {}
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}