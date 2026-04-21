package dev.sethan8r.grammar.app.ui.screens.statistics

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.sethan8r.grammar.app.ui.theme.TextPrimary

@Composable
fun StatisticsScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Статистика", color = TextPrimary)
    }
}