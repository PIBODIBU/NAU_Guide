package ua.nau.edu.NAU_Guide;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGImageView;
import com.caverock.androidsvg.SVGParseException;

public class FloorActivity extends BaseNavigationDrawerActivity {
    public FloorActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor);

        try {
            SVG svg = SVG.getFromResource(this, R.raw.splatter);

            SVGImageView svgImageView = new SVGImageView(this);
            svgImageView.setSVG(svg);
            svgImageView.setLayoutParams(
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));

            RelativeLayout layout =
                    (RelativeLayout) findViewById(R.id.main_layout);

            layout.addView(svgImageView);

        } catch (SVGParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
