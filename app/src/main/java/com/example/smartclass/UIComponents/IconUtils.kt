package com.example.smartclass.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallSplit
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector



fun getIconByName(iconName: String): ImageVector {
    return when (iconName.lowercase()) {
        "calculate" -> Icons.Default.Calculate
        "edit" -> Icons.Default.Edit
        "auto_graph" -> Icons.Default.Subscript
        "percent" -> Icons.Default.Percent
        "grid_4x4" -> Icons.Default.LineAxis
        "format_frac" -> Icons.Default.FormatListNumbered
        "change_history" -> Icons.Default.ChangeHistory
        "square" -> Icons.Default.Superscript
        "grid_on" -> Icons.Default.Calculate
        "call_split" -> Icons.AutoMirrored.Filled.CallSplit
        "show_chart" -> Icons.AutoMirrored.Filled.ShowChart
        "compare_arrows" -> Icons.AutoMirrored.Filled.CompareArrows
        "trending_up" -> Icons.AutoMirrored.Filled.TrendingUp
        "functions" -> Icons.Default.Functions
        "menu_book" -> Icons.AutoMirrored.Filled.MenuBook
        "quiz" -> Icons.Default.Quiz
        "query_builder" -> Icons.Default.QueryBuilder
        "waves" -> Icons.Outlined.Circle
        "superscript" -> Icons.Default.Superscript
        else -> Icons.AutoMirrored.Filled.Help
    }
}