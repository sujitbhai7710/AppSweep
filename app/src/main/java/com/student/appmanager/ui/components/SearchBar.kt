package com.student.appmanager.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.student.appmanager.ui.theme.*

/**
 * Search bar component for filtering the app list.
 *
 * Design:
 * - Rounded search input with search icon
 * - Clear button (X) when text is present
 * - Light gray background with blue accent
 * - Hint text "Search apps..."
 *
 * The search filters apps in real-time as the user types,
 * matching against both app name and package name.
 *
 * @param query Current search text
 * @param onQueryChange Called when search text changes
 * @param onClear Called when the clear button is tapped
 * @param modifier Optional modifier
 * @param placeholder Optional custom placeholder text
 */
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search apps..."
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp)),
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = Gray400
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Blue500
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear search",
                        tint = Gray400
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Blue500,
            unfocusedBorderColor = Gray200,
            focusedContainerColor = White,
            unfocusedContainerColor = Gray50,
            cursorColor = Blue500
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { /* Search is real-time, no action needed */ }
        ),
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = Gray900
        )
    )
}
