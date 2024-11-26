package ru.yogago.goyoga.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
    var passwordVisible by remember { mutableStateOf(false) }
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
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        textStyle = TextStyle(fontSize = textSize),
        trailingIcon = {
            Row {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) stringResource(R.string.hide_password) else stringResource(R.string.show_password)
                    )
                }
                if (password.isNotEmpty()) {
                    IconButton(onClick = { onPasswordChange("") }) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = stringResource(R.string.clear_text),
                            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    )
}