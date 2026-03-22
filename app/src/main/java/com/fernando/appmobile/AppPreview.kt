package com.fernando.appmobile

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.fernando.appmobile.ui.theme.AppMobileTheme

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AppPreviewScreen() {
    AppMobileTheme {
        AppScreen(previewOnly = true)
    }
}
