package ua.nau.edu.University;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

public class NAU extends University {

    public NAU() {
        this.setFullName("National aviation university");
        this.setNameAbbreviation("NAU");
    }

    public void init() {
        HashMap<Integer, LatLng> Corps = new HashMap<>();

        // CORPS
        Corps.put(1, new LatLng(50.440259, 30.429853));
        Corps.put(2, new LatLng(50.4386988, 30.4314867));
        Corps.put(3, new LatLng(50.437539, 30.433644));
        Corps.put(4, new LatLng(50.438143, 30.433748));
        Corps.put(5, new LatLng(50.438557, 30.432804));
        Corps.put(6, new LatLng(50.438014, 30.432101));
        Corps.put(7, new LatLng(50.435427, 30.432425));
        Corps.put(8, new LatLng(50.439341, 30.427063));
        Corps.put(9, new LatLng(50.436783, 30.432273));
        Corps.put(10, new LatLng(50.437662, 30.428408));
        Corps.put(11, new LatLng(50.438901, 30.430643));
        Corps.put(12, new LatLng(50.438676, 30.429303));

        // CKI
        Corps.put(13, new LatLng(50.438798, 30.427380));

        // BISTRO
        Corps.put(14, new LatLng(50.440893, 30.431773));

        // MED CENTER
        Corps.put(15, new LatLng(50.440592, 30.433082));

        // SPORT
        Corps.put(16, new LatLng(50.436868, 30.422972));

        // HOSTEL
        Corps.put(17, new LatLng(50.440152, 30.435087)); // 1
        //Corps.put(18, new LatLng(, )); // 2
        Corps.put(19, new LatLng(50.439593, 30.434185)); // 3
        Corps.put(20, new LatLng(50.438905, 30.434146)); // 4
        Corps.put(21, new LatLng(50.441425, 30.433795)); // 5
        Corps.put(22, new LatLng(50.439317, 30.435665)); // 6
        Corps.put(23, new LatLng(50.437694, 30.438223)); // 7
        Corps.put(24, new LatLng(50.438350, 30.438223)); // 8
        Corps.put(25, new LatLng(50.437886, 30.439007)); // 9
        //Corps.put(25, new LatLng(50.437886, 30.439007)); // 10
        Corps.put(25, new LatLng(50.437879, 30.440026)); // 11

        this.setCorps(Corps);
    }
}
