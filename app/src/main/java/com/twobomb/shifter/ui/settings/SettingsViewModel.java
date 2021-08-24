package com.twobomb.shifter.ui.settings;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.twobomb.shifter.MainActivity;

public class SettingsViewModel extends AndroidViewModel {

    private MutableLiveData<Integer> currentGroup;//группв
    private MutableLiveData<Boolean> is_friday_remind;//Напоминать в пятницу
    private MutableLiveData<Integer> friday_hour;//час
    private MutableLiveData<Integer> friday_minute;//минуты

    private MutableLiveData<Boolean> is_friday_remind_repeat;//повторять напоминать в пятницу
    private MutableLiveData<Integer> friday_repeat_hour;//час
    private MutableLiveData<Integer> friday_repeat_minute;//минуты

    private MutableLiveData<Boolean> is_auto_add_alarm;//добавлять будильник на выходные
    private MutableLiveData<Integer> alarm_hour;//час
    private MutableLiveData<Integer> alarm_minute;//минуты

    private MutableLiveData<Boolean> is_show_notification_my_group;//показывать уведомление если дежурит моя группа
    private MutableLiveData<Integer> notif_hour;//час
    private MutableLiveData<Integer> notif_minute;//минуты

    private SharedPreferences sp;
    Context context;
    public SettingsViewModel(final Application application) {
        super(application);
        this.context = application.getApplicationContext();
        currentGroup = new MutableLiveData<>();
        is_friday_remind = new MutableLiveData<>();
        friday_hour = new MutableLiveData<>();
        friday_minute = new MutableLiveData<>();
        is_friday_remind_repeat = new MutableLiveData<>();
        friday_repeat_hour = new MutableLiveData<>();
        friday_repeat_minute = new MutableLiveData<>();
        is_auto_add_alarm = new MutableLiveData<>();
        alarm_hour = new MutableLiveData<>();
        alarm_minute = new MutableLiveData<>();
        is_show_notification_my_group = new MutableLiveData<>();
        notif_hour = new MutableLiveData<>();
        notif_minute = new MutableLiveData<>();


         sp = getApplication().getApplicationContext().getSharedPreferences("APP_SETTINGS",Context.MODE_PRIVATE);
        currentGroup.setValue(sp.getInt("group_index",0));
        is_friday_remind.setValue(sp.getBoolean("is_friday_remind",true));
        friday_hour.setValue(sp.getInt("friday_hour",10));
        friday_minute.setValue(sp.getInt("friday_minute",0));
        is_friday_remind_repeat.setValue(sp.getBoolean("is_friday_remind_repeat",true));
        friday_repeat_hour.setValue(sp.getInt("friday_repeat_hour",14));
        friday_repeat_minute.setValue(sp.getInt("friday_repeat_minute",30));
        is_auto_add_alarm.setValue(sp.getBoolean("is_auto_add_alarm",true));
        alarm_hour.setValue(sp.getInt("alarm_hour",7));
        alarm_minute.setValue(sp.getInt("alarm_minute",45));
        is_show_notification_my_group.setValue(sp.getBoolean("is_show_notification_my_group",true));
        notif_hour.setValue(sp.getInt("notif_hour",7));
        notif_minute.setValue(sp.getInt("notif_minute",0));
    }

    public LiveData<Integer> getGroup() {
        return currentGroup;
    }
    public void setGroup(Integer inx) {
         currentGroup.setValue(inx);
    }

    public void Save(){
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("group_index",currentGroup.getValue());
        editor.putBoolean("is_friday_remind",is_friday_remind.getValue());
        editor.putInt("friday_hour",friday_hour.getValue());
        editor.putInt("friday_minute",friday_minute.getValue());
        editor.putBoolean("is_friday_remind_repeat",is_friday_remind_repeat.getValue());
        editor.putInt("friday_repeat_hour",friday_repeat_hour.getValue());
        editor.putInt("friday_repeat_minute",friday_repeat_minute.getValue());
        editor.putBoolean("is_auto_add_alarm",is_auto_add_alarm.getValue());
        editor.putInt("alarm_hour",alarm_hour.getValue());
        editor.putInt("alarm_minute",alarm_minute.getValue());
        editor.putBoolean("is_show_notification_my_group",is_show_notification_my_group.getValue());
        editor.putInt("notif_hour",notif_hour.getValue());
        editor.putInt("notif_minute",notif_minute.getValue());
        editor.apply();
        MainActivity.UpdateTimers(context);
    }


    public LiveData<Boolean> getIs_friday_remind() {
        return is_friday_remind;
    }

    public void setIs_friday_remind(Boolean val) {
        this.is_friday_remind.setValue(val);
    }

    public MutableLiveData<Integer> getFriday_hour() {
        return friday_hour;
    }

    public void setFriday_hour(Integer hour) {
        this.friday_hour.setValue(hour);
    }

    public MutableLiveData<Integer> getFriday_minute() {
        return friday_minute;
    }

    public void setFriday_minute(Integer minute) {
        this.friday_minute.setValue(minute);
    }

    public MutableLiveData<Boolean> getIs_friday_remind_repeat() {
        return is_friday_remind_repeat;
    }

    public void setIs_friday_remind_repeat(Boolean val) {
        this.is_friday_remind_repeat.setValue(val);
    }

    public MutableLiveData<Integer> getFriday_repeat_hour() {
        return friday_repeat_hour;
    }

    public void setFriday_repeat_hour(Integer friday_repeat_hour) {
        this.friday_repeat_hour.setValue(friday_repeat_hour);
    }

    public MutableLiveData<Integer> getFriday_repeat_minute() {
        return friday_repeat_minute;
    }

    public void setFriday_repeat_minute(Integer friday_repeat_minute) {
        this.friday_repeat_minute.setValue(friday_repeat_minute);
    }

    public MutableLiveData<Boolean> getIs_auto_add_alarm() {
        return is_auto_add_alarm;
    }

    public void setIs_auto_add_alarm(Boolean is_auto_add_alarm) {
        this.is_auto_add_alarm.setValue(is_auto_add_alarm);
    }

    public MutableLiveData<Integer> getAlarm_hour() {
        return alarm_hour;
    }

    public void setAlarm_hour(Integer alarm_hour) {
        this.alarm_hour.setValue(alarm_hour);
    }

    public MutableLiveData<Integer> getAlarm_minute() {
        return alarm_minute;
    }

    public void setAlarm_minute(Integer alarm_minute) {
        this.alarm_minute.setValue(alarm_minute);
    }

    public MutableLiveData<Boolean> getIs_show_notification_my_group() {
        return is_show_notification_my_group;
    }

    public void setIs_show_notification_my_group(Boolean val) {
        this.is_show_notification_my_group.setValue(val);
    }

    public MutableLiveData<Integer> getNotif_hour() {
        return notif_hour;
    }

    public void setNotif_hour(Integer hour) {
        this.notif_hour.setValue(hour);
    }

    public MutableLiveData<Integer> getNotif_minute() {
        return notif_minute;
    }

    public void setNotif_minute(Integer minute) {
        this.notif_minute.setValue(minute);
    }
}