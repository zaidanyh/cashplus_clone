package com.pasukanlangit.id.core.presentation.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pasukanlangit.id.core.R

private val backgroundColor = Color(0xFF0A57FF)
private val textColor = Color(0xFFF8FAFC)

@Composable
internal fun GeneralAppBarView(
    title: String,
    onButtonBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(color = backgroundColor),
        contentAlignment = Alignment.CenterStart
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_arrow_back),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 8.dp)
                .clickable(onClick = onButtonBackClick)
        )
        Text(
            text = title,
            modifier = Modifier.fillMaxWidth(),
            fontFamily = FontFamily(Font(R.font.poppins_medium)),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = textColor
        )
    }
}

@Preview
@Composable
private fun GeneralAppBarViewPreview() {
    GeneralAppBarView(title = "Judul", onButtonBackClick = {})
}
