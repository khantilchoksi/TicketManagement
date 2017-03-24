package com.example.khantil.ticketmanagement;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShowTicketActivityFragment extends Fragment {
    public ShowImageArrayAdapter showImageArrayAdapter;
    private TextView descriptiveText;
    private TextView projectTitleText;
    private TextView issueTypeText;
    private TextView personNameText;
    private TextView personNumberText;
    private TextView personEmailText;

/*    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            // loading of the bitmap was a success
            // TODO do some action with the bitmap
            issueImageArrayAdapter.add(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            // loading of the bitmap failed
            // TODO do some action/warning/error message
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };*/

//    private Target[] targets = new Target[3];

/*    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };*/

    public ShowTicketActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_show_ticket, container, false);

        Intent receiveTicketIntent = getActivity().getIntent();

        if(receiveTicketIntent!= null && receiveTicketIntent.hasExtra("ticket_obj")){
            final Ticket detailTicket = (Ticket) receiveTicketIntent.getParcelableExtra("ticket_obj");

            /*CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
            collapsingToolbarLayout.setTitle(detailTicket.ticketTitle);*/

//            ArrayList<String> thumbPaths = new ArrayList<>(Arrays.asList(detailTicket.imagePaths));

            showImageArrayAdapter = new ShowImageArrayAdapter(getActivity(), new ArrayList<String>());

            GridView gridView = (GridView) rootView.findViewById(R.id.showIssueGridView);
            gridView.setAdapter(showImageArrayAdapter);

//            String clientIssueImageBasePath = getResources().getString(R.string.base_url).concat("uploadedimages/thumbnails/");

            //No photos text to be shown or not
            if(detailTicket.imagePaths[0].isEmpty()){
                //No photos are there for this ticket
                TextView noPhotosTextView = (TextView) rootView.findViewById(R.id.noPhotosTextView);
                noPhotosTextView.setVisibility(View.VISIBLE);
            }else{
                gridView.setVisibility(View.VISIBLE);
            }


            for(int i =0; i<3 ; i++){
                //First check if the image is there or not
                if(detailTicket.imagePaths[i].isEmpty())
                {
                    break;
                }


                showImageArrayAdapter.add(detailTicket.imagePaths[i]);
//                final int finalI = i;
//                targets[i] = new Target() {
//                    @Override
//                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
////                        issueImageArrayAdapter.add(bitmap);
//                        //To add the bitmaps in the array exactly in the same position as in database tables
//                        issueImageArrayAdapter.insert(bitmap, finalI);
//                    }
//
//                    @Override
//                    public void onBitmapFailed(Drawable errorDrawable) {
//
//                    }
//
//                    @Override
//                    public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//                    }
//                };
//                Log.d("Log", "Image Path " + " : +" + detailTicket.imagePaths[i]);
//                String clientIssueImagePath = clientIssueImageBasePath.concat(detailTicket.imagePaths[i]);
//
//                Picasso.with(getContext()).load(clientIssueImagePath).into(targets[i]);
            }



//            ((TextView) rootView.findViewById(R.id.showTicketTitle)).setText(detailTicket.ticketTitle);
            descriptiveText = (TextView) rootView.findViewById(R.id.showTicketDescription);
            projectTitleText = (TextView) rootView.findViewById(R.id.showProjectTitle);
            issueTypeText = (TextView) rootView.findViewById(R.id.showIssueType);
            personNameText = (TextView) rootView.findViewById(R.id.showContactPersonName);
            personNumberText = (TextView) rootView.findViewById(R.id.showContactPersonNumber);
            personEmailText = (TextView) rootView.findViewById(R.id.showContactPersonEmail);

//            ((TextView) rootView.findViewById(R.id.showTicketStatus)).setText(detailTicket.status);


            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent pagerIntent = new Intent(getActivity(), IssueImagesPagerActivity.class);
//                    pagerIntent.putExtra("IssueImageAdapter",issueImageArrayAdapter);
                    Bundle bundle = new Bundle();
                    bundle.putStringArray("galleryIssues", detailTicket.imagePaths);
                    bundle.putInt("size", showImageArrayAdapter.getCount());
                    bundle.putInt("currentPosition", position);
                    pagerIntent.putExtras(bundle);
                    startActivity(pagerIntent);
                }
            });

            ShowTicketAsyncTask showTicketAsyncTask = new ShowTicketAsyncTask();
            showTicketAsyncTask.execute(detailTicket);

        }

/*        final NestedScrollView scrollview = ((NestedScrollView) rootView.findViewById(R.id.nestedScrollView));
        scrollview.post(new Runnable() {
            @Override
            public void run() {
                scrollview.fullScroll(ScrollView.FOCUS_UP);
            }
        });*/

        return rootView;
    }


    private class ShowTicketAsyncTask extends AsyncTask<Ticket,Void,String[]>{

        @Override
        protected String[] doInBackground(Ticket... params) {

            Ticket ticket = params[0];
            String[] ticketDetailsString = new String[6];
            ticketDetailsString[0] = ticket.projectName;
            ticketDetailsString[1] = ticket.issueName;
            ticketDetailsString[2] = ticket.ticketDescription;
            ticketDetailsString[3] = ticket.contactName;
            ticketDetailsString[4] = ticket.contactNumber;
            ticketDetailsString[5] = ticket.contactEmail;
            return ticketDetailsString;
        }

        @Override
        protected void onPostExecute(String[] details) {

            projectTitleText.setText(details[0]);
            issueTypeText.setText(details[1]);
            descriptiveText.setText(details[2]);
            personNameText.setText(details[3]);
            personNumberText.setText(details[4]);
            personEmailText.setText(details[5]);
        }
    }

}
