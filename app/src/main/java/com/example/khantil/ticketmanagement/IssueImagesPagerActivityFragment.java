package com.example.khantil.ticketmanagement;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

/**
 * A placeholder fragment containing a simple view.
 */
public class IssueImagesPagerActivityFragment extends Fragment {
    private ImagePagerAdapter imagePagerAdapter;

    private View progressView;
    private ViewPager viewPager;

    public IssueImagesPagerActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_issue_images_pager, container, false);

        progressView = rootView.findViewById(R.id.imagePagerProgress);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);

        Intent receiveIntent = getActivity().getIntent();

        if(receiveIntent != null){
            Bundle bundle = receiveIntent.getExtras();
            String[] paths = bundle.getStringArray("galleryIssues");
            int size = bundle.getInt("size");
            int currentPosition = bundle.getInt("currentPosition");


            Log.d("Log","Received Intent & issue image adapter");
            imagePagerAdapter = new ImagePagerAdapter(getContext(),paths,size,this);

            viewPager.setAdapter(imagePagerAdapter);
            viewPager.setCurrentItem(currentPosition);

        }
        return rootView;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            viewPager.setVisibility(show ? View.GONE : View.VISIBLE);
            viewPager.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    viewPager.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            viewPager.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
