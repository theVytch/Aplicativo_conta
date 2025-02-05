package br.com.contas.activities;

import android.app.Activity;
import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                updateWidgets();
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                updateWidgets();
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });
    }

    private void updateWidgets() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ComponentName widget = new ComponentName(this, ContasWidget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(widget);

        for (int appWidgetId : appWidgetIds) {
            ContasWidget.updateAppWidget(this, appWidgetManager, appWidgetId);
        }
    }
}
