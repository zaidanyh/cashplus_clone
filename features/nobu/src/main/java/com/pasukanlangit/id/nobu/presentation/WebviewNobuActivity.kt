package com.pasukanlangit.id.nobu.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.nobu.databinding.ActivityWebviewNobuBinding

class WebviewNobuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebviewNobuBinding
    private var stateExit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        binding = ActivityWebviewNobuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { return }
        })

        val url = intent.getStringExtra(URL_WEBVIEW_KEY).toString().replace("\\/", "/")
        with(binding) {
            btnExit.setOnClickListener { onEventExit() }
            webViewNobu.loadUrl(url)
            with(webViewNobu.settings) {
                javaScriptEnabled = true
                domStorageEnabled = true
                loadsImagesAutomatically = true
                // webview performance
                webViewNobu.setLayerType(View.LAYER_TYPE_HARDWARE, null)
                cacheMode = WebSettings.LOAD_DEFAULT
            }
            webViewNobu.webViewClient = object: WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    Log.d("WebView", "shouldOverrideUrl : ${request?.url}")
                    val urlD = request?.url

                    if (urlD.toString().contains(Constants.deepLinkPrefix)) {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = urlD
                        }
                        startActivity(intent)
                        finish()
                    }
                    return true
                }
            }

            webViewNobu.webChromeClient = object: WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    progressBar.isVisible = true
                    stateExit = false
                    if (newProgress == 100) {
                        progressBar.isVisible = false
                        title = view?.title
                        stateExit = true
                    }
                    super.onProgressChanged(view, newProgress)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.webViewNobu.saveState(outState)
    }

    private fun onEventExit() {
        if (!stateExit) return
        else finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.webViewNobu.clearCache(true)
        binding.webViewNobu.clearHistory()
    }

    companion object {
        const val URL_WEBVIEW_KEY = "url_webview_key"
    }
}