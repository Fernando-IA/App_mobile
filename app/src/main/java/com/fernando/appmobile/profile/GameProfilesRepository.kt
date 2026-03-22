package com.fernando.appmobile.profile

object GameProfilesRepository {

    val profiles: List<GameProfile> = listOf(
        GameProfile(
            name = "PUBG Mobile",
            packageId = "com.tencent.ig",
            recommendations = listOf(
                "Ative 90/120 Hz na tela antes de abrir o jogo.",
                "Mantenha brilho entre 50% e 75% para reduzir aquecimento.",
                "Feche apps de vídeo e navegador em segundo plano.",
                "Evite jogar carregando em carregadores lentos/quentes."
            )
        ),
        GameProfile(
            name = "Call of Duty Mobile",
            packageId = "com.activision.callofduty.shooter",
            recommendations = listOf(
                "Priorize FPS alto e textura média no jogo.",
                "Ative modo desempenho do fabricante se existir.",
                "Desative sincronização pesada de nuvem durante a partida.",
                "Use rede 5 GHz para menor oscilação de frame-time."
            )
        ),
        GameProfile(
            name = "Free Fire",
            packageId = "com.dts.freefireth",
            recommendations = listOf(
                "Limite apps de chat em sobreposição durante a partida.",
                "Mantenha armazenamento com folga para evitar stutter.",
                "Use taxa de atualização alta com sensibilidade ajustada.",
                "Reinicie a sessão de jogo se houver aquecimento progressivo."
            )
        )
    )
}
