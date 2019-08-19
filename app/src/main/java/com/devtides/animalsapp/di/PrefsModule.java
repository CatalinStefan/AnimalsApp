package com.devtides.animalsapp.di;

import android.app.Application;

import androidx.appcompat.app.AppCompatActivity;

import com.devtides.animalsapp.util.SharedPreferencesHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.devtides.animalsapp.di.TypeOfContext.CONTEXT_ACTIVITY;
import static com.devtides.animalsapp.di.TypeOfContext.CONTEXT_APP;

@Module
public class PrefsModule {

    @Provides
    @Singleton
    @TypeOfContext(CONTEXT_APP)
    public SharedPreferencesHelper providesSharedPreferences(Application app) {
        return new SharedPreferencesHelper(app);
    }

    @Provides
    @Singleton
    @TypeOfContext(CONTEXT_ACTIVITY)
    public SharedPreferencesHelper providesActivitySharedPreferences(AppCompatActivity activity) {
        return new SharedPreferencesHelper(activity);
    }
}
