package ua.nau.edu.NAU_Guide;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.CustomView;
import com.google.android.gms.maps.model.LatLng;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import ua.nau.edu.Enum.ClipBoardKeys;
import ua.nau.edu.Enum.EnumExtras;
import ua.nau.edu.Enum.EnumMaps;
import ua.nau.edu.Support.View.CircleImageView;
import ua.nau.edu.University.NAU;

public class InfoActivity extends BaseToolbarActivity {

    private final String TAG = getClass().getSimpleName();

    private static final String CORP_ID_KEY = EnumExtras.CORP_ID_KEY.toString();
    private static final String CORP_LABEL_KEY = EnumExtras.CORP_LABEL_KEY.toString();

    private static final String CURRENT_LATITUDE = EnumMaps.CURRENT_LATITUDE.toString();
    private static final String CURRENT_LONGTITUDE = EnumMaps.CURRENT_LONGTITUDE.toString();

    private ClipboardManager clipboard;
    private NAU university;


    private CollapsingToolbarLayout collapsingToolbarLayout;

    private TextView GerbBlock_head;
    private TextView GerbBlock_subhead;
    private ImageView GerbBlock_gerbImage;

    private CustomView CallBlock_buttonCall;
    private CustomView CallBlock_buttonCopy;
    private TextView CallBlock_textPhoneNumber;

    private CustomView WebBlock_buttonGo;
    private CustomView WebBlock_buttonCopy;
    private TextView WebBlock_textUrl;

    private CustomView NavBlock_buttonGo;
    private CustomView NavBlock_buttonCopy;
    private TextView NavBlock_textSubhead;

    private CircleImageView headCirclePhoto;
    private ExpandableTextView historyExpandText;


    private int currentCorp = -1;

    public InfoActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        getToolbar();
        setToolbarTitle(getIntent().getStringExtra(CORP_LABEL_KEY));

        currentCorp = getIntent().getIntExtra(CORP_ID_KEY, -1);
        Log.d(TAG, "currentCorp: " + currentCorp);

        clipboard = (ClipboardManager) getSystemService(Activity.CLIPBOARD_SERVICE);
        university = new NAU(this);
        university.init();

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        setUpLayoutCorp();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void setUpLayoutCorp() {
        /*** VIEWS ***/
        GerbBlock_head = (TextView) findViewById(R.id.gerb_text_head);
        GerbBlock_subhead = (TextView) findViewById(R.id.gerb_text_subhead);
        GerbBlock_gerbImage = (ImageView) findViewById(R.id.gerb_image);

        CallBlock_buttonCall = (CustomView) findViewById(R.id.call_button_call);
        CallBlock_buttonCopy = (CustomView) findViewById(R.id.call_button_copy);
        CallBlock_textPhoneNumber = (TextView) findViewById(R.id.call_text_subhead);

        WebBlock_buttonGo = (CustomView) findViewById(R.id.web_button_go);
        WebBlock_buttonCopy = (CustomView) findViewById(R.id.web_button_copy);
        WebBlock_textUrl = (TextView) findViewById(R.id.web_text_subhead);

        NavBlock_buttonGo = (CustomView) findViewById(R.id.nav_button_go);
        NavBlock_buttonCopy = (CustomView) findViewById(R.id.nav_button_copy);
        NavBlock_textSubhead = (TextView) findViewById(R.id.nav_text_subhead);

        headCirclePhoto = (CircleImageView) findViewById(R.id.head_photo);
        historyExpandText = (ExpandableTextView) findViewById(R.id.history_text_expand);

        /** Set TextView's **/
        try {
            String label = university.getCorpsMarkerLabel().get(currentCorp);
            String nameShort = university.getCorpsInfoNameShort().get(currentCorp);
            String nameFull = university.getCorpsInfoNameFull().get(currentCorp);
            String number = university.getCorpsInfoPhone().get(currentCorp);
            String url = university.getCorpsInfoUrl().get(currentCorp);
            int imageId = university.getCorpsGerb().get(currentCorp);

            Picasso
                    .with(this)
                    .load(imageId)
                    .into(headCirclePhoto);

            collapsingToolbarLayout.setTitle(label);

            GerbBlock_head.setText(nameShort);
            GerbBlock_subhead.setText(nameFull);
            GerbBlock_gerbImage.setImageResource(imageId);

            CallBlock_textPhoneNumber.setText(number);

            WebBlock_textUrl.setText(url);

            NavBlock_textSubhead.setText("Please, wait...");

            historyExpandText.setText(getString(R.string.lorem_ipsum));

        } catch (Exception ex) {
            Log.e(TAG, "setUpLayoutCorp() -> ", ex);
        }

        /** Click Listeners **/
        CallBlock_buttonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use Intent.ACTION_CALL for direct call
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + CallBlock_textPhoneNumber.getText().toString().trim())));
            }
        });
        CallBlock_buttonCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipData clip = ClipData.newPlainText(ClipBoardKeys.CallNumber.toString(), CallBlock_textPhoneNumber.getText().toString().trim());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.Text_copied), Toast.LENGTH_SHORT).show();
            }
        });

        WebBlock_buttonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(WebBlock_textUrl.getText().toString()));
                startActivity(browserIntent);
            }
        });
        WebBlock_buttonCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipData clip = ClipData.newPlainText(ClipBoardKeys.CorpUrl.toString(), WebBlock_textUrl.getText().toString().trim());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.Text_copied), Toast.LENGTH_SHORT).show();
            }
        });

        NavBlock_buttonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initNavigationWindow(getMyCoordinate(), university.getCorps().get(currentCorp));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public LatLng getMyCoordinate() {
        return new LatLng(getIntent().getDoubleExtra(CURRENT_LATITUDE, 1.0), getIntent().getDoubleExtra(CURRENT_LONGTITUDE, 1.0));
    }

    public void initNavigationWindow(LatLng currCoordinate, LatLng destCoordinate) {
        try {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?   saddr=" +
                            currCoordinate.latitude + "," +
                            currCoordinate.longitude + "&daddr=" +
                            destCoordinate.latitude + "," + destCoordinate.longitude));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}