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
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
public class NewTicketActivityFragment extends Fragment {

//    private int clientId;
    private final String LOG_TAG = NewTicketActivityFragment.class.getSimpleName();

    private ArrayAdapter<String> projectArrayAdapter;

    private int[] projectIds;
    private String[] projectsTitleStr;

    public NewTicketActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_ticket, container, false);

        Intent receiveMainIntent = getActivity().getIntent();
        Log.d(LOG_TAG," Recieved Intent : "+receiveMainIntent.toString());

        if (receiveMainIntent != null) {
//            Bundle extrasInfo = receiveMainIntent.getExtras();
//            clientId = extrasInfo.getInt("Client ID");
//            Log.d(LOG_TAG, " Received Client ID : " + clientId);

            String[] projectsData = {
                    "Internet of Things",
                    "Artificial Intelligence",
                    "Enterprise iOS Application",
                    "Cloud Computing",
            };

            List<String> projectsList = new ArrayList<String> (Arrays.asList(projectsData));

            projectArrayAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    R.layout.list_item_project,
                    R.id.projectNametextView,
                    new ArrayList<String>()
            );

//            for(String project: projectsList){
//                projectArrayAdapter.add(project);
//            }

            ListView listView = (ListView) rootView.findViewById(R.id.projectListView);
            listView.setAdapter(projectArrayAdapter);

            //Toast.makeText(getActivity(),getActivity().toString(),Toast.LENGTH_SHORT).show();
            //Log.d("Issue: ", getActivity().toString());

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    String projectTitle = projectArrayAdapter.getItem(position);

                    //Toast.makeText(getActivity(), projectTitle, Toast.LENGTH_SHORT).show();

                    //Intent issueSelectIntent = new Intent(getActivity(), TicketDetailsActivity.class);
                    //issueSelectIntent.putExtra(Intent.EXTRA_TEXT,projectTitle);
                    //Toast.makeText(getActivity(),"Screen"+ projectTitle,Toast.LENGTH_LONG).show();
                    //startActivity(issueSelectIntent);

                    Intent issueIntent = new Intent(getActivity(), IssueActivity.class);
                    Bundle extras = new Bundle();                   //parcelable
                    extras.putInt("projectId", projectIds[position]);  //key - value pair
                    extras.putString("projectTitle",projectsTitleStr[position]);
                    issueIntent.putExtras(extras);
                    startActivity(issueIntent);
                }
            });

        }



        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(Utility.isOnline(getContext())){
            //Call the Async Task
            ProjectsTask myProjectTask = new ProjectsTask();
            myProjectTask.execute();
        }

    }

    /**
     * Represents an asynchronous task used to populate clients project titles
     */
    public class ProjectsTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String projectsStr;

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

                final String PROJECT_BASE_URL = getResources().getString(R.string.base_url).concat("project/getClientProjects");
                URL url = new URL(PROJECT_BASE_URL);

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder();
                Map<String, String> parameters = new HashMap<>();
                parameters.put("q", ""+LoginActivity.clientId);

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

                projectsStr = buffer.toString();

                Log.d(LOG_TAG, "Client Credential JSON String : " + projectsStr);

                return getProjectsFromJson(projectsStr);

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
        protected void onPostExecute(String[] projectsStr) {

            Log.d(LOG_TAG,"Projects Received from server: "+projectsStr);

            if (projectsStr != null) {
                //Log.d(LOG_TAG,"Inside Success: "+success.toString());
                projectArrayAdapter.clear();

                for (String projectTitleString : projectsStr) {
                    projectArrayAdapter.add(projectTitleString);
                }

            } else {
                Toast.makeText(getActivity(),"You don't have any projects!",Toast.LENGTH_LONG).show();
            }
        }


        private String[] getProjectsFromJson(String projectsJsonStr) throws JSONException{

            final String projectsList = "ticketproj";
            final String projectPrimaryId = "project_id";
            final String projectTitle = "project_title";


            JSONObject projectsJson = new JSONObject(projectsJsonStr);

            JSONArray projectsJsonArray = projectsJson.getJSONArray(projectsList);

            projectsTitleStr = new String[projectsJsonArray.length()];
            projectIds = new int[projectsJsonArray.length()];

            for(int i=0; i<projectsJsonArray.length(); i++){
                JSONObject project = projectsJsonArray.getJSONObject(i);

                projectsTitleStr[i] = project.getString(projectTitle);
                projectIds[i] = project.getInt(projectPrimaryId);
            }

            return projectsTitleStr;
        }

    }
}
