package com.feeleasy.project.sw;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SelectDatas extends AsyncTask<String,Void,String> {
    Context context;
    ArrayList<String> activities;
    ArrayAdapter<String> adapter;

    public SelectDatas(Context context, ArrayList<String> activities, ArrayAdapter<String> adapter) {
        this.context = context;
        this.activities = activities;
        this.adapter = adapter;
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            String[] datas = result.split("#");

            TextView tvLight = ((Activity) context).findViewById(R.id.txtUseLight);
            TextView tvFur = ((Activity) context).findViewById(R.id.txtUseFur);
            String[] date = datas[0].split(",");  //최근 전등, 가구 사용한 시각

            if (!date[0].equals("@")) {
                tvLight.setText("전등을 사용한 지 " + calcTime(date[0]) + "시간째");
            }
            if (!date[1].equals("@")) {
                tvLight.setText("전등을 사용한 지 " + calcTime(date[1]) + "시간째");
            }
            if (!date[2].equals("@")) {
                tvFur.setText(MainActivity.userDatas[2] + "을/를 사용한 지 " + calcTime(date[2]) + "시간째");
            }
            if (!date[0].equals("@") && !date[1].equals("@")) {
                int on = Integer.parseInt(calcTime(date[0]));
                int off = Integer.parseInt(calcTime(date[1]));
                if (on > off) {
                    tvLight.setText("전등을 사용한 지 " + String.valueOf(off) + "시간째");
                } else {
                    tvLight.setText("전등을 사용한 지 " + String.valueOf(on) + "시간째");
                }
            }

            if (datas.length == 2) {  //최근활동이 있는 경우
                String[] acts = datas[1].split(",");
                for (int i=0; i<acts.length; i++) {
                    activities.add(acts[i]);
                }
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected String doInBackground(String... arg0) {
        String UId =  arg0[0];

        try {
            String link = "http://192.168.35.159/FeelEasy/getDatas.php?uId=" + UId;
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

    public String calcTime(String date) {
        Date now = new Date(System.currentTimeMillis());  //현재 시간 얻기
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            Date recent = sdf.parse(date);
            long duration = (now.getTime() - recent.getTime()) / (60000*60);  //시간 차이 계산

            return String.valueOf(duration);
        } catch (ParseException e) {
            return e.getMessage();
        }
    }
}