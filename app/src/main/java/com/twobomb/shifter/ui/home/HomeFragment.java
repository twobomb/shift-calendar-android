package com.twobomb.shifter.ui.home;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;
import com.twobomb.shifter.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    Switch sw_show_all;
    CaldroidFragment caldroidFragment;
    SharedPreferences sp;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        sp = getActivity().getApplicationContext().getSharedPreferences("APP_SETTINGS", Context.MODE_PRIVATE);

        sw_show_all = root.findViewById(R.id.sw_show_all);
        TextView tv_shift_now = root.findViewById(R.id.tv_shift_now);
        tv_shift_now.setText(String.format("Сегодня дежурит %d группа",getGroupIndexByDate(new Date())+1));


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
            public void onSelectDate(Date date, View view) {
                Toast.makeText(getContext(),date.toString(),500).show();
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
    public int getGroupIndexByDate(Date date){//Какая группа дежурит в дату
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