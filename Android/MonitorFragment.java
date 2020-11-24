package com.feeleasy.project.sw;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class MonitorFragment extends Fragment {
    View v;

    static final int REQUEST_ENABLE_BT = 10;
    BluetoothAdapter bluetoothAdapter;
    int pairedDeviceCount = 0;
    Set<BluetoothDevice> devices;
    BluetoothDevice device;
    BluetoothSocket socket = null;
    OutputStream oStream = null;
    InputStream iStream = null;

    Thread thread = null;
    char charDelimiter = '\n';
    byte[] readBuffer;
    int readBufferPosition;

    Switch sw;
    TextView tvLight, tvFur;
    ListView lvRecent;

    ArrayList<String> activities;
    ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_monitor, container, false);

        sw = v.findViewById(R.id.swOut);
        tvLight = v.findViewById(R.id.txtUseLight);
        tvFur = v.findViewById(R.id.txtUseFur);
        lvRecent = v.findViewById(R.id.listRecent);

        String myFur = tvFur.getText().toString();
        tvFur.setText(myFur.replace("가구", MainActivity.userDatas[2]));

        activities = new ArrayList<>();
        adapter = new ArrayAdapter<>(MonitorFragment.this.getContext(), android.R.layout.simple_list_item_1, activities);
        lvRecent.setAdapter(adapter);

        sw.setChecked(getPreferenceBoolean("switch"));
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String msg;
                Intent intent = new Intent(getContext(), OutService.class);
                if (b) {
                    msg = "외출모드가 켜졌습니다.";
                    (getActivity()).startService(intent);
                } else {
                    msg = "외출모드가 꺼졌습니다.";
                    (getActivity()).stopService(intent);
                }
                setPreference("switch", b);
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });

        SelectDatas task = new SelectDatas(getContext(), activities, adapter);
        if (MainActivity.userDatas[1].equals("P")) {  //보호자의 경우 거주자의 아이디 전달
            task.execute(MainActivity.userDatas[4]);
        } else {
            task.execute(HomeFragment.uId);
        }

        checkBluetooth();

        return v;
    }

    public void setPreference(String key, boolean value){
        SharedPreferences pref = getActivity().getSharedPreferences("SwitchPreference", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getPreferenceBoolean(String key){
        SharedPreferences pref = getActivity().getSharedPreferences("SwitchPreference", MODE_PRIVATE);
        return pref.getBoolean(key, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {  //블루투스가 활성 상태인 경우
                    selectDevice();
                } else if (resultCode == RESULT_CANCELED) {  //블루투스가 비활성 상태인 경우
                    getActivity().finish();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    void checkBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {  //장치가 블루투스를 지원하지 않는 경우
            getActivity().finish();
        } else {
            if (!bluetoothAdapter.isEnabled()) {  //블루투스를 지원하지만 비활성 상태인 경우
                //블루투스를 활성 상태로 바꾸기 위해 사용자 동의 요청
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, REQUEST_ENABLE_BT);
            } else {
                selectDevice();  //페어링된 기기 목록을 보여주고 연결할 장치 선택
            }
        }
    }

    void selectDevice() {
        devices = bluetoothAdapter.getBondedDevices();
        pairedDeviceCount = devices.size();

        if (pairedDeviceCount == 0) {  //페어링된 장치가 없는 경우
            getActivity().finish();
        }

        for (BluetoothDevice device : devices) {
            String arduino = device.getName();
            if (arduino.equals("HC-06")) {
                connectToSelectedDevice(arduino);
            }
        }
    }

    void beginListenForData() {
        final Handler handler = new Handler();

        readBuffer = new byte[1024];  //수신 버퍼
        readBufferPosition = 0;  //버퍼 내 수신 문자 저장 위치

        //문자열 수신 쓰레드
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        int bytesAvailable = iStream.available();
                        if (bytesAvailable > 0) {  //데이터가 수신된 경우
                            byte[] packetBytes = new byte[bytesAvailable];
                            iStream.read(packetBytes);
                            for (int i=0; i<bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == charDelimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            update(data);  //수신된 데이터 앱에 반영
                                        }
                                    });
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    BluetoothDevice getDeviceFromBondedList(String name) {
        BluetoothDevice selectedDevice = null;

        for (BluetoothDevice device : devices) {
            if (name.equals(device.getName())) {
                selectedDevice = device;
                break;
            }
        }
        return selectedDevice;
    }

    @Override
    public void onDestroy() {
        try {
            thread.interrupt();  //데이터 수신 쓰레드 종료
            iStream.close();
            oStream.close();
            socket.close();
        } catch (Exception e) { }

        super.onDestroy();
    }

    void connectToSelectedDevice(String selectedDeviceName) {
        device = getDeviceFromBondedList(selectedDeviceName);
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try {
            socket = device.createRfcommSocketToServiceRecord(uuid);  //소켓 생성
            socket.connect();  //RFCOMM 채널을 통한 연결

            //데이터 송수신을 위한 스트림 얻기
            oStream = socket.getOutputStream();
            iStream = socket.getInputStream();

            Toast.makeText(v.getContext(), "블루투스가 연결되었습니다!", Toast.LENGTH_SHORT).show();

            beginListenForData();  //데이터 수신 준비
        } catch (Exception e) {  //블루투스 연결 중 오류 발생
            e.printStackTrace();
        }
    }

    public void update(String data) {
        String table = "";
        String content = "";

        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        if (data.contains("on")) {
            content = "전등을켰습니다.";
            table = "light_on";
            tvLight.setText("전등을 사용한 지 0시간째");
        }
        if (data.contains("off")) {
            content = "전등을껐습니다.";
            table = "light_off";
            tvLight.setText("전등을 사용한 지 0시간째");
        }
        if (data.contains("fur")) {
            content = MainActivity.userDatas[2] + "을/를사용했습니다.";
            table = "fur_usage";
            tvFur.setText(MainActivity.userDatas[2] + "을/를 사용한 지 0시간째");
        }

        activities.add(sdf.format(now) + " " + content);
        adapter.notifyDataSetChanged();

        UpdateDatas task = new UpdateDatas(getContext());
        if (MainActivity.userDatas[1].equals("P")) {
            task.execute(table, MainActivity.userDatas[4], content);
        } else {
            task.execute(table, HomeFragment.uId, content);
        }
    }
}