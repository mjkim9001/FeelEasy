package com.feeleasy.project.sw;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class HomeFragment extends Fragment {
    View v;

    static String uId;

    ListView listView;
    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    HashMap<String, String> item;
    SimpleAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home, container, false);

        uId = MainActivity.userDatas[0];

        TextView tv = v.findViewById(R.id.txtIntro);
        tv.setText(MainActivity.userDatas[6] + "님 안녕하세요.");

        listView = v.findViewById(R.id.listWarn);
        adapter = new SimpleAdapter(getContext(), list, android.R.layout.simple_list_item_2,
                new String[]{"content", "time"}, new int[]{android.R.id.text1, android.R.id.text2});
        listView.setAdapter(adapter);

        SelectWarn task = new SelectWarn();
        if (MainActivity.userDatas[1].equals("P")) {
            task.execute(MainActivity.userDatas[4]);
        } else {
            task.execute(uId);
        }

        return v;
    }

    public class SelectWarn extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String result) {
            if (!result.isEmpty()) {
                String[] warns = result.split(",");
                String content = "";

                for (int i=0; i<warns.length; i++) {
                    String[] warn = warns[i].split("#");
                    switch (warn[1]) {
                        case "light":
                            content = "오늘 전등을 한 번도 사용하지 않으셨네요!";
                            break;
                        case "fur":
                            content = "오늘 가구를 한 번도 사용하지 않으셨네요!";
                            break;
                    }
                    item = new HashMap<>();
                    item.put("content", content);
                    item.put("time", warn[0]);
                    list.add(item);
                }
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        protected String doInBackground(String... arg0) {
            String UId =  arg0[0];

            try {
                String link = "http://192.168.35.159/FeelEasy/getWarn.php?uId=" + UId;
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

                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

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
}