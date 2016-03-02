package ua.nau.edu.Adapters.AdapterInfoActivity.Fragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.CustomView;
import com.google.android.gms.maps.model.LatLng;

import ua.nau.edu.Enum.ClipBoardKeys;
import ua.nau.edu.Enum.EnumSharedPreferences;
import ua.nau.edu.NAU_Guide.InfoActivity;
import ua.nau.edu.NAU_Guide.R;
import ua.nau.edu.Support.SharedPrefUtils.SharedPrefUtils;
import ua.nau.edu.University.NAU;

/**
 * Created by Roman on 27.11.2015.
 */
public class FragmentInfo extends Fragment {
    private static final String APP_PREFERENCES = EnumSharedPreferences.APP_PREFERENCES.toString();
    private static final String CORP_ID_KEY = EnumSharedPreferences.CORP_ID_KEY.toString();

    private SharedPreferences settings = null;
    private ClipboardManager clipboard;

    private View FragmentView;
    private InfoActivity supportActivity;
    private NAU university;

    private int currentCorp;
    private LatLng currentPosition;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        settings = this.getActivity().getSharedPreferences(SharedPrefUtils.APP_PREFERENCES, Context.MODE_PRIVATE);

        supportActivity = (InfoActivity) getActivity();

        currentCorp = supportActivity.getIntent().getExtras().getInt(CORP_ID_KEY, -1);
        clipboard = (ClipboardManager) getActivity().getSystemService(Activity.CLIPBOARD_SERVICE);

        setUpLayout(inflater, container);

        return FragmentView;
    }

    private void setUpLayout(LayoutInflater inflater, ViewGroup container) {
        // TODO remove layout ID check
        if (currentCorp <= 12) {
            // This is corp
            FragmentView = inflater.inflate(R.layout.fragment_info, container, false);
            initHashMaps(FragmentView.getContext());
            setUpLayoutCorp();
        } else if (currentCorp == 13) {
            //This is CKM
            setUpLayoutCkm();
        } else if (currentCorp == 14) {
            //This is Bistro
            setUpLayoutBistro();
        } else if (currentCorp == 15) {
            // This is MED Center
            setUpLayoutMed();
        } else if (currentCorp == 16) {
            // This is Sport
            setUpLayoutSport();
        } else {
            //This is host
            setUpLayoutHost();
        }
    }

    private void initHashMaps(Context context) {
        university = new NAU(context);
        university.init();
    }

    private void setUpLayoutCorp() {
        /*** VIEWS ***/
        GerbBlock_head = (TextView) FragmentView.findViewById(R.id.gerb_text_head);
        GerbBlock_subhead = (TextView) FragmentView.findViewById(R.id.gerb_text_subhead);
        GerbBlock_gerbImage = (ImageView) FragmentView.findViewById(R.id.gerb_image);

        CallBlock_buttonCall = (CustomView) FragmentView.findViewById(R.id.call_button_call);
        CallBlock_buttonCopy = (CustomView) FragmentView.findViewById(R.id.call_button_copy);
        CallBlock_textPhoneNumber = (TextView) FragmentView.findViewById(R.id.call_text_subhead);

        WebBlock_buttonGo = (CustomView) FragmentView.findViewById(R.id.web_button_go);
        WebBlock_buttonCopy = (CustomView) FragmentView.findViewById(R.id.web_button_copy);
        WebBlock_textUrl = (TextView) FragmentView.findViewById(R.id.web_text_subhead);

        NavBlock_buttonGo = (CustomView) FragmentView.findViewById(R.id.nav_button_go);
        NavBlock_buttonCopy = (CustomView) FragmentView.findViewById(R.id.nav_button_copy);
        NavBlock_textSubhead = (TextView) FragmentView.findViewById(R.id.nav_text_subhead);

        /** Set TextView's **/
        GerbBlock_head.setText(university.getCorpsInfoNameShort().get(currentCorp));
        GerbBlock_subhead.setText(university.getCorpsInfoNameFull().get(currentCorp));
        GerbBlock_gerbImage.setImageResource(university.getCorpsGerb().get(currentCorp));

        CallBlock_textPhoneNumber.setText(university.getCorpsInfoPhone().get(currentCorp));

        NavBlock_textSubhead.setText(university.getCorpsInfoUrl().get(currentCorp));

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

                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.Text_copied), Toast.LENGTH_SHORT).show();
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

                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.Text_copied), Toast.LENGTH_SHORT).show();
            }
        });

        NavBlock_buttonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initNavigationWindow(supportActivity.getMyCoordinate(), university.getCorps().get(currentCorp));
            }
        });
    }

    private void setUpLayoutCkm() {

    }

    private void setUpLayoutBistro() {

    }

    private void setUpLayoutMed() {

    }

    private void setUpLayoutSport() {

    }

    private void setUpLayoutHost() {

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