package ua.nau.edu.NAU_Guide;

import android.os.Bundle;

public class FloorActivity extends BaseNavigationDrawerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor);

        getDrawer();
    }
}
