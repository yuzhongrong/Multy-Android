/*
 * Copyright 2018 Idealnaya rabota LLC
 * Licensed under Multy.io license.
 * See LICENSE for details
 */

package io.multy.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import java.util.List;
import io.multy.api.socket.CurrenciesRate;
import io.multy.api.socket.SocketManager;
import io.multy.api.socket.TransactionUpdateEntity;
import io.multy.model.entities.wallet.Wallet;
import io.multy.storage.RealmManager;
import io.multy.util.SingleLiveEvent;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public class BaseViewModel extends ViewModel {

    public SingleLiveEvent<String> errorMessage = new SingleLiveEvent<>();
    public SingleLiveEvent<String> criticalMessage = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> isLoading = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> isConnectionAvailable = new SingleLiveEvent<>();

    public MutableLiveData<List<Wallet>> wallets = new MutableLiveData<>();
    public MutableLiveData<CurrenciesRate> rates = new MutableLiveData<>();
    public SingleLiveEvent<TransactionUpdateEntity> transactionUpdate = new SingleLiveEvent<>();


    {
        isConnectionAvailable.setValue(true);
    }

    @NonNull
    private final CompositeDisposable disposables = new CompositeDisposable();

    public final void addDisposable(@NonNull Disposable disposable, @NonNull Disposable... disposables) {
        this.disposables.add(disposable);

        for (Disposable d : disposables) {
            this.disposables.add(d);
        }
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }

    public void subscribeToSockets(String TAG){
        SocketManager.getInstance().connect(TAG);
        SocketManager.getInstance().listenRatesAndTransactions(rates, transactionUpdate);
        SocketManager.getInstance().listenEvent(SocketManager.getEventReceive(RealmManager.getSettingsDao().getUserId().getUserId()), args -> {
            transactionUpdate.postValue(new TransactionUpdateEntity());
        });
    }

    public MutableLiveData<CurrenciesRate> getRatesSubscribtion() { return this.rates;}
    public SingleLiveEvent<TransactionUpdateEntity> getTransactionsSubscribtion() { return this.transactionUpdate;}
    public void unsubscribeSockets(String TAG){
        SocketManager.getInstance().lazyDisconnect(TAG);
    }
}
