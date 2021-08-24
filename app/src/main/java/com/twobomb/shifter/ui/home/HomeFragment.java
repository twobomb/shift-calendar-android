package com.twobomb.shifter.ui.home;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;
import com.twobomb.shifter.MainActivity;
import com.twobomb.shifter.R;
import com.twobomb.shifter.Reciever;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    Switch sw_show_all;
    CaldroidFragment caldroidFragment;
    SharedPreferences sp;
    ConstraintLayout layout_down;


    public static String getKeyDateData(Date date){
        return "data_" +(new SimpleDateFormat("dd_MM_yyyy")).format(date);
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        sp = getActivity().getApplicationContext().getSharedPreferences(MainActivity.APP_SETTINGS, Context.MODE_PRIVATE);

        sw_show_all = root.findViewById(R.id.sw_show_all);
        layout_down = root.findViewById(R.id.layout_down);
        TextView tv_shift_now = root.findViewById(R.id.tv_shift_now);
        tv_shift_now.setText(String.format("Сегодня %s, дежурит %d группа",new SimpleDateFormat("EEEE dd MMMM").format(new Date()),getGroupIndexByDate(new Date())+1));


        caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance(Locale.forLanguageTag("ru"));
        sw_show_all.setChecked(sp.getBoolean("IS_SHOW_ALL",false));
        sw_show_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor edit = sp.edit();
                edit.putBoolean("IS_SHOW_ALL",b);
                edit.apply();
                updateColors();
                caldroidFragment.refreshView();
            }
        });
        caldroidFragment.setCaldroidListener(new CaldroidListener() {
            @Override
            public void onSelectDate(final Date date, View view) {
                if(sp.getInt("group_index",0) != getGroupIndexByDate(date))
                    return;
                final String keyName = getKeyDateData(date);
                String text = sp.getString(keyName,"");
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View v = getLayoutInflater().inflate(R.layout.fragment_dialog_note,null);

                Button btn = v.findViewById(R.id.btn_save_note);
                final EditText textField = v.findViewById(R.id.tb_note);
                final ImageButton btnAddAlarm = v.findViewById(R.id.ibtn_add_alarm);
                textField.setText(text);

                builder.setView(v);
                final AlertDialog dlg = builder.create();


                btnAddAlarm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
                        String name = textField.getText().toString();
                        if(name.length() == 0)
                            name = String.format("Дежурство %d группы",(getGroupIndexByDate(date)+1));
                        i.putExtra(AlarmClock.EXTRA_MESSAGE, name);
                        ArrayList<Integer> days = new ArrayList<Integer>();
                        GregorianCalendar gc = new GregorianCalendar();
                        gc.setTime(date);
                        days.add(gc.get(Calendar.DAY_OF_WEEK));
                        i.putExtra(AlarmClock.EXTRA_DAYS,days);
                        i.putExtra(AlarmClock.EXTRA_HOUR, sp.getInt("alarm_hour",7));
                        i.putExtra(AlarmClock.EXTRA_MINUTES, sp.getInt("alarm_minute",45));
                        i.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
                        startActivity(i);

                        Toast.makeText(getContext(),String.format("Будильник был добавлен на %d:%d на %s ",sp.getInt("alarm_hour",7),sp.getInt("alarm_minute",45),new SimpleDateFormat("EEEE").format(gc.getTime())),1000).show();
                        dlg.hide();
                    }
                });


                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences.Editor edit = sp.edit();
                        edit.putString(keyName,textField.getText().toString());
                        edit.apply();
                        dlg.hide();
                        Toast.makeText(getContext(),"Сохранено",500).show();
                    }
                });

                dlg.setTitle(new SimpleDateFormat("dd MMMM yyyy").format(date));
                dlg.show();
            }

            @Override
            public void onChangeMonth(int month, int year) {
                super.onChangeMonth(month, year);
                updateColors();
            }
        });
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
        caldroidFragment.setArguments(args);
        FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar, caldroidFragment);
        t.commit();


        return root;
    }
    public static int getGroupIndexByDate(Date date){//Какая группа дежурит в дату
        GregorianCalendar dateInit = new GregorianCalendar(2021,8-1,19);//ДАТА ДЛЯ ИНИЦИАЛИЗАЦИИ КАКАЯ ГРУППА ДЕЖУРИТ УКАЗАНА ДЛЯ первой группы
        GregorianCalendar begin =  new GregorianCalendar();
        begin.setTime(date);

        Long  diffDays = Long.valueOf(0);
        if(dateInit.getTimeInMillis() > begin.getTimeInMillis())
            diffDays = dateInit.getTimeInMillis() - begin.getTimeInMillis();
        else
            diffDays = begin.getTimeInMillis() - dateInit.getTimeInMillis();

        diffDays = Double.valueOf(Math.floor(diffDays.doubleValue() / (1000 * 60 * 60 * 24))).longValue();
        return (int) (diffDays%4) ;
    }
    public void updateColors(){

        int myGroup =        sp.getInt("group_index",0);
        boolean sw_show_all =        sp.getBoolean("IS_SHOW_ALL",false);
        if(sw_show_all)
            layout_down.setVisibility(View.VISIBLE);
        else
            layout_down.setVisibility(View.INVISIBLE);
        ColorDrawable[] colors=  new ColorDrawable[]{
                new ColorDrawable(getResources().getColor(R.color.group_0, getActivity().getTheme())),
                new ColorDrawable(getResources().getColor(R.color.group_1, getActivity().getTheme())),
                new ColorDrawable(getResources().getColor(R.color.group_2, getActivity().getTheme())),
                new ColorDrawable(getResources().getColor(R.color.group_3, getActivity().getTheme())),
        };
        GregorianCalendar begin = new GregorianCalendar(caldroidFragment.getYear(),caldroidFragment.getMonth()-1,1);


        //Очистить фоны с предудущего месяца до следующего включителноо
        GregorianCalendar beginClear = (GregorianCalendar) begin.clone();
        beginClear.add(Calendar.MONTH,2);
        int monthTo = beginClear.get(Calendar.MONTH);
        beginClear.add(Calendar.MONTH,-3);
        ArrayList<Date> clearDates = new ArrayList<>();
        while (beginClear.get(Calendar.MONTH) != monthTo){
            clearDates.add(beginClear.getTime());
            beginClear.add(Calendar.DATE,1);
        }
        caldroidFragment.clearBackgroundDrawableForDates(clearDates);


        int groupCur = getGroupIndexByDate(begin.getTime()) ;
        while (begin.get(Calendar.MONTH)+1 == caldroidFragment.getMonth()){
            if(!sw_show_all && groupCur == myGroup){
                caldroidFragment.setBackgroundDrawableForDate(colors[groupCur], begin.getTime());
                begin.add(Calendar.DATE,1);
                groupCur = (groupCur+1) %4;
            }
            else if(!sw_show_all) {
                groupCur = (groupCur+1) %4;
                begin.add(Calendar.DATE,1);
                continue;
            }
            else{
                caldroidFragment.setBackgroundDrawableForDate(colors[groupCur],begin.getTime());
                groupCur = (groupCur+1) %4;
                begin.add(Calendar.DATE,1);
            }

        }
    }
}