package ru.artyommukhin.lvtrainer.dictionary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.artyommukhin.lvtrainer.R
import ru.artyommukhin.lvtrainer.dictionary.data.DictionaryWord
import ru.artyommukhin.lvtrainer.dictionary.data.Language

@Composable
fun DictionaryItem(
    word: DictionaryWord,
    onReset: () -> Unit,
    onDelete: () -> Unit,
) {

  val wordLanguageSymbol = Language(word.wordLanguageTag).symbol

  val translationLanguageSymbol = Language(word.translationLanguageTag).symbol

  Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
        modifier = Modifier.padding(end = 8.dp).weight(1f),
        fontSize = 24.sp,
        text =
            buildAnnotatedString {
              withStyle(SpanStyle(fontSize = 14.sp)) { append(wordLanguageSymbol) }
              append(" ")
              append(word.word)
              append(" - ")
              withStyle(SpanStyle(fontSize = 14.sp)) { append(translationLanguageSymbol) }
              append(" ")
              append(word.translation)
            },
    )

    Text("${word.trainCount * 20}%")

    IconButton(onClick = { onReset() }) {
      Icon(
          painterResource(id = R.drawable.refresh),
          contentDescription = "Reset word progress",
      )
    }

    IconButton(onClick = { onDelete() }) {
      Icon(
          painter = painterResource(id = R.drawable.delete),
          contentDescription = "Delete a word",
      )
    }
  }
}
