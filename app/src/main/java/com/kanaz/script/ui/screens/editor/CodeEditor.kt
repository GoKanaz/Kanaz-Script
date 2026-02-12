package com.kanaz.script.ui.screens.editor
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.input.TextFieldValue
@Composable
fun CodeEditor(
    content: String,
    onContentChange: (String) -> Unit,
    language: String,
    fileName: String,
    viewModel: EditorViewModel
) {
    val verticalScroll = rememberScrollState()
    val horizontalScroll = rememberScrollState()
    val lineHeight = 20.sp
    val fontSize = 14.sp
    var textFieldValue by remember(content) {
        mutableStateOf(TextFieldValue(content))
    }
    val lines = textFieldValue.text.lines()
    val lineCount = lines.size
    Box(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            LineNumbersColumn(
                lineCount = lineCount,
                scrollState = verticalScroll,
                fontSize = fontSize,
                lineHeight = lineHeight
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(horizontalScroll)
                    .verticalScroll(verticalScroll)
            ) {
                BasicTextField(
                    value = textFieldValue,
                    onValueChange = { newValue ->
                        textFieldValue = newValue
                        if (newValue.text != content) {
                            onContentChange(newValue.text)
                        }
                    },
                    textStyle = TextStyle(
                        fontSize = fontSize,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onBackground,
                        lineHeight = lineHeight
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .defaultMinSize(minWidth = 600.dp)
                )
            }
        }
        if (viewModel.editorState.value.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            )
        }
    }
}
@Composable
private fun LineNumbersColumn(
    lineCount: Int,
    scrollState: androidx.compose.foundation.ScrollState,
    fontSize: androidx.compose.ui.unit.TextUnit,
    lineHeight: androidx.compose.ui.unit.TextUnit
) {
    Box(
        modifier = Modifier
            .width(48.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .verticalScroll(scrollState)
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            repeat(lineCount) { index ->
                Text(
                    text = (index + 1).toString(),
                    style = TextStyle(
                        fontSize = fontSize,
                        lineHeight = lineHeight,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp)
                )
            }
        }
    }
}
