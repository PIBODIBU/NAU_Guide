package ua.nau.edu.NAU_Guide;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class SearchActivity extends BaseNavigationDrawerActivity {

    public SearchActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        SharedPreferences settings = getSharedPreferences("VK_PREFERENCES", MainActivity.MODE_PRIVATE);

        getDrawer(
                settings.getString("VK_INFO_KEY", ""),
                settings.getString("VK_PHOTO_KEY", ""),
                settings.getString("VK_EMAIL_KEY", "")
        );

        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            TextView text = (TextView) findViewById(R.id.textView);

            String query = text.getText() + intent.getStringExtra(SearchManager.QUERY);
            text.setText(query);
        } else {
            TextView text = (TextView) findViewById(R.id.textView);
            text.setVisibility(View.GONE);

        }
    }

    public void toastShowLong(String TEXT) {
        Toast.makeText(getApplicationContext(), TEXT, Toast.LENGTH_LONG).show();
    }

    public void toastShowShort(String TEXT) {
        Toast.makeText(getApplicationContext(), TEXT, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        getCurrentSelection();
        super.onResume();
    }

}