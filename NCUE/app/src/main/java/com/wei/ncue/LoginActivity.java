package com.wei.ncue;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {


    SharedPreferences pref;
    EditText accountEdit;
    EditText passwordEdit;
    ContentLoadingProgressBar progressBar;
    TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = getSharedPreferences("Account", MODE_PRIVATE);

        accountEdit = findViewById(R.id.editText);
        passwordEdit = findViewById(R.id.editText2);
        progressBar = findViewById(R.id.progress);
        message = findViewById(R.id.message);

        String a = pref.getString("account", "");
        String p = pref.getString("password", "");
        if (!a.equals(""))
            accountEdit.setText(a);
        if (!p.equals(""))
            passwordEdit.setText(p);

    }

    public void login(View view) {
        message.setText("");
        progressBar.show();
        final String account = accountEdit.getText().toString();
        final String password = passwordEdit.getText().toString();
        new API.Login(account, password) {
            @Override
            void complete(int result) {
                if (result == 0) {
                    pref.edit()
                            .putString("account", account)
                            .putString("password", password)
                            .apply();
                    //setResult(RESULT_OK);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else if (result == 1) {
                    pref.edit()
                            .putString("password", "")
                            .apply();
                    progressBar.hide();
                    message.setText("帳號或密碼錯誤！");
                } else {
                    progressBar.hide();
                    message.setText("錯誤，請稍後再試！");
                }
            }
        };
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("結束程式？")
                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // FIRE ZE MISSILES!
                            //setResult(RESULT_CANCELED);
                            finish();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            builder.create()
                    .show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
