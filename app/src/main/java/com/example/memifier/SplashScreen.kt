package com.example.memifier

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    Surface(modifier = Modifier.fillMaxSize()) {
        // Imagem do logo
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.skyportugal_logo), // Certifique-se de que o logo está na pasta drawable
                contentDescription = "Logo",
                modifier = Modifier.size(200.dp) // Ajuste o tamanho conforme necessário
            )
        }
    }

    // Navegar para a tela principal após 3 segundos
    LaunchedEffect(Unit) {
        delay(3000) // Tempo de exibição em milissegundos
        navController.navigate("main_screen") // Navegar para a tela principal
    }
}