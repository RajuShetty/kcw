package com.kannadachristianwallpapers.app.fragment;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ProgressBar;

import com.kannadachristianwallpapers.app.R;
import com.kannadachristianwallpapers.app.utils.AppUtility;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {

    private ProgressBar progressBar;

    public BaseFragment() {
        // Required empty public constructor
    }

    protected void initProgressBar(View view) {
        progressBar = view.findViewById(R.id.progressBar);
    }

    protected void showProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    protected void hideProgressBar() {
        if (progressBar != null && progressBar.isShown()) {
            progressBar.setVisibility(View.GONE);
        }
    }

    protected void showServerError(View view) {
        AppUtility.showSnackBar(view, getString(R.string.server_error));
    }

    protected void noInternetWarning(View view, Context context) {
        AppUtility.noInternetWarning(view, context);
    }

}
