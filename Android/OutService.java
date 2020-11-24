package com.feeleasy.project.sw;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;

public class OutService extends Service {
    NotificationManager manager;
    ServiceThread thread;
    Notification notifi;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //백그라운드에서 수행되는 동작
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        OutServiceHandler handler = new OutServiceHandler();
        thread = new ServiceThread(handler, 1);
        thread.start();

        return START_STICKY;
    }

    public void onDestroy() {
        thread.stopForever();
        thread = null;  //쓰레기 값을 만들어서 빠르게 회수하라고 null 대입
    }

    class OutServiceHandler extends Handler {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void handleMessage(android.os.Message msg) {

            Intent intent = new Intent(OutService.this, IntroActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(OutService.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

//            notifi = new Notification.Builder(getApplicationContext())
//                    .setContentTitle("Feel Easy 외출 확인")
//                    .setContentText("아직도 외출 중이신가요?")
//                    .setSmallIcon(R.drawable.ic_logo)
//                    .setTicker("알림!!!")
//                    .setContentIntent(pendingIntent)
//                    .build();
            notifi = new Notification.Builder(getApplicationContext())
                    .setContentTitle("Feel Easy 미사용 알림")
                    .setContentText("오늘 전등 또는 가구를 한번도 사용하시지 않으셨네요!")
                    .setSmallIcon(R.drawable.ic_logo)
                    .setTicker("알림!!!")
                    .setContentIntent(pendingIntent)
                    .build();

            //소리 추가
            notifi.defaults = Notification.DEFAULT_SOUND;
            //소리는 한 번만
            notifi.flags = Notification.FLAG_ONLY_ALERT_ONCE;
            //확인하면 자동으로 알림 제거
            notifi.flags = Notification.FLAG_AUTO_CANCEL;

            manager.notify( 777 , notifi);
        }
    }
}