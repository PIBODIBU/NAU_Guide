package ua.nau.edu.InfoAdapter.Fragments;

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
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.CustomView;

import ua.nau.edu.Enum.EnumSharedPreferences;
import ua.nau.edu.NAU_Guide.R;

/**
 * Created by Roman on 27.11.2015.
 */
public class FragmentInfo extends Fragment {
    private static final String APP_PREFERENCES = EnumSharedPreferences.APP_PREFERENCES.toString();
    private static final String CORP_ID_KEY = EnumSharedPreferences.CORP_ID_KEY.toString();

    private SharedPreferences settings = null;

    private View FragmentView;
    CustomView buttonCall;
    CustomView buttonCopy;
    TextView textCall;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        settings = this.getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        switch (settings.getInt(CORP_ID_KEY, -1)) {
            case 1: {
                FragmentView = inflater.inflate(R.layout.fragment_info_1, container, false);
                break;
            }
            case 2: {
                FragmentView = inflater.inflate(R.layout.fragment_info_2, container, false);
                break;
            }
            case 3: {
                FragmentView = inflater.inflate(R.layout.fragment_info_3, container, false);
                break;
            }
            case 4: {
                FragmentView = inflater.inflate(R.layout.fragment_info_4, container, false);
                break;
            }
            case 5: {
                FragmentView = inflater.inflate(R.layout.fragment_info_5, container, false);
                break;
            }
            case 6: {
                FragmentView = inflater.inflate(R.layout.fragment_info_6, container, false);
                break;
            }
            case 7: {
                FragmentView = inflater.inflate(R.layout.fragment_info_7, container, false);
                break;
            }
            case 8: {
                FragmentView = inflater.inflate(R.layout.fragment_info_8, container, false);
                break;
            }
            case 9: {
                FragmentView = inflater.inflate(R.layout.fragment_info_9, container, false);
                break;
            }
            case 10: {
                FragmentView = inflater.inflate(R.layout.fragment_info_10, container, false);
                break;
            }
            case 11: {
                FragmentView = inflater.inflate(R.layout.fragment_info_11, container, false);
                break;
            }
            case 12: {
                FragmentView = inflater.inflate(R.layout.fragment_info_12, container, false);
                break;
            }
            default: {
                break;
            }
        }

        /*** VIEWS ***/
        buttonCall = (CustomView) FragmentView.findViewById(R.id.button_call);
        buttonCopy = (CustomView) FragmentView.findViewById(R.id.button_copy);
        textCall = (TextView) FragmentView.findViewById(R.id.text_call);

        buttonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use Intent.ACTION_CALL for direct call
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + textCall.getText().toString().trim())));
            }
        });

        buttonCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Activity.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Call_number", textCall.getText().toString().trim());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.Text_copied), Toast.LENGTH_SHORT).show();
            }
        });

        return FragmentView;
    }
}