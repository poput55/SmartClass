package com.example.smartclass.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartclass.data.Formulas
import com.example.smartclass.ui.components.*
import com.example.smartclass.viewmodel.AlgebraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormulaScreen(
    viewModel: AlgebraViewModel,
    onNavigateBack: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    val categories = Formulas.getAllCategories()

    val formulasForGrade = viewModel.getFormulasForCurrentGrade()
    
    val formulas = if (selectedCategory != null) {
        formulasForGrade.filter { it.category == selectedCategory }
    } else {
        formulasForGrade
    }

    Scaffold(
        topBar = {
            AlgebraTopBar(
                title = "Справочник по формулам",
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
                onGradeSelected = {
                    viewModel.selectGrade(it)
                    selectedCategory = null
                }
            )

            // Category Filter
            Text(
                text = "Фильтровать по категории",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            ScrollableTabRow(
                selectedTabIndex = if (selectedCategory == null) 0 else categories.indexOf(selectedCategory) + 1,
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp
            ) {
                Tab(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    text = { Text("Все") }
                )
                categories.forEach { category ->
                    Tab(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        text = { Text(category) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Formulas List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(formulas) { formula ->
                    FormulaCard(
                        name = formula.name,
                        formula = formula.formula,
                        description = formula.description,
                        category = formula.category
                    )
                }
            }
        }
    }
}
