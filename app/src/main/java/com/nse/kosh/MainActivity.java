package com.nse.kosh;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    WebView webView;
    ImageView noWifi;
    SwipeRefreshLayout refreshLayout;
    FloatingActionButton cartBtn;

    ProgressBar progressBar;

   // ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar1);


        webView = findViewById(R.id.web_view);
        noWifi = findViewById(R.id.no_wifi);
        cartBtn = findViewById(R.id.cart);
        refreshLayout = findViewById(R.id.refresh_layout);
        webView.setWebViewClient(new Browser());
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().getDisplayZoomControls();
        webView.loadUrl("http://www.kosh-n.com");
       // progressDialog = new ProgressDialog(MainActivity.this);
       // progressDialog.setTitle("تكایه‌ چاوه‌ڕوان به‌");

       // progressDialog.setMessage("تــكــایـه‌ چـــــاوه‌روان بــــه‌");

      //  progressDialog.show();

        if (InternetConnection.checkConnection(MainActivity.this)) {
            noWifi.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
        } else {
            noWifi.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
            Toasty.error(MainActivity.this, "No Internet Connection").show();
        }

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl("http://www.kosh-n.com/cart-2");
            }
        });
    }


    class Browser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);

            return false;
        }

        public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl) {
            try {
                webView.stopLoading();
                noWifi.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);
                refreshLayout.setRefreshing(false);
            } catch (Exception e) {
            }

            if (webView.canGoBack()) {
                webView.goBack();
            }

            webView.loadUrl("about:blank");
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Error");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alertDialog.setIcon(getDrawable(R.drawable.ic_no_wifi));
            }
            alertDialog.setMessage("Check your internet connection and try again.");
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Try Again", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    startActivity(getIntent());
                }
            });

            alertDialog.show();
            super.onReceivedError(webView, errorCode, description, failingUrl);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progressBar.setVisibility(View.VISIBLE);
            super.onPageStarted(view, url, favicon);
        }

        public void onPageFinished(WebView view, String url){

            progressBar.setVisibility(View.GONE);
        }
    }


}
