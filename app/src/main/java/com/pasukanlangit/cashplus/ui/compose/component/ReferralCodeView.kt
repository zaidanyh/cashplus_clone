package com.pasukanlangit.cashplus.ui.compose.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pasukanlangit.cashplus.R

private val textColor = Color(0xFFF8FAFC)
private val borderColor = Color(0xFFE2E8F0)
private val textReferralColor = Color(0XFF475569)
private val textReferralCodeColor = Color(0XFF1E293B)
private val backgroundButton = Color(0xFF0D9488)

@Composable
internal fun ReferralCodeView(
    myReferralCode: String?,
    onBtnCallCsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(10.dp)
    ) {
        Image(
            modifier = Modifier.align(Alignment.TopEnd),
            painter = painterResource(id = R.drawable.ic_decoration_green),
            contentDescription = null
        )
        Image(
            modifier = Modifier.align(Alignment.BottomStart),
            painter = painterResource(id = R.drawable.ic_decoration_green2),
            contentDescription = null
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            Row {
                Image(
                    painter = painterResource(id = R.drawable.ic_referral),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(id = R.string.referral_code),
                    color = textReferralColor,
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                    fontSize = 12.sp
                )
            }

            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = myReferralCode ?: stringResource(id = R.string.not_available),
                color = textReferralCodeColor,
                fontFamily = FontFamily(Font(R.font.poppins_medium)),
                fontSize = 14.sp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    modifier = Modifier
                        .background(backgroundButton, shape = RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .clickable(onClick = onBtnCallCsClick),
                    text = if (myReferralCode.isNullOrEmpty())
                        stringResource(id = R.string.call_cs)
                    else stringResource(id = R.string.invite_friend),
                    color = textColor,
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Preview
@Composable
private fun ReferralCodeViewPreview() {
    ReferralCodeView(
        myReferralCode = "REF143",
        onBtnCallCsClick = {}
    )
}
