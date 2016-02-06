package ua.nau.edu.Adapters.AdapterInfoActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ua.nau.edu.Adapters.AdapterInfoActivity.Fragments.FragmentInfo;
import ua.nau.edu.Adapters.AdapterInfoActivity.Fragments.FragmentVectors;

/**
 * Created by Roman on 27.11.2015.
 */
public class AdapterInfo extends FragmentPagerAdapter {

    public AdapterInfo(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return (position == 0) ? "Информация" : "Кафедры";
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {
        return (position == 0) ? new FragmentInfo() : new FragmentVectors();
    }
}