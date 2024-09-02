package com.slobozhaninova.compose

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

class DropdownOptionsPreviewProvider : PreviewParameterProvider<List<String>> {
    override val values: Sequence<List<String>>
        get() = sequenceOf(
            listOf(
                "option A",
                "option B",
                "option C"
            )
        )
}

@Preview()
@Composable
private fun dropdownPreview() {
    DropdownComponent(
        options = listOf(
            "option A",
            "option B",
            "option C"
        ), hint = "hint", selectedOption = -1, onSelectedOptionChange = {}
    )
}


@Composable
fun DropdownComponent(
    options: List<String>,
    hint: String,
    selectedOption: Int,
    onSelectedOptionChange: (Int) -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable {
                expanded = true
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = options.getOrElse(selectedOption) {
                hint
            },
            color = if (selectedOption in options.indices) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(
                    start = 16.dp
                )
                .padding(vertical = 16.dp)
        )

        Box(
            modifier = Modifier.padding(end = 16.dp)
        ) {
            val animatedAngle by animateFloatAsState(
                if (expanded) 180f else 0f
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier
                    .rotate(animatedAngle)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(),
            offset = DpOffset(0.dp, (-8).dp)
        ) {
            options.forEachIndexed { index, label ->
                DropdownMenuItem(
                    onClick = {
                    onSelectedOptionChange(index)
                    expanded = false
                },
                    text = { Text(text = label) })

            }
        }
    }
}
