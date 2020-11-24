package com.feeleasy.project.sw;

import android.app.Notification;
import android.app.NotificationManager;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CheckUsage extends AsyncTask<String,Void,String> {
    NotificationManager manager;
    Notification notif;

    public CheckUsage(NotificationManager manager, Notification notif) {
        this.manager = manager;
        this.notif = notif;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result.contains("success")) {
            manager.notify( 778 , notif);
        }
    }

    @Override
    protected String doInBackground(String... arg0) {
        try {
            String UId =  arg0[0];

            String link = "http://192.168.35.159/FeelEasy/putWarn.php?uId=" + UId;
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