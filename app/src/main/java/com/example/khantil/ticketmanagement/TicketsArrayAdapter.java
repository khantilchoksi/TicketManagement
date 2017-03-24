package com.example.khantil.ticketmanagement;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by Khantil on 25-05-2016.
 */
public class TicketsArrayAdapter extends ArrayAdapter<Ticket> implements Filterable {
    private ArrayList<Ticket> mObjects;
    private Context mContext;
//    private Filter customFilter;
    public TicketsArrayAdapter(Activity context, ArrayList<Ticket> objects) {
        super(context, 0, objects);
        this.mObjects = objects;
        this.mContext = context;
        /*customFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                Log.d("Search:","Constraint : "+constraint);
                FilterResults filterResults = new FilterResults();
                ArrayList<Ticket> tempList = new ArrayList<>();
                //constraint is the result from text you want to filter against.
                //objects is your data set you will filter from
                if(constraint != null && mObjects!=null) {
                    int length=mObjects.size();
                    int i=0;
                    while(i<length){
                        Ticket item = mObjects.get(i);
                        //do whatever you wanna do here
                        //adding result set output array
                        Log.d("Search:","Serach got ticket for check "+item.ticketTitle);
                        if(item.ticketTitle.toLowerCase().contains(constraint)){
                            Log.d("Search:","Item Added "+item.ticketTitle);
                            tempList.add(item);
                        }

                        i++;
                    }
                    //following two lines is very important
                    //as publish result can only take FilterResults objects
                    filterResults.values = tempList;
                    filterResults.count = tempList.size();
                }
                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                Log.d("Search:","Called publish results, with mObjects"+mObjects.toString());
                mObjects = (ArrayList<Ticket>) results.values;
                if (results.count > 0) {
                    notifyDataSetChanged();
                    Log.d("Search:","Notified Data Set Changed");
                } else {
                    notifyDataSetInvalidated();
                    Log.d("Search:", "Notified Data Set Invalidated");
                }
            }
        };*/
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Ticket ticket = getItem(position);

        if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_ticket, parent, false);
        }

        TextView title = ((TextView) convertView.findViewById(R.id.ticketTitle));
        TextView status = ((TextView) convertView.findViewById(R.id.ticketStatus));
        title.setText(ticket.ticketTitle);
        status.setText("Status : " + ticket.status);

        return convertView;
    }

    /*@Override
    public Filter getFilter() {
        return customFilter;
    }*/
}
