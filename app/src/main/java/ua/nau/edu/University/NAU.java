package ua.nau.edu.University;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

import ua.nau.edu.NAU_Guide.R;

public class NAU extends University {
    private Context context;
    private String[] arrayCorpNameShort;
    private String[] arrayCorpNameFull;
    private String[] arrayCorpInfoPhone;
    private String[] arrayCorpInfoUrl;
    private int hashMapSize = -1;

    public NAU(Context currentContext) {
        this.setFullName("National aviation university");
        this.setNameAbbreviation("NAU");
        this.context = currentContext;
    }

    public void init() {
        HashMap<Integer, LatLng> Corps = new HashMap<>();
        HashMap<Integer, Integer> CorpsIcon = new HashMap<>();
        HashMap<Integer, Integer> CorpsGerb = new HashMap<>();
        HashMap<Integer, String> CorpsMarkerLabel = new HashMap<>();
        HashMap<Integer, String> CorpsLabel = new HashMap<>();

        HashMap<Integer, String> CorpsInfoNameShort = new HashMap<>();
        HashMap<Integer, String> CorpsInfoNameFull = new HashMap<>();
        HashMap<Integer, String> CorpsInfoPhone = new HashMap<>();
        HashMap<Integer, String> CorpsInfoUrl = new HashMap<>();

        arrayCorpNameShort = context.getResources().getStringArray(R.array.arrayCorpNameShort);
        new ArrayAdapter<>(
                context,
                android.R.layout.simple_list_item_1,
                arrayCorpNameShort);

        arrayCorpNameFull = context.getResources().getStringArray(R.array.arrayCorpNameFull);
        new ArrayAdapter<>(
                context,
                android.R.layout.simple_list_item_1,
                arrayCorpNameFull);

        arrayCorpInfoPhone = context.getResources().getStringArray(R.array.arrayCorpPhone);
        new ArrayAdapter<>(
                context,
                android.R.layout.simple_list_item_1,
                arrayCorpInfoPhone);

        arrayCorpInfoUrl = context.getResources().getStringArray(R.array.arrayCorpUrl);
        new ArrayAdapter<>(
                context,
                android.R.layout.simple_list_item_1,
                arrayCorpInfoUrl);

/** CORPS **/
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

        CorpsIcon.put(1, R.drawable.corp_1);
        CorpsIcon.put(2, R.drawable.corp_2);
        CorpsIcon.put(3, R.drawable.corp_3);
        CorpsIcon.put(4, R.drawable.corp_4);
        CorpsIcon.put(5, R.drawable.corp_5);
        CorpsIcon.put(6, R.drawable.corp_6);
        CorpsIcon.put(7, R.drawable.corp_7);
        CorpsIcon.put(8, R.drawable.corp_8);
        CorpsIcon.put(9, R.drawable.corp_9);
        CorpsIcon.put(10, R.drawable.corp_10);
        CorpsIcon.put(11, R.drawable.corp_11);
        CorpsIcon.put(12, R.drawable.corp_12);

        CorpsGerb.put(1, R.drawable.gerb_3);
        CorpsGerb.put(2, R.drawable.gerb_3);
        CorpsGerb.put(3, R.drawable.gerb_3);
        CorpsGerb.put(4, R.drawable.gerb_3);
        CorpsGerb.put(5, R.drawable.gerb_5);
        CorpsGerb.put(6, R.drawable.gerb_6);
        CorpsGerb.put(7, R.drawable.gerb_3);
        CorpsGerb.put(8, R.drawable.gerb_3);
        CorpsGerb.put(9, R.drawable.gerb_3);
        CorpsGerb.put(10, R.drawable.gerb_3);
        CorpsGerb.put(11, R.drawable.gerb_11);
        CorpsGerb.put(12, R.drawable.gerb_3);

        for (int i = 1; i <= 12; i++) {
            CorpsMarkerLabel.put(i, context.getApplicationContext().getResources().getString(R.string.corp) + " " + i);
        }

        for (int i = 1; i <= 12; i++) {
            CorpsLabel.put(i, context.getApplicationContext().getResources().getString(R.string.corp) + " " + i + ", " + arrayCorpNameShort[i]);
        }

        for (int i = 1; i <= 12; i++) {
            CorpsInfoNameShort.put(i, arrayCorpNameShort[i]);
        }

        for (int i = 1; i <= 12; i++) {
            CorpsInfoNameFull.put(i, arrayCorpNameFull[i]);
        }

        for (int i = 1; i <= 12; i++) {
            CorpsInfoPhone.put(i, arrayCorpInfoPhone[i]);
        }

        for (int i = 1; i <= 12; i++) {
            CorpsInfoUrl.put(i, arrayCorpInfoUrl[i]);
        }

/** CKM **/
        Corps.put(13, new LatLng(50.438798, 30.427380));

        CorpsIcon.put(13, R.drawable.mark_ckm);

        CorpsMarkerLabel.put(13, context.getApplicationContext().getResources().getString(R.string.ckm));

        CorpsLabel.put(13, context.getApplicationContext().getResources().getString(R.string.ckm));

/** BISTRO **/
        Corps.put(14, new LatLng(50.440893, 30.431773));

        CorpsIcon.put(14, R.drawable.mark_bistro);

        CorpsMarkerLabel.put(14, context.getApplicationContext().getResources().getString(R.string.bistro));

        CorpsLabel.put(14, context.getApplicationContext().getResources().getString(R.string.bistro));

/** MED CENTER **/
        Corps.put(15, new LatLng(50.440592, 30.433082));

        CorpsIcon.put(15, R.drawable.mark_med);

        CorpsMarkerLabel.put(15, context.getApplicationContext().getResources().getString(R.string.med));

        CorpsLabel.put(15, context.getApplicationContext().getResources().getString(R.string.med));

/** SPORT **/
        Corps.put(16, new LatLng(50.436868, 30.422972));

        CorpsIcon.put(16, R.drawable.mark_sport);

        CorpsMarkerLabel.put(16, context.getApplicationContext().getResources().getString(R.string.sport));

        CorpsLabel.put(16, context.getApplicationContext().getResources().getString(R.string.sport));

/** HOSTEL **/
        Corps.put(17, new LatLng(50.440152, 30.435087)); // 1
        Corps.put(18, new LatLng(0, 0)); // 2
        Corps.put(19, new LatLng(50.439593, 30.434185)); // 3
        Corps.put(20, new LatLng(50.438905, 30.434146)); // 4
        Corps.put(21, new LatLng(50.441425, 30.433795)); // 5
        Corps.put(22, new LatLng(50.439317, 30.435665)); // 6
        Corps.put(23, new LatLng(50.437694, 30.438223)); // 7
        Corps.put(24, new LatLng(50.438350, 30.438223)); // 8
        Corps.put(25, new LatLng(50.437886, 30.439007)); // 9
        Corps.put(26, new LatLng(50.437886, 30.439007)); // 10
        Corps.put(27, new LatLng(50.437879, 30.440026)); // 11

        CorpsIcon.put(17, R.drawable.host_1);
        CorpsIcon.put(18, R.drawable.host_1);
        CorpsIcon.put(19, R.drawable.host_3);
        CorpsIcon.put(20, R.drawable.host_4);
        CorpsIcon.put(21, R.drawable.host_5);
        CorpsIcon.put(22, R.drawable.host_6);
        CorpsIcon.put(23, R.drawable.host_7);
        CorpsIcon.put(24, R.drawable.host_8);
        CorpsIcon.put(25, R.drawable.host_9);
        CorpsIcon.put(26, R.drawable.host_10);
        CorpsIcon.put(27, R.drawable.host_11);

        for (int i = 17; i <= 27; i++) {
            CorpsMarkerLabel.put(i, context.getApplicationContext().getResources().getString(R.string.host) + " " + Integer.toString(i - 16));

            CorpsLabel.put(i, context.getApplicationContext().getResources().getString(R.string.host) + " " + Integer.toString(i - 16));
        }

/** LIBRARY **/
        Corps.put(28, new LatLng(50.439730, 30.428707));

        CorpsIcon.put(28, R.drawable.mark_library);

        CorpsMarkerLabel.put(28, context.getApplicationContext().getResources().getString(R.string.library));

        CorpsLabel.put(28, context.getApplicationContext().getResources().getString(R.string.library));

/** SIZE **/
        hashMapSize = CorpsIcon.size();

        this.setCorps(Corps);
        this.setCorpsLabel(CorpsLabel);
        this.setCorpsMarkerLabel(CorpsMarkerLabel);
        this.setCorpsIcon(CorpsIcon);
        this.setCorpsGerb(CorpsGerb);

        this.setCorpsInfoNameShort(CorpsInfoNameShort);
        this.setCorpsInfoNameFull(CorpsInfoNameFull);
        this.setCorpsInfoPhone(CorpsInfoPhone);
        this.setCorpsInfoUrl(CorpsInfoUrl);

        this.setHashMapSize(hashMapSize);
    }
}
