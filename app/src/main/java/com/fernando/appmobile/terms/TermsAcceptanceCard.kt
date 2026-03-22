package com.fernando.appmobile.terms

import android.graphics.Color as AndroidColor
import android.widget.TextView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp

@Composable
fun TermsAcceptanceCard(modifier: Modifier = Modifier) {
    var reachedBottom by rememberSaveable { mutableStateOf(false) }
    var accepted by rememberSaveable { mutableStateOf(false) }
    var confirmed by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xB3000000))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Termos de Uso",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )

            Text(
                text = "Role ate o final para habilitar o aceite.",
                color = Color(0xFFD7E3FC),
                style = MaterialTheme.typography.bodySmall
            )

            val listener = remember {
                { isBottom: Boolean ->
                    if (isBottom) {
                        reachedBottom = true
                    }
                }
            }

            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                factory = { context ->
                    val scrollView = ScrollView(context).apply {
                        setPadding(12, 12, 12, 12)
                    }
                    val textView = TextView(context).apply {
                        text = TermsOfUseText
                        setTextColor(AndroidColor.WHITE)
                        textSize = 14f
                        setLineSpacing(0f, 1.2f)
                    }
                    scrollView.addView(textView)
                    scrollView.setOnBottomReachedChanged(listener)
                    scrollView
                },
                update = { view ->
                    view.setOnBottomReachedChanged(listener)
                }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Checkbox(
                    checked = accepted,
                    onCheckedChange = { checked ->
                        accepted = checked
                        if (!checked) {
                            confirmed = false
                        }
                    },
                    enabled = reachedBottom
                )
                Text(
                    text = "Aceito os Termos",
                    color = if (reachedBottom) Color.White else Color(0xFF9CA3AF),
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            if (!reachedBottom) {
                Text(
                    text = "Checkbox bloqueado ate atingir o fim do texto.",
                    color = Color(0xFFFFC857),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = { confirmed = true },
                enabled = reachedBottom && accepted,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar cadastro")
            }

            if (confirmed) {
                Text(
                    text = "Cadastro confirmado com aceite dos Termos de Uso.",
                    color = Color(0xFF8DEB95),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
