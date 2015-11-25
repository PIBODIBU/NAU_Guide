package ua.nau.edu.NAU_Guide;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import ua.nau.edu.Enum.EnumSharedPreferences;
import ua.nau.edu.Enum.EnumSharedPreferencesVK;
import ua.nau.edu.Systems.FileAdapter;


public class TimetableActivity extends BaseNavigationDrawerActivity {

    public TimetableActivity() {
    }

    private static final String VK_PREFERENCES = EnumSharedPreferencesVK.VK_PREFERENCES.toString();
    private static final String VK_INFO_KEY = EnumSharedPreferencesVK.VK_INFO_KEY.toString();
    private static final String VK_EMAIL_KEY = EnumSharedPreferencesVK.VK_EMAIL_KEY.toString();
    private SharedPreferences settingsVK = null;

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        // Get and set system services & Buttons & SharedPreferences & Requests
        settingsVK = getSharedPreferences(VK_PREFERENCES, MainActivity.MODE_PRIVATE);

        getDrawer(
                settingsVK.getString(VK_INFO_KEY, ""),
                settingsVK.getString(VK_EMAIL_KEY, "")
        );

        listView = (ListView) findViewById(R.id.list_view);
        FileAdapter connector = new FileAdapter();
        String[] groupes = new String[connector.listGroup.size()];
        connector.listGroup.toArray(groupes);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, groupes);
        listView.setAdapter(adapter);
    }
}
