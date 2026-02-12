package com.kanaz.script.ui.screens.terminal
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
@Composable
fun TerminalScreen(
    viewModel: TerminalViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val bgColor = when (state.terminalTheme) {
        "Light" -> Color(0xFFF5F5F5)
        "Green on Black" -> Color(0xFF0D1117)
        else -> Color(0xFF1E1E1E)
    }
    val textColor = when (state.terminalTheme) {
        "Light" -> Color(0xFF212121)
        "Green on Black" -> Color(0xFF00FF41)
        else -> Color(0xFFD4D4D4)
    }
    val promptColor = when (state.terminalTheme) {
        "Light" -> Color(0xFF1565C0)
        "Green on Black" -> Color(0xFF00FF41)
        else -> Color(0xFF569CD6)
    }
    LaunchedEffect(state.lines.size) {
        if (state.lines.isNotEmpty()) {
            listState.animateScrollToItem(state.lines.size - 1)
        }
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(bgColor.copy(alpha = 0.8f))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = state.shell.uppercase(),
                color = promptColor,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
            Row {
                IconButton(
                    onClick = { viewModel.historyUp() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, "History Up", tint = textColor, modifier = Modifier.size(18.dp))
                }
                IconButton(
                    onClick = { viewModel.historyDown() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, "History Down", tint = textColor, modifier = Modifier.size(18.dp))
                }
                IconButton(
                    onClick = { viewModel.executeCommand("clear") },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Clear, "Clear", tint = textColor, modifier = Modifier.size(18.dp))
                }
            }
        }
        Divider(color = textColor.copy(alpha = 0.2f))
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(state.lines) { line ->
                Text(
                    text = line.text,
                    color = when (line.type) {
                        LineType.COMMAND -> promptColor
                        LineType.ERROR -> Color(0xFFFF6B6B)
                        LineType.INFO -> textColor.copy(alpha = 0.6f)
                        LineType.OUTPUT -> textColor
                    },
                    fontSize = state.fontSize.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        if (state.isRunning) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = promptColor
            )
        }
        Divider(color = textColor.copy(alpha = 0.2f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(bgColor)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$ ",
                color = promptColor,
                fontSize = state.fontSize.sp,
                fontFamily = FontFamily.Monospace
            )
            BasicTextField(
                value = state.currentInput,
                onValueChange = { viewModel.updateInput(it) },
                textStyle = TextStyle(
                    color = textColor,
                    fontSize = state.fontSize.sp,
                    fontFamily = FontFamily.Monospace
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        viewModel.executeCommand(state.currentInput)
                        coroutineScope.launch {
                            if (state.lines.isNotEmpty()) {
                                listState.animateScrollToItem(state.lines.size - 1)
                            }
                        }
                    }
                ),
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                cursorBrush = androidx.compose.ui.graphics.SolidColor(promptColor)
            )
            if (state.currentInput.isNotEmpty()) {
                IconButton(
                    onClick = {
                        viewModel.executeCommand(state.currentInput)
                        coroutineScope.launch {
                            if (state.lines.isNotEmpty()) {
                                listState.animateScrollToItem(state.lines.size - 1)
                            }
                        }
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Send,
                        "Run",
                        tint = promptColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(bgColor.copy(alpha = 0.9f))
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("ls", "pwd", "cd ~", "cat", "mkdir", "rm", "help").forEach { shortcut ->
                TextButton(
                    onClick = {
                        if (shortcut == "cd ~") {
                            viewModel.executeCommand("cd ~")
                        } else {
                            viewModel.updateInput(shortcut + if (shortcut in listOf("cat", "mkdir", "rm")) " " else "")
                            focusRequester.requestFocus()
                        }
                    },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text(
                        text = shortcut,
                        color = promptColor,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
