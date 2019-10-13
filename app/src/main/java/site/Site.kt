package site

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import br.com.icaro.filme.R
import kotlinx.android.synthetic.main.activity_site.*
import utils.BaseActivityKt
import utils.Constantes

/**
 * Created by icaro on 02/08/16.
 */
class Site : BaseActivityKt() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site)
        val url = intent.getStringExtra(Constantes.SITE)

        if (url.contains("https://play.google.com/store/apps/details?id=")) {
            val appPackageName = packageName // getPackageName() from Context or Activity object
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
                finish()
            } catch (e: android.content.ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                finish()
            }
        }

        setWebViewClient(webView!!)

        webView.loadUrl(url)
        configJavascript()

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return false
            }
        }

        swipeToRefresh.setOnRefreshListener {
            webView.reload()
        }

        swipeToRefresh.setColorSchemeResources(R.color.primary, R.color.primary_dark, R.color.accent)
        }

    private fun configJavascript() {
        val webSettings = webView?.settings
        webSettings?.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }

    private fun setWebViewClient(webViewClient: WebView) {
        webViewClient.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
                super.onPageStarted(view, url, favicon)
                if (!swipeToRefresh.isShown) {
                    progress.visibility = View.VISIBLE
                }
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                progress.visibility = View.INVISIBLE
                swipeToRefresh.isRefreshing = false
            }
        }
    }
}
