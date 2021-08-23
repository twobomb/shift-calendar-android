package com.twobomb.shifter.ui.settings;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.twobomb.shifter.R;

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

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(ctx, "Сохранено!" , Toast.LENGTH_SHORT).show();
            }
        });
        settingsViewModel.getGroup().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                String selected = getResources().getStringArray(R.array.shift_groups)[s_groups.getSelectedItemPosition()];
                Toast.makeText(ctx, "Выбранно  " + selected, Toast.LENGTH_SHORT).show();

            }
        });
        return root;
    }
}