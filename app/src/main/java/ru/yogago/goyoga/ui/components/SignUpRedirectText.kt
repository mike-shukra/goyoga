package ru.yogago.goyoga.ui.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import ru.yogago.goyoga.R
import ru.yogago.goyoga.ui.theme.Dimensions
@Composable
fun SignUpRedirectText(
    text: String = stringResource(R.string.don_t_have_an_account_sign_in),
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = MaterialTheme.colors.surface, // Цвет текста из темы
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = Dimensions.PaddingLarge, // Глобальные отступы
                vertical = Dimensions.PaddingMedium
            )
            .clickable(
                onClick = onClick,
                indication = LocalIndication.current, // Ripple эффект
                interactionSource = remember { MutableInteractionSource() }
            ),
        textAlign = TextAlign.End,
        fontSize = Dimensions.FontSizeMedium // Размер шрифта из темы
    )
}
