package com.feeleasy.project.sw;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConfirmResi extends AsyncTask<String,Void,String> {
    Context context;

    public ConfirmResi(Context context) {
        this.context = context;
    }

    @Override
    protected void onPostExecute(String result){
        String msg;
        if (result.isEmpty()) {
            msg = "존재하지 않는 거주자입니다.";
        } else {
            String[] uDatas = result.split(",");
            msg = "거주자 확인이 완료되었습니다.";
            JoinActivity.fur = uDatas[1];
            JoinActivity.agree = uDatas[2];
            JoinActivity.related = uDatas[0];
        }
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected String doInBackground(String... arg0) {
        try {
            String Name =  arg0[0];
            String Tel =  arg0[1];

            String link = "http://192.168.35.159/FeelEasy/checkResi.php?name=" + Name + "&tel=" + Tel;
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