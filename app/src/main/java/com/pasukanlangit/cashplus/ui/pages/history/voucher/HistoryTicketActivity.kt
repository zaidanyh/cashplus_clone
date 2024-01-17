package com.pasukanlangit.cashplus.ui.pages.history.voucher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView

class HistoryTicketActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myWebView = WebView(this)
        setContentView(myWebView)
//        setContentView(R.layout.activity_history_ticket)
        val urlIntent = intent.getStringExtra(KEY_URL_VOUCHER)

        myWebView.settings.javaScriptEnabled = true

        urlIntent?.let { url ->
            myWebView.loadUrl(url)
        }

    }

    companion object {
        const val KEY_URL_VOUCHER = "KEY_URL_VOUCHER"
    }
}