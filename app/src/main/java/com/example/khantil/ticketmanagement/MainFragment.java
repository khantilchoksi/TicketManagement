package com.example.khantil.ticketmanagement;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class MainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private TicketsAdapter ticketsAdapter;
    private TicketsArrayAdapter ticketsArrayAdapter;

    public static String filteredProjectIdTag = "filteredResultProjectId";
    public static String filteredProjectTitleTag = "filteredResultProjectTitle";
    private static final int PROJECT_FILTER_REQUEST = 1;

    private static int filteredProjectId = 0;
    private static String fileteredProjectTitle = "";

    private  ViewTicketsTask viewTicketsTask;

    private RelativeLayout filterRelativeLayout;
    private TextView filteredProjectNameTextView;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private final String LOG_TAG = MainFragment.class.getSimpleName();

    public MainFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ticketsArrayAdapter.getFilter().filter(query);
                Log.d(LOG_TAG,"Search Submit Applied");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ticketsArrayAdapter.getFilter().filter(newText);
                Log.d(LOG_TAG, "Search Text Change Applied");
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_logout:{
                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences settings = getActivity().getSharedPreferences(getResources().getString(R.string.login_pref), getActivity().MODE_PRIVATE);
                                SharedPreferences.Editor editor = settings.edit();

                                //Set "hasLoggedIn" to false on logout
                                editor.putBoolean("hasLoggedIn", false);
                                editor.commit();

                                //Calling login activity
                                Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                                loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(loginIntent);

                                getActivity().finish();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();

                break;
            }

            case R.id.action_filter:{
                Intent filterIntent = new Intent(getActivity(), ProjectFilter.class);
                startActivityForResult(filterIntent, PROJECT_FILTER_REQUEST);
                break;
            }
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case PROJECT_FILTER_REQUEST:{
                Log.d(LOG_TAG,"Returned on activity result");
                if(resultCode == Activity.RESULT_OK){



                    filteredProjectId = data.getIntExtra(filteredProjectIdTag,0);
                    fileteredProjectTitle = data.getStringExtra(filteredProjectTitleTag);

                    filteredProjectNameTextView.setText("Tickets shown for "+fileteredProjectTitle);

                    //set the above content
                    filterRelativeLayout.setVisibility(View.VISIBLE);

                    Log.d(LOG_TAG, "Returned project id" + filteredProjectId);
                    Toast.makeText(getActivity(),"Filtered project :"+filteredProjectId+" : "+fileteredProjectTitle,Toast.LENGTH_SHORT).show();

                    //cancel the current running Async task
                    viewTicketsTask.cancel(true);

                    ViewTicketsTask filteredTicketsTask = new ViewTicketsTask();
                    filteredTicketsTask.execute();

                }else if(resultCode == Activity.RESULT_CANCELED){
                    Log.d(LOG_TAG,"Returned project id CANCELLED"+filteredProjectId);
                }
                break;
            }
        }
    }
