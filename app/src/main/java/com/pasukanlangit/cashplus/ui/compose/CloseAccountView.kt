package com.pasukanlangit.cashplus.ui.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pasukanlangit.cashplus.R

@Composable
fun CloseAccountView(
    modifier: Modifier = Modifier,
    visibility: Boolean = false
) {
    Scaffold(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(Color(0XFF0A57FF)),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = null,
                        modifier = Modifier.padding(start = 8.dp)
                            .clickable {

                            }
                    )
                    Text(
                        text = stringResource(id = R.string.close_account),
                        modifier = Modifier.fillMaxWidth(),
                        fontFamily = FontFamily(Font(R.font.poppins_medium)),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = Color(0XFFFFFFFF)
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 20.dp, end = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0XFFEFF8FF),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                BorderStroke(1.dp, Color(0XFFD1E9FF)),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier
                                .width(48.dp)
                                .height(48.dp),
                            painter = painterResource(id = R.drawable.dummy_onboarding),
                            contentDescription = null
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp)
                        ) {
                            Text(
                                text = "Zinedine Zidane",
                                fontFamily = FontFamily(Font(R.font.poppins_medium)),
                                fontSize = 14.sp,
                                color = Color(0XFF1E293B)
                            )
                            Text(
                                text = "zinedinezdn10@gmail.com",
                                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                                fontSize = 12.sp,
                                color = Color(0XFF334155)
                            )
                        }
                    }
                }
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 12.dp, end = 16.dp),
                    text = stringResource(id = R.string.things_to_note),
                    fontFamily = FontFamily(Font(R.font.poppins_medium)),
                    fontSize = 13.sp,
                    color = Color(0XFF334155)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 8.dp, end = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Canvas(modifier = Modifier.size(6.dp), onDraw = {
                        drawCircle(color = Color(0XFF475569))
                    })
                    Text(
                        text = stringResource(id = R.string.note_close_account_1 ),
                        modifier = Modifier.padding(start = 12.dp),
                        color = Color(0XFF334155), fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.poppins_regular))
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 6.dp, end = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Canvas(modifier = Modifier.size(6.dp), onDraw = {
                        drawCircle(color = Color(0XFF475569))
                    })
                    Text(
                        text = stringResource(id = R.string.note_close_account_2),
                        modifier = Modifier.padding(start = 12.dp),
                        color = Color(0XFF334155), fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.poppins_regular))
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 6.dp, end = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Canvas(modifier = Modifier.size(6.dp), onDraw = {
                        drawCircle(color = Color(0XFF475569))
                    })
                    Text(
                        text = stringResource(id = R.string.note_close_account_3),
                        modifier = Modifier.padding(start = 12.dp),
                        color = Color(0XFF334155), fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.poppins_regular))
                    )
                }
                Box(
                    modifier = Modifier
                        .alpha(if (visibility) 1f else 0f)
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0XFFF8FAFC),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                BorderStroke(1.dp, Color(0XFFF1F5F9)),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.reason_close_account),
                            color = Color(0XFF1E293B), fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.poppins_medium))
                        )
                        Text(
                            text = "Ketidaksesuaian denga kebutuhan bisnis",
                            modifier = Modifier.padding(top = 2.dp),
                            color = Color(0XFF3C4D5F), fontSize = 12.sp,
                            fontFamily = FontFamily(Font(R.font.poppins_regular))
                        )
                    }
                }
            }
            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 38.dp)
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A57FF))
            ) {
                Text(
                    text = stringResource(id = R.string.next),
                    fontSize = 13.sp,
                    fontFamily = FontFamily(Font(R.font.poppins_medium)),
                    textAlign = TextAlign.Center,
                    color = Color(0xFFF8FAFC)
                )
            }
        }
    }
}

@Preview
@Composable
fun CloseAccountViewPreview() {
    CloseAccountView()
}