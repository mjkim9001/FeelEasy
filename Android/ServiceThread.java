package com.feeleasy.project.sw;

import android.os.Handler;

public class ServiceThread extends Thread {
    Handler handler;
    boolean isRun = true;
    int type;

    public ServiceThread(OutService.OutServiceHandler handler, int type) {
        this.handler = handler;
        this.type = type;
    }

    public ServiceThread(CheckService.CheckServiceHandler handler, int type) {
        this.handler = handler;
        this.type = type;
    }

    public void stopForever(){
        synchronized (this) {
            this.isRun = false;
        }
    }

    public void run(){
        while(isRun){
            try {
                switch (type) {
                    case 1:  //외출 모드 확인
                        Thread.sleep(1000*15);
//                        Thread.sleep(1000*60*60*4);  //4시간 간격
                        break;
                    case 2:  //전등, 가구 사용 횟수 검사
                        Thread.sleep(1000*60*60*8);  //8시간 간격
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.sendEmptyMessage(0);
        }
    }
}