/*public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true  );
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        filterRelativeLayout = (RelativeLayout) rootView.findViewById(R.id.filterRelativeLayout);
        filterRelativeLayout.setVisibility(View.GONE);

        filteredProjectNameTextView = (TextView) rootView.findViewById(R.id.filteredTicketsTitle);


        //Clear filter image button
        ImageButton clearFilterImageButton = (ImageButton) rootView.findViewById(R.id.removeFilterButton);
        clearFilterImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filteredProjectId = 0;  //to show all the tickets
                ViewTicketsTask displayAllTicketsTask = new ViewTicketsTask();
                displayAllTicketsTask.execute();
                filterRelativeLayout.setVisibility(View.GONE);
            }
        });

        //Extracting the clientId
        Intent receiveLoginIntent = getActivity().getIntent();

        if (receiveLoginIntent != null) {

            ticketsArrayAdapter = new TicketsArrayAdapter(getActivity(), new ArrayList<Ticket>());

            ListView ticketListView = (ListView) rootView.findViewById(R.id.ticketListView);
            ticketListView.setAdapter(ticketsArrayAdapter);

            ticketListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Ticket ticket = ticketsArrayAdapter.getItem(position);
                    Toast.makeText(getActivity(), (CharSequence) ticket.toString(), Toast.LENGTH_SHORT).show();


                    Intent detailIntent = new Intent(getActivity(), ShowTicketActivity.class);
                    detailIntent.putExtra("ticket_obj", ticket);

                    // create the transition animation - the images in the layouts
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        View androidRobotView = view.findViewById(R.id.ticketTitle);
                        ActivityOptions options = ActivityOptions
                                .makeSceneTransitionAnimation(getActivity(), androidRobotView, getResources().getString(R.string.ticket_title_transition_name));
                        // start the new activity
                        startActivity(detailIntent, options.toBundle());
                    }else{
                        startActivity(detailIntent);
                    }

                }
            });



//            Log.d(LOG_TAG," Received Client ID : "+ LoginActivity.clientId);
//
//            String[] ticketTitlesData = {
//                    " Dummy Bug 1",
//                    " Dummy Bug 2",
//                    " Dummy Bug 3",
//                    " Dummy Bug 4",
//                    " Dummy Bug 5",
//                    " Dummy Bug 6",
//                    " Dummy Bug 7",
//                    " Dummy Bug 8",
//                    " Dummy Bug 9",
//                    " Dummy Bug 10",
//                    " Dummy Bug 11",
//                    " Dummy Bug 12",
//            };
//
//            String[] ticketStatusData = {
//                    " Opened",
//                    " Completed",
//                    " In Process",
//                    " Not Valid",
//                    " Opened",
//                    " Completed",
//                    " In Process",
//                    " Not Valid",
//                    " Opened",
//                    " Completed",
//                    " In Process",
//                    " Not Valid",
//            };
//
//            List<String> ticketTitleList = new ArrayList(Arrays.asList(ticketTitlesData));
//
//            List<String> ticketStatusList = new ArrayList(Arrays.asList(ticketStatusData));
//
//            ticketsAdapter = new TicketsAdapter(getActivity(), ticketTitleList, ticketStatusList);
//
//            ListView ticketListView = (ListView) rootView.findViewById(R.id.ticketListView);
//            ticketListView.setAdapter(ticketsAdapter);
//
//            ticketListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    String ticket =  ticketsAdapter.getItem(position).toString();
//                    Toast.makeText(getActivity(), ticket, Toast.LENGTH_SHORT).show();
//
//                    Intent detailIntent = new Intent(getActivity(), ShowTicketActivity.class);
//                    startActivity(detailIntent);
//                }
//            });


        }

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    /*public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            //mListener.onFragmentInteraction(uri);
        }
    }*/
    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }*/

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onStart() {
        super.onStart();

        if(Utility.isOnline(getContext())){
            //First check whether there is internet connection or not
            //If we have internet connection then and only then call the AsyncTask
            viewTicketsTask = new ViewTicketsTask();
            viewTicketsTask.execute();
        }

    }

    /*//The helper method to check if there is internet connection or not
    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(getActivity(), "No Internet connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }*/

    /**
     * Represents an asynchronous task used to populate clients project titles
     */
    public class ViewTicketsTask extends AsyncTask<Void, Void, Ticket[]> {

        @Override
        protected Ticket[] doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String ticketsJsonStr;

            try {
                // Construct the URL for the Client Login Credentials query
                //GET Method
                /*final String CLIENT_BASE_URL =
                        //"http://192.168.227.1/mylaravel/blog/public/index.php/client?q=";
                        "http://192.168.0.154/mylaravel/blog/public/index.php/client?q=";


                Uri builtUri = Uri.parse( CLIENT_BASE_URL.concat(mEmail) ).buildUpon().build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "BUILT URI " + builtUri.toString());

                // Create the request to my webservice, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();*/

                final String TICKET_BASE_URL = getResources().getString(R.string.base_url).concat("ticket/getAllTickets");
                URL url = new URL(TICKET_BASE_URL);

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder();
                Map<String, String> parameters = new HashMap<>();
                parameters.put("c", "" + LoginActivity.clientId);
                parameters.put("pid", String.valueOf(filteredProjectId));

                // encode parameters
                Iterator entries = parameters.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry entry = (Map.Entry) entries.next();
                    builder.appendQueryParameter(entry.getKey().toString(), entry.getValue().toString());
                    entries.remove();
                }
                String requestBody = builder.build().getEncodedQuery();
                Log.d(LOG_TAG, "Post parameters : " + requestBody);

                //OutputStream os = urlConnection.getOutputStream();
                OutputStream os = new BufferedOutputStream(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(requestBody);

                writer.flush();
                writer.close();
                os.close();

                urlConnection.connect();

                // Read the input stream into a String
                //InputStream inputStream = urlConnection.getInputStream();
                InputStream inputStream;
                int status = urlConnection.getResponseCode();
                Log.d(LOG_TAG, "URL Connection Response Code " + status);

                //if(status >= 400)
                //  inputStream = urlConnection.getErrorStream();
                //else
                inputStream = urlConnection.getInputStream();


                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                ticketsJsonStr = buffer.toString();

                Log.d(LOG_TAG, "Tickets JSON String : " + ticketsJsonStr);

                return getTicketsFromJson(ticketsJsonStr);

            } catch (IOException e) {
                Log.d(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } catch (JSONException e) {
                Log.d(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.d(LOG_TAG, "Error closing stream", e);
                    }
                }

            }

        }

        private Ticket[] getTicketsFromJson(String ticketsJsonStr) throws JSONException {

            final String ticketsList = "ticketdetails";

            JSONObject ticketsJson = new JSONObject(ticketsJsonStr);

            JSONArray ticketsJsonArray = ticketsJson.getJSONArray(ticketsList);

            Ticket[] tickets = new Ticket[ticketsJsonArray.length()];
            long tid;   //ticket id
            String ticketTitle;
            int cid;    //Client Id
            int pid;       //Project Id
            String projectName;
            int issueId;    //issueId
            String issueName;
            String ticketDescription;
            String status;
            String contactName;
            String contactNumber;
            String contactEmail;
            String imagePath1;
            String imagePath2;
            String imagePath3;

            for (int i = 0; i < ticketsJsonArray.length(); i++) {

                //Check if the Async Task has been cancelled due to filter or not
                if(isCancelled())
                    break;

                JSONObject ticketJSONObject = ticketsJsonArray.getJSONObject(i);
                tid = ticketJSONObject.getLong("ticket_id");
                ticketTitle = ticketJSONObject.getString("ticket_title");
                cid = ticketJSONObject.getInt("client_id");
                pid = ticketJSONObject.getInt("project_id");
                issueId = ticketJSONObject.getInt("issue_id");
                ticketDescription = ticketJSONObject.getString("ticket_description");
                status = ticketJSONObject.getString("status");
                contactName = ticketJSONObject.getString("contact_person_name");
                contactNumber = ticketJSONObject.getString("contact_person_number");
                contactEmail = ticketJSONObject.getString("contact_person_email");
                imagePath1 = ticketJSONObject.getString("fullpath1");
                imagePath2 = ticketJSONObject.getString("fullpath2");
                imagePath3 = ticketJSONObject.getString("fullpath3");
                projectName = ticketJSONObject.getString("project_title");
                issueName = ticketJSONObject.getString("issue_name");


                tickets[i] = new Ticket(tid, ticketTitle, cid, pid, projectName, issueId, issueName, ticketDescription, status, contactName, contactNumber, contactEmail,imagePath1,imagePath2,imagePath3);
                Log.d(LOG_TAG, tickets[i].toString());
            }

            return tickets;
        }

        @Override
        protected void onPostExecute(Ticket[] tickets) {

            if (tickets != null) {
                ticketsArrayAdapter.clear();

                for (Ticket t : tickets) {
                    ticketsArrayAdapter.add(t);
                }

            } else {
                Toast.makeText(getActivity(), "You don't have any tickets! Create a new ticket.", Toast.LENGTH_LONG).show();
            }
        }

    }

}

