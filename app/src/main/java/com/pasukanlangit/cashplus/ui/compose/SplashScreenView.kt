package com.pasukanlangit.cashplus.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pasukanlangit.cashplus.R
import java.lang.Math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun SplashScreenView(
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .gradientBackground(
                    colors = listOf(
                        Color(0XFF12ACE3),
                        Color(0XFF3547F2),
                    ),
                    angle = 315f
                )
                .padding(it)
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon_cashplus),
                contentDescription = null,
                modifier = Modifier
                    .width(265.dp)
                    .align(Alignment.Center)
                    .aspectRatio(18f / 6f),
                contentScale = ContentScale.Fit
            )

            Row(
                modifier = Modifier
                    .padding(
                        start = 26.dp,
                        end = 26.dp,
                        bottom = 42.dp
                    )
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max)
                    .align(Alignment.BottomStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_bi),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )

                Divider(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(color = Color.White)
                )

                Text(
                    text = stringResource(id = R.string.cashplus_licence),
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                    color = Color(0xFFE2E8F0)
                )
            }
        }
    }
}

fun Modifier.gradientBackground(colors: List<Color>, angle: Float) = this.then(
    Modifier.drawBehind {
        val angleRad = angle / 180f * PI
        val x = cos(angleRad).toFloat() //Fractional x
        val y = sin(angleRad).toFloat() //Fractional y

        val radius = sqrt(size.width.pow(2) + size.height.pow(2)) / 2f
        val offset = center + Offset(x * radius, y * radius)

        val exactOffset = Offset(
            x = min(offset.x.coerceAtLeast(0f), size.width),
            y = size.height - min(offset.y.coerceAtLeast(0f), size.height)
        )

        drawRect(
            brush = Brush.linearGradient(
                colors = colors,
                start = Offset(size.width, size.height) - exactOffset,
                end = exactOffset
            ),
            size = size
        )
    }
)

@Preview
@Composable
fun SplashScreenViewPreview() {
    SplashScreenView()
}
