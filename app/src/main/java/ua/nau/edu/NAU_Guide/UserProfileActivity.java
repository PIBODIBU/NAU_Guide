package ua.nau.edu.NAU_Guide;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gc.materialdesign.views.CustomView;

import ua.nau.edu.Enum.EnumSharedPreferences;
import ua.nau.edu.Enum.EnumSharedPreferencesVK;
import ua.nau.edu.NAU_Guide.LoginLector.LoginLectorActivity;
import ua.nau.edu.Systems.SharedPrefUtils.SharedPrefUtils;

public class UserProfileActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(
                getSharedPreferences(EnumSharedPreferences.APP_PREFERENCES.toString(), MODE_PRIVATE),
                getSharedPreferences(EnumSharedPreferencesVK.VK_PREFERENCES.toString(), LoginLectorActivity.MODE_PRIVATE));

        textView = (TextView) findViewById(R.id.textView);
        textView.setText(
                sharedPrefUtils.getName() + "\n" +
                        sharedPrefUtils.getEmail() + "\n" +
                        sharedPrefUtils.getToken()
        );

        CustomView button = (CustomView) findViewById(R.id.goToLectorsListActivity);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserProfileActivity.this, LectorsListActivity.class));
            }
        });

    }
}
