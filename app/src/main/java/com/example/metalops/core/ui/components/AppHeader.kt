package com.example.metalops.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.metalops.R
import com.example.metalops.ui.agente.theme.MetalOpsTheme

/**
 * Header estándar: MetalOps + logo juntos y centrados
 */
@Composable
fun AppHeader(
    modifier: Modifier = Modifier,
    title: String = "MetalOps"
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        // Texto
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontSize = 22.sp // más pequeño
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Logo pequeño
        Image(
            painter = painterResource(id = R.drawable.logo_metalops),
            contentDescription = "MetalOps Logo",
            modifier = Modifier.size(26.dp) // más pequeño
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppHeaderPreview() {
    MetalOpsTheme {
        AppHeader()
    }
}
