package com.feeleasy.project.sw;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class InsertUser extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        try {
            String Type = params[0];
            String Name = params[1];
            String Tel = params[2];
            String Fur = params[3];
            String Agree = params[4];
            String Related = params[5];

            String link = "http://192.168.35.159/FeelEasy/putUser.php";
            String data = URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(Type, "UTF-8");
            data += "&" + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(Name, "UTF-8");
            data += "&" + URLEncoder.encode("tel", "UTF-8") + "=" + URLEncoder.encode(Tel, "UTF-8");
            data += "&" + URLEncoder.encode("fur", "UTF-8") + "=" + URLEncoder.encode(Fur, "UTF-8");
            data += "&" + URLEncoder.encode("agree", "UTF-8") + "=" + URLEncoder.encode(Agree, "UTF-8");
            data += "&" + URLEncoder.encode("related", "UTF-8") + "=" + URLEncoder.encode(Related, "UTF-8");

            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
                break;
            }
            return sb.toString();
        } catch (Exception e) {
            return new String("Join Exception: " + e.getMessage());
        }
    }

}