package com.example.khantil.ticketmanagement;

import android.content.ContentValues;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by Khantil on 27-05-2016.
 */
public class ImagePagerAdapter extends PagerAdapter {
    Context mContext;
    LayoutInflater mLayoutInflater;
    String[] fullImagePaths;
    int mSize;
//    int mResources[];
    IssueImagesPagerActivityFragment mIssueImagesPagerActivityFragment;

    public ImagePagerAdapter(Context context, String[] fullImagePaths, int size, IssueImagesPagerActivityFragment issueImagesPagerActivityFragment){
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.fullImagePaths = fullImagePaths;
        this.mSize = size;
        this.mIssueImagesPagerActivityFragment = issueImagesPagerActivityFragment;
//        mIssueImagesPagerActivityFragment.showProgress(true);
//        mResources = new int[]{R.drawable.ic_delete_forever_black_18dp,
//                R.drawable.ic_launcher,
//        R.drawable.ic_delete_forever_black_36dp,};
//        mIssueImagesPagerActivityFragment.showProgress(false);
    }
    @Override
    public int getCount() {
        return mSize;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate( R.layout.pager_item, container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.pagerImageView);

        String clientIssueImageBasePath = mContext.getResources().getString(R.string.base_url).concat("uploads/tickets/full/")
                .concat(fullImagePaths[position]);

        Picasso.with(mContext).load(clientIssueImageBasePath).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                Log.d("Success","Image loaded successfully.");
//                mIssueImagesPagerActivityFragment.showProgress(false);
            }

            @Override
            public void onError() {
                Toast.makeText(mContext,"Issue downloading image!",Toast.LENGTH_SHORT).show();
            }
        });

        container.addView(itemView);
        return  itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
