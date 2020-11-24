package com.feeleasy.project.sw;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateDatas extends AsyncTask<String,Void,String> {
    Context context;

    public UpdateDatas(Context context) {
        this.context = context;
    }

    @Override
    protected void onPostExecute(String s) {
        Toast.makeText(context, "데이터가 수집되었습니다!", Toast.LENGTH_LONG).show();
    }

    @Override
    protected String doInBackground(String... arg0) {
        String Table =  arg0[0];
        String UId =  arg0[1];
        String Content =  arg0[2];

        try {
            String link = "http://192.168.35.159/FeelEasy/putDatas.php?table=" + Table
                    + "&uId=" + UId + "&content=" + Content;
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
            } else {
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