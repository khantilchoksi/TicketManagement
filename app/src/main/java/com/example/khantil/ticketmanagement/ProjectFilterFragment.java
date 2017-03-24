package com.example.khantil.ticketmanagement;

import android.app.Activity;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProjectFilterFragment extends Fragment {

    private final String LOG_TAG = ProjectFilterFragment.class.getSimpleName();

    private ArrayAdapter<String> projectArrayAdapter;

    private int[] projectIds;
    private String[] projectsTitleStr;

//    private int filteredProjectId = 0;

    public ProjectFilterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_project_filter, container, false);

        projectArrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_project,
                R.id.projectNametextView,
                new ArrayList<String>()
        );

        ListView projectsListView = (ListView) rootView.findViewById(R.id.projectFilterListView);
        projectsListView.setAdapter(projectArrayAdapter);

        projectsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Set the result to the intent
                Intent returnIntent = new Intent();

                returnIntent.putExtra(MainFragment.filteredProjectIdTag,projectIds[position]);
                returnIntent.putExtra(MainFragment.filteredProjectTitleTag, projectsTitleStr[position]);

/*                Bundle extras = new Bundle();       //parcelable
                extras.putInt(MainFragment.filteredProjectIdTag, projectIds[position]);
                extras.putString(MainFragment.filteredProjectTitleTag, projectsTitleStr[position]);  //key - value pair
                returnIntent.putExtras(extras);*/

                getActivity().setResult(Activity.RESULT_OK, returnIntent);
                getActivity().finish();
            }
        });

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
                Toast.makeText(getActivity(), "You don't have any projects!", Toast.LENGTH_LONG).show();
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
