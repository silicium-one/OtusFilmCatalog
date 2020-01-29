package com.silicium.otusfilmcatalog.ui.cuctomcomponents;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.silicium.otusfilmcatalog.R;
import com.tingyik90.snackprogressbar.SnackProgressBar;
import com.tingyik90.snackprogressbar.SnackProgressBarManager;

public class DisappearingSnackCircularProgressBar {

    @NonNull
    private final SnackProgressBarManager snackProgressBarManager;
    @NonNull
    private final SnackProgressBar circularProgressBar;
    private final Integer duration;
    @NonNull
    private final CountDownTimer hideTimer;

    public DisappearingSnackCircularProgressBar(@NonNull View providedView, @NonNull String messageText, @Nullable SnackProgressBarManager.OnDisplayListener callback, @Nullable LifecycleOwner lifecycleOwner)
    {
        this.duration = providedView.getResources().getInteger(R.integer.back_pressed_twice_await_time_ms);

        snackProgressBarManager = new SnackProgressBarManager(providedView, lifecycleOwner);

        snackProgressBarManager
                // (optional) change progressBar color, default = R.color.colorAccent
                .setProgressBarColor(R.color.colorAccent) //TODO: связь со стилем/темой //fixme: вот, что бывает, когда используешь стороннюю библиотеку! Ей нужен ColorRes вместо ColorInt и плевать она хотела на AppTheme
                // (optional) change background color, default = BACKGROUND_COLOR_DEFAULT (#FF323232)
                .setBackgroundColor(SnackProgressBarManager.BACKGROUND_COLOR_DEFAULT)
                // чтобы не затемнялась (забелялась точнее) активность под высплывающей подсказкой - ставим нулевую прозрачность
                .setOverlayLayoutAlpha(0)
                .setOnDisplayListener(callback);

         circularProgressBar = new SnackProgressBar(
                SnackProgressBar.TYPE_CIRCULAR, messageText)
                .setIsIndeterminate(false)
                .setProgressMax(100)
                .setShowProgressPercentage(true);

        final int countDownInterval = duration / 100;
        hideTimer = new CountDownTimer(duration, countDownInterval) {
            @SuppressLint("SyntheticAccessor")
            @Override
            public void onTick(long millisUntilFinished) {
                snackProgressBarManager.setProgress((int)millisUntilFinished/countDownInterval, String.valueOf(1 + millisUntilFinished/1000));
            }
            @SuppressLint("SyntheticAccessor")
            @Override
            public void onFinish() {
                snackProgressBarManager.dismiss();
            }
        };
    }

    public void Show()
    {
        snackProgressBarManager.show(circularProgressBar, duration);
        hideTimer.start();
    }

    public void dismiss()
    {
        snackProgressBarManager.dismiss();
    }
}
