# App_mobile

Aplicativo Android (Kotlin + Jetpack Compose) para melhorar estabilidade de FPS por meio de otimização do aparelho, sem modificar arquivos de jogos.

O objetivo principal e aumentar o FPS do celular quando o usuario estiver jogando titulos incluidos no aplicativo.

## Objetivo

Este projeto foca em 3 frentes seguras:

1. Reduzir impacto de apps em segundo plano (identificando apps com maior uso recente).
2. Ativar sessão de prioridade de performance com API oficial do Android (`PerformanceHintManager`) quando disponível.
3. Guiar o usuário para configurações de sistema que influenciam FPS (acesso de uso, bateria e tela).
4. Aplicar o fluxo de uso somente para jogos incluidos/selecionados no app.

## Aviso Importante

- O app **nao** altera arquivos internos do PUBG Mobile (ou de qualquer jogo).
- Alterar engine/config interna de jogos pode violar Termos de Servico e causar banimento.
- O foco aqui e otimizar o dispositivo, nao modificar o jogo.

## Estrutura

- `app/src/main/java/com/fernando/appmobile/MainActivity.kt`
	- Tela principal com status de sessao, lista de apps pesados e atalhos de configuracao.
- `app/src/main/java/com/fernando/appmobile/performance/PerformanceHintController.kt`
	- Gerencia sessao de performance (`PerformanceHintManager`) em Android 12+.
- `app/src/main/java/com/fernando/appmobile/performance/BackgroundLoadAnalyzer.kt`
	- Usa `UsageStatsManager` para listar apps com maior uso recente.
- `app/src/main/java/com/fernando/appmobile/performance/DeviceOptimizationNavigator.kt`
	- Abre telas de configuracao do Android para ajuste manual.
- `app/src/main/java/com/fernando/appmobile/performance/RealtimeMonitor.kt`
	- Coleta CPU do sistema, RAM usada/livre e temperatura da bateria em tempo real.
- `app/src/main/java/com/fernando/appmobile/profile/GameProfilesRepository.kt`
	- Define perfis de jogo com recomendacoes praticas por titulo.
- `app/src/main/java/com/fernando/appmobile/reminder/ReminderScheduler.kt`
	- Agenda lembrete pre-jogo com WorkManager.

## Como usar

1. Abra no Android Studio.
2. Sincronize o Gradle.
3. Rode em dispositivo Android 10+.
4. No app:
	 - toque em `Conceder acesso de uso` para habilitar analise de uso.
	 - toque em `Iniciar` para iniciar sessao de desempenho (Android 12+).
	 - acompanhe CPU, RAM e temperatura no bloco de monitor em tempo real.
	 - selecione um perfil de jogo para ver recomendacoes.
	 - agende lembrete pre-jogo (5, 10 ou 15 min).
	 - use os atalhos para ajustar bateria, taxa de atualizacao e notificacoes.

## Limites Tecnicos Reais

- Apps comuns no Android nao podem matar processos arbitrarios de outros apps em versoes recentes.
- Ganho de FPS depende de SoC, temperatura, limite termico e configuracoes do fabricante.
- Em alguns aparelhos, o impacto principal vem de reduzir throttling termico e limitar apps paralelos.

## Proximos passos sugeridos

1. Substituir textos do monitor por graficos (linha temporal de CPU e temperatura).
2. Salvar perfil favorito e historico de sessoes localmente (DataStore).
3. Incluir benchmark rapido antes/depois para estimar ganho de estabilidade.
