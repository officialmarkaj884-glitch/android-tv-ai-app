package com.example.aitv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AiTvApp() }
    }
}

private data class Show(
    val title: String,
    val genre: String,
    val description: String,
    val color: Color
)

private val catalog = listOf(
    Show("Beyond the Stars", "Sci-fi · Adventure", "A crew follows a mysterious signal to the edge of the galaxy.", Color(0xFF315A9B)),
    Show("The Last Recipe", "Drama · Feel-good", "A young chef rediscovers family and flavor in a small coastal town.", Color(0xFF9A573D)),
    Show("Wild Neighbors", "Nature · Documentary", "A close-up look at the surprising lives thriving in our cities.", Color(0xFF41745B)),
    Show("Laugh Track", "Comedy · Family", "An unlikely group of friends turns everyday mishaps into big laughs.", Color(0xFF8155A5)),
    Show("Deep Current", "Thriller · Mystery", "A marine scientist uncovers a secret hidden beneath the waves.", Color(0xFF257C88)),
    Show("Racing the Sun", "Sports · Documentary", "Athletes push the limits in a race across the desert.", Color(0xFFB17832))
)

@Composable
private fun AiTvApp() {
    var query by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("Tell me what you're in the mood for and I'll find something to watch.") }
    var recommended by remember { mutableStateOf(catalog.take(3)) }
    var selectedShow by remember { mutableStateOf<Show?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    MaterialTheme(colorScheme = darkColorScheme(
        primary = Color(0xFFB9C7FF),
        background = Color(0xFF10131B),
        surface = Color(0xFF1A1F2B)
    )) {
        Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF10131B)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 56.dp, end = 48.dp, top = 36.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("AI TV", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.width(18.dp))
                    Text("Discover something great", fontSize = 18.sp, color = Color(0xFFB9C0D0))
                }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("What do you feel like watching?", fontSize = 25.sp, fontWeight = FontWeight.SemiBold)
                    Text("Ask for a mood, genre, story, or activity — the assistant will suggest a match.", color = Color(0xFFB9C0D0), fontSize = 16.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            label = { Text("Try ‘a funny family movie’") },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { submitQuery(query) { loading, text, shows ->
                                isLoading = loading
                                answer = text
                                recommended = shows
                            } })
                        )
                        Button(
                            onClick = {
                                submitQuery(query) { loading, text, shows ->
                                    isLoading = loading
                                    answer = text
                                    recommended = shows
                                }
                            },
                            enabled = query.isNotBlank() && !isLoading
                        ) { Text(if (isLoading) "Thinking…" else "Ask AI") }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf("Something funny", "Space adventure", "Relaxing nature").forEach { suggestion ->
                            Button(onClick = {
                                query = suggestion
                                submitQuery(suggestion) { loading, text, shows ->
                                    isLoading = loading
                                    answer = text
                                    recommended = shows
                                }
                            }, enabled = !isLoading) { Text(suggestion) }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Your AI picks", fontSize = 23.sp, fontWeight = FontWeight.SemiBold)
                    Text(answer, color = Color(0xFFD4D9E5), fontSize = 16.sp)
                    if (recommended.isEmpty()) {
                        Text("No exact matches yet. Try another mood or genre.", color = Color(0xFFB9C0D0))
                    } else {
                        ShowRow(shows = recommended, onSelect = { selectedShow = it })
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Browse all", fontSize = 23.sp, fontWeight = FontWeight.SemiBold)
                    ShowRow(shows = catalog, onSelect = { selectedShow = it })
                }
            }
        }

        selectedShow?.let { show ->
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { selectedShow = null },
                title = { Text(show.title) },
                text = { Text("${show.genre}\n\n${show.description}\n\nDemo catalog item — connect a licensed streaming catalog to enable playback.") },
                confirmButton = { Button(onClick = { selectedShow = null }) { Text("Close") } },
                containerColor = Color(0xFF1A1F2B)
            )
        }
    }
}

@Composable
private fun ShowRow(shows: List<Show>, onSelect: (Show) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(shows, key = { it.title }) { show ->
            var focused by remember { mutableStateOf(false) }
            Card(
                onClick = { onSelect(show) },
                modifier = Modifier
                    .width(230.dp)
                    .height(150.dp)
                    .onFocusChanged { focused = it.isFocused }
                    .border(
                        BorderStroke(if (focused) 3.dp else 0.dp, if (focused) Color.White else Color.Transparent),
                        RoundedCornerShape(18.dp)
                    )
                    .focusable(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = show.color)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(18.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(show.title, fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.height(5.dp))
                    Text(show.genre, fontSize = 13.sp, color = Color(0xFFE7EAF2))
                }
            }
        }
    }
}

private fun submitQuery(
    text: String,
    onState: (loading: Boolean, answer: String, shows: List<Show>) -> Unit
) {
    if (text.isBlank()) return
    // This lightweight demo keeps the assistant usable without a server; a configured backend
    // can replace these local matches with generated recommendations.
    val matches = localRecommendations(text)
    onState(false, "Here are a few picks based on ‘$text’. Connect an AI backend for conversational recommendations.", matches)
}

private fun localRecommendations(query: String): List<Show> {
    val terms = query.lowercase().split(Regex("[^a-z0-9]+"))
        .filter { it.length > 2 && it !in setOf("something", "watching", "movie", "show") }
    val ranked = catalog.map { show ->
        val searchable = "${show.title} ${show.genre} ${show.description}".lowercase()
        show to terms.count(searchable::contains)
    }.sortedByDescending { it.second }
    val matches = ranked.filter { it.second > 0 }.map { it.first }.take(3)
    return matches.ifEmpty { catalog.shuffled().take(3) }
}
