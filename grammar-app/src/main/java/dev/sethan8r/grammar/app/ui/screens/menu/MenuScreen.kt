package dev.sethan8r.grammar.app.ui.screens.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Subject
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.GetApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.app.R
import dev.sethan8r.grammar.app.ui.components.MenuButton
import dev.sethan8r.grammar.app.ui.theme.Accent

@Composable
fun MenuScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.menu_title),
            color = Accent,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(24.dp))

        MenuButton(stringResource(R.string.menu_profile), Icons.Filled.AccountCircle) {}
        Spacer(Modifier.height(12.dp))

        MenuButton(stringResource(R.string.menu_add_word), Icons.Filled.Add) {}
        Spacer(Modifier.height(12.dp))

        MenuButton(stringResource(R.string.menu_dictionary), ImageVector.vectorResource(R.drawable.ic_book_5)) {}
        Spacer(Modifier.height(12.dp))

        MenuButton(stringResource(R.string.menu_backup), Icons.Filled.Publish) {}
        Spacer(Modifier.height(12.dp))

        MenuButton(stringResource(R.string.menu_restore), Icons.Filled.GetApp) {}
        Spacer(Modifier.height(12.dp))

        MenuButton(stringResource(R.string.menu_hint_limit), Icons.Filled.Edit) {}
        Spacer(Modifier.height(12.dp))

        MenuButton(stringResource(R.string.menu_notifications), Icons.Filled.Notifications) {}
        Spacer(Modifier.height(12.dp))

        MenuButton(stringResource(R.string.menu_settings), Icons.Filled.Tune) {}
        Spacer(Modifier.height(12.dp))

        MenuButton(stringResource(R.string.menu_support), Icons.Filled.Email) {}
        Spacer(Modifier.height(12.dp))

        MenuButton(stringResource(R.string.menu_about), Icons.AutoMirrored.Filled.Subject) {}
        Spacer(Modifier.height(24.dp))
    }
}