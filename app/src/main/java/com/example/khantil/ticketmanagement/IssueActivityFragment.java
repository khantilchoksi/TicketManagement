package com.example.khantil.ticketmanagement;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

/**
 * A placeholder fragment containing a simple view.
 */
public class IssueActivityFragment extends Fragment {

    private ArrayAdapter<String> issueAdapter;
    private final String LOG_TAG = NewTicketActivityFragment.class.getSimpleName();
    private int selectedProjectID;
    private int[] issueIds;
    private String[] issueNameStr;

    public IssueActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_issue, container, false);

        Intent receiveProjectIntent = getActivity().getIntent();
        final String projectName;


        if(receiveProjectIntent!= null){
            Bundle extrasInfo = receiveProjectIntent.getExtras();
            selectedProjectID = extrasInfo.getInt("projectId");
            projectName = extrasInfo.getString("projectTitle");
            ((TextView) rootView.findViewById(R.id.projectTitleOnIssue)).setText("for  "+ projectName);
        }else{
            projectName = "Project Title";
        }

        issueAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_issue,
                R.id.issueName,
                new ArrayList<String>()
        );


        ListView listView = (ListView)rootView.findViewById(R.id.issueListView);
        listView.setAdapter(issueAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String issueType = issueAdapter.getItem(position);

                Intent intent = new Intent(getActivity(), TicketDetailsActivity.class);
                Bundle extras = new Bundle();       //parcelable
                extras.putString("Project Name", projectName);  //key - value pair
                extras.putString("Issue Type", issueNameStr[position]);
                extras.putInt("projectId", selectedProjectID);
                extras.putInt("issueId",issueIds[position]);
                intent.putExtras(extras);

                startActivity(intent);

            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(Utility.isOnline(getContext())){
            //Call the Async Task
            IssuesTask myIssueTask = new IssuesTask();
            myIssueTask.execute();
        }

    }

    /**
     * Represents an asynchronous task used to populate clients project titles
     */
    public class IssuesTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String issuesStr;

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

                final String ISSUE_BASE_URL = getResources().getString(R.string.base_url).concat("issue/getIssues");
                URL url = new URL(ISSUE_BASE_URL);

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                urlConnection.setDoInput(true);
                //We want to retrieve all the issues
//                urlConnection.setDoOutput(true);
//
//                Uri.Builder builder = new Uri.Builder();
//                Map<String, String> parameters = new HashMap<>();
//                parameters.put("q", ""+clientId);
//
//                // encode parameters
//                Iterator entries = parameters.entrySet().iterator();
//                while (entries.hasNext()) {
//                    Map.Entry entry = (Map.Entry) entries.next();
//                    builder.appendQueryParameter(entry.getKey().toString(), entry.getValue().toString());
//                    entries.remove();
//                }
//                String requestBody = builder.build().getEncodedQuery();
//                Log.d(LOG_TAG, "Post parameters : " + requestBody);
//
//                //OutputStream os = urlConnection.getOutputStream();
//                OutputStream os = new BufferedOutputStream(urlConnection.getOutputStream());
//                BufferedWriter writer = new BufferedWriter(
//                        new OutputStreamWriter(os, "UTF-8"));
//                writer.write(requestBody);
//
//                writer.flush();
//                writer.close();
//                os.close();

                urlConnection.connect();

                // Read the input stream into a String
                //InputStream inputStream = urlConnection.getInputStream();
                InputStream inputStream;
                int status = urlConnection.getResponseCode();
                Log.d(LOG_TAG,"URL Connection Response Code "+status);

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

                issuesStr = buffer.toString();

                Log.d(LOG_TAG, "Issues JSON String : " + issuesStr);

                return getIssuesFromJson(issuesStr);

            } catch (IOException e) {
                Log.d(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } catch (JSONException e){
                Log.d(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
                return null;
            }
            finally {
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

        @Override
        protected void onPostExecute(String[] issuesStr) {

            Log.d(LOG_TAG,"issues Received from server: "+issuesStr);

            if (issuesStr != null) {
                //Log.d(LOG_TAG,"Inside Success: "+success.toString());
                issueAdapter.clear();

                for (String issueString : issuesStr) {
                    issueAdapter.add(issueString);
                }

            } else {
                //Toast.makeText(getActivity(), "You don't have any projects!", Toast.LENGTH_LONG).show();
            }
        }


        private String[] getIssuesFromJson(String issuesStr) throws JSONException{

            final String issuesList = "ticketissue";
            final String issueId = "issue_id";
            final String issueName = "issue_name";

            JSONObject issuesJson = new JSONObject(issuesStr);

            JSONArray issuesJsonArray = issuesJson.getJSONArray(issuesList);

            issueNameStr = new String[issuesJsonArray.length()];
            issueIds = new int[issuesJsonArray.length()];

            for(int i=0; i<issuesJsonArray.length(); i++){
                JSONObject issueJsonObj = issuesJsonArray.getJSONObject(i);

                issueNameStr[i] = issueJsonObj.getString(issueName);
                issueIds[i] = issueJsonObj.getInt(issueId);
            }

            return issueNameStr;
        }

    }
}
