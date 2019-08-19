package com.devtides.animalsapp.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.devtides.animalsapp.di.AppModule;
import com.devtides.animalsapp.di.DaggerViewModelComponent;
import com.devtides.animalsapp.di.TypeOfContext;
import com.devtides.animalsapp.model.AnimalApiService;
import com.devtides.animalsapp.model.AnimalModel;
import com.devtides.animalsapp.model.ApiKeyModel;
import com.devtides.animalsapp.util.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.devtides.animalsapp.di.TypeOfContext.CONTEXT_APP;

public class ListViewModel extends AndroidViewModel {

    @Inject
    AnimalApiService apiService;
    private CompositeDisposable disposable = new CompositeDisposable();

    public MutableLiveData<List<AnimalModel>> animals = new MutableLiveData<List<AnimalModel>>();
    public MutableLiveData<Boolean> loadError = new MutableLiveData<Boolean>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<Boolean>();

    @Inject
    @TypeOfContext(CONTEXT_APP)
    SharedPreferencesHelper prefs;

    private Boolean invalidApiKey = false;
    private Boolean injected = false;

    public ListViewModel(Application application) {
        super(application);
    }

    public ListViewModel(Application application, Boolean isTest) {
        super(application);
        injected = true;
    }

    private void inject() {
        if(!injected) {
            DaggerViewModelComponent.builder()
                    .appModule(new AppModule(getApplication()))
                    .build()
                    .inject(this);
        }
    }

    public void refresh() {
        inject();
        loading.setValue(true);
        invalidApiKey = false;
        String key = prefs.getApiKey();
        if(key == null || key.equals("")) {
            getKey();
        } else {
            getAnimals(key);
        }
    }

    public void hardRefresh() {
        loading.setValue(true);
        getKey();
    }

    private void getKey() {
        disposable.add(
                apiService.getApiKey()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<ApiKeyModel>() {
                            @Override
                            public void onSuccess(ApiKeyModel key) {
                                if (key.key.isEmpty()) {
                                    loadError.setValue(true);
                                    loading.setValue(false);
                                } else {
                                    prefs.saveApiKey(key.key);
                                    getAnimals(key.key);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                loading.setValue(false);
                                loadError.setValue(true);
                            }
                        })
        );
    }

    private void getAnimals(String key) {
        disposable.add(
                apiService.getAnimals(key)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<AnimalModel>>() {
                            @Override
                            public void onSuccess(List<AnimalModel> animalModels) {
                                loadError.setValue(false);
                                animals.setValue(animalModels);
                                loading.setValue(false);
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (!invalidApiKey) {
                                    invalidApiKey = true;
                                    getKey();
                                } else {
                                    e.printStackTrace();
                                    loading.setValue(false);
                                    loadError.setValue(true);
                                }
                            }
                        })
        );
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
