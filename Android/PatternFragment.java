package com.feeleasy.project.sw;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class PatternFragment extends Fragment {
    View v;

    LineChart chartLight, chartFur;
    ArrayList<Entry> entriesLight, entriesFur;
    ArrayList<String> labels;

    String joinDate = MainActivity.userDatas[5].substring(0, 10);  //시간은 제외

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_pattern, container, false);

        chartLight = v.findViewById(R.id.chartLight);
        chartFur = v.findViewById(R.id.chartFur);

        entriesLight = new ArrayList<>();
        entriesFur = new ArrayList<>();
        labels = new ArrayList<>();

        SelectCounts task = new SelectCounts();
        if (MainActivity.userDatas[1].equals("P")) {  //보호자의 경우 거주자의 아이디 전달
            task.execute(MainActivity.userDatas[4]);
        } else {
            task.execute(HomeFragment.uId);
        }

        return v;
    }

    public void setValues(LineChart chart, ArrayList<Entry> entries, String label) {
        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        dataSet.setValueTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //그래프 x축에 라벨 달기
        setLabels();

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return labels.get((int) value);
            }
        };
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);

        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(false);

        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setGranularity(1f);

        LineData data = new LineData(dataSet);
        chart.setData(data);
        chart.animateX(2500);
        chart.invalidate();
    }

    //전등, 가구 사용 횟수를 조회하는 클래스
    public class SelectCounts extends AsyncTask<String,Void,String> {

        @Override
        protected void onPostExecute(String result) {
            try {
                String[] datas = result.split(" ");
                String[] lights = datas[0].split(",");  //요일별 전등 켜진 횟수
                String[] furs = datas[1].split(",");  //요일별 가구 사용 횟수

                int day;
                if (Integer.parseInt(calcDate(joinDate)) < 7) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(joinDate));
                    day = calendar.get(Calendar.DAY_OF_WEEK);
                } else {
                    day = (Calendar.getInstance()).get(Calendar.DAY_OF_WEEK);  //1(일)~7(토) 사이 값 반환
                }
                int index = day - 1;
                for (int i=0; i<7; i++) {
                    entriesLight.add(new Entry(i, Integer.parseInt(lights[index])));
                    if (index == (lights.length-1))
                        index = -1;
                    index++;
                }
                //조회한 데이터를 바탕으로 그래프 좌표 설정
                setValues(chartLight, entriesLight, "전등 켜진 횟수");

                for (int i=0; i<7; i++) {
                    entriesFur.add(new Entry(i, Integer.parseInt(furs[index])));
                    if (index == (lights.length-1))
                        index = -1;
                    index++;
                }
                setValues(chartFur, entriesFur, "가구 사용 횟수");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                String UId =  arg0[0];

                String link = "http://192.168.35.159/FeelEasy/getCounts.php?uId=" + UId;
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

    public void setLabels() {
        Date date = new Date(System.currentTimeMillis());  //현재 시간 얻기
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd");
        if (Integer.parseInt(calcDate(joinDate)) >= 7) {  //가입날로부터 7일 후가 되면
            for (int i = 0; i < 7; i++) {
                labels.add(sdf2.format(date));
                date = getNextDate(date);
            }
        } else {
            try {
                date = sdf1.parse(joinDate);
                for (int i = 0; i < 7; i++) {
                    labels.add(sdf2.format(date));
                    date = getNextDate(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    //다음 날짜 구하는 함수
    public Date getNextDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, 1);
        return cal.getTime();
    }

    public String calcDate(String date) {
        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date recent = sdf.parse(date);
            long duration = (now.getTime() - recent.getTime()) / (60000*60*24);  //날짜 차이 계산

            return String.valueOf(duration);
        } catch (ParseException e) {
            return e.getMessage();
        }
    }
}