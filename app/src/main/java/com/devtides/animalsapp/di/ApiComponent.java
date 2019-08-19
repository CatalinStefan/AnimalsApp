package com.devtides.animalsapp.di;

import com.devtides.animalsapp.model.AnimalApiService;

import dagger.Component;

@Component(modules = {ApiModule.class})
public interface ApiComponent {
    void inject(AnimalApiService service);
}
