package ru.artyommukhin.lvtrainer.training

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.artyommukhin.lvtrainer.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TrainingPage(
    onNavigateBack: () -> Unit,
    onNavigateToDictionary: () -> Unit,
    viewModel: TrainingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }

    Scaffold(
        Modifier
            .imePadding()
            .imeNestedScroll(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = "Localized description"
                        )
                    }
                },
                title = { Text("Тренировка") })
        },
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .padding(top = 24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (val s = state) {
                TrainingUiState.Loading -> CircularProgressIndicator()
                TrainingUiState.NoWords -> {
                    Text(
                        "В словаре ещё нет слов",
                        Modifier.padding(horizontal = 16.dp),
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(16.dp))

                    FilledTonalButton(
                        onClick = { onNavigateToDictionary() }
                    ) {
                        Text("Добавить")
                    }
                }

                TrainingUiState.NoUntrainedWords -> {
                    Text(
                        "Все слова уже изучены",
                        Modifier.padding(horizontal = 16.dp),
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(16.dp))

                    FilledTonalButton(
                        onClick = { onNavigateToDictionary() }
                    ) {
                        Text("Добавить")
                    }
                }

                is TrainingUiState.Active -> {
                    var input by rememberSaveable { mutableStateOf("") }

                    Text(
                        "Слово изучено на ${s.word.trainCount * 20}%",
                        Modifier
                            .align(Alignment.End)
                            .padding(horizontal = 16.dp),
                    )

                    Spacer(Modifier.height(32.dp))

                    Text(
                        buildAnnotatedString {
                            append(s.word.word)
                            append(" - ")
                            if (!s.isAnswered)
                                withStyle(style = SpanStyle(color = Color.Gray)) {
                                    append("???")
                                }

                            if (s.isAnsweredCorrectly == true)
                                withStyle(
                                    style = SpanStyle(
                                        color = Color(76, 175, 80, 255),
                                    )
                                ) {
                                    append(s.word.translation)
                                }

                            if (s.isAnsweredCorrectly == false) {
                                withStyle(
                                    style = SpanStyle(
                                        color = Color(196, 57, 48, 255),
                                        textDecoration = TextDecoration.LineThrough,
                                    )
                                ) {
                                    append(input)
                                }
                                append(" ")
                                withStyle(
                                    style = SpanStyle(
                                        color = Color(76, 175, 80, 255),
                                    )
                                ) {
                                    append(s.word.translation)
                                }
                            }
                        },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(Modifier.weight(1f))

                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        value = input,
                        onValueChange = { if (!s.isAnswered) input = it },
                        trailingIcon = {
                            if (input.trim() == "") return@TextField

                            if (s.isAnswered)
                                IconButton(
                                    onClick = {
                                        input = ""
                                        viewModel.restart()
                                    },
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.arrow_forward),
                                        contentDescription = "Next word",
                                    )
                                }
                            else
                                IconButton(onClick = { viewModel.answer(s.word, input) }) {
                                    Icon(
                                        painter = painterResource(R.drawable.send),
                                        contentDescription = "Send the word",
                                    )
                                }
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction =
                                if (input.trim() == "") ImeAction.None
                                else if (s.isAnswered) ImeAction.Next
                                else ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { viewModel.answer(s.word, input) },
                            onNext = {
                                input = ""
                                viewModel.restart()
                            },
                        ),
                        singleLine = true,
                    )

                    LaunchedEffect(1) {
                        focusRequester.requestFocus()
                    }
                }
            }
        }
    }
}
