package com.fernando.appmobile

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fernando.appmobile.performance.RealtimeMetrics
import com.fernando.appmobile.performance.RealtimeMonitor
import com.fernando.appmobile.performance.BackgroundLoadAnalyzer
import com.fernando.appmobile.performance.DeviceOptimizationNavigator
import com.fernando.appmobile.performance.PerformanceHintController
import com.fernando.appmobile.profile.GameProfile
import com.fernando.appmobile.profile.GameProfilesRepository
import com.fernando.appmobile.reminder.ReminderScheduler
import com.fernando.appmobile.terms.TermsAcceptanceCard
import com.fernando.appmobile.ui.theme.AppMobileTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppMobileTheme {
                AppScreen(previewOnly = false)
            }
        }
    }
}

@Composable
fun AppScreen(previewOnly: Boolean) {
    val isPreview = previewOnly || LocalInspectionMode.current
    val context = LocalContext.current
    val analyzer = remember(isPreview) {
        if (isPreview) null else BackgroundLoadAnalyzer(context)
    }
    val navigator = remember(isPreview) {
        if (isPreview) null else DeviceOptimizationNavigator(context)
    }
    val hintController = remember(isPreview) {
        if (isPreview) null else PerformanceHintController(context)
    }
    val realtimeMonitor = remember(isPreview) {
        if (isPreview) null else RealtimeMonitor(context)
    }
    val reminderScheduler = remember(isPreview) {
        if (isPreview) null else ReminderScheduler(context)
    }

    var heavyApps by remember { mutableStateOf<List<String>>(emptyList()) }
    var metrics by remember {
        mutableStateOf(
            RealtimeMetrics(
                systemCpuPercent = 0,
                usedRamMb = 0,
                freeRamMb = 0,
                batteryTempC = null
            )
        )
    }
    val profiles = remember { GameProfilesRepository.profiles }
    var selectedProfile by remember { mutableStateOf<GameProfile?>(null) }
    var includedGamePackageIds by rememberSaveable { mutableStateOf(listOf<String>()) }
    var showGamePicker by remember { mutableStateOf(false) }
    var systemSafetyModeEnabled by rememberSaveable { mutableStateOf(true) }
    var reminderMinutes by remember { mutableStateOf(10L) }
    var statusMessage by remember {
        mutableStateOf("Modo seguranca ativo. Pronto para iniciar uma sessao de desempenho.")
    }

    LaunchedEffect(isPreview) {
        heavyApps = if (isPreview) {
            listOf("Chrome", "WhatsApp", "Instagram")
        } else {
            analyzer?.findHeavyRecentApps().orEmpty()
        }
    }

    LaunchedEffect(realtimeMonitor, isPreview) {
        if (isPreview) {
            metrics = RealtimeMetrics(
                systemCpuPercent = 36,
                usedRamMb = 4720,
                freeRamMb = 1890,
                batteryTempC = 39.2f
            )
        } else {
            while (true) {
                metrics = realtimeMonitor?.readSnapshot() ?: metrics
                delay(1000)
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF0D1B2A), Color(0xFF1B263B), Color(0xFF415A77))
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "FPS Booster Assistant",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Objetivo principal: aumentar FPS do celular durante jogos incluidos no app.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFD7E3FC)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xB3000000))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Instalar no celular",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text("1. Instale o APK no Android.", color = Color(0xFFD7E3FC))
                    Text("2. Abra o app e selecione o jogo que deseja incluir.", color = Color(0xFFD7E3FC))
                    Text("3. Sempre que for jogar, confirme o jogo selecionado antes de iniciar.", color = Color(0xFFD7E3FC))
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xB3000000))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Modo seguranca do sistema",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = if (systemSafetyModeEnabled) {
                            "Ativo: aplica bloqueios preventivos antes de iniciar a otimizacao."
                        } else {
                            "Desativado: executa fluxo direto de otimizacao."
                        },
                        color = Color(0xFFD7E3FC)
                    )
                    Button(
                        onClick = {
                            systemSafetyModeEnabled = !systemSafetyModeEnabled
                            statusMessage = if (systemSafetyModeEnabled) {
                                "Modo seguranca ativado."
                            } else {
                                "Modo seguranca desativado."
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (systemSafetyModeEnabled) "Desativar modo seguranca" else "Ativar modo seguranca")
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xB3000000))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Status da sessão",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = statusMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFD7E3FC)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {
                            val currentProfile = selectedProfile
                            val hasIncludedGames = includedGamePackageIds.isNotEmpty()
                            val isCurrentGameIncluded = currentProfile != null && includedGamePackageIds.contains(currentProfile.packageId)
                            val tempTooHigh = (metrics.batteryTempC ?: 0f) >= 42f

                            if (!hasIncludedGames) {
                                showGamePicker = true
                                statusMessage = "Inclua um jogo para o app aplicar otimizacoes de FPS."
                            } else if (!isCurrentGameIncluded) {
                                showGamePicker = true
                                statusMessage = "Selecione um jogo incluido antes de iniciar."
                            } else if (systemSafetyModeEnabled && tempTooHigh) {
                                statusMessage = "Modo seguranca bloqueou inicio: temperatura alta (${metrics.batteryTempC}C)."
                            } else {
                                val started = hintController?.startGamingSession() ?: true
                                statusMessage = if (started) {
                                    "Otimizacao de FPS ativa para ${currentProfile.name}."
                                } else {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                        "Sistema não disponibilizou sessão de performance."
                                    } else {
                                        "API de performance requer Android 12+ (API 31)."
                                    }
                                }
                            }
                        }) {
                            Text("Iniciar")
                        }

                        Button(onClick = {
                            hintController?.stopGamingSession()
                            statusMessage = "Sessão finalizada."
                        }) {
                            Text("Finalizar")
                        }
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xB3000000))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Monitor em tempo real",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = "CPU sistema: ${metrics.systemCpuPercent}%",
                        color = Color(0xFFD7E3FC)
                    )
                    Text(
                        text = "RAM usada: ${metrics.usedRamMb} MB",
                        color = Color(0xFFD7E3FC)
                    )
                    Text(
                        text = "RAM livre: ${metrics.freeRamMb} MB",
                        color = Color(0xFFD7E3FC)
                    )
                    Text(
                        text = "Temp. bateria: ${metrics.batteryTempC?.let { "${it}°C" } ?: "indisponivel"}",
                        color = Color(0xFFD7E3FC)
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xB3000000))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Apps pesados recentes",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (heavyApps.isEmpty()) {
                        Text(
                            text = "Conceda acesso de uso para identificar consumo recente.",
                            color = Color(0xFFD7E3FC)
                        )
                    } else {
                        HeavyAppsList(items = heavyApps)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = {
                        heavyApps = analyzer?.findHeavyRecentApps().orEmpty()
                    }) {
                        Text("Atualizar lista")
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xB3000000))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Jogos incluidos",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Button(
                        onClick = { showGamePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val gameLabel = selectedProfile?.name ?: "Incluir/selecionar jogo para funcionar"
                        Text(gameLabel)
                    }
                    IncludedGamesList(
                        profiles = profiles,
                        includedGamePackageIds = includedGamePackageIds
                    )
                    ProfileChooser(
                        profiles = profiles,
                        selected = selectedProfile,
                        onSelect = {
                            selectedProfile = it
                            if (!includedGamePackageIds.contains(it.packageId)) {
                                includedGamePackageIds = includedGamePackageIds + it.packageId
                            }
                            statusMessage = "Jogo incluido e ativo: ${it.name}."
                        }
                    )
                    selectedProfile?.let { RecommendationsList(profile = it) }

                    Text(
                        text = "Lembrete pre-jogo",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    ReminderChooser(
                        selectedMinutes = reminderMinutes,
                        onSelect = { reminderMinutes = it }
                    )
                    Button(
                        onClick = {
                            reminderScheduler?.schedulePreGameReminder(reminderMinutes)
                            statusMessage = "Lembrete agendado para ${reminderMinutes} min."
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Agendar lembrete")
                    }

                    Text(
                        text = "Atalhos de otimização",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Button(onClick = { navigator?.openUsageAccessSettings() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Conceder acesso de uso")
                    }
                    Button(onClick = { navigator?.openBatteryOptimizationSettings() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Configurar bateria e modo desempenho")
                    }
                    Button(onClick = { navigator?.openDisplaySettings() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Ajustar taxa de atualização")
                    }
                    Button(onClick = { navigator?.openNotificationSettings() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Configurar notificações")
                    }
                }
            }

            TermsAcceptanceCard(modifier = Modifier.fillMaxWidth())

            if (showGamePicker) {
                GamePickerDialog(
                    profiles = profiles,
                    onDismiss = { showGamePicker = false },
                    onSelected = {
                        if (!includedGamePackageIds.contains(it.packageId)) {
                            includedGamePackageIds = includedGamePackageIds + it.packageId
                        }
                        selectedProfile = it
                        showGamePicker = false
                        statusMessage = "Jogo incluido e ativo: ${it.name}."
                    }
                )
            }
        }
    }
}

@Composable
private fun IncludedGamesList(
    profiles: List<GameProfile>,
    includedGamePackageIds: List<String>
) {
    if (includedGamePackageIds.isEmpty()) {
        Text(
            text = "Nenhum jogo incluido ainda.",
            color = Color(0xFFFFC857),
            style = MaterialTheme.typography.bodySmall
        )
        return
    }

    val includedNames = profiles
        .filter { includedGamePackageIds.contains(it.packageId) }
        .map { it.name }

    Text(
        text = "Incluidos: ${includedNames.joinToString(separator = ", ")}",
        color = Color(0xFFD7E3FC),
        style = MaterialTheme.typography.bodySmall
    )
}

@Composable
private fun HeavyAppsList(items: List<String>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.height(130.dp)) {
        items(items) { appName ->
            Text(
                text = "• $appName",
                color = Color(0xFFD7E3FC),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ProfileChooser(
    profiles: List<GameProfile>,
    selected: GameProfile?,
    onSelect: (GameProfile) -> Unit
) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        profiles.forEach { profile ->
            val selectedColor = if (selected != null && profile.packageId == selected.packageId) Color(0xFF2A9D8F) else Color(0xFF374151)
            Surface(color = selectedColor, shape = MaterialTheme.shapes.medium) {
                Button(onClick = { onSelect(profile) }) {
                    Text(profile.name)
                }
            }
        }
    }
}

@Composable
private fun GamePickerDialog(
    profiles: List<GameProfile>,
    onDismiss: () -> Unit,
    onSelected: (GameProfile) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Qual jogo incluir?") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Selecione o jogo para o app funcionar durante a partida.")
                profiles.forEach { profile ->
                    Button(onClick = { onSelected(profile) }, modifier = Modifier.fillMaxWidth()) {
                        Text(profile.name)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fechar")
            }
        }
    )
}

@Composable
private fun RecommendationsList(profile: GameProfile) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Sugestoes para ${profile.name}", color = Color.White, fontWeight = FontWeight.SemiBold)
        profile.recommendations.forEach { item ->
            Text("- $item", color = Color(0xFFD7E3FC), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun ReminderChooser(
    selectedMinutes: Long,
    onSelect: (Long) -> Unit
) {
    val options = listOf(5L, 10L, 15L)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { value ->
            val label = "${value}m"
            Button(onClick = { onSelect(value) }) {
                val marker = if (selectedMinutes == value) "* " else ""
                Text("$marker$label")
            }
        }
    }
}
