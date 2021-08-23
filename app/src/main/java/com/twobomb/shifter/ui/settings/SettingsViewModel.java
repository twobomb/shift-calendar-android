package com.twobomb.shifter.ui.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {

    private MutableLiveData<String> currentGroup;

    public SettingsViewModel() {
        currentGroup = new MutableLiveData<>();
        //currentGroup.setValue("This is tools fragment");
    }

    public LiveData<String> getGroup() {
        return currentGroup;
    }
}