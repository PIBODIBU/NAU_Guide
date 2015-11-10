package ua.nau.edu.NAU_Guide;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

import ua.nau.edu.Systems.SVG;
import ua.nau.edu.Systems.SVGParser;

public class FloorActivity extends BaseNavigationDrawerActivity {
    public FloorActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor);

        SVG svg = SVGParser.getSVGFromResource(getResources(), R.raw.splatter);
        Drawable svgDrawable = svg.createPictureDrawable();

        ImageView map = (ImageView) findViewById(R.id.map);
        map.setImageDrawable(svgDrawable);
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
