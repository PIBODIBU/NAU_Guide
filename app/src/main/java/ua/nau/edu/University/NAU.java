package ua.nau.edu.University;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by gaponec on 20.09.15.
 */
public class NAU extends University {

    public NAU() {
        this.setFullName("National aviation university");
        this.setNameAbbreviation("NAU");
    }

    public void init() {
        HashMap<Integer, LatLng> Corps = new HashMap<>();
        Corps.put(1, new LatLng(50.44027774, 30.42991608));
        Corps.put(2, new LatLng(50.43925277, 30.43287188));
        Corps.put(3, new LatLng(50.43761962, 30.43354779));
        Corps.put(4, new LatLng(50.43814579, 30.43374628));
        Corps.put(5, new LatLng(50.43855579, 30.43280751));
        Corps.put(6, new LatLng(50.43794421, 30.43179899));
        // Corps.put(7,new LatLng(50.43794421,30.43179899));
        Corps.put(8, new LatLng(50.43953977, 30.4267028));
        //...

        this.setCorps(Corps);
    }
}
