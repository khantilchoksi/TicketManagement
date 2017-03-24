package com.example.khantil.ticketmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShowTicketActivity extends AppCompatActivity{

/*    @Bind(R.id.app_bar_layout)
    AppBarLayout appBarLayout;

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.toolbar_header_view)
    HeaderView toolbarHeaderView;

    @Bind(R.id.float_header_view)
    HeaderView floatHeaderView;

    private boolean isHideToolbarView = false;*/



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ticket);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        ButterKnife.bind(this);
/*        collapsingToolbarLayout.setTitle(" ");
        appBarLayout.addOnOffsetChangedListener(this);*/

        Intent receiveTicketIntent = getIntent();

        if(receiveTicketIntent!= null && receiveTicketIntent.hasExtra("ticket_obj")) {
            final Ticket detailTicket = (Ticket) receiveTicketIntent.getParcelableExtra("ticket_obj");

            toolbar.setTitle(detailTicket.ticketTitle);

            //For collapsible tool bar
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
            collapsingToolbarLayout.setTitle(detailTicket.ticketTitle);

            TextView statusToolbarView = (TextView) findViewById(R.id.toolbarStatus);
            statusToolbarView.setText("Status : "+detailTicket.status);
/*
            toolbarHeaderView.bindTo(detailTicket.ticketTitle, "Status : "+detailTicket.status);
            floatHeaderView.bindTo(detailTicket.ticketTitle, "Status : "+detailTicket.status);*/
        }

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

/*    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
*//*        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        if (percentage == 1f && isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.VISIBLE);
            isHideToolbarView = !isHideToolbarView;

        } else if (percentage < 1f && !isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.GONE);
            isHideToolbarView = !isHideToolbarView;
        }*//*
    }*/
}
