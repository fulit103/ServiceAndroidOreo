package co.rapigo.JobIntentService.models;

import com.pixplicity.easyprefs.library.Prefs;

import java.io.Serializable;

import co.rapigo.JobIntentService.utils.Constantes;

public class ParametersService implements Serializable {

    public String tokenDevice;
    public int fastedIntrval;
    public int uptateInterval;

    public ParametersService(String tokenDevice, int fastedInterval, int updateInterval) {
        this.tokenDevice = tokenDevice;
        this.fastedIntrval = fastedInterval;
        this.uptateInterval = updateInterval;
    }

    public void save() {
        Prefs.putString(Constantes.TOKEN_DEVICE_KEY, tokenDevice);
        Prefs.putInt(Constantes.UPDATE_INTERVAL_SECONDS_KEY, uptateInterval);
        Prefs.putInt(Constantes.FASTEST_INTERVAL_SECONDS_KEY, fastedIntrval);
    }
}
