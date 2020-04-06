package com.wei.ncue;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

public class WebviewFragment extends Fragment {

    String url;
    String intentURL = null;
    ProgressBar progressBar;

    public WebviewFragment(String s, String intent) {
        Log.d("viewFragment", "NEW");
        url = s;
        intentURL = intent;
    }

    WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("viewFragment", ".");
        View rootView = inflater.inflate(R.layout.fragment_webview, container, false);
        progressBar = rootView.findViewById(R.id.progressWebview);
        /*TextView textView = (TextView) rootView.findViewById(R.id.txt_label);
        textView.setText("ViewFragment1");*/
        webView = rootView.findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //webSettings.setBlockNetworkImage(true);
        SharedPreferences prefAccount = getContext().getSharedPreferences("Account", MODE_PRIVATE);
        webView.setWebViewClient(new MyWebViewClient(url,progressBar,prefAccount));

        if (webView == null)
            Log.d("webview", "NULL???");
        else Log.d("webview", "NULLXXX");

        init();


        return rootView;
    }

    void init() {
        if (webView != null) {
            webView.loadUrl(url);
            if (intentURL != null)
            {
                webView.loadUrl(intentURL);
                //webView.loadUrl("javascript:window.location.href='"+intentURL+"'");
                Log.d("intentURL",intentURL);
                intentURL = null;
            }
            Log.d("webview", url);
        } else {
            Log.d("webview", "null");
        }
    }

}
