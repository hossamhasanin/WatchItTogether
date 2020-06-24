package com.hossam.hasanin.watchittogeter

import android.content.Context
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.webkit.*
import android.widget.Toast
//import org.jsoup.Jsoup
//import org.jsoup.nodes.Document
import java.lang.Exception

class WebviewTestCase {
    fun test(){
//        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )
//
//        val webview = WebView(this)
//        setContentView(R.layout.activity_main)

//        val webSettings = webview.settings
//        webSettings.javaScriptEnabled = true
//        webSettings.setSupportZoom(false)
//        webSettings.allowFileAccess = true
//        webSettings.javaScriptCanOpenWindowsAutomatically = false
//
//        webview.webViewClient = object : WebViewClient() {
//            override fun shouldInterceptRequest(
//                view: WebView?,
//                request: WebResourceRequest?
//            ): WebResourceResponse? {
//                Log.v("koko" , request?.url.toString())
//
//                try {
//                    if(request?.url?.lastPathSegment!!.contains("mp4")) {
//                        Log.v("koko" , "video")
//
//                        //view!!.loadUrl(request.url.toString())
//
//                    }
//                }catch (e: Exception){}
//                return super.shouldInterceptRequest(view, request)
//            }
//
//            override fun shouldOverrideUrlLoading(
//                view: WebView?,
//                url: String
//            ): Boolean {
//                return if (!url.contains("vid")){
//                    view!!.loadUrl("https://w.egy.best/watch/?v=ZLLLZfGCNruBfsZLnXHLnTerXLXppHZnZLZofiforIqDZLnLLLZoBVLZLnLZeeeLCruvCruEsvZuqsZnZLZavrlZLnLZSGBXLaljXZrBLnrHSUjXLLZLNnZLZruPLZLnLZXHLXnLppXnrLppLXLLpHNXNLZnZLZruvLZLnLZFerMHdrfavrNALZnZLZvuSZLnLZKruvopBUSCLrUCrfUvLZLN&h=a98679e9cafcd7dc68811615d4b85c72")
//                    false
//                } else true
//            }
//        }

        //webview.addJavascriptInterface(WebScriptInterface(this), "App")

        //This will load the webpage that we want to see

        //This will load the webpage that we want to see
        //webview.loadUrl("https://w.egy.best/watch/?v=ZLLLZfGCNruBfsZLnXHLnTerXLXppHZnZLZofiforIqDZLnLLLZoBVLZLnLZeeeLCruvCruEsvZuqsZnZLZavrlZLnLZSGBXLaljXZrBLnrHSUjXLLZLNnZLZruPLZLnLZXHLXnLppXnrLppLXLLpHNXNLZnZLZruvLZLnLZFerMHdrfavrNALZnZLZvuSZLnLZKruvopBUSCLrUCrfUvLZLN&h=a98679e9cafcd7dc68811615d4b85c72");

    }

    class WebScriptInterface(val context: Context) {

        @JavascriptInterface
        fun showToast(s: Int) {
            Toast.makeText(context , "Video playing $s" , Toast.LENGTH_LONG).show()
            Log.v("koko", "jiji $s")
        }
    }

//    class Scrap {
//        var c = 0
//        fun scrap(s: String?){
//            try {
//                Log.v("koko" , "km,nm,")
//                val doc: Document = Jsoup.connect("https://w.egy.best/movie/julie-julia-2009$s")
//                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
//                    .get()
//                val s = doc.select(".dls_table tbody tr").last()
//                val g = s.select(".tar a").first()
//                val h = g.attr("data-url")
//                //Log.v("koko" , doc.toString())
//                Log.v("koko" , h)
//                Log.v("koko" , g.toString())
//                if (c < 2){
//                    c += 1
//                    scrap(h)
//                } else {
//                    val v: Document = Jsoup.connect("https://w.egy.best$h")
//                        .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
//                        .get()
//                    val l = v.select(".mainbody div p").last()
//                    Log.v("koko" , v.toString())
//                }
//
//            } catch (e:Exception){
//                e.printStackTrace()
//            }
//
//        }
//    }

}