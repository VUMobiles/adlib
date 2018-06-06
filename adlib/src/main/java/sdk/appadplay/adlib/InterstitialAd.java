package sdk.appadplay.adlib;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;

public class InterstitialAd extends AppCompatActivity {

    public static String myPublisherId;
    WebView mWebView;
    String adUrl, clickUrl, logoUrl, logoClickUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();

        adUrl = intent.getStringExtra("adUrl");
        clickUrl = intent.getStringExtra("clickUrl");
        logoUrl = intent.getStringExtra("logoUrl");
        logoClickUrl = intent.getStringExtra("logoClickUrl");

        loadAd();
    }

    @SuppressLint("ResourceType")
    public void loadAd(){

        final RelativeLayout adLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams subLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);


        adLayout.setBackgroundColor(Color.BLACK);
//                FrameLayout frameLayout = new FrameLayout(context);
//                FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        Button btnClose = new Button(this);
        btnClose.setBackgroundResource(R.drawable.btnclose);
        btnClose.setId(2);
        RelativeLayout.LayoutParams btnCloseParams = new RelativeLayout.LayoutParams(40, 40);
        btnCloseParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        mWebView = new WebView(this);
        RelativeLayout.LayoutParams webViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        webViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        webViewParams.addRule(RelativeLayout.CENTER_VERTICAL);

//        webViewParams.addRule(RelativeLayout.BELOW, btnClose.getId());
        mWebView.setBackgroundColor(Color.BLACK);


        String UserAgent = (new WebView(this)).getSettings().getUserAgentString();
        String userAgent = UserAgent.replaceAll(" ", "%20");
        String bannerAdUrl = adUrl;
        Log.d("HtmlAdUrl", bannerAdUrl);

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("Urlllll", url);

                if (url.contains("android_asset")) {
                    return false;
                } else if (!url.contains("http://")) {
                    Log.d("Error", "No url found");
                }
                try {

                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // Do something on page loading started
                // Visible the progressbar
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // Do something when page loading finished
                Log.d("something", url);
                view.loadUrl("javascript:(function() { document.getElementsByTagName('video')[0].play(); })()");
            }

        });

        /*
            WebView
                A View that displays web pages. This class is the basis upon which you can roll your
                own web browser or simply display some online content within your Activity. It uses
                the WebKit rendering engine to display web pages and includes methods to navigate
                forward and backward through a history, zoom in and out, perform text searches and more.

            WebChromeClient
                 WebChromeClient is called when something that might impact a browser UI happens,
                 for instance, progress updates and JavaScript alerts are sent here.
        */
        mWebView.setWebChromeClient(new WebChromeClient() {
            /*
                public void onProgressChanged (WebView view, int newProgress)
                    Tell the host application the current progress of loading a page.

                Parameters
                    view : The WebView that initiated the callback.
                    newProgress : Current page loading progress, represented by an integer
                        between 0 and 100.
            */
            public void onProgressChanged(WebView view, int newProgress) {
                // Update the progress bar with page loading progress
                Log.d("progress", String.valueOf(newProgress) + "%");
                //mProgressBar.setProgress(newProgress);
            }
        });

        mWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(clickUrl));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                return true;
            }
        });


        // Enable the javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        // Render the web page
        mWebView.loadUrl(bannerAdUrl);

//                frameLayout.addView(mWebView, webViewParams);
//                frameLayout.addView(btnClose, btnCloseParams);
        adLayout.addView(mWebView, webViewParams);
        adLayout.addView(btnClose, btnCloseParams);
        setContentView(adLayout, subLayoutParams);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                mWebView.destroy();
                adLayout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mWebView.destroy();
        finish();
    }
}
