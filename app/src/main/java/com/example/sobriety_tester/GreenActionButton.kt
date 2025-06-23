package com.example.sobriety_tester

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sobriety_tester.ui.theme.GreenPrimary

/**
 * GreenActionButton is a reusable composable that creates a green action button
 * with a specified text and click action. It is styled to fit the sobriety tester app's theme.
 *
 * @param text The text to be displayed on the button.
 * @param onClick The action to be performed when the button is clicked.
 * @param modifier Optional modifier to customize the button's appearance and layout.
 */
@Composable
fun GreenActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = GreenPrimary,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        // elevation to give the button a raised effect
        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 6.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}
