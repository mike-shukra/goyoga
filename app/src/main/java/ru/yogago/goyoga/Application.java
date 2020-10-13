package ru.yogago.goyoga;

import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;

import static ru.yogago.goyoga.data.AppConstants.API_key;

public class Application extends android.app.Application {

    private static boolean sIsLocationTrackingEnabled = true;

    public static void setLocationTrackingEnabled(final boolean value) {
        sIsLocationTrackingEnabled = value;
    }

    public static boolean isIsLocationTrackingEnabled() {
        return sIsLocationTrackingEnabled;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Creating an extended library configuration.
        YandexMetricaConfig config = YandexMetricaConfig.newConfigBuilder(API_key).build();
        // Initializing the AppMetrica SDK.
        YandexMetrica.activate(getApplicationContext(), config);
        // Automatic tracking of user activity.
        YandexMetrica.enableActivityAutoTracking(this);

    }
}
