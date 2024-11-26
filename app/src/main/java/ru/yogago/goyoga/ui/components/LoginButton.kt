package ru.yogago.goyoga.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.yogago.goyoga.R
import ru.yogago.goyoga.ui.theme.Dimensions.ButtonFontSize
import ru.yogago.goyoga.ui.theme.Dimensions.ButtonHeight
import ru.yogago.goyoga.ui.theme.Dimensions.ButtonHorizontalPadding
import ru.yogago.goyoga.ui.theme.Dimensions.ButtonVerticalPaddingBottom
import ru.yogago.goyoga.ui.theme.Dimensions.ButtonVerticalPaddingTop
import ru.yogago.goyoga.ui.theme.Dimensions.LoadingIndicatorSize
import ru.yogago.goyoga.ui.theme.LoadingIndicatorColor
import ru.yogago.goyoga.ui.theme.getButtonColors

@Composable
fun LoginButton(
    isLoginEnabled: Boolean,
    isLoading: Boolean,
    onLoginClick: () -> Unit
) {
    val buttonStyle = getButtonColors(isLoginEnabled)

    Button(
        onClick = { onLoginClick() },
        modifier = Modifier
            .safeContentPadding()
            .padding(top = ButtonVerticalPaddingTop, bottom = ButtonVerticalPaddingBottom)
            .height(ButtonHeight),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = buttonStyle.backgroundColor,
            contentColor = buttonStyle.contentColor
        ),
        shape = RoundedCornerShape(buttonStyle.cornerRadius),
        enabled = isLoginEnabled && !isLoading // Блокируем, если идет загрузка
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = LoadingIndicatorColor,
                modifier = Modifier.size(LoadingIndicatorSize)
            )
        } else {
            Text(
                text = stringResource(R.string.login).uppercase(),
                fontSize = ButtonFontSize,
                modifier = Modifier.padding(horizontal = ButtonHorizontalPadding)
            )
        }
    }
}
