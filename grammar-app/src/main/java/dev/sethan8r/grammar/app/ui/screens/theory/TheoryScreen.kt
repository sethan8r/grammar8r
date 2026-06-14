package dev.sethan8r.grammar.app.ui.screens.theory

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.sethan8r.grammar.app.R
import dev.sethan8r.grammar.app.ui.theme.TextPrimary

@Composable
fun TheoryScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(stringResource(R.string.learn_placeholder), color = TextPrimary)
    }
}