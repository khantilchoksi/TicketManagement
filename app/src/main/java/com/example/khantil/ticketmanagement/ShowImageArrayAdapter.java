package com.example.khantil.ticketmanagement;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

/**
 * Created by Khantil on 28-05-2016.
 */
public class ShowImageArrayAdapter extends ArrayAdapter<String> {

    Context mContext;

    public ShowImageArrayAdapter(Activity context, List<String> imagePaths) {
        super(context, 0, imagePaths);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String fileName = getItem(position);

        String clientIssueImageBasePath = mContext.getResources().getString(R.string.base_url).concat("uploads/tickets/thumbs/thumb_");

        String clientIssueImagePath = clientIssueImageBasePath.concat(fileName);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_issue_image, parent, false);
        }

        final ImageView thumbImage = (ImageView) convertView.findViewById(R.id.issueImageItem);

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                thumbImage.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        Picasso.with(getContext()).load(clientIssueImagePath).into(target);

        return convertView;
    }
}
