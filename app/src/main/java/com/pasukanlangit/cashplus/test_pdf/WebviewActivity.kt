package com.pasukanlangit.cashplus.test_pdf

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity

class WebviewActivity: AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myWebView = WebView(this)
        myWebView.settings.javaScriptEnabled = true
        myWebView.loadUrl("http://10.9.81.62:3000/print/transportation?data={%20%22date%22:%20%221%20Desember%202021%22,%20%22departure_date%22:%20%2221%20Desember%202021%22,%20%22departure_time%22:%2206:00%20-%2007:15%22,%20%22booking_code%22:%20%228875996421%22,%20%22transport_name%22:%20%22KM.KELUD%22,%20%22transport_number%22:%20%22NP-119-B10%22,%20%22transport_class%22:%20%22EKONOMI%22,%20%22transport_rute%22:%20%22Tanjung%20Priok,%20Jakarta-Batu%20Ampar,%20Batam%20(Tanjung%20Priok,%20Jakarta-Kijang,%20Bintan-Batu%20Ampar,%20Batam)%22,%20%22print_category%22:%20%22KAPAL%22,%20%22passengers%22:%20[%20{%20%22name%22%20:%20%22Andika%20Pruawna%20Nawsaga%22,%20%22id%22%20:%20%2212412312%22,%20%22title%22%20:%20%22Mr%22,%20%22type%22:%20%22Adult%22,%20%22seat%22:%20%224%22,%20%22gerbong%22%20:%20%223%22%20},%20{%20%22name%22%20:%20%22dsfsadf%22,%20%22id%22%20:%20%2212412312%22,%20%22title%22%20:%20%22Mr%22,%20%22type%22:%20%22Adult%22,%20%22seat%22:%20%22%22,%20%22gerbong%22%20:%20%22%22%20},%20{%20%22name%22%20:%20%22Andika%20Pruawna%20Nawsaga%22,%20%22id%22%20:%20%2212412312%22,%20%22title%22%20:%20%22Mr%22,%20%22type%22:%20%22Adult%22,%20%22seat%22:%20%22%22,%20%22gerbong%22%20:%20%22%22%20},%20{%20%22name%22%20:%20%22Dava%20Mohammad%20Hamka%20Alamat%22,%20%22id%22%20:%20%2212412312%22,%20%22title%22%20:%20%22Mr%22,%20%22type%22:%20%22Adult%22,%20%22seat%22:%20%22%22,%20%22gerbong%22%20:%20%22%22%20}%20]%20}")
        setContentView(myWebView)

    }
}