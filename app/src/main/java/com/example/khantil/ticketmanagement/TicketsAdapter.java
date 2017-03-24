package com.example.khantil.ticketmanagement;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Khantil on 10-05-2016.
 */
public class TicketsAdapter extends BaseAdapter {

    LayoutInflater ticketInflater;
    List<String> ticketsTitleList;
    List<String> ticketsStatusList;

    public TicketsAdapter(Context context, List<String> ticketsTitleList, List<String> ticketsStatusList){
        ticketInflater = LayoutInflater.from(context);
        this.ticketsTitleList = ticketsTitleList;
        this.ticketsStatusList = ticketsStatusList;
    }

    @Override
    public int getCount() {
        return ticketsTitleList.size();
    }

    @Override
    public String getItem(int position) {
        return ticketsTitleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView title;
        TextView status;

        if(convertView == null){
            convertView = ticketInflater.inflate(R.layout.list_view_ticket, null);
        }

        title = ((TextView) convertView.findViewById(R.id.ticketTitle));
        status = ((TextView) convertView.findViewById(R.id.ticketStatus));
        title.setText(ticketsTitleList.get(position));
        status.setText("Status : "+ticketsStatusList.get(position));

        return convertView;
    }
}
