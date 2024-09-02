package com.slobozhaninova.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun OptionGroup(
    @PreviewParameter(provider = OptionPreviewParameterProvider::class)
    options: List<String>,
    selectedOption: Int = -1,
    validOption: Int = -1,
    onOptionSelected: (Int) -> Unit = {}
) {
        LazyColumn {
            itemsIndexed(options) { index, item ->
                OptionItem(
                    isSelected = index == selectedOption,
                    text = item,
                    onClick = {
                        if (selectedOption != index) {
                            onOptionSelected(index)
                        }
                    },
                    showStatus = selectedOption == index,
                    isValid = index == validOption
                )
            }
        }
    }


private class OptionPreviewParameterProvider : PreviewParameterProvider<List<String>> {
    override val values: Sequence<List<String>>
        get() = sequenceOf(listOf("option A", "option B", "option C"))
}

@Composable
private fun OptionItem(
    isSelected: Boolean,
    isValid: Boolean,
    showStatus: Boolean,
    text: String,
    onClick: () -> Unit
) {
    val bkgColor = if(showStatus) {
        if(isValid) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.error
        }
    } else {
        MaterialTheme.colorScheme.primary
    }

    Surface(
        color = bkgColor,
        modifier = Modifier.padding(4.dp),
        onClick = onClick,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
            )
            Text(text = text)
        }
    }


}