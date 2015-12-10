package ua.nau.edu.Fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gc.materialdesign.views.CustomView;
import com.squareup.picasso.Picasso;

import ua.nau.edu.NAU_Guide.MainActivity;
import ua.nau.edu.NAU_Guide.MapsActivity;
import ua.nau.edu.NAU_Guide.R;

/**
 * Created by Roman on 04.12.2015.
 */
public class MainFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View mainFragmentView = inflater.inflate(R.layout.fragment_main, container, false);
        final Context mainFragmentContext = getActivity().getApplicationContext();
        final MainActivity supportActivity = (MainActivity) getActivity();

        //Thread.currentThread().setName("Thread MainActivity Layout setUp");            /**Thread name**/

        ImageView image_1 = (ImageView) mainFragmentView.findViewById(R.id.image_1);
        ImageView image_2 = (ImageView) mainFragmentView.findViewById(R.id.image_2);
        ImageView image_3 = (ImageView) mainFragmentView.findViewById(R.id.image_3);
        ImageView image_4 = (ImageView) mainFragmentView.findViewById(R.id.image_4);
        ImageView image_5 = (ImageView) mainFragmentView.findViewById(R.id.image_5);
        ImageView image_6 = (ImageView) mainFragmentView.findViewById(R.id.image_6);
        ImageView image_7 = (ImageView) mainFragmentView.findViewById(R.id.image_7);
        ImageView image_8 = (ImageView) mainFragmentView.findViewById(R.id.image_8);
        ImageView image_9 = (ImageView) mainFragmentView.findViewById(R.id.image_9);
        ImageView image_10 = (ImageView) mainFragmentView.findViewById(R.id.image_10);
        ImageView image_11 = (ImageView) mainFragmentView.findViewById(R.id.image_11);
        ImageView image_12 = (ImageView) mainFragmentView.findViewById(R.id.image_12);

        image_1.setImageDrawable(ContextCompat.getDrawable(mainFragmentContext, R.drawable.gerb_3));
        image_2.setImageDrawable(ContextCompat.getDrawable(mainFragmentContext, R.drawable.gerb_3));
        image_3.setImageDrawable(ContextCompat.getDrawable(mainFragmentContext, R.drawable.gerb_3));
        image_4.setImageDrawable(ContextCompat.getDrawable(mainFragmentContext, R.drawable.gerb_3));
        image_5.setImageDrawable(ContextCompat.getDrawable(mainFragmentContext, R.drawable.gerb_3));
        image_6.setImageDrawable(ContextCompat.getDrawable(mainFragmentContext, R.drawable.gerb_3));
        image_7.setImageDrawable(ContextCompat.getDrawable(mainFragmentContext, R.drawable.gerb_3));
        image_8.setImageDrawable(ContextCompat.getDrawable(mainFragmentContext, R.drawable.gerb_3));
        image_9.setImageDrawable(ContextCompat.getDrawable(mainFragmentContext, R.drawable.gerb_3));
        image_10.setImageDrawable(ContextCompat.getDrawable(mainFragmentContext, R.drawable.gerb_3));
        image_11.setImageDrawable(ContextCompat.getDrawable(mainFragmentContext, R.drawable.gerb_3));
        image_12.setImageDrawable(ContextCompat.getDrawable(mainFragmentContext, R.drawable.gerb_3));

        return mainFragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}