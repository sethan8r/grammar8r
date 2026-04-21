package dev.sethan8r.grammar.app.ui.screens.menu

import android.content.Intent
import androidx.core.net.toUri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Subject
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.GetApp
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.app.ui.components.MenuButton
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.CardBackground
import dev.sethan8r.grammar.app.ui.theme.TextSecondary

@Composable
fun MenuScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.height(16.dp))
        Text(
            text = "МЕНЮ",
            color = Accent,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(24.dp))

        MenuButton("ДОБАВИТЬ СЛОВО", Icons.Filled.Add) {}
        Spacer(Modifier.height(12.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp)
                    .background(
                        color = CardBackground.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                    )
                    .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
            ) {
                Text(
                    text = "Может занять некоторое время. Не закрывайте приложение, пока не пропадёт значок загрузки.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                )
            }
            MenuButton("СДЕЛАТЬ БЕКАП", Icons.Filled.Publish) {}
        }
        Spacer(Modifier.height(12.dp))

        MenuButton("ВОССТАНОВИТЬ ПРОГРЕСС", Icons.Filled.GetApp) {}
        Spacer(Modifier.height(12.dp))

        MenuButton("МОИ СЛОВА", Icons.Filled.Bookmarks) {}
        Spacer(Modifier.height(12.dp))

        MenuButton("СБРОСИТЬ СТАТИСТИКУ", Icons.Filled.Refresh) {}
        Spacer(Modifier.height(12.dp))

        MenuButton("ПОДДЕРЖКА", Icons.Filled.Email) {}
        Spacer(Modifier.height(12.dp))

        MenuButton("О ПРИЛОЖЕНИИ", Icons.AutoMirrored.Filled.Subject) {}

        Spacer(Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "powered by sethan8r",
                color = TextSecondary,
                fontSize = 12.sp,
            )
            Text(
                text = "github.com/sethan8r",
                color = Accent,
                fontSize = 12.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, "https://github.com/sethan8r".toUri())
                    )
                },
            )
        }
    }
}