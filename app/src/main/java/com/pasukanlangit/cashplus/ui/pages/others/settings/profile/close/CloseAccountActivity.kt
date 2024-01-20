package com.pasukanlangit.cashplus.ui.pages.others.settings.profile.close

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color as color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ActivityCloseAccountBinding
import com.pasukanlangit.cashplus.ui.login.LoginActivity
import com.pasukanlangit.id.core.PROFILE_KEY_DATA
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.model.ProfileResponse
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.presentation.components.PositiveButtonAction
import com.pasukanlangit.id.core.utils.parcelable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class CloseAccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCloseAccountBinding
    private val viewModel: CloseAccountViewModel by viewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    private var uuid: String? = null
    private var accessToken: String? = null
    private var stateAlready = false

    private val chooseReason = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val resultData = result?.data?.getStringExtra(ReasonCloseAccountActivity.REASON_RESULT_KEY)
            binding.apply {
                wrapperResult.isVisible = resultData?.isNotEmpty() == true
                if (!resultData.isNullOrEmpty()) {
                    stateAlready = true
                    tvReason.text = resultData
                    btnNext.text = getString(R.string.close_account_now)
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCloseAccountBinding.inflate(layoutInflater)
//        setContentView(binding.root)
        val data = intent.parcelable<ProfileResponse>(PROFILE_KEY_DATA)
        setContent {
            CloseAccountView(modifier = Modifier.fillMaxSize(), data = data)
        }

        uuid = sharedPref.getUUID()
        accessToken = sharedPref.getAccessToken()
//        intent.parcelable<ProfileResponse>(PROFILE_KEY_DATA)?.let { data ->
//            binding.apply {
//                appBar.setOnBackPressed { finish() }
//                tvNameProfile.text = data.full_name
//                tvEmailPhoneProfile.text = if (data.email.isEmpty()) data.phones?.first()?.phone
//                else getString(R.string.email_phone_profile_format, data.email.lowercase(), data.phones?.first()?.phone)
//
//                val imgUrl = data.img_url.ifEmpty {
//                    "https://ui-avatars.com/api/?name=${data.full_name}&size=300&rounded=true&background=0A57FF&color=FFFFFF&bold=true"
//                }
//                Glide.with(this@CloseAccountActivity)
//                    .load(imgUrl)
//                    .thumbnail(
//                        Glide.with(this@CloseAccountActivity)
//                            .load(R.raw.image_loading_state)
//                    )
//                    .error(R.drawable.ic_empty)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .skipMemoryCache(true)
//                    .into(ivProfile)
//
//                wrapperResult.setOnClickListener {
//                    if (wrapperResult.isVisible) {
//                        chooseReason.launch(
//                            Intent(this@CloseAccountActivity, ReasonCloseAccountActivity::class.java).apply {
//                                putExtra(ReasonCloseAccountActivity.REASON_RESULT_KEY, tvReason.text)
//                            }
//                        )
//                    }
//                }
//                btnNext.setOnClickListener {
//                    if (stateAlready) {
//                        val reason = tvReason.text.toString()
//                        GenericModalDialogCashplus.Builder()
//                            .title(getString(R.string.confirm))
//                            .image(R.drawable.illustration_login_error)
//                            .description(getString(R.string.confirm_close_account))
//                            .buttonPositive(
//                                PositiveButtonAction(
//                                    btnLabel = getString(R.string.yes_close_account),
//                                    backgroundButton = R.drawable.bg_primary_rounded_20,
//                                    buttonTextColor = Color.parseColor("#FFFFFF"),
//                                    onBtnClicked = {
//                                        viewModel.deleteAccount(uuid.toString(), reason, accessToken.toString())
//                                    }
//                                )
//                            )
//                            .buttonNegative(
//                                NegativeButtonAction(
//                                    btnLabel = getString(R.string.cancel),
//                                    backgroundButton = R.drawable.bg_transparent_border_primary_rounded_20,
//                                    buttonTextColor = Color.parseColor("#0A57FF"),
//                                    setClickOnDismiss = true
//                                )
//                            )
//                            .show(supportFragmentManager)
//                        return@setOnClickListener
//                    }
//                    chooseReason.launch(Intent(this@CloseAccountActivity, ReasonCloseAccountActivity::class.java))
//                }
//            }
//        }

        collectData()
    }

    private fun collectData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.deleteAccountLoading.collectLatest {
                        binding.apply {
                            progressBarLoading.isVisible = it
                            btnNext.isEnabled = !it
                            appBar.isEnabled = !it
                        }
                    }
                }
                launch {
                    viewModel.deleteAccount.collectLatest { response ->
                        if (response != null) {
                            sharedPref.deleteAuth()
                            sharedPref.deleteProfile()
                            GenericModalDialogCashplus.Builder()
                                .title(getString(R.string.close_account_success))
                                .image(R.drawable.illustration_bye)
                                .description(getString(R.string.acknowledgments))
                                .setIsCanClose(isCanClose = false)
                                .buttonPositive(
                                    PositiveButtonAction(
                                        btnLabel = getString(R.string.sign_out),
                                        backgroundButton = R.drawable.bg_primary_rounded_20,
                                        buttonTextColor = Color.parseColor("#FFFFFF"),
                                        onBtnClicked = {
                                            finishAffinity()
                                            val intent = Intent(this@CloseAccountActivity, LoginActivity::class.java).apply {
                                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            }
                                            startActivity(intent)
                                        }
                                    )
                                )
                                .show(supportFragmentManager)
                        }
                    }
                }
                launch {
                    viewModel.deleteAccountError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(this@CloseAccountActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.removeMessageError()
                        }
                    }
                }
            }
        }
    }
    @Composable
    private fun CloseAccountView(modifier: Modifier = Modifier, data: ProfileResponse? = null) {
        Scaffold(modifier = modifier) {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(it)) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(color = color(0xFF0A57FF)),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clickable { finish() }
                        )
                        Text(
                            text = stringResource(id = R.string.close_account),
                            modifier = Modifier.fillMaxWidth(),
                            fontFamily = FontFamily(Font(R.font.poppins_medium)),
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            color = color(0XFFFFFFFF)
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
                                    color = color(0XFFEFF8FF),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .border(
                                    BorderStroke(1.dp, color(0XFFD1E9FF)),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val imgUrl = data?.img_url?.ifEmpty {
                                "https://ui-avatars.com/api/?name=${data.full_name}&size=300&rounded=true&background=0A57FF&color=FFFFFF&bold=true"
                            }
                            LoadImage(url = imgUrl.toString())
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 8.dp)
                            ) {
                                Text(
                                    text = data?.full_name.toString(),
                                    fontFamily = FontFamily(Font(R.font.poppins_medium)),
                                    fontSize = 14.sp,
                                    color = color(0XFF1E293B)
                                )
                                Text(
                                    text = if (data?.email.isNullOrEmpty()) data?.phones?.first()?.phone.toString()
                                    else stringResource(id = R.string.email_phone_profile_format, data?.email.toString().lowercase(), data?.phones?.first()?.phone.toString()),
                                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                                    fontSize = 12.sp,
                                    color = color(0XFF334155)
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
                        color = color(0XFF334155)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 8.dp, end = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Canvas(modifier = Modifier.size(6.dp), onDraw = {
                            drawCircle(color = color(0XFF475569))
                        })
                        Text(
                            text = stringResource(id = R.string.note_close_account_1 ),
                            modifier = Modifier.padding(start = 12.dp),
                            color = color(0XFF334155), fontSize = 12.sp,
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
                            drawCircle(color = color(0XFF475569))
                        })
                        Text(
                            text = stringResource(id = R.string.note_close_account_2),
                            modifier = Modifier.padding(start = 12.dp),
                            color = color(0XFF334155), fontSize = 12.sp,
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
                            drawCircle(color = color(0XFF475569))
                        })
                        Text(
                            text = stringResource(id = R.string.note_close_account_3),
                            modifier = Modifier.padding(start = 12.dp),
                            color = color(0XFF334155), fontSize = 12.sp,
                            fontFamily = FontFamily(Font(R.font.poppins_regular))
                        )
                    }
                    Box(
                        modifier = Modifier
                            .alpha(1f)
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = color(0XFFF8FAFC),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .border(
                                    BorderStroke(1.dp, color(0XFFF1F5F9)),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.reason_close_account),
                                color = color(0XFF1E293B), fontSize = 14.sp,
                                fontFamily = FontFamily(Font(R.font.poppins_medium))
                            )
                            Text(
                                text = "Ketidaksesuaian denga kebutuhan bisnis",
                                modifier = Modifier.padding(top = 2.dp),
                                color = color(0XFF3C4D5F), fontSize = 12.sp,
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
                    colors = ButtonDefaults.buttonColors(containerColor = color(0xFF0A57FF))
                ) {
                    Text(
                        text = stringResource(id = R.string.next),
                        fontSize = 13.sp,
                        fontFamily = FontFamily(Font(R.font.poppins_medium)),
                        textAlign = TextAlign.Center,
                        color = color(0xFFF8FAFC)
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    private fun LoadImage(url: String) {
        GlideImage(
            model = url, contentDescription = "image_user",
            modifier = Modifier
                .width(48.dp)
                .height(48.dp)
                .clip(CircleShape)
        ) { glide ->
            glide.error(R.drawable.ic_empty).load(url)
        }
    }

    @Preview
    @Composable
    private fun CloseAccountPreview() {
        CloseAccountView()
    }
}