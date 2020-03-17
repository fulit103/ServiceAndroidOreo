package co.rapigo.JobIntentService;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContextWrapper;
import android.os.Build;

import com.google.firebase.FirebaseApp;
import com.onesignal.OneSignal;
import com.pixplicity.easyprefs.library.Prefs;

public class JobIntentApplication extends Application {

    public static final String CHANEL_ID = "exampleServiceChaknel";

    @Override
    public void onCreate() {
        super.onCreate();

        //FirebaseApp.initializeApp(JobIntentApplication.this);

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        OneSignal.startInit(this)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        createNotificationChannel();
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANEL_ID,
                    "Example Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager mamager = getSystemService(NotificationManager.class);
            mamager.createNotificationChannel(serviceChannel);
        }
    }
}
