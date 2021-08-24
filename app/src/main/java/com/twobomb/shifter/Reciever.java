package com.twobomb.shifter;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.drm.DrmStore;
import android.provider.AlarmClock;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.twobomb.shifter.ui.home.HomeFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Reciever extends BroadcastReceiver {
    @SuppressLint("NotificationTrampoline")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Shifter","Called intent with action: "+intent.getAction());
        Intent openAppIntent = new Intent(context, Reciever.class);
        openAppIntent.setAction("OpenApp");
        PendingIntent openAppPendingIntent = PendingIntent.getBroadcast(context, 3, openAppIntent,0);

        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(MainActivity.APP_SETTINGS, Context.MODE_PRIVATE);

        int myGroup = sp.getInt("group_index",0);
        switch (intent.getAction()){
            case "OpenApp":
                Intent start = context.getPackageManager().getLaunchIntentForPackage("com.twobomb.shifter");
                start.addFlags(FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(start);
                break;
            case MainActivity.ACTION_FRIDAY_REMIND:
                if(sp.getBoolean("is_friday_remind",true)) {
                    GregorianCalendar gc = new GregorianCalendar();
                    gc.setTime(new Date());
                    if(gc.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY)
                        break;
                    gc.add(Calendar.DATE,1);
                    if(HomeFragment.getGroupIndexByDate(gc.getTime()) == myGroup) {
                        String note = sp.getString(HomeFragment.getKeyDateData(gc.getTime()),"");
                        if(note.equals(""))
                            note = "Нет записи в заметке на этот день!";
                        ShowNitification(context,"Дежурство на выходных!",String.format("В субботу дежурит %d группа.\n%s",myGroup+1,note),openAppPendingIntent);
                    }
                    gc.add(Calendar.DATE,1);
                    if(HomeFragment.getGroupIndexByDate(gc.getTime()) == myGroup) {
                        String note = sp.getString(HomeFragment.getKeyDateData(gc.getTime()),"");
                        if(note.equals(""))
                            note = "Нет записи в заметке на этот день!";
                        ShowNitification(context,"Дежурство на выходных!",String.format("В воскресенье дежурит %d группа.\n%s",myGroup+1,note),openAppPendingIntent);
                    }
                }
                break;
            case MainActivity.ACTION_FRIDAY_REMIND_REPEAT:
                if(sp.getBoolean("is_friday_remind_repeat",true)) {
                    GregorianCalendar gc = new GregorianCalendar();
                    gc.setTime(new Date());
                    if(gc.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY)
                        break;
                    gc.add(Calendar.DATE,1);
                    if(HomeFragment.getGroupIndexByDate(gc.getTime()) == myGroup) {
                        String note = sp.getString(HomeFragment.getKeyDateData(gc.getTime()),"");
                        if(note.equals(""))
                            note = "Нет записи в заметке на этот день!";
                        ShowNitification(context,"Дежурство на выходных!",String.format("В субботу дежурит %d группа.\n%s",myGroup+1,note),openAppPendingIntent);
                    }
                    gc.add(Calendar.DATE,1);
                    if(HomeFragment.getGroupIndexByDate(gc.getTime()) == myGroup) {
                        String note = sp.getString(HomeFragment.getKeyDateData(gc.getTime()),"");
                        if(note.equals(""))
                            note = "Нет записи в заметке на этот день!";
                        ShowNitification(context,"Дежурство на выходных!",String.format("В воскресенье дежурит %d группа.\n%s",myGroup+1,note),openAppPendingIntent);
                    }
                }
                break;
            case MainActivity.ACTION_AUTO_ADD_ALARM:
                if(sp.getBoolean("is_auto_add_alarm",true)){
                    GregorianCalendar gc = new GregorianCalendar();
                    gc.setTime(new Date());
                    if(gc.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY)
                        break;
                    gc.add(Calendar.DATE,1);
                    if(HomeFragment.getGroupIndexByDate(gc.getTime()) != myGroup){
                        gc.add(Calendar.DATE, 1);
                        if (HomeFragment.getGroupIndexByDate(gc.getTime()) != myGroup)
                            break;
                    }

                    String note = sp.getString(HomeFragment.getKeyDateData(gc.getTime()), "");
                    Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
                    i.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    if(note.length() == 0)
                        note = String.format("Дежурство %d группы",myGroup);
                    i.putExtra(AlarmClock.EXTRA_MESSAGE, note);
                    ArrayList<Integer> days = new ArrayList<Integer>();
                    days.add(gc.get(Calendar.DAY_OF_WEEK));
                    i.putExtra(AlarmClock.EXTRA_DAYS,days);
                    i.putExtra(AlarmClock.EXTRA_HOUR, sp.getInt("alarm_hour",7));
                    i.putExtra(AlarmClock.EXTRA_MINUTES, sp.getInt("alarm_minute",45));
                    i.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
                    context.startActivity(i);
                    ShowNitification(context, "Будильник  добавлен!",
                            String.format("Будильник был добавлен на %d:%d на %s ",sp.getInt("alarm_hour",7),sp.getInt("alarm_minute",45),new SimpleDateFormat("EEEE").format(gc.getTime()))
                            , openAppPendingIntent);
                }
                break;
            case MainActivity.ACTION_NOTIFICATION_MY_GROUP:
                if(sp.getBoolean("is_show_notification_my_group",true)){
                    GregorianCalendar gc = new GregorianCalendar();
                    gc.setTime(new Date());
                    if(HomeFragment.getGroupIndexByDate(gc.getTime()) == myGroup)
                        ShowNitification(context, "Дежурство", String.format("Сегодня дежурит %d группа", myGroup + 1), openAppPendingIntent);
                }
                break;
        }
        MainActivity.UpdateTimers(context.getApplicationContext());
    }

    public static void ShowNitification(Context context, String title, String text, PendingIntent contentIntent){
        Notification notification = new NotificationCompat.Builder(context, "Main")
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(contentIntent)
                .setVibrate(new long[] { 500, 100, 100, 100 })
                .build();
        int notificationId = 2;
        NotificationManagerCompat.from(context).notify(notificationId, notification);
    }

}
