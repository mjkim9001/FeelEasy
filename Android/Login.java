package com.feeleasy.project.sw;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class Login extends AsyncTask<String,Void,String> {
    Context context;
    String name;
    String tel;

    public Login(Context context, String name, String tel) {
        this.context = context;
        this.name = name;
        this.tel = tel;
    }

    @Override
    protected void onPostExecute(String result){
        try {
            if (result.isEmpty()) {
                Toast.makeText(context, "존재하지 않는 사용자입니다.", Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences auto = context.getSharedPreferences("AutoLogin", Activity.MODE_PRIVATE);
                SharedPreferences.Editor autoLogin = auto.edit();
                autoLogin.putString("UserName", name);
                autoLogin.putString("UserTel", tel);
                autoLogin.commit();

                //로그인과 동시에 전등, 가구 사용 검사 시작
                context.startService(new Intent(context, CheckService.class));

                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("UserData", result);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected String doInBackground(String... arg0) {
        try {
            String Name =  arg0[0];
            String Tel =  arg0[1];

            String link = "http://192.168.35.159/FeelEasy/login.php?name=" + Name + "&tel=" + Tel;
            URL url = new URL(link);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();

            int responseStatusCode = httpURLConnection.getResponseCode();

            InputStream inputStream;
            if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
            }
            else{
                inputStream = httpURLConnection.getErrorStream();
            }

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader in = new BufferedReader(inputStreamReader);

            StringBuffer sb = new StringBuffer("");
            String line = "";

            while ((line = in.readLine()) != null) {
                sb.append(line);
                break;
            }
            in.close();
            return sb.toString();
        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());
        }
    }
}