package ua.nau.edu.Systems;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.widget.EditText;

public class SearchViewUtils {
    public static void setHintColor(Context context, SearchView searchView, int color) {
        ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text))
                .setHintTextColor(ContextCompat.getColor(context, color));
    }
}
