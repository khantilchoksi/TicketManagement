package com.example.khantil.ticketmanagement;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_CONTACTS;
import static android.widget.Toast.LENGTH_SHORT;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    private final String LOG_TAG = LoginActivity.class.getSimpleName();

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    /*private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };*/
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;


    static int clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //First check if the client is already logged in or not
        SharedPreferences settings = getSharedPreferences(getResources().getString(R.string.login_pref), 0);

        //Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
        boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);

        if(hasLoggedIn)
        {
            clientId = settings.getInt("clientId",0);
            //Go directly to main activity.
            successfulLoggedIn();

        }else{
            // Client is using the app for the first time
            // Set up the login form.
            mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
            populateAutoComplete();

            mPasswordView = (EditText) findViewById(R.id.password);
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.login || id == EditorInfo.IME_NULL) {
                        attemptLogin();
                        return true;
                    }
                    return false;
                }
            });

            Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });

            mLoginFormView = findViewById(R.id.login_form);
            mProgressView = findViewById(R.id.login_progress);
        }

    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailContainsAt(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.s
            focusView.requestFocus();
        } else {

            //Check first if there is internet connection
            if(Utility.isOnline(this)){
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                showProgress(true);

                //Uncomment the following lines for checking id and password with async task
                mAuthTask = new UserLoginTask(email, password);
                mAuthTask.execute((Void) null);

                //For testing
                //Intent ticketHomepage = new Intent(LoginActivity.this,MainActivity.class);
                //startActivity(ticketHomepage);
                //finish();
            }

        }
    }

    private boolean isEmailContainsAt(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    /*private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 0;
    }*/

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        private boolean isEmailInvalid = false;
        private boolean isPasswordInvalid = false;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            /*try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }*/

            /*for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }*/

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String clientCredStr;

            try {
                //System.setProperty("http.keepAlive", "false");
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

                final String CLIENT_BASE_URL = getResources().getString(R.string.base_url).concat("client/getClientLogin");
                URL url = new URL(CLIENT_BASE_URL);

                /*//Handling Cookies
                // Manage Cookies
                String cookieString="";
                String csrftoken="";

                Map<String,List<String >> headers= urlConnection.getHeaderFields();
                CookieManager cookieManager=null;
                List<String> cookiesHeader=headers.get("Set-Cookie");

                if(cookiesHeader!=null){
                    for(String cookie: cookiesHeader){
                        cookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                    }
                }

                List<HttpCookie> cookies=cookieManager.getCookieStore().getCookies();
                Iterator<HttpCookie> cookieIterator=cookies.iterator();
                while (cookieIterator.hasNext()){
                    HttpCookie cookie = cookieIterator.next();
                    cookieString+=cookie.getName()+"="+cookie.getValue()+";";

                    if(cookie.getName().equals("csrftoken")){
                        csrftoken=cookie.getValue();
                    }
                }*/

                urlConnection = (HttpURLConnection) url.openConnection();
                //urlConnection.setReadTimeout(10000);
                //urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
//                urlConnection.setRequestProperty("X-CSRFToken", csrftoken);
//                urlConnection.setRequestProperty("Cookie", cookieString);

                /*StringBuilder queryBuilder = new StringBuilder();

                queryBuilder.append(URLEncoder.encode("q","UTF-8"));
                queryBuilder.append("=");
                queryBuilder.append(URLEncoder.encode(mEmail,"UTF-8"));
                Log.d(LOG_TAG, "Writer : " + queryBuilder.toString());*/

                Uri.Builder builder = new Uri.Builder();
                Map<String, String> parameters = new HashMap<>();
                parameters.put("q", mEmail);
                parameters.put("p", mPassword);
                //parameters.put("password", "bnk123");

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
                    return false;
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
                    return false;
                }

                clientCredStr = buffer.toString();

                Log.d(LOG_TAG, "Client Credential JSON String : " + clientCredStr);

                return validatePassword(clientCredStr);

            } catch (IOException e) {
                Log.d(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return false;
            } catch (JSONException e){
                Log.d(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
                return false;
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
                //return false;
            }

            // TODO: register the new account here.
            //return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            Log.d(LOG_TAG, "Success Boolean Tag: " + success.toString());
            if (success) {
                //Log.d(LOG_TAG,"Inside Success: "+success.toString());

                //User has successfully logged in, save this information
                // We need an Editor object to make preference changes.
                SharedPreferences settings = getSharedPreferences(getResources().getString(R.string.login_pref), MODE_PRIVATE); // 0 - for private mode
                SharedPreferences.Editor editor = settings.edit();

                //Set "hasLoggedIn" to true
                editor.putBoolean("hasLoggedIn", true);
                editor.putInt("clientId",clientId);
                editor.putString("clientEmailID", mEmail);

                // Commit the edits!
                editor.commit();

                successfulLoggedIn();

            } else {
                SharedPreferences settings = getSharedPreferences(getResources().getString(R.string.login_pref), MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();

                //Set "hasLoggedIn" to true
                editor.putBoolean("hasLoggedIn", false);
                editor.commit();

                if(isEmailInvalid){
                    mEmailView.setError(getString(R.string.error_invalid_email));
                    mEmailView.requestFocus();
                }else if(isPasswordInvalid){
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                }

            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

        private boolean validatePassword(String clientCredStr) throws JSONException{

            final String clientEmailValid = "emailValid";
            final String clientPasswordValid = "pwdValid";
/*            final String clientEmail = "emailid";
            final String clientPassword = "passwd";*/


            JSONObject clientJson = new JSONObject(clientCredStr);

            /*JSONArray clientsArray = clientJson.getJSONArray(clientsList);

            if(clientsArray.length()==0){
                isEmailInvalid = true;
            }*/

/*            for(int i=0; i<clientsArray.length(); i++){
                JSONObject client = clientsArray.getJSONObject(i);

                clientId = client.getInt(clientPrimaryId);
                Log.d(LOG_TAG,"CLient Id : "+clientId);
                if( mEmail.equals(client.getString(clientEmail))) {
                    Log.d(LOG_TAG,"Client Email from JSON Object : "+client.getString(clientEmail));
                    if (mPassword .equals(client.getString(clientPassword))){
                        Log.d(LOG_TAG, "Client Password from JSON Object : " + client.getString(clientPassword));
                        //Toast.makeText(LoginActivity.this, "Successful Login",Toast.LENGTH_LONG).show();
                        return true;
                        //Log.d(LOG_TAG,"Client Password2 from JSON Object : "+client.getString(clientPassword));
                    }else{
                        isPasswordInvalid = true;
                    }
                }

            }*/

            String isValidEmail = clientJson.getString(clientEmailValid);
            if(isValidEmail.contains("true")){
                //Email is registred with server
                //Check for the password
                String isValidPassword = clientJson.getString(clientPasswordValid);

                if(isValidPassword.contains("true")){
                    //Password is also valid
                    clientId = clientJson.getInt("cid");
                    Log.d(LOG_TAG,"Got client Id : "+clientId);
                    return true;
                } else{
                      //Password is not valid
                    isPasswordInvalid = true;
                }
            }else{
                //Email is not registered
                Log.d(LOG_TAG,"Email Not Valid");
                //Set the error
                isEmailInvalid = true;
            }
            return false;
        }

    }

    private void successfulLoggedIn(){
        Intent ticketHomepage = new Intent(LoginActivity.this,MainActivity.class);
//                Bundle extras = new Bundle();           //parcelable
//                extras.putInt("Client ID", clientId);  //key - value pair
//                ticketHomepage.putExtras(extras);
        startActivity(ticketHomepage);
        finish();
    }
}

