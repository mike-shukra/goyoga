package ru.yogago.goyoga.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import ru.yogago.goyoga.R
import ru.yogago.goyoga.ui.theme.Dimensions.TextFieldHorizontalPadding
import ru.yogago.goyoga.ui.theme.Dimensions.TextFieldVerticalPaddingPassword
import ru.yogago.goyoga.ui.theme.TextFieldFocusedIndicatorColor
import ru.yogago.goyoga.ui.theme.TextFieldTextColor
import ru.yogago.goyoga.ui.theme.TextFieldUnfocusedIndicatorColor

@Composable
fun PasswordTextField(
    password: String,
    onPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val textSize = MaterialTheme.typography.body1.fontSize
    TextField(
        value = password,
        onValueChange = onPasswordChange,
        placeholder = {
            Text(
                text = stringResource(R.string.password),
                color = MaterialTheme.colors.onSurface,
                fontSize = textSize
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = TextFieldHorizontalPadding,
                vertical = TextFieldVerticalPaddingPassword
            ),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = TextFieldFocusedIndicatorColor,
            unfocusedIndicatorColor = TextFieldUnfocusedIndicatorColor,
            textColor = TextFieldTextColor
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next
        ),
        visualTransformation = PasswordVisualTransformation(),
        textStyle = TextStyle(fontSize = textSize)
    )
}
