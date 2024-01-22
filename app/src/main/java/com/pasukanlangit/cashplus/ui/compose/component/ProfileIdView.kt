package com.pasukanlangit.cashplus.ui.compose.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pasukanlangit.cashplus.R

private val backgroundColor = Color(0xFFE2E8F0)
private val textColorIdLabel = Color(0XFF475569)
private val textColorId = Color(0XFF1E293B)

@Composable
internal fun ProfileIdView(
    id: String,
    onBtnCopyClick: () -> Unit,
    onBtnShareClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(10.dp)
    ) {
        Image(
            modifier = Modifier.align(Alignment.TopEnd),
            painter = painterResource(id = R.drawable.ic_decoration_primary),
            contentDescription = null
        )

        Image(
            modifier = Modifier.align(Alignment.BottomStart),
            painter = painterResource(id = R.drawable.ic_decoration_primary2),
            contentDescription = null
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            Row {
                Image(
                    painter = painterResource(id = R.drawable.ic_user_octagon),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = stringResource(id = R.string.id_anda),
                    color = textColorIdLabel,
                    fontSize = 12.sp
                )
            }

            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = id,
                color = textColorId,
                fontSize = 14.sp
            )

            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Image(
                    modifier = Modifier.clickable(onClick = onBtnCopyClick),
                    painter = painterResource(id = R.drawable.ic_copy_blue),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(6.dp))

                Image(
                    modifier = Modifier.clickable(onClick = onBtnShareClick),
                    painter = painterResource(id = R.drawable.icon_share_blue),
                    contentDescription = null
                )
            }
        }
    }
}

@Preview
@Composable
private fun ProfileIdViewPreview() {
    ProfileIdView(
        id = "081221312412321",
        onBtnCopyClick = {},
        onBtnShareClick = {}
    )
}
