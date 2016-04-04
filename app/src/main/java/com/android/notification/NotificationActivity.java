package com.android.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;

import com.android.libcore.Toast.T;
import com.android.libcore.log.L;
import com.android.libcore_ui.activity.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.List;
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
    private Button btn_float;
    private Button btn_Heads_up;
    private Button btn_start_activity;
    private Button btn_start_activity_task;
    private Button btn_start_single_activity;

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
        btn_float = (Button) findViewById(R.id.btn_Heads_up);
        btn_float.setOnClickListener(this);
        btn_Heads_up = (Button) findViewById(R.id.btn_Heads_up);
        btn_Heads_up.setOnClickListener(this);
        btn_start_activity = (Button) findViewById(R.id.btn_start_activity_task);
        btn_start_activity.setOnClickListener(this);
        btn_start_activity_task = (Button) findViewById(R.id.btn_start_activity_task);
        btn_start_activity_task.setOnClickListener(this);
        btn_start_single_activity = (Button) findViewById(R.id.btn_start_single_activity);
        btn_start_single_activity.setOnClickListener(this);
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
        } else {
            T.getInstance().showShort("you close notification!!");
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
                        .setNumber((int) (Math.random() * 1000))
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
                RemoteViews smallView = new RemoteViews(getPackageName(), R.layout.layout_notification);
                smallView.setTextViewText(R.id.tv_number, parseDate());
                smallView.setImageViewResource(R.id.iv_icon, R.mipmap.ic_launcher);

                mBuilder = new NotificationCompat.Builder(NotificationActivity.this);
                mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                        .setNumber((int) (Math.random() * 1000))
                                //No longer displayed in the status bar as of API 21.
                        .setTicker("you got a new message")
                        .setDefaults(Notification.DEFAULT_SOUND
                                | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                        .setAutoCancel(true)
                        .setWhen(0)
                        .setPriority(NotificationCompat.PRIORITY_LOW);
                intent = new Intent(NOTIFY_ACTION);
                pendingIntent = PendingIntent.getBroadcast(NotificationActivity.this,
                        1000, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(pendingIntent);

                //在5.0版本之后，可以支持在锁屏界面显示notification
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                }
                notification = mBuilder.build();
                notification.contentView = smallView;

                //如果系统版本 >= Android 4.1，设置大视图 RemoteViews
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    RemoteViews view = new RemoteViews(getPackageName(), R.layout.layout_big_notification);
                    view.setTextViewText(R.id.tv_name, "我是名字1我是名字2我是名字3我是名字4我是名字5我是名字6我是名字7我是名字");
                    view.setOnClickPendingIntent(R.id.btn_click_close,
                            PendingIntent.getBroadcast(NotificationActivity.this, 1001,
                                    new Intent(CLICK_ACTION), PendingIntent.FLAG_UPDATE_CURRENT));
                    //textview marquee property is useless for bigContentView
                    notification.bigContentView = view;
                }

                notificationManager.notify(NOTIFY_ID3, notification);
                break;
            case R.id.btn_dismiss_dynamic:
                if (notification != null) {
                    notificationManager.cancel(NOTIFY_ID2);
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                        task = null;
                    }
                }
                break;
            case R.id.btn_Heads_up:
                RemoteViews headsUpView = new RemoteViews(getPackageName(), R.layout.layout_heads_up_notification);

                intent = new Intent(NOTIFY_ACTION);
                pendingIntent = PendingIntent.getBroadcast(NotificationActivity.this,
                        1000, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder = new NotificationCompat.Builder(NotificationActivity.this);
                mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("this is notification title test")
                        .setContentText("this is notification text test")
                        .setNumber((int) (Math.random() * 1000))
                        .setTicker("you got a new message")
                                //must set pendingintent for this notification, or will be crash
                        .setContentIntent(pendingIntent)
                        .setDefaults(Notification.DEFAULT_SOUND
                                | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                        .setAutoCancel(true)
                        .setWhen(0);
                notification = mBuilder.build();
                if (Build.VERSION.SDK_INT >= 21) {
                    notification.priority = Notification.PRIORITY_MAX;
                    notification.headsUpContentView = headsUpView;
                }
                notificationManager.notify(NOTIFY_ID1, notification);
                break;
            case R.id.btn_start_activity_task:
                mBuilder = new NotificationCompat.Builder(NotificationActivity.this);
                mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("this is notification title test")
                        .setContentText("this is notification text test")
                        .setNumber((int) (Math.random() * 1000))
                        .setTicker("you got a new message")
                        .setDefaults(Notification.DEFAULT_SOUND
                                | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                        .setAutoCancel(true)
                        .setWhen(0);

                intent = new Intent(this, ChildActivity.class);
                TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
                taskStackBuilder.addParentStack(ChildActivity.class);
                taskStackBuilder.addNextIntent(intent);
                PendingIntent resultPendingIntent =
                        taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                mBuilder.setContentIntent(resultPendingIntent);
                notification = mBuilder.build();
                notificationManager.notify(NOTIFY_ID1, notification);
                break;
            case R.id.btn_start_single_activity:

                mBuilder = new NotificationCompat.Builder(NotificationActivity.this);
                mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("this is notification title test")
                        .setContentText("this is notification text test")
                        .setNumber((int) (Math.random() * 1000))
                        .setTicker("you got a new message")
                        .setDefaults(Notification.DEFAULT_SOUND
                                | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                        .setAutoCancel(true)
                        .setWhen(0);

                intent = new Intent(this, SingleActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent notifyPendingIntent =
                        PendingIntent.getActivity(
                                this,
                                0,
                                intent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                mBuilder.setContentIntent(notifyPendingIntent);
                notification = mBuilder.build();
                notificationManager.notify(NOTIFY_ID1, notification);
                setBadge(this, 3);
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
                .setTicker("you got a new message")
                .setOngoing(true)
                .setContent(view);
        notification = mBuilder.build();
        notificationManager.notify(NOTIFY_ID2, notification);
    }

    public static void setBadge(Context context, int count) {
        String launcherClassName = getLauncherClassName(context);
        if (launcherClassName == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", count);
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", launcherClassName);
        context.sendBroadcast(intent);
    }

    public static String getLauncherClassName(Context context) {

        PackageManager pm = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        L.e("onDestroy");
    }
}
