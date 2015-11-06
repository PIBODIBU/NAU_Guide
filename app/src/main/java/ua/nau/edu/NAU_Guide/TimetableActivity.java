package ua.nau.edu.NAU_Guide;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import ua.nau.edu.Systems.FileAdapter;

public class TimetableActivity extends BaseNavigationDrawerActivity {

    private static final String GLOBAL_PREFERENCES = "GLOBAL_PREFERENCES";
    private static final String FIRST_LAUNCH_KEY = "FIRST_LAUNCH_KEY";

    SharedPreferences settings_global = null;
    SharedPreferences settings_vk = null;
    SharedPreferences.Editor editor_global;
    SharedPreferences.Editor editor_vk;

    /***
     * VKONTAKTE SDK VARIABLES
     ***/

    private int appId = 5084652;

    private static final String VK_PREFERENCES = "VK_PREFERENCES";
    private static final String VK_INFO_KEY = "VK_INFO_KEY";
    private static final String VK_PHOTO_KEY = "VK_PHOTO_KEY";
    private static final String VK_EMAIL_KEY = "VK_EMAIL_KEY";
    private static final String VK_SIGNED_KEY = "VK_SIGNED_KEY";
    private static final String VK_ID_KEY = "VK_ID_KEY";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        // Get and set system services & Buttons & SharedPreferences & Requests
        settings_global = getSharedPreferences(GLOBAL_PREFERENCES, MODE_PRIVATE);
        settings_vk = getSharedPreferences(VK_PREFERENCES, MainActivity.MODE_PRIVATE);
        editor_global = settings_global.edit();
        editor_vk = settings_vk.edit();
//

        getDrawer(
                settings_vk.getString(VK_INFO_KEY, ""),
                settings_vk.getString(VK_PHOTO_KEY, ""),
                settings_vk.getString(VK_EMAIL_KEY, "")
        );

        FileAdapter connector = new FileAdapter();

        Context context = getApplicationContext();
        CharSequence text = connector.listGroup.toString();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
