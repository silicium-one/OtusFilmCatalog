package com.silicium.otusfilmcatalog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.silicium.otusfilmcatalog.logic.controller.MetricsStorage;
import com.silicium.otusfilmcatalog.logic.model.IMetricNotifier;

public class SharedQuantityCounter extends BroadcastReceiver {

    @Override
    public void onReceive(@Nullable Context context, @NonNull Intent intent) {
        MetricsStorage.getMetricNotifier().increment(IMetricNotifier.SHARED_TAG);
    }
}