package co.rapigo.JobIntentService;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.pixplicity.easyprefs.library.Prefs;

import co.rapigo.JobIntentService.models.ParametersService;
import co.rapigo.JobIntentService.services.GpsService;
import co.rapigo.JobIntentService.utils.Constantes;

public class MainActivity extends AppCompatActivity {

    private EditText editTextDevice;
    private EditText editTextFastedInterval;
    private EditText editTextUpdateInterval;
    private int ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextDevice = findViewById(R.id.device);
        editTextFastedInterval = findViewById(R.id.fasted_interval);
        editTextUpdateInterval = findViewById(R.id.update_interval);

        setData();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,

                    },
                    ACCESS_FINE_LOCATION);
        } else {
            Toast.makeText( this, "Permisos ok" , Toast.LENGTH_LONG  ).show();
        }
    }

    public void setData(){
        int FASTEST_INTERVAL_SECONDS = Prefs.getInt(Constantes.FASTEST_INTERVAL_SECONDS_KEY, Constantes.FASTEST_INTERVAL_SECONDS);
        int UPDATE_INTERVAL_SECONDS = Prefs.getInt(Constantes.UPDATE_INTERVAL_SECONDS_KEY, Constantes.UPDATE_INTERVAL_SECONDS);
        String TOKEN_DEVICE = Prefs.getString(Constantes.TOKEN_DEVICE_KEY, Constantes.TOKEN_DEVICE);

        editTextDevice.setText(TOKEN_DEVICE);
        editTextFastedInterval.setText("" + FASTEST_INTERVAL_SECONDS);
        editTextUpdateInterval.setText("" + UPDATE_INTERVAL_SECONDS);
    }

    public ParametersService saveData(){
        String tokenDevice = editTextDevice.getText().toString();
        int fastedInterval = Integer.valueOf(editTextFastedInterval.getText().toString());
        int updateInterval = Integer.valueOf(editTextUpdateInterval.getText().toString());

        ParametersService pS = new ParametersService(tokenDevice, fastedInterval, updateInterval);
        pS.save();
        return pS;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //Toast.makeText( this, "length " +grantResults.length , Toast.LENGTH_LONG  ).show();
        if (requestCode == ACCESS_FINE_LOCATION ) {
            if(grantResults.length == 2 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                // We can now safely use the API we requested access to
                Toast.makeText( this, "Permisos concedidos" , Toast.LENGTH_LONG  ).show();

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                        },
                        ACCESS_FINE_LOCATION
                );
            }
        }
    }

    public void startService(View v) {

        ParametersService pS = saveData();

        Intent serviceIntent = new Intent(this, GpsService.class);
        serviceIntent.putExtra("parameters", pS);

        startService(serviceIntent);
    }

    public void stopService(View v){
        Intent serviceIntent = new Intent(this, GpsService.class);
        stopService(serviceIntent);
    }

}
