package com.devtides.animalsapp;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.devtides.animalsapp.di.AppModule;
import com.devtides.animalsapp.di.DaggerViewModelComponent;
import com.devtides.animalsapp.model.AnimalApiService;
import com.devtides.animalsapp.model.AnimalModel;
import com.devtides.animalsapp.model.ApiKeyModel;
import com.devtides.animalsapp.util.SharedPreferencesHelper;
import com.devtides.animalsapp.viewmodel.ListViewModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.concurrent.Executor;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.internal.schedulers.ExecutorScheduler;
import io.reactivex.plugins.RxJavaPlugins;

public class ListViewModelTest {

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Mock
    AnimalApiService animalService;

    @Mock
    SharedPreferencesHelper prefs;

    Application application = Mockito.mock(Application.class);

    ListViewModel listViewModel = new ListViewModel(application, true);

    private String key = "Test key";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        DaggerViewModelComponent.builder()
                .appModule(new AppModule(application))
                .apiModule(new ApiModuleTest(animalService))
                .prefsModule(new PrefsModuleTest(prefs))
                .build()
                .inject(listViewModel);
    }

    @Test
    public void getAnimalsSuccess() {
        Mockito.when(prefs.getApiKey()).thenReturn(key);
        AnimalModel animal = new AnimalModel("cow");
        ArrayList<AnimalModel> animalList = new ArrayList<>();
        animalList.add(animal);

        Single testSingle = Single.just(animalList);

        Mockito.when(animalService.getAnimals(key)).thenReturn(testSingle);

        listViewModel.refresh();

        Assert.assertEquals(1, listViewModel.animals.getValue().size());
        Assert.assertEquals(false, listViewModel.loadError.getValue());
        Assert.assertEquals(false, listViewModel.loading.getValue());
    }

    @Test
    public void getAnimalsFailure() {
        Mockito.when(prefs.getApiKey()).thenReturn(key);
        Single testSingle = Single.error(new Throwable());
        Single keySingle = Single.just(new ApiKeyModel("OK", key));

        Mockito.when(animalService.getAnimals(key)).thenReturn(testSingle);
        Mockito.when(animalService.getApiKey()).thenReturn(keySingle);

        listViewModel.refresh();

        Assert.assertEquals(null, listViewModel.animals.getValue());
        Assert.assertEquals(false, listViewModel.loading.getValue());
        Assert.assertEquals(true, listViewModel.loadError.getValue());
    }

    @Test
    public void getKeySuccess() {
        Mockito.when(prefs.getApiKey()).thenReturn(null);
        ApiKeyModel apiKey = new ApiKeyModel("OK", key);
        Single keySingle = Single.just(apiKey);

        Mockito.when(animalService.getApiKey()).thenReturn(keySingle);

        AnimalModel animal = new AnimalModel("cow");
        ArrayList<AnimalModel> animalList = new ArrayList<>();
        animalList.add(animal);

        Single testSingle = Single.just(animalList);

        Mockito.when(animalService.getAnimals(key)).thenReturn(testSingle);

        listViewModel.refresh();

        Assert.assertEquals(1, listViewModel.animals.getValue().size());
        Assert.assertEquals(false, listViewModel.loadError.getValue());
        Assert.assertEquals(false, listViewModel.loading.getValue());
    }

    @Test
    public void getKeyFailure() {
        Mockito.when(prefs.getApiKey()).thenReturn(null);
        Single keySingle = Single.error(new Throwable());

        Mockito.when(animalService.getApiKey()).thenReturn(keySingle);

        listViewModel.refresh();

        Assert.assertEquals(null, listViewModel.animals.getValue());
        Assert.assertEquals(false, listViewModel.loading.getValue());
        Assert.assertEquals(true, listViewModel.loadError.getValue());
    }

    @Before
    public void setupRxSchedulers() {
        Scheduler immediate = new Scheduler() {
            @Override
            public Worker createWorker() {
                return new ExecutorScheduler.ExecutorWorker(runnable -> {
                    runnable.run();
                }, true);
            }
        };

        RxJavaPlugins.setInitNewThreadSchedulerHandler(schedulerCallable -> immediate);
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> immediate);
    }
}
