package com.devtides.animalsapp;

import android.app.Application;

import com.devtides.animalsapp.di.PrefsModule;
import com.devtides.animalsapp.util.SharedPreferencesHelper;

public class PrefsModuleTest extends PrefsModule {

    SharedPreferencesHelper mockPrefs;

    public PrefsModuleTest(SharedPreferencesHelper mockPrefs) {
        this.mockPrefs = mockPrefs;
    }

    @Override
    public SharedPreferencesHelper providesSharedPreferences(Application app) {
        return mockPrefs;
    }
}
