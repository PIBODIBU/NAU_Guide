package ua.nau.edu.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ua.nau.edu.NAU_Guide.R;

/**
 * Created by Roman on 11.12.2015.
 */

public class MapsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View mainFragmentView = inflater.inflate(R.layout.fragment_maps, container, false);

        return mainFragmentView;
    }
}
