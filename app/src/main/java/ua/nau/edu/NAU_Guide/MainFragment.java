package ua.nau.edu.NAU_Guide;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gc.materialdesign.views.CustomView;

/**
 * Created by Roman on 04.12.2015.
 */
public class MainFragment extends Fragment {

    private MainActivity supportActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        supportActivity = (MainActivity) getActivity();

        new Thread(new Runnable() {
            @Override
            public void run() {
                CustomView button_1 = (CustomView) view.findViewById(R.id.button_map_1);
                CustomView button_2 = (CustomView) view.findViewById(R.id.button_map_2);
                CustomView button_3 = (CustomView) view.findViewById(R.id.button_map_3);
                CustomView button_4 = (CustomView) view.findViewById(R.id.button_map_4);
                CustomView button_5 = (CustomView) view.findViewById(R.id.button_map_5);
                CustomView button_6 = (CustomView) view.findViewById(R.id.button_map_6);

                View.OnClickListener click = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.button_map_1: {
                                startActivity(new Intent(supportActivity, MapsActivity.class)
                                        .putExtra("MAINACTIVITY_CORP_ID", 1));
                                break;
                            }
                            case R.id.button_map_2: {
                                startActivity(new Intent(supportActivity, MapsActivity.class)
                                        .putExtra("MAINACTIVITY_CORP_ID", 2));
                                break;
                            }
                            case R.id.button_map_3: {
                                startActivity(new Intent(supportActivity, MapsActivity.class)
                                        .putExtra("MAINACTIVITY_CORP_ID", 3));
                                break;
                            }
                            case R.id.button_map_4: {
                                startActivity(new Intent(supportActivity, MapsActivity.class)
                                        .putExtra("MAINACTIVITY_CORP_ID", 4));
                                break;
                            }
                            case R.id.button_map_5: {
                                startActivity(new Intent(supportActivity, MapsActivity.class)
                                        .putExtra("MAINACTIVITY_CORP_ID", 5));
                                break;
                            }
                            case R.id.button_map_6: {
                                startActivity(new Intent(supportActivity, MapsActivity.class)
                                        .putExtra("MAINACTIVITY_CORP_ID", 6));
                                break;
                            }
                            case R.id.button_map_7: {
                                startActivity(new Intent(supportActivity, MapsActivity.class)
                                        .putExtra("MAINACTIVITY_CORP_ID", 7));
                                break;
                            }
                            case R.id.button_map_8: {
                                startActivity(new Intent(supportActivity, MapsActivity.class)
                                        .putExtra("MAINACTIVITY_CORP_ID", 8));
                                break;
                            }
                            case R.id.button_map_9: {
                                startActivity(new Intent(supportActivity, MapsActivity.class)
                                        .putExtra("MAINACTIVITY_CORP_ID", 9));
                                break;
                            }
                            case R.id.button_map_10: {
                                startActivity(new Intent(supportActivity, MapsActivity.class)
                                        .putExtra("MAINACTIVITY_CORP_ID", 10));
                                break;
                            }
                            case R.id.button_map_11: {
                                startActivity(new Intent(supportActivity, MapsActivity.class)
                                        .putExtra("MAINACTIVITY_CORP_ID", 11));
                                break;
                            }
                            case R.id.button_map_12: {
                                startActivity(new Intent(supportActivity, MapsActivity.class)
                                        .putExtra("MAINACTIVITY_CORP_ID", 12));
                                break;
                            }
                            default: {
                                break;
                            }
                        }
                    }
                };

                button_1.setOnClickListener(click);
                button_2.setOnClickListener(click);
                button_3.setOnClickListener(click);
                button_4.setOnClickListener(click);
                button_5.setOnClickListener(click);
                button_6.setOnClickListener(click);

            }
        }).start();

        return view;
    }

}