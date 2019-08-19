package com.devtides.animalsapp.model;

import com.devtides.animalsapp.di.DaggerApiComponent;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class AnimalApiService {

    @Inject
    AnimalApi api;

    public AnimalApiService() {
        DaggerApiComponent.create().inject(this);
    }

    public Single<ApiKeyModel> getApiKey() {
        return api.getApiKey();
    }

    public Single<List<AnimalModel>> getAnimals(String key) {
        return api.getAnimals(key);
    }
}
