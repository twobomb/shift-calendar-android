package com.twobomb.shifter;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.twobomb.shifter.ui.home.HomeFragment;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    public static final  String APP_SETTINGS = "APP_SETTINGS";
    private AppBarConfiguration mAppBarConfiguration;

    public static final String ACTION_FRIDAY_REMIND = "FRIDAY_REMIND";
    public static final String ACTION_FRIDAY_REMIND_REPEAT = "FRIDAY_REMIND_REPEAT";
    public static final String ACTION_AUTO_ADD_ALARM = "AUTO_ADD_ALARM";
    public static final String ACTION_NOTIFICATION_MY_GROUP = "NOTIFICATION_MY_GROUP";

    public static void UpdateTimers(Context ctx){
        SharedPreferences sp = ctx.getApplicationContext().getSharedPreferences(MainActivity.APP_SETTINGS, Context.MODE_PRIVATE);
        AlarmManager manager = (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(ctx, Reciever.class);
        intent.setAction(MainActivity.ACTION_FRIDAY_REMIND);
        GregorianCalendar fridayfind = new GregorianCalendar();
        fridayfind.setTime(new Date());
        fridayfind.set(Calendar.HOUR_OF_DAY,sp.getInt("friday_hour",10));
        fridayfind.set(Calendar.MINUTE,sp.getInt("friday_minute",0));
        fridayfind.set(Calendar.SECOND,0);

        //Найти ближайшую пятницу впереди
        while (fridayfind.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY || fridayfind.getTimeInMillis() < System.currentTimeMillis())
            fridayfind.add(Calendar.DATE,1);

        //Устанавливаем напоминалку на пятницу
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx,4,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        manager.set(AlarmManager.RTC_WAKEUP,
                fridayfind.getTimeInMillis()
                ,pendingIntent);
        Log.d("Shifter",String.format("Установлен %s на %s","ACTION_FRIDAY_REMIND",(new SimpleDateFormat("EEEE dd MMMM yyyy HH:mm:ss")).format(fridayfind.getTime()).toString()));
        if(!sp.getBoolean("is_friday_remind",true))
            pendingIntent.cancel();


        fridayfind.setTime(new Date());
        fridayfind.set(Calendar.HOUR_OF_DAY,sp.getInt("friday_repeat_hour",14));
        fridayfind.set(Calendar.MINUTE,sp.getInt("friday_repeat_minute",30));
        fridayfind.set(Calendar.SECOND,0);
        //Найти ближайшую пятницу впереди
        while (fridayfind.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY || fridayfind.getTimeInMillis() < System.currentTimeMillis())
            fridayfind.add(Calendar.DATE,1);

        //Устанавливаем напоминалку на пятницу ПОВТОР
        intent = new Intent(ctx, Reciever.class);
        intent.setAction(MainActivity.ACTION_FRIDAY_REMIND_REPEAT);
         pendingIntent = PendingIntent.getBroadcast(ctx,5,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        Log.d("Shifter",String.format("Установлен %s на %s","ACTION_FRIDAY_REMIND_REPEAT",(new SimpleDateFormat("EEEE dd MMMM yyyy HH:mm:ss")).format(fridayfind.getTime()).toString()));
        manager.set(AlarmManager.RTC_WAKEUP,
                fridayfind.getTimeInMillis()
                ,pendingIntent);
        if(!sp.getBoolean("is_friday_remind_repeat",true))
            pendingIntent.cancel();


        //Найти ближайшую пятницу впереди

        fridayfind.setTime(new Date());
        fridayfind.set(Calendar.HOUR_OF_DAY,17);
        fridayfind.set(Calendar.MINUTE,0);
        fridayfind.set(Calendar.SECOND,0);

        while (fridayfind.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY || fridayfind.getTimeInMillis() < System.currentTimeMillis())
            fridayfind.add(Calendar.DATE,1);
        //Устанавливаем автодобавление будильнка в пятницу в  17:00
        intent = new Intent(ctx, Reciever.class);
        intent.setAction(MainActivity.ACTION_AUTO_ADD_ALARM);
        pendingIntent = PendingIntent.getBroadcast(ctx,6,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        Log.d("Shifter",String.format("Установлен %s на %s","ACTION_AUTO_ADD_ALARM",(new SimpleDateFormat("EEEE dd MMMM yyyy HH:mm:ss")).format(fridayfind.getTime()).toString()));
        manager.set(AlarmManager.RTC_WAKEUP,
                fridayfind.getTimeInMillis()
                ,pendingIntent);
        if(!sp.getBoolean("is_auto_add_alarm",true))
            pendingIntent.cancel();

        //Ищем ближайший день нашего дежурства
        GregorianCalendar findDayMyGroup = new GregorianCalendar();

        findDayMyGroup.setTime(new Date());
        findDayMyGroup.set(Calendar.HOUR_OF_DAY,sp.getInt("notif_hour",7));
        findDayMyGroup.set(Calendar.MINUTE,sp.getInt("notif_minute",0));
        findDayMyGroup.set(Calendar.SECOND,0);
        int myGroup = sp.getInt("group_index",0);
        while (HomeFragment.getGroupIndexByDate(findDayMyGroup.getTime()) != myGroup || findDayMyGroup.getTimeInMillis() < System.currentTimeMillis()){
            findDayMyGroup.add(Calendar.DATE,1);
        }
        //Устанавливаем показ уведомления
        intent = new Intent(ctx, Reciever.class);
        intent.setAction(MainActivity.ACTION_NOTIFICATION_MY_GROUP);
        pendingIntent = PendingIntent.getBroadcast(ctx,7,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        manager.set(AlarmManager.RTC_WAKEUP,
                findDayMyGroup.getTimeInMillis()
                ,pendingIntent);
        Log.d("Shifter",String.format("Установлен %s на %s","ACTION_NOTIFICATION_MY_GROUP",(new SimpleDateFormat("EEEE dd MMMM yyyy HH:mm:ss")).format(findDayMyGroup.getTime()).toString()));
        if(!sp.getBoolean("is_show_notification_my_group",true))
            pendingIntent.cancel();

        Log.d("Shifter","Timers Updated!");


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity.UpdateTimers(getApplicationContext());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
