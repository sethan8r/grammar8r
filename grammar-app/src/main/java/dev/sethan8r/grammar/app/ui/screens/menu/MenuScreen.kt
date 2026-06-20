package dev.sethan8r.grammar.app.ui.screens.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.sp
import dev.sethan8r.grammar.app.R
import dev.sethan8r.grammar.app.ui.components.menu.MenuAccountCard
import dev.sethan8r.grammar.app.ui.components.menu.MenuGroup
import dev.sethan8r.grammar.app.ui.components.menu.MenuItem
import dev.sethan8r.grammar.app.ui.theme.Accent
import dev.sethan8r.grammar.app.ui.theme.Dimens

/** Серия заходов в карточке аккаунта — плейсхолдер до подключения реального стрика (Фаза 4). */
private const val PLACEHOLDER_STREAK_DAYS = 7

@Composable
fun MenuScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.screenPadding)
            .padding(bottom = Dimens.spaceXLarge),
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceXLarge),
    ) {
        Text(
            text = stringResource(R.string.menu_title),
            modifier = Modifier.padding(top = Dimens.spaceLarge),
            color = Accent,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )

        MenuAccountCard(
            name = stringResource(R.string.menu_account_name),
            streakDays = PLACEHOLDER_STREAK_DAYS,
            onClick = {},
        )

        MenuGroup(
            title = stringResource(R.string.menu_section_account),
            items = listOf(
                MenuItem(stringResource(R.string.menu_profile), Icons.Filled.AccountCircle) {},
                MenuItem(stringResource(R.string.menu_add_word), Icons.Filled.Add) {},
                MenuItem(stringResource(R.string.menu_dictionary), bookIcon()) {},
            ),
        )

        MenuGroup(
            title = stringResource(R.string.menu_section_data),
            items = listOf(
                MenuItem(stringResource(R.string.menu_backup), Icons.Filled.Publish) {},
                MenuItem(stringResource(R.string.menu_restore), Icons.Filled.GetApp) {},
            ),
        )

        MenuGroup(
            title = stringResource(R.string.menu_section_settings),
            items = listOf(
                MenuItem(stringResource(R.string.menu_notifications), Icons.Filled.Notifications) {},
                MenuItem(stringResource(R.string.menu_hint_limit), Icons.Filled.Edit) {},
                MenuItem(stringResource(R.string.menu_settings), Icons.Filled.Tune) {},
            ),
        )

        MenuGroup(
            title = stringResource(R.string.menu_section_help),
            items = listOf(
                MenuItem(stringResource(R.string.menu_support), Icons.Filled.Email) {},
                MenuItem(stringResource(R.string.menu_about), Icons.AutoMirrored.Filled.Subject) {},
            ),
        )
    }
}

@Composable
private fun bookIcon(): ImageVector = ImageVector.vectorResource(R.drawable.ic_book_5)