package com.twobomb.shifter.ui.settings;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.twobomb.shifter.R;

import java.util.ArrayList;
import java.util.Arrays;

public class SettingsFragment extends Fragment {

    private SettingsViewModel settingsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel =
                ViewModelProviders.of(this).get(SettingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        final Context ctx=  this.getActivity().getBaseContext();
        final Button btn_save = root.findViewById(R.id.btn_save);
        final Spinner s_groups = root.findViewById(R.id.s_groups);
        final Switch sw_is_friday = root.findViewById(R.id.sw_is_friday);
        final TimePicker tp_friday = root.findViewById(R.id.tp_fiday);
        tp_friday.setIs24HourView(true);
        final Switch sw_is_friday_repeat = root.findViewById(R.id.sw_is_friday_repeat);
        final TimePicker tp_friday_repeat = root.findViewById(R.id.tb_friday_repeat);
        tp_friday_repeat.setIs24HourView(true);
        final Switch sw_auto_add_alarm = root.findViewById(R.id.sw_auto_add_alarm);
        final TimePicker tp_alarm = root.findViewById(R.id.tp_alarm);
        tp_alarm.setIs24HourView(true);
        final Switch sw_notif_my_group = root.findViewById(R.id.sw_notif_my_group);
        final TimePicker tp_notif_time = root.findViewById(R.id.tp_notif_time);
        tp_notif_time.setIs24HourView(true);


        s_groups.setSelection(settingsViewModel.getGroup().getValue());

        sw_is_friday.setChecked(settingsViewModel.getIs_friday_remind().getValue());
        tp_friday.setHour(settingsViewModel.getFriday_hour().getValue());
        tp_friday.setMinute(settingsViewModel.getFriday_minute().getValue());

        sw_is_friday_repeat.setChecked(settingsViewModel.getIs_friday_remind_repeat().getValue());
        tp_friday_repeat.setHour(settingsViewModel.getFriday_repeat_hour().getValue());
        tp_friday_repeat.setMinute(settingsViewModel.getFriday_repeat_minute().getValue());

        sw_auto_add_alarm.setChecked(settingsViewModel.getIs_auto_add_alarm().getValue());
        tp_alarm.setHour(settingsViewModel.getAlarm_hour().getValue());
        tp_alarm.setMinute(settingsViewModel.getAlarm_minute().getValue());

        sw_notif_my_group.setChecked(settingsViewModel.getIs_show_notification_my_group().getValue());
        tp_notif_time.setHour(settingsViewModel.getNotif_hour().getValue());
        tp_notif_time.setMinute(settingsViewModel.getNotif_minute().getValue());

        //--------
        sw_is_friday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settingsViewModel.setIs_friday_remind(b);
            }
        });


        tp_friday.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                settingsViewModel.setFriday_hour(i);
                settingsViewModel.setFriday_minute(i1);
            }
        });


        //--------
        sw_is_friday_repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settingsViewModel.setIs_friday_remind_repeat(b);
            }
        });


        tp_friday_repeat.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                settingsViewModel.setFriday_repeat_hour(i);
                settingsViewModel.setFriday_repeat_minute(i1);
            }
        });

        //--------
        sw_auto_add_alarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settingsViewModel.setIs_auto_add_alarm(b);
            }
        });


        tp_alarm.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                settingsViewModel.setAlarm_hour(i);
                settingsViewModel.setAlarm_minute(i1);
            }
        });
        //--------
        sw_notif_my_group.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settingsViewModel.setIs_show_notification_my_group(b);
            }
        });
        tp_notif_time.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                settingsViewModel.setNotif_hour(i);
                settingsViewModel.setNotif_minute(i1);
            }
        });




        s_groups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                settingsViewModel.setGroup(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(ctx, "NothingSelected!" , Toast.LENGTH_SHORT).show();
            }
        });



        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsViewModel.Save();
                Toast.makeText(ctx, "Сохранено!" , Toast.LENGTH_SHORT).show();
            }
        });


        settingsViewModel.getGroup().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer s) {
                s_groups.setSelection(s);
            }
        });
        return root;
    }
}