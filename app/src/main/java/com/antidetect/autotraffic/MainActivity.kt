package com.antidetect.autotraffic

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    
    private val targetUrls = listOf(
        "https://example.com", "https://bing.com", "https://wikipedia.org",
        "https://yahoo.com", "https://duckduckgo.com", "https://archive.org",
        "https://w3schools.com", "https://github.com", "https://wordpress.org", "https://cloudflare.com"
    )

    private var currentUrlIndex = 0
    private var refreshCount = 0
    private val maxRefreshes = 30

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        configureWebView()

        startTrafficAutomation()
    }

    private fun configureWebView() {
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.loadsImagesAutomatically = false

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }
        }
    }

    private fun startTrafficAutomation() {
        if (currentUrlIndex >= targetUrls.size) {
            return
        }

        val url = targetUrls[currentUrlIndex]

        if (refreshCount < maxRefreshes) {
            clearSessionAndSpoof()
            webView.loadUrl(url)
            refreshCount++

            object : CountDownTimer(15000, 1000) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    startTrafficAutomation()
                }
            }.start()

        } else {
            executeMainTask(url)
        }
    }

    private fun executeMainTask(url: String) {
        webView.loadUrl(url)

        object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                refreshCount = 0
                currentUrlIndex++
                startTrafficAutomation()
            }
        }.start()
    }

    private fun clearSessionAndSpoof() {
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
        webView.clearCache(true)
        webView.clearHistory()

        val userAgents = listOf(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36",
            "Mozilla/5.0 (Linux; Android 13; SM-S918B) AppleWebKit/537.36"
        )
        webView.settings.userAgentString = userAgents.random()
    }
}
