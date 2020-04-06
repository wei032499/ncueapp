package com.wei.ncue;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;

public class StartupActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intentF = getIntent();
        MainActivity.intentURL = intentF.getDataString();
        if (MainActivity.intentURL != null) {
            String[] s = MainActivity.intentURL.split("/");
            String p = s[s.length - 1];
            if (p.contains("index"))
                p = s[s.length - 2];
            if (p.equals("sign_up"))
                p = "signup.php";
            MainActivity.intentURL = "https://aps.ncue.edu.tw/app/" + p;
            SharedPreferences prefPage = getSharedPreferences("Page", MODE_PRIVATE);
            prefPage.edit()
                    .putInt("pMain", 0)
                    .putInt("pSub0", 1)
                    .apply();
            Log.d("intent", MainActivity.intentURL);
        }


        setContentView(R.layout.activity_startup);


        new API.Update(this) {

            @Override
            void updateChecked(int responseCode) {
                if (responseCode == UPDATED) {
                    SharedPreferences pref = getSharedPreferences("Account", MODE_PRIVATE);
                    String account = pref.getString("account", "");
                    String password = pref.getString("password", "");
                    new API.Login(account, password) {
                        @Override
                        void complete(int result) {
                            if (result == 0) //logined
                            {
                                Intent intent = new Intent(StartupActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(StartupActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    };
                } else if (responseCode == NEED_UPDATE) //wait for download and update
                {
                    Toast.makeText(context, "發現新版本，下載中...", Toast.LENGTH_LONG).show();
                    ProgressBar progressBar = findViewById(R.id.progressStartup);
                    progressBar.bringToFront();
                    progressBar.setVisibility(View.VISIBLE);

                } else if (responseCode == IO_ERROR) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("錯誤")
                            .setMessage("請確認網路連接狀況！")
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            });

                    builder.create()
                            .show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("錯誤")
                            .setMessage("請稍後再試！")
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            });

                    builder.create()
                            .show();
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
