package ua.nau.edu.NAU_Guide;

import android.app.Activity;
import android.os.Bundle;

import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;

public class VKActivity extends Activity {
    private String scope = VKScope.EMAIL;
    int appId = 5084652;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        VKSdk.initialize(getApplicationContext(), appId, "");
        setContentView(R.layout.activity_auth);

        VKSdk.login(VKActivity.this, scope);
    }
}

