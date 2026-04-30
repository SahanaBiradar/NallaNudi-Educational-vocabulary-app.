package com.example.nallanudiapp

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nallanudiapp.ui.theme.NallaNudiAppTheme

class MainActivity : ComponentActivity() {

    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        tts = TextToSpeech(this) {
            tts.language = java.util.Locale.US
        }

        setContent {
            MyAppUI(tts)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.stop()
        tts.shutdown()
    }
}

@Composable
fun MyAppUI(tts: TextToSpeech) {

    val wordList = wordDatabase

    var text by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var savedWords by remember { mutableStateOf(listOf<String>()) }
    var history by remember { mutableStateOf(listOf<String>()) }
    var selectedSubject by remember { mutableStateOf("All") }

    var flashMode by remember { mutableStateOf(false) }
    var flashIndex by remember { mutableStateOf(0) }

    val wordOfDay = wordList.random()

    NallaNudiAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(20.dp)
            ) {

                Text("Nalla Nudi", style = MaterialTheme.typography.headlineMedium)

                Spacer(modifier = Modifier.height(10.dp))

                Text("Word of the Day: ${wordOfDay.english}")

                Spacer(modifier = Modifier.height(16.dp))

                DropdownMenuBox(selectedSubject) {
                    selectedSubject = it
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Enter word") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ✅ SMART SEARCH LOGIC
                Button(onClick = {

                    val exactMatch = wordList.find {
                        it.english == text.lowercase() &&
                                (selectedSubject == "All" || it.subject == selectedSubject)
                    }

                    val otherSubjectMatch = wordList.find {
                        it.english == text.lowercase() &&
                                it.subject != selectedSubject
                    }

                    result = when {
                        exactMatch != null -> {
                            "Kannada: ${exactMatch.kannada}\n${exactMatch.meaning}"
                        }

                        otherSubjectMatch != null -> {
                            "Word found in ${otherSubjectMatch.subject} subject"
                        }

                        else -> "Word not found"
                    }

                    if (text.isNotEmpty()) {
                        history = listOf(text) + history.take(4)
                    }

                }) {
                    Text("Search")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(onClick = {
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
                }) {
                    Text("🔊 Speak")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(onClick = {
                    if (text.isNotEmpty() && !savedWords.contains(text)) {
                        savedWords = savedWords + text
                    }
                }) {
                    Text("⭐ Save")
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ✅ FLASHCARD BUTTON
                Button(onClick = {
                    flashMode = !flashMode
                }) {
                    Text("Flashcard Mode")
                }

                Spacer(modifier = Modifier.height(20.dp))

                Card {
                    Text(result, modifier = Modifier.padding(10.dp))
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ✅ SMART FLASHCARD
                if (flashMode && savedWords.isNotEmpty()) {

                    val word = savedWords[flashIndex]

                    Card {
                        Text("Flashcard: $word", modifier = Modifier.padding(10.dp))
                    }

                    Row {
                        Button(onClick = {
                            if (flashIndex > 0) flashIndex--
                        }) {
                            Text("Prev")
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Button(onClick = {
                            if (flashIndex < savedWords.size - 1) flashIndex++
                        }) {
                            Text("Next")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text("Search History:")
                history.forEach { Text(it) }

                Spacer(modifier = Modifier.height(20.dp))

                Text("Saved Words:")
                savedWords.forEach { Text("⭐ $it") }
            }
        }
    }
}

@Composable
fun DropdownMenuBox(selected: String, onSelect: (String) -> Unit) {

    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(onClick = { expanded = true }) {
            Text("Subject: $selected")
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            listOf("All", "Science", "Math", "Commerce").forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onSelect(it)
                        expanded = false
                    }
                )
            }
        }
    }
}