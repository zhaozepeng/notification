package com.android.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;

import com.android.libcore.Toast.T;
import com.android.libcore.log.L;
import com.android.libcore_ui.activity.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Description: #TODO
 *
 * @author zzp(zhao_zepeng@hotmail.com)
 * @since 2016-03-20
 */
public class NotificationActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_show_dynamic;
    private Button btn_big;
    private Button btn_show_normal;

    private NotificationCompat.Builder mBuilder;
    private Notification notification;
    private NotificationManager notificationManager;

    public static final int NOTIFY_ID1 = 255;
    public static final int NOTIFY_ID2 = 256;
    public static final int NOTIFY_ID3 = 257;
    public static final String NOTIFY_ACTION = "com.android.notification";
    public static final String CLICK_ACTION = "com.android.click";

    private Timer timer;
    private TimerTask task;
    private Button btn_dismiss_dynamic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initView();
        initData();
    }

    private void initView() {
        btn_show_dynamic = (Button) findViewById(R.id.btn_show_dynamic);
        btn_big = (Button) findViewById(R.id.btn_big);
        btn_show_normal = (Button) findViewById(R.id.btn_show_normal);

        btn_show_normal.setOnClickListener(this);
        btn_show_dynamic.setOnClickListener(this);
        btn_big.setOnClickListener(this);
        btn_dismiss_dynamic = (Button) findViewById(R.id.btn_dismiss_dynamic);
        btn_dismiss_dynamic.setOnClickListener(this);
    }

    private void initData() {
        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        registerReceiver(NOTIFY_ACTION);
        registerReceiver(CLICK_ACTION);
    }

    @Override
    protected void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(NOTIFY_ACTION)) {
            T.getInstance().showShort("you click notification");
        }else{
            notificationManager.cancel(NOTIFY_ID3);
        }
    }

    private String parseDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy hh:mm:ss", Locale.getDefault());
        return format.format(System.currentTimeMillis());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_show_normal:
                mBuilder = new NotificationCompat.Builder(NotificationActivity.this);
                mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("this is notification title test")
                        .setContentText("this is notification text test")
                        .setNumber((int) (Math.random() *1000))
                .setTicker("you got a new message")
                        .setDefaults(Notification.DEFAULT_SOUND
                                | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                        .setAutoCancel(true)
                        .setWhen(0);
                Intent intent = new Intent(NOTIFY_ACTION);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(NotificationActivity.this,
                        1000, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(pendingIntent);
                notification = mBuilder.build();
                notificationManager.notify(NOTIFY_ID1, notification);
            break;
            case R.id.btn_show_dynamic:
                startTimeTask();
                break;
            case R.id.btn_big:
                mBuilder = new NotificationCompat.Builder(NotificationActivity.this);
                mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("this is notification title test")
                        .setContentText("this is notification text test")
                        .setNumber((int) (Math.random() *1000))
                        .setTicker("you got a new message")
                        .setDefaults(Notification.DEFAULT_SOUND
                                | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                        .setAutoCancel(true)
                        .setWhen(0);
                intent = new Intent(NOTIFY_ACTION);
                pendingIntent = PendingIntent.getBroadcast(NotificationActivity.this,
                        1000, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(pendingIntent);
                notification = mBuilder.build();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    RemoteViews view = new RemoteViews(getPackageName(), R.layout.layout_big_notification);
                    view.setOnClickPendingIntent(R.id.btn_click_close,
                            PendingIntent.getBroadcast(NotificationActivity.this, 1001,
                                    new Intent(), PendingIntent.FLAG_UPDATE_CURRENT));
                    notification.bigContentView = view;
                }

                notificationManager.notify(NOTIFY_ID3, notification);
                break;
            case R.id.btn_dismiss_dynamic:
                if (notification != null) {
                    notificationManager.cancel(NOTIFY_ID2);
                    timer.cancel();
                    timer = null;
                    task = null;
                }
                break;
        }
    }

    private void startTimeTask() {
        if (timer != null)
            return;
        timer = new Timer("time");
        task = new TimerTask() {
            @Override
            public void run() {
                showDynamicNotification();
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    private void showDynamicNotification() {
        L.i("show dynamic notification");
        mBuilder = new NotificationCompat.Builder(NotificationActivity.this);
        RemoteViews view = new RemoteViews(getPackageName(), R.layout.layout_notification);
        view.setTextViewText(R.id.tv_number, parseDate());
        view.setImageViewResource(R.id.iv_icon, R.mipmap.ic_launcher);

        Intent intent = new Intent(NOTIFY_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(NotificationActivity.this,
                1000, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setContentTitle("this is notification title test")
                .setTicker("you got a new message")
                .setOngoing(true)
                .setContent(view);
        notification = mBuilder.build();
        notificationManager.notify(NOTIFY_ID2, notification);
    }
}
