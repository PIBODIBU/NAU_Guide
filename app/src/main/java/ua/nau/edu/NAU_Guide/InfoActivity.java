package ua.nau.edu.NAU_Guide;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import ua.nau.edu.Enum.EnumSharedPreferences;
import ua.nau.edu.Enum.EnumSharedPreferencesVK;

public class InfoActivity extends BaseNavigationDrawerActivity {
    private static final String APP_PREFERENCES = EnumSharedPreferences.APP_PREFERENCES.toString();
    private static final String VK_PREFERENCES = EnumSharedPreferencesVK.VK_PREFERENCES.toString();
    private static final String VK_INFO_KEY = EnumSharedPreferencesVK.VK_INFO_KEY.toString();
    private static final String VK_EMAIL_KEY = EnumSharedPreferencesVK.VK_EMAIL_KEY.toString();
    private static final String CORP_ID_KEY = EnumSharedPreferences.CORP_ID_KEY.toString();
    private static final String CORP_LABEL_KEY = EnumSharedPreferences.CORP_LABEL_KEY.toString();


    private SharedPreferences settings = null;
    private SharedPreferences settingsVK = null;

    public InfoActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (getIntent().getIntExtra(CORP_ID_KEY, -1)) {
            case 1: {
                setContentView(R.layout.activity_info_1);
                setLabel();

                break;
            }
            case 2: {
                setContentView(R.layout.activity_info_2);
                setLabel();

                break;
            }
            case 3: {
                setContentView(R.layout.activity_info_3);
                setLabel();

                break;
            }
            case 4: {
                setContentView(R.layout.activity_info_4);
                setLabel();

                break;
            }
            case 5: {
                setContentView(R.layout.activity_info_5);
                setLabel();

                break;
            }
            case 6: {
                setContentView(R.layout.activity_info_6);
                setLabel();

                break;
            }
            case 7: {
                setContentView(R.layout.activity_info_7);
                setLabel();

                break;
            }
            case 8: {
                setContentView(R.layout.activity_info_8);
                setLabel();

                break;
            }
            case 9: {
                setContentView(R.layout.activity_info_9);
                setLabel();

                break;
            }
            case 10: {
                setContentView(R.layout.activity_info_10);
                setLabel();

                break;
            }
            case 11: {
                setContentView(R.layout.activity_info_11);
                setLabel();

                break;
            }
            case 12: {
                setContentView(R.layout.activity_info_12);
                setLabel();

                break;
            }

            default: {
                break;
            }
        }

// Get and set system services & Buttons & SharedPreferences & Requests
        settings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        settingsVK = getSharedPreferences(VK_PREFERENCES, MainActivity.MODE_PRIVATE);

        /*getDrawer(
                settingsVK.getString(VK_INFO_KEY, ""),
                settingsVK.getString(VK_EMAIL_KEY, "")
        );*/

        ScrollView scroll_main = (ScrollView) findViewById(R.id.scroll_main);

/*****************************/
        final RelativeLayout layout_vectors = (RelativeLayout) findViewById(R.id.body_info_contacts);
        layout_vectors.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                final TextView text_contacts_head = (TextView) findViewById(R.id.textView11);
                final ImageView arrow_contacts = (ImageView) findViewById(R.id.imageView2);
                final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout_vectors.getLayoutParams();
                final int layout_vectors_size = layout_vectors.getHeight();
                ViewTreeObserver vto = layout_vectors.getViewTreeObserver();

                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        layout_vectors.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                });

                params.height = 0;
                layout_vectors.setLayoutParams(params);

                text_contacts_head.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (params.height == 0) {
                            params.height = layout_vectors_size;
                            layout_vectors.setLayoutParams(params);
                            rotateImageView(arrow_contacts, 180);
                        } else {
                            params.height = 0;
                            layout_vectors.setLayoutParams(params);
                            rotateImageView(arrow_contacts, 0);
                        }

                        return false;
                    }
                });

                removeOnGlobalLayoutListener(layout_vectors, this);
            }
        });

/*****************************/

/*****************************/
        final TextView text_vectors = (TextView) findViewById(R.id.textView5);
        final TextView text_vectors_head = (TextView) findViewById(R.id.textView10);
        final ImageView arrow_vectors = (ImageView) findViewById(R.id.fab_2);
        final int text_vectors_size = text_vectors.getHeight();

        text_vectors.setMovementMethod(new ScrollingMovementMethod());
        text_vectors.setHeight(0);

        text_vectors_head.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (text_vectors.getHeight() == 0) {
                    text_vectors.setHeight(200);
                    rotateImageView(arrow_vectors, 180);
                } else {
                    text_vectors.setHeight(0);
                    rotateImageView(arrow_vectors, 0);
                }

                return false;
            }
        });

        scroll_main.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                text_vectors.getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });

        text_vectors.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                text_vectors.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
/*****************************/

    }

    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener victim) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            removeLayoutListenerJB(v, victim);
        } else removeLayoutListener(v, victim);
    }

    @SuppressWarnings("deprecation")
    private static void removeLayoutListenerJB(View v, ViewTreeObserver.OnGlobalLayoutListener victim) {
        v.getViewTreeObserver().removeGlobalOnLayoutListener(victim);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static void removeLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener victim) {
        v.getViewTreeObserver().removeOnGlobalLayoutListener(victim);
    }

    private void rotateImageView(ImageView imgview, float degree) {
        final RotateAnimation rotateAnim = new RotateAnimation(0.0f, degree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        rotateAnim.setDuration(0);
        rotateAnim.setFillAfter(true);
        imgview.startAnimation(rotateAnim);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void toastShowLong(String TEXT) {
        Toast.makeText(getApplicationContext(), TEXT, Toast.LENGTH_LONG).show();
    }

    public void toastShowShort(String TEXT) {
        Toast.makeText(getApplicationContext(), TEXT, Toast.LENGTH_SHORT).show();
    }

    public void setLabel() {
        this.setTitle(Integer.toString(getIntent().getIntExtra(CORP_ID_KEY, -1)) +
                getString(R.string.corp) +
                ", " +
                getIntent().getStringExtra(CORP_LABEL_KEY));
    }
}