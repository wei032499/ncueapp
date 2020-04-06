package com.wei.ncue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.WebView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {


    //static SharedPreferences pref;
    //static String account, password;

    //https://aps.ncue.edu.tw/app/
    static String intentURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SharedPreferences prefPage = getSharedPreferences("Page", MODE_PRIVATE);




        Log.d("mainactivity", "oncreate");


        final BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_view);


        final FragmentManager fragmentManager = getSupportFragmentManager();
        //final Fragment mainFragment = (Fragment) new MainFragment();
        //final Fragment academicFragment = (Fragment) new AcademicFragment();

        final BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.d("bottom", item.toString());
                if (item.getItemId() == R.id.menu_home) {
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    if (mainFragment[1].isAdded())
                        transaction.hide(mainFragment[1]);
                    if (mainFragment[0].isAdded()) { // 如果 affair fragment 已經被 add 過，
                        transaction.show(mainFragment[0]); // 顯示它。
                    } else { // 反之，
                        transaction.add(R.id.fragment_container, mainFragment[0], "HOME"); // 使用 add 方法。
                    }
                    transaction.commit();



                    prefPage.edit()
                            .putInt("pMain", 0)
                            .apply();

                    return true;

                } else if (item.getItemId() == R.id.menu_academic) {
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    if (mainFragment[0].isAdded())
                        transaction.hide(mainFragment[0]);
                    if (mainFragment[1].isAdded()) { // 如果 affair fragment 已經被 add 過，
                        transaction.show(mainFragment[1]); // 顯示它。
                    } else { // 反之，
                        transaction.add(R.id.fragment_container, mainFragment[1], "ACADEMIC"); // 使用 add 方法。

                        /* FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.fragment_container, homeFragment);
                    transaction.commit();*/
                    }
                    transaction.commit();

                    prefPage.edit()
                            .putInt("pMain", 1)
                            .apply();


                    return true;

                }
                return false;
            }
        };
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        bottomNavigationView.getMenu().getItem(prefPage.getInt("pMain", 0)).setChecked(true);
        navigationItemSelectedListener.onNavigationItemSelected(bottomNavigationView.getMenu().getItem(prefPage.getInt("pMain", 0)));








    }

   /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK)
                loadContent();
            else
                finish();
        }


    }*/

    Fragment[] mainFragment = {new AffairFragment(), new AcademicFragment()};



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            SharedPreferences prefPage = getSharedPreferences("Page", MODE_PRIVATE);
            WebviewFragment fragment;
            if (prefPage.getInt("pMain", 0) == 0) {
                AffairFragment m = (AffairFragment) mainFragment[0];
                fragment = (WebviewFragment) m.adapter.mFragmentList.get(prefPage.getInt("pSub0", 0));

            } else {
                AcademicFragment m = (AcademicFragment) mainFragment[1];
                fragment = (WebviewFragment) m.adapter.mFragmentList.get(prefPage.getInt("pSub1", 0));
            }


            WebView webView = fragment.webView;
            Log.d("back", String.valueOf(webView.canGoBack()));
            if (webView.canGoBack()) {
                webView.goBack();

            } else {
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
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
