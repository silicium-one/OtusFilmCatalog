package com.silicium.otusfilmcatalog.ui.cuctomcomponents;

import android.os.CountDownTimer;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;

import com.silicium.otusfilmcatalog.R;
import com.tingyik90.snackprogressbar.SnackProgressBar;
import com.tingyik90.snackprogressbar.SnackProgressBarManager;

public class HideableSnackCircularProgressBar {

    private final SnackProgressBarManager snackProgressBarManager;
    private final SnackProgressBar circularProgressBar;
    private final Integer duration;
    private final CountDownTimer hideTimer;

    public HideableSnackCircularProgressBar(View providedView, LifecycleOwner lifecycleOwner, String messageText, Integer duration, SnackProgressBarManager.OnDisplayListener callback)
    {
        this.duration = duration;

        snackProgressBarManager = new SnackProgressBarManager(providedView, lifecycleOwner);

        snackProgressBarManager
                // (optional) change progressBar color, default = R.color.colorAccent
                .setProgressBarColor(R.color.colorAccent) //TODO: связь со стилем/темой
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
            @Override
            public void onTick(long millisUntilFinished) {
                snackProgressBarManager.setProgress((int)millisUntilFinished/countDownInterval, String.valueOf(1 + millisUntilFinished/1000));
            }
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
}
