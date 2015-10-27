package ua.nau.edu.NAU_Guide;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by root on 10/5/15.
 */
public class InfoActivity extends BaseNavigationDrawerActivity {

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

    /*****/

    public InfoActivity() {
    }

    /***
     * VIEWS
     ***/


    /*****/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (getIntent().getIntExtra("Corp_id", -1)) {
            case 1: {
                setContentView(R.layout.activity_info_1);
                setLabel();

                break;
            }
            case 2: {
                setContentView(R.layout.activity_info_2);
                setLabel();

                break;
            }
            case 3: {
                setContentView(R.layout.activity_info_3);
                setLabel();

                break;
            }
            case 4: {
                setContentView(R.layout.activity_info_4);
                setLabel();

                break;
            }
            case 5: {
                setContentView(R.layout.activity_info_5);
                setLabel();

                break;
            }
            case 6: {
                setContentView(R.layout.activity_info_6);
                setLabel();

                break;
            }
            case 7: {
                setContentView(R.layout.activity_info_7);
                setLabel();

                break;
            }
            case 8 : {
                setContentView(R.layout.activity_info_8);
                setLabel();

                break;
            }
            case 9: {
                setContentView(R.layout.activity_info_9);
                setLabel();

                break;
            }
            case 10: {
                setContentView(R.layout.activity_info_10);
                setLabel();

                break;
            }
            case 11: {
                setContentView(R.layout.activity_info_11);
                setLabel();

                break;
            }
            case 12: {
                setContentView(R.layout.activity_info_12);
                setLabel();

                break;
            }

            default: {
                setContentView(R.layout.activity_info_default);
                this.setTitle("null");

                break;
            }
        }


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
    }

    public void toastShowLong(String TEXT) {
        Toast.makeText(getApplicationContext(), TEXT, Toast.LENGTH_LONG).show();
    }

    public void toastShowShort(String TEXT) {
        Toast.makeText(getApplicationContext(), TEXT, Toast.LENGTH_SHORT).show();
    }

    public void setLabel () {
        this.setTitle(Integer.toString(getIntent().getIntExtra("Corp_id", -1)) +
                getString(R.string.corp) +
                ", " +
                getIntent().getStringExtra("Corp_label"));
    }
}