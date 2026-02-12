package com.kanaz.script.ui.screens.editor
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
@Composable
fun CodeEditor(
    content: String,
    onContentChange: (String) -> Unit,
    language: String,
    fileName: String,
    viewModel: EditorViewModel,
    readOnly: Boolean = false
) {
    val verticalScroll = rememberScrollState()
    val horizontalScroll = rememberScrollState()
    val fontSize = 14.sp
    val lineHeight = 22.sp
    var textFieldValue by remember(content) {
        mutableStateOf(TextFieldValue(content))
    }
    val lines = textFieldValue.text.lines()
    val lineCount = lines.size
    val annotatedContent = remember(content, language) {
        buildSyntaxHighlighted(content, language)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .verticalScroll(verticalScroll)
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    repeat(lineCount) { index ->
                        Text(
                            text = (index + 1).toString(),
                            style = TextStyle(
                                fontSize = fontSize,
                                lineHeight = lineHeight,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 6.dp)
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(horizontalScroll)
                    .verticalScroll(verticalScroll)
            ) {
                if (readOnly) {
                    Text(
                        text = annotatedContent,
                        style = TextStyle(
                            fontSize = fontSize,
                            lineHeight = lineHeight,
                            fontFamily = FontFamily.Monospace
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .defaultMinSize(minWidth = 600.dp)
                    )
                } else {
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
                            lineHeight = lineHeight,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        readOnly = readOnly,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .defaultMinSize(minWidth = 600.dp)
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
fun buildSyntaxHighlighted(
    code: String,
    language: String
): androidx.compose.ui.text.AnnotatedString {
    return buildAnnotatedString {
        val keywords = when (language) {
            "kotlin" -> setOf(
                "fun", "val", "var", "class", "object", "interface", "if", "else",
                "when", "for", "while", "return", "import", "package", "data",
                "sealed", "override", "private", "public", "internal", "protected",
                "suspend", "companion", "lateinit", "by", "in", "is", "as", "null",
                "true", "false", "this", "super", "try", "catch", "finally", "throw",
                "init", "constructor", "typealias", "enum", "annotation", "abstract",
                "open", "final", "const", "lazy"
            )
            "java" -> setOf(
                "public", "private", "protected", "class", "interface", "void",
                "int", "long", "boolean", "String", "return", "import", "package",
                "new", "if", "else", "for", "while", "static", "final", "null",
                "true", "false", "this", "super", "try", "catch", "finally"
            )
            "python" -> setOf(
                "def", "class", "import", "from", "return", "if", "elif", "else",
                "for", "while", "in", "not", "and", "or", "True", "False", "None",
                "try", "except", "finally", "with", "as", "pass", "break", "continue",
                "lambda", "yield", "global", "nonlocal", "del", "raise", "assert"
            )
            "javascript", "typescript" -> setOf(
                "const", "let", "var", "function", "return", "if", "else", "for",
                "while", "class", "import", "export", "from", "async", "await",
                "true", "false", "null", "undefined", "new", "this", "typeof",
                "try", "catch", "finally", "throw", "switch", "case", "break"
            )
            else -> emptySet()
        }
        val keywordColor = Color(0xFF569CD6)
        val stringColor = Color(0xFFCE9178)
        val commentColor = Color(0xFF6A9955)
        val numberColor = Color(0xFFB5CEA8)
        val tagColor = Color(0xFF4EC9B0)
        val defaultColor = Color(0xFFD4D4D4)
        val lines = code.lines()
        lines.forEachIndexed { lineIndex, line ->
            var i = 0
            while (i < line.length) {
                when {
                    (language == "kotlin" || language == "java" ||
                     language == "javascript" || language == "typescript" ||
                     language == "python") &&
                    (line.startsWith("//", i) || line.startsWith("#", i)) -> {
                        withStyle(SpanStyle(color = commentColor)) {
                            append(line.substring(i))
                        }
                        i = line.length
                    }
                    line[i] == '"' || line[i] == '\'' -> {
                        val quote = line[i]
                        var end = i + 1
                        while (end < line.length && line[end] != quote) {
                            if (line[end] == '\\') end++
                            end++
                        }
                        if (end < line.length) end++
                        withStyle(SpanStyle(color = stringColor)) {
                            append(line.substring(i, end))
                        }
                        i = end
                    }
                    line[i].isDigit() && (i == 0 || !line[i-1].isLetterOrDigit()) -> {
                        var end = i
                        while (end < line.length && (line[end].isDigit() || line[end] == '.')) end++
                        withStyle(SpanStyle(color = numberColor)) {
                            append(line.substring(i, end))
                        }
                        i = end
                    }
                    line[i].isLetter() || line[i] == '_' -> {
                        var end = i
                        while (end < line.length && (line[end].isLetterOrDigit() || line[end] == '_')) end++
                        val word = line.substring(i, end)
                        if (word in keywords) {
                            withStyle(SpanStyle(color = keywordColor)) { append(word) }
                        } else if (word[0].isUpperCase()) {
                            withStyle(SpanStyle(color = tagColor)) { append(word) }
                        } else {
                            withStyle(SpanStyle(color = defaultColor)) { append(word) }
                        }
                        i = end
                    }
                    else -> {
                        withStyle(SpanStyle(color = defaultColor)) {
                            append(line[i])
                        }
                        i++
                    }
                }
            }
            if (lineIndex < lines.size - 1) append("\n")
        }
    }
}
