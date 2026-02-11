package com.kanaz.script.ui.screens.editor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@Composable
fun CodeEditor(
    content: String,
    onContentChange: (String) -> Unit,
    language: String,
    fileName: String,
    viewModel: EditorViewModel
) {
    val lines = content.lines()
    val listState = rememberLazyListState()
    var showLineNumbers by remember { mutableStateOf(true) }
    val visibleLines by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val first = layoutInfo.visibleItemsInfo.firstOrNull()?.index ?: 0
            val last = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: lines.size - 1
            first..last
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            if (showLineNumbers) {
                LineNumbersColumn(
                    lineCount = lines.size,
                    visibleLines = visibleLines,
                    modifier = Modifier.width(48.dp)
                )
            }
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(lines.size) { index ->
                    CodeLine(
                        lineNumber = index + 1,
                        content = lines[index],
                        isSelected = index in visibleLines,
                        onTextChange = { newLine ->
                            val newLines = lines.toMutableList()
                            newLines[index] = newLine
                            onContentChange(newLines.joinToString("\n"))
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
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
    visibleLines: IntRange,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant)) {
        LazyColumn(modifier = Modifier.fillMaxHeight()) {
            items(lineCount) { index ->
                val lineNumber = index + 1
                Text(
                    text = lineNumber.toString(),
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}
@Composable
private fun CodeLine(
    lineNumber: Int,
    content: String,
    isSelected: Boolean,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        BasicTextField(
            value = content,
            onValueChange = onTextChange,
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 2.dp)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    else Color.Transparent
                )
        )
    }
}
