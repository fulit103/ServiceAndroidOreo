package co.rapigo.JobIntentService.services;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import co.rapigo.JobIntentService.MainActivity;
import co.rapigo.JobIntentService.R;
import co.rapigo.JobIntentService.models.ParametersService;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

import static co.rapigo.JobIntentService.JobIntentApplication.CHANEL_ID;

public class GpsService extends Service {

    private static final String TAG = "LocationService";

    private FusedLocationProviderClient mFusedLocationClient;
    //private final static long UPDATE_INTERVAL = 4 * 1000;  /* 4 secs */
    //private final static long FASTEST_INTERVAL = 2000; /* 2 sec */
    //private long UPDATE_INTERVAL = 30 * 1000;  /* 4 secs */
    //private long FASTEST_INTERVAL = 15000; /* 2 sec */

    private ParametersService parameters;

    @Override
    public void onCreate() {
        super.onCreate();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        this.parameters = (ParametersService) intent.getSerializableExtra("parameters");

        if (Build.VERSION.SDK_INT >= 26) {
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Notification notification = new NotificationCompat.Builder(this, CHANEL_ID)
                    .setContentText("Example Service GPS")
                    .setContentText("gps service")
                    .setSmallIcon(R.drawable.ic_android_black_24dp)
                    .setContentIntent(pendingIntent)
                    .build();

            startForeground(1, notification);
        }

        getLocation();

        return START_NOT_STICKY;
    }

    private void getLocation() {
        // ---------------------------------- LocationRequest ------------------------------------
        // Create the location request to start receiving updates
        LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(this.parameters.uptateInterval*1000);
        mLocationRequestHighAccuracy.setFastestInterval(this.parameters.fastedIntrval*1000);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocation: stopping the location service.");
            stopSelf();
            return;
        }
        Log.d(TAG, "getLocation: getting location information.");
        mFusedLocationClient.requestLocationUpdates(mLocationRequestHighAccuracy, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        Log.d(TAG, "onLocationResult: got location result.");
                        Location location = locationResult.getLastLocation();
                        if (location != null) {
                            Log.d(TAG, "location: " + location.getLatitude() + "," + location.getLongitude());
                            //User user = ((UserClient)(getApplicationContext())).getUser();
                            //GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                            //UserLocation userLocation = new UserLocation(user, geoPoint, null);
                            //saveUserLocation(userLocation);
                            postLocation(location.getLatitude(), location.getLongitude(), location.getSpeed());
                        }
                    }
                },
                Looper.myLooper()
        ); // Looper.myLooper tells this to repeat forever until thread is destroyed
    }

    private void postLocation(final double lat, final double lon, final float velocity){
        try {
            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        JSONObject json = new JSONObject();
                        json.put("latitude", "" + lat);
                        json.put("longitude", "" + lon);
                        json.put("velocity", "" + velocity);

                        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

                        try {
                            HttpPost request = new HttpPost("http://iot.webspro.co/api/v1/"+parameters.tokenDevice+"/telemetry");
                            StringEntity params = new StringEntity(json.toString());
                            request.addHeader("content-type", "application/json");
                            request.setEntity(params);
                            HttpResponse response = httpClient.execute(request);
                            Log.e("Response", "" + response.getStatusLine().getStatusCode());
                        } catch (Exception ex) {
                            // handle exception here
                            Log.e(TAG, ex.getMessage());
                        } finally {
                            httpClient.close();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            });
            thread.start();
        }catch(Exception e ){
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
