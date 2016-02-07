package ua.nau.edu.NAU_Guide;

/**
 * Created by root on 2/7/16.
 */
public class BaseBackNavigationDrawerActivity extends BaseNavigationDrawerActivity {
    @Override
    public void getDrawer(String ACCOUNT_NAME, String ACCOUNT_EMAIL) {
        setUpDrawerBuilder(ACCOUNT_NAME, ACCOUNT_EMAIL);
        this.drawerBuilder.withActionBarDrawerToggle(false);
        setUpDrawer();
        getCurrentSelection();
    }

}
