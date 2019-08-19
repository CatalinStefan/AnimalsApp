package com.devtides.animalsapp;

import com.devtides.animalsapp.di.ApiModule;
import com.devtides.animalsapp.model.AnimalApiService;

public class ApiModuleTest extends ApiModule {

    AnimalApiService mockService;

    public ApiModuleTest(AnimalApiService mockService) {
        this.mockService = mockService;
    }

    @Override
    public AnimalApiService provideAnimalApiService() {
        return mockService;
    }
}
