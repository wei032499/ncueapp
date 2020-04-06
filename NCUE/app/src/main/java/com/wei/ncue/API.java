package com.wei.ncue;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.CookieManager;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

public class API {
    abstract static class Login {
        Login(String account, String password) {
            new loginAsync().execute(account, password);
        }

        abstract void complete(int result);

        class loginAsync extends AsyncTask<String, Void, Integer> {


            @Override
            protected Integer doInBackground(String... strings) {
                Connection.Response res = null;
                try {
                    res = Jsoup.connect("https://aps.ncue.edu.tw/app/sess_student.php")
                            .data("p_usr", strings[0])
                            .data("p_pwd", strings[1])
                            .method(Connection.Method.POST)
                            .execute();
                    Document doc = res.parse();
                    String result = doc.toString();
                    if (result.contains("認證成功")) {
                        CookieManager cookieManager = CookieManager.getInstance();
                        cookieManager.setAcceptCookie(true);
                        cookieManager.setCookie("aps.ncue.edu.tw", "PHPSESSID=" + res.cookie("PHPSESSID"));
                        return 0;
                    } else if (result.contains("帳號密碼錯誤"))
                        return 1;
                    else
                        return 2;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return 2;
            }

            @Override
            protected void onPostExecute(Integer result) {
                super.onPostExecute(result);
                complete(result);
            }
        }
    }


    static abstract class Update {
        Context context;

        static int ERROR = -2;
        static int IO_ERROR = -1;
        static int UPDATED = 0;
        static int NEED_UPDATE = 1;

        Update(Context context) {
            this.context = context;
            new Update.updateAsync().execute();
        }

        class updateAsync extends AsyncTask<Void, Void, Integer> {



            String updateLog = "";


            /*return updated or not*/
            @Override
            protected Integer doInBackground(Void... voids) {
                try {
                    /*Document doc = Jsoup.connect("https://raw.githubusercontent.com/wei032499/ncueapp/master/apk/latest/version")
                            .get();*/

                    Document doc = Jsoup.connect("https://raw.githubusercontent.com/wei032499/ncueapp/" + BuildConfig.FLAVOR + "/apk/latest/version")
                            .get();
                    Log.d("version", doc.getElementById("veriosn").html());
                    Float latestV = Float.valueOf(doc.getElementById("veriosn").html());
                    updateLog = doc.getElementById("log").html();
                    updateLog = updateLog.replace("\\n", "\n");
                    PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                    String version = pInfo.versionName;
                    SharedPreferences prefUpdate = context.getSharedPreferences("Update",MODE_PRIVATE);
                    if(prefUpdate.getFloat("version",0)<latestV)
                    {
                        prefUpdate.edit()
                                .putFloat("version",latestV)
                                .putBoolean("downloaded",false)
                                .putString("log",updateLog)
                                .apply();
                    }
                    if (latestV > Float.parseFloat(version))
                        return NEED_UPDATE;
                    else
                        return UPDATED;//updated


                } catch (IOException e) {
                    e.printStackTrace();
                    return IO_ERROR;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                return ERROR;
            }

            @Override
            protected void onPostExecute(Integer responseCode) {
                super.onPostExecute(responseCode);
                SharedPreferences prefUpdate = context.getSharedPreferences("Update",MODE_PRIVATE);
                if(responseCode==NEED_UPDATE)
                {
                    if(prefUpdate.getBoolean("downloaded",false))
                    {
                        UpdateApp.installNormal(context,prefUpdate.getString("path",""));
                        return;
                    }
                    else
                        UpdateApp.downloadBySelf(context, "https://raw.githubusercontent.com/wei032499/ncueapp/" + BuildConfig.FLAVOR + "/apk/latest/ncue_by_wei.apk", "ncue_by_wei", updateLog);

                }

                updateChecked(responseCode);


            }
        }


        abstract void updateChecked(int responseCode);
    }
}
