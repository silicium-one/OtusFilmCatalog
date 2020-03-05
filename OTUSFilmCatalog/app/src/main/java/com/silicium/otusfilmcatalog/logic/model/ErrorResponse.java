package com.silicium.otusfilmcatalog.logic.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.silicium.otusfilmcatalog.App;

/**
 * Класс ошибок для асинхронных операций
 */
public class ErrorResponse {

    /**
     * сообщение об ошибке в пригодном для чтения виде
     */
    @NonNull
    public final String message;

    /**
     * исключение, которое вызвало ощибку
     */
    @Nullable
    private final Throwable t;

    /**
     * код ошибки ответа по HTTP, или -1, если не применимо
     */
    private final int httpResponseCode;

    public ErrorResponse(@NonNull String message) {
        this.message = message;
        t = null;
        httpResponseCode = -1;
    }

    public ErrorResponse(int resId) {
        this.message = App.getAppResources().getString(resId);
        t = null;
        httpResponseCode = -1;
    }

    public ErrorResponse(int resId, @NonNull Throwable t) {
        this.message = App.getAppResources().getString(resId);
        this.t = t;
        httpResponseCode = -1;
    }

    public ErrorResponse(int resId, int httpResponseCode) {
        this.message = App.getAppResources().getString(resId);
        this.t = null;
        this.httpResponseCode = httpResponseCode;
    }
}
