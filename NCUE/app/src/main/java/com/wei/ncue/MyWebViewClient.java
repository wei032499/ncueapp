package com.wei.ncue;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import static android.content.Context.MODE_PRIVATE;

public class MyWebViewClient extends WebViewClient {

    boolean first = true;
    String oriURL;
    ProgressBar progressBar;
    SharedPreferences prefAccount;


    public MyWebViewClient(String url, ProgressBar p,SharedPreferences prefAccount) {
        oriURL = url;
        progressBar = p;
        this.prefAccount = prefAccount;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        progressBar.setVisibility(View.GONE);
        Log.d("finished",url);

        if (url.equals(oriURL))
            view.clearHistory();
        if(url.equals("https://aps.ncue.edu.tw/app/sess_student.php"))
        {
            view.loadUrl(oriURL);

        }
        else if(url.contains("nam.ncue.edu.tw"))
        {

            view.loadUrl("javascript:$('form')[0][0].value='"+prefAccount.getString("account","")+"';$('form')[0][1].value='"+prefAccount.getString("password","")+"';check();");
        }

    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        Log.d("startLoad",url);
        progressBar.bringToFront();
        progressBar.setVisibility(View.VISIBLE);
    }


}
