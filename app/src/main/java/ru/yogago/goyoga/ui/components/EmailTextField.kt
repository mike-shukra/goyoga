package ru.yogago.goyoga.ui.components


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import ru.yogago.goyoga.R
import ru.yogago.goyoga.ui.theme.Dimensions.TextFieldHorizontalPadding
import ru.yogago.goyoga.ui.theme.Dimensions.TextFieldVerticalPaddingEmail
import ru.yogago.goyoga.ui.theme.TextFieldErrorColor
import ru.yogago.goyoga.ui.theme.TextFieldFocusedIndicatorColor
import ru.yogago.goyoga.ui.theme.TextFieldTextColor
import ru.yogago.goyoga.ui.theme.TextFieldUnfocusedIndicatorColor
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EmailTextField(
    email: String,
    onEmailChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val textSize = MaterialTheme.typography.body1.fontSize

    val autofillNode = remember {
        AutofillNode(
            autofillTypes = listOf(AutofillType.EmailAddress),
            onFill = {onEmailChange(it)}
        )
    }
    val autofill = LocalAutofill.current

    LocalAutofillTree.current += autofillNode

    TextField(
        value = email,
        onValueChange = onEmailChange,
        placeholder = {
            Text(
                text = stringResource(R.string.email),
                color = MaterialTheme.colors.onSurface,
                fontSize = textSize
            )
        },
        modifier = modifier.onGloballyPositioned {
                autofillNode.boundingBox = it.boundsInWindow()
            }.onFocusChanged { focusState ->
                autofill?.run {
                    if (focusState.isFocused) {
                        requestAutofillForNode(autofillNode)
                    } else {
                        cancelAutofillForNode(autofillNode)
                    }
                }
            }
            .fillMaxWidth()
            .padding(
                horizontal = TextFieldHorizontalPadding,
                vertical = TextFieldVerticalPaddingEmail
            ),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = if (isEmailValid) TextFieldFocusedIndicatorColor else TextFieldErrorColor,
            unfocusedIndicatorColor = if (isEmailValid) TextFieldUnfocusedIndicatorColor else TextFieldErrorColor,
            textColor = TextFieldTextColor
        ),
        isError = !isEmailValid,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        visualTransformation = VisualTransformation.None,
        textStyle = TextStyle(fontSize = textSize)
    )
}