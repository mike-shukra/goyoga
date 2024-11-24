package ru.yogago.goyoga.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.yogago.goyoga.R
import ru.yogago.goyoga.ui.theme.Dimensions.ButtonFontSize
import ru.yogago.goyoga.ui.theme.Dimensions.ButtonHeight
import ru.yogago.goyoga.ui.theme.Dimensions.ButtonHorizontalPadding
import ru.yogago.goyoga.ui.theme.Dimensions.ButtonVerticalPaddingBottom
import ru.yogago.goyoga.ui.theme.getButtonColors

@Composable
fun GoogleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonText: String = stringResource(R.string.google_sign_in)
) {
    val buttonStyle = getButtonColors(true)
    Button(
        onClick = onClick,
        modifier = modifier
            .safeContentPadding()
            .padding(bottom = ButtonVerticalPaddingBottom)
            .height(ButtonHeight),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = buttonStyle.backgroundColor,
            contentColor = buttonStyle.contentColor
        ),
        shape = RoundedCornerShape(buttonStyle.cornerRadius)
    ) {
        Text(
            text = buttonText.uppercase(),
            fontSize = ButtonFontSize,
            modifier = Modifier.padding(horizontal = ButtonHorizontalPadding)
        )
    }
}
