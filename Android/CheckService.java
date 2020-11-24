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

public class CheckService extends Service {
    NotificationManager manager;
    ServiceThread thread;
    Notification notifi;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        CheckServiceHandler handler = new CheckServiceHandler();
        thread = new ServiceThread(handler, 2);
        thread.start();

        return START_STICKY;
    }

    public void onDestroy() {
        thread.stopForever();
        thread = null;
    }

    class CheckServiceHandler extends Handler {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void handleMessage(android.os.Message msg) {
            Intent intent = new Intent(CheckService.this, IntroActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(CheckService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            notifi = new Notification.Builder(getApplicationContext())
                    .setContentTitle("Feel Easy 미사용 알림")
                    .setContentText("오늘 전등 또는 가구를 한번도 사용하시지 않으셨네요!")
                    .setSmallIcon(R.drawable.ic_logo)
                    .setTicker("알림!!!")
                    .setContentIntent(pendingIntent)
                    .build();

            notifi.defaults = Notification.DEFAULT_SOUND;
            notifi.flags = Notification.FLAG_ONLY_ALERT_ONCE;
            notifi.flags = Notification.FLAG_AUTO_CANCEL;

            CheckUsage task = new CheckUsage(manager, notifi);
            task.execute(HomeFragment.uId);
        }
    }
}