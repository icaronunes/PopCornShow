package site

import activity.BaseActivity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import br.com.icaro.filme.R
import kotlinx.android.synthetic.main.activity_site.swipeToRefresh
import kotlinx.android.synthetic.main.activity_site.webView
import utils.Constant
import utils.kotterknife.bindBundle

/**
 * Created by icaro on 02/08/16.
 */
class Site(override var layout: Int = Layout.activity_site) : BaseActivity() {

    val url: String by bindBundle(Constant.SITE, "")

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        setWebViewClient(webView)

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
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                swipeToRefresh.isRefreshing = false
            }
        }
    }
}
