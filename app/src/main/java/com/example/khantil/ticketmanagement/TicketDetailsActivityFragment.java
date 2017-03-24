package com.example.khantil.ticketmanagement;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class TicketDetailsActivityFragment extends Fragment {

    static final int PICK_CONTACT_REQUEST = 1;  // The request code for phone
    static final int PHOTO_CAPTURE_REQUEST = 2;  // The request code for taking picture
    static final int PICK_PHOTO_REQUEST = 3;        //The request code for getting image from gallery
    static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 4;
    static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 5;  // Runtime permissions

    private final String LOG_TAG = TicketDetailsActivity.class.getSimpleName();

    private Uri outputFileUri;  // For the image selection

    private EditText contactPhone;
//    private ImageView[] previewImages;
    private GridView issuesImagesGrid;
    private IssueImageArrayAdapter issueImageArrayAdapter;

    private View rootView;
    private View mTicketProgress;
    private View mNewTicketForm;

    private EditText ticketTitleText;
    private EditText ticketDescriptionText;
    private EditText ticketContactPersonNameText;
    private EditText ticketContactPersonNumberText;
    private EditText ticketContactPersonEmailText;

    private String ticketTitle;
    private String ticketDescription;
    private String ticketContactPersonName;
    private String ticketContactPersonNumber;
    private String ticketContactPersonEmail;

    private int selectedIssueID;
    private int selectedProjectId;

    private String filePath;        //file name of the image - for server upload

    private String[] encodedBitMapImage;  //bitmap encoded string image url
    private String[] encodedThumbnailImage;

    private int countImages = 0;

    public TicketDetailsActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_ticket_details, container, false);

        //Extracting the project name and issue name to display on the details screen
        Intent issueIntent = getActivity().getIntent();

        if (issueIntent != null) {
            Bundle extrasInfo = issueIntent.getExtras();
            String projectName = extrasInfo.getString("Project Name");
            String issueName = extrasInfo.getString("Issue Type");
            selectedProjectId = extrasInfo.getInt("projectId");
            selectedIssueID = extrasInfo.getInt("issueId");
            ((TextView) rootView.findViewById(R.id.projectNameOnDetails)).setText(projectName);
            ((TextView) rootView.findViewById(R.id.issueTypeOnDetails)).setText(issueName);
        }

        ticketTitleText = (EditText) rootView.findViewById(R.id.ticketTitleEditText);
        ticketTitleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ticketTitleText.setError(null);
            }
        });

        ticketDescriptionText = (EditText) rootView.findViewById(R.id.ticketDescriptionEditText);
        ticketDescriptionText.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                // TODO Auto-generated method stub
                if (view.getId() == R.id.ticketDescriptionEditText) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });

        ticketDescriptionText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ticketDescriptionText.setError(null);
            }
        });

        ticketContactPersonNameText = (EditText) rootView.findViewById(R.id.contactNameEditText);
        ticketContactPersonNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ticketContactPersonNameText.setError(null);
            }
        });

        ticketContactPersonNumberText = (EditText) rootView.findViewById(R.id.contactPhoneEditText);
        ticketContactPersonNumberText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ticketContactPersonNumberText.setError(null);
            }
        });

        ticketContactPersonEmailText = (EditText) rootView.findViewById(R.id.contactEmailEditText);
        ticketContactPersonEmailText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ticketContactPersonEmailText.setError(null);
            }
        });

        //For progress bar
        mTicketProgress = rootView.findViewById(R.id.ticket_progress);
        mNewTicketForm = rootView.findViewById(R.id.newTicketView);


        //To select the phone number form the contacts app

        contactPhone = (EditText) rootView.findViewById(R.id.contactPhoneEditText);

        contactPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mayRequestContacts();
            }

        });

        //To upload the image
        Button upload = (Button) rootView.findViewById(R.id.uploadImageButton);

//        previewImages = new ImageView[3];
//        previewImages[0] = (ImageView) rootView.findViewById(R.id.previewImage1);
//        previewImages[1] = (ImageView) rootView.findViewById(R.id.previewImage2);
//        previewImages[2] = (ImageView) rootView.findViewById(R.id.previewImage3);

        issueImageArrayAdapter = new IssueImageArrayAdapter(getActivity(), new ArrayList<Bitmap>());

        issuesImagesGrid = (GridView) rootView.findViewById(R.id.issueGridView);
        issuesImagesGrid.setAdapter(issueImageArrayAdapter);


        final Vibrator vibrator = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        issuesImagesGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view,
                                           int position, long arg3) {
                vibrator.vibrate(100);
                ImageView selectedImage = (ImageView) view.findViewById(R.id.issueImageItem);
                selectedImage.setColorFilter(Color.GRAY, PorterDuff.Mode.LIGHTEN);

                ImageButton removeImageButton = (ImageButton) view.findViewById(R.id.deleteImageButton);
                removeImageButton.setVisibility(View.VISIBLE);
                return true;
            }
        });

        issuesImagesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOG_TAG,"On Item Click");

                ImageButton removeImageButton = (ImageButton) view.findViewById(R.id.deleteImageButton);
                removeImageButton.setVisibility(View.INVISIBLE);

                ImageView selectedImage = (ImageView) view.findViewById(R.id.issueImageItem);
                selectedImage.clearColorFilter();

            }
        });





        /*upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                  //      android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Intent pickPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(pickPhoto , GET_PHOTO_REQUEST);
            }
        });*/

        //Upload Image
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if the user has already uploaded 3 images or not
                if(issueImageArrayAdapter.getCount()<3)
                    mayRequestUploadImage();
                else
                    Toast.makeText(getActivity(), "You can upload maximum 3 images!", Toast.LENGTH_LONG).show();

                //Using Alert Dialog Builder
                    /*final CharSequence[] items = { "Take Photo", "Choose from Library",
                            "Cancel" };

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Add Photo!");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (items[item].equals("Take Photo")) {
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, PHOTO_CAPTURE_REQUEST);
                            } else if (items[item].equals("Choose from Library")) {
                                Intent intent = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent, PICK_PHOTO_REQUEST);
                            } else if (items[item].equals("Cancel")) {
                                dialog.dismiss();
                            }
                        }
                    });
                    builder.show();*/

                    /*List<Intent> yourIntentsList = new ArrayList<Intent>();

                    List<ResolveInfo> listCam = packageManager.queryIntentActivities(camIntent, 0);
                    for (ResolveInfo res : listCam) {
                        final Intent finalIntent = new Intent(camIntent);
                        finalIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                        yourIntentsList.add(finalIntent);
                    }

                    List<ResolveInfo> listGall = packageManager.queryIntentActivities(gallIntent, 0);
                    for (ResolveInfo res : listGall) {
                        final Intent finalIntent = new Intent(gallIntent);
                        finalIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                        yourIntentsList.add(finalIntent);
                    }*/


                // Determine Uri of camera image to save.
                /*final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
                root.mkdirs();
                final String fname = "img_"+System.currentTimeMillis()+".jpg";
                Log.v("File Name",fname);
                final File sdImageMainDirectory = new File(root, fname);
                outputFileUri = Uri.fromFile(sdImageMainDirectory);*/


            }
        });


        //Submit button action performed

        Button submitButton = (Button) rootView.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Tag : ","Submit button clicked");
                attemptSubmit();
            }
        });


        return rootView;
    }

    /**
     * Shows the progress UI and hides the new ticket details form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mNewTicketForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mNewTicketForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mNewTicketForm.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mTicketProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mTicketProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mTicketProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mTicketProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mNewTicketForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void attemptSubmit(){
        //Reset Errors
        ticketTitleText.setError(null);
        ticketDescriptionText.setError(null);
        ticketContactPersonNameText.setError(null);
        ticketContactPersonNumberText.setError(null);
        ticketContactPersonEmailText.setError(null);

        View focusView = null;
        boolean cancel = false;

        ticketTitle = ticketTitleText.getText().toString();
        ticketDescription = ticketDescriptionText.getText().toString();
        ticketContactPersonName = ticketContactPersonNameText.getText().toString();
        ticketContactPersonNumber = ticketContactPersonNumberText.getText().toString();
        ticketContactPersonEmail = ticketContactPersonEmailText.getText().toString();

//        Log.d("Ticket Title : ",ticketTitle);

        if(TextUtils.isEmpty(ticketContactPersonEmail)){
            ticketContactPersonEmailText.setError("Please give email address of the contact person!");
            focusView = ticketContactPersonEmailText;
            cancel = true;
        }else if(!isEmailContainsAt(ticketContactPersonEmail)){
            ticketContactPersonEmailText.setError("Please enter valid email address!");
            focusView = ticketContactPersonEmailText;
            cancel = true;
        }

        if(TextUtils.isEmpty(ticketContactPersonNumber)){
            ticketContactPersonNumberText.setError("Please provide contact number!");
            focusView = ticketContactPersonNumberText;
            cancel = true;
        }

        if(TextUtils.isEmpty(ticketContactPersonName)){
            ticketContactPersonNameText.setError("Please give contact person name!");
            focusView = ticketContactPersonNameText;
            cancel = true;
        }

        if(TextUtils.isEmpty(ticketDescription)){
            ticketDescriptionText.setError("Please provide brief information of ticket!");
            focusView = ticketDescriptionText;
            cancel = true;
        }
        if(TextUtils.isEmpty(ticketTitle)){
            ticketTitleText.setError("Please enter project title!");
            focusView = ticketTitleText;
            cancel = true;
        }

        if(cancel)
            focusView.requestFocus();
        else{
            //Call the async task and create the new ticket
            Toast.makeText(getActivity(),"All details verfied", Toast.LENGTH_LONG).show();

            if(Utility.isOnline(getContext())){
                showProgress(true);
                CreateTicketTask newTicket = new CreateTicketTask();
                newTicket.execute();
            }

        }

    }

    private boolean isEmailContainsAt(String email) {
        return email.contains("@");
    }

    private void mayRequestContacts(){
        if(Build.VERSION.SDK_INT < 23){
            callReadContactsIntent();
        }else{
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                //That is Permission is denied
                // Should we show an explanation?
                Log.d("Permission","Permission is not granted");
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.READ_CONTACTS)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    // Display a SnackBar with an explanation and a button to trigger the request.
                    Snackbar.make(rootView, R.string.permission_contacts_rationale,
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                                    Log.d("Permission", "After denying, Permission is Requested!");
                                }

                            })
                            .show();

                }
                else {
                    // No explanation needed, we can request the permission.
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                    Log.d("Permission", "Permission is not granted, should show request rational is false");

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }else{
                //Permission is already granted
                callReadContactsIntent();
            }
        }

    }

    private void mayRequestUploadImage(){
        if(Build.VERSION.SDK_INT < 23){
            uploadImageIntent();
        }else{
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //That is Permission is denied
                // Should we show an explanation?
                Log.d("Permission","Permission is not granted for write external Storage");
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    // Display a SnackBar with an explanation and a button to trigger the request.
                    Snackbar.make(rootView, R.string.permission_camera,
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                                    Log.d("Permission", "After denying, Permission is Requested for camera!");
                                }

                            })
                            .show();

                }
                else {
                    // No explanation needed, we can request the permission.
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_WRITE_STORAGE);

                    Log.d("Permission", "Permission is not granted, should show request rational is false for camera");

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }else{
                //Permission is already granted
                uploadImageIntent();
            }
        }


    }

        private void uploadImageIntent(){
        //If permission is granted
        outputFileUri = getOutputMediaFile();

        final List<Intent> cameraIntents = new ArrayList<Intent>();

        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        final PackageManager packageManager = getActivity().getPackageManager();

        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        final Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, PHOTO_CAPTURE_REQUEST);
    }

    @Override
        public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d("Permission","Permission is Granted.. Calling Intent!");
                    callReadContactsIntent();

                } else {
                    //Permission is denied
                    //User has to enter phone number manually
                    contactPhone.setFocusableInTouchMode(true);

                    Log.d("Permission", "Permission is not granted in result.");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.READ_CONTACTS)) {

                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                        // Display a SnackBar with an explanation and a button to trigger the request.
                        Log.d("Permission", "In the result if");
                        Snackbar.make(rootView, R.string.permission_contacts_rationale,
                                Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.ok, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS},
                                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                                    }
                                })
                                .show();

                    }else{
                        //User has checked never show me again
                        Log.d("Permission", "User has checked never show me again.");
                        Snackbar.make(rootView, R.string.permission_setting,
                                Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.settings, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", getActivity().getPackageName(), null));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                })
                                .show();
                    }

                }
                break;
            }

            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE:{
                //Case for read and write app permission
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d("Permission","Permission is Granted for camera.. Calling Intent!");
                    uploadImageIntent();

                } else {
                    //Permission is denied
                    //User has to enter phone number manually

                    Log.d("Permission", "Permission is not granted in result for camera.");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                        // Display a SnackBar with an explanation and a button to trigger the request.
                        Log.d("Permission", "In the result if");
                        Snackbar.make(rootView, R.string.permission_camera,
                                Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.ok, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                                    }
                                })
                                .show();

                    }else{
                        //User has checked never show me again
                        Log.d("Permission", "User has checked never show me again.");
                        Snackbar.make(rootView, R.string.permission_setting,
                                Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.settings, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", getActivity().getPackageName(), null));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                })
                                .show();
                    }

                }
                break;
            }

            // other 'case' lines to check for other
            // permissions this app might request

        }   //switch ended
    }

    public void callReadContactsIntent(){
        //contactPhone.setFocusableInTouchMode(false);
        if(contactPhone.isFocusableInTouchMode()){
                // keyborad will be displayed
        }else{
            Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
            // Show user only contacts w/ phone numbers
            pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
            contactPhone.setFocusableInTouchMode(true);
        }

    }

    /** Create a File for saving an image */
    private static Uri getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        String externalStorageState = Environment.getExternalStorageState();
        Log.d("Storage State:", externalStorageState);

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "TicketMgmtApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("TicketMgmtApp", "failed to create directory");
                return null;
            }
        }else{
            Log.d("Storage: ",mediaStorageDir.getPath().toString());
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");

        return Uri.fromFile(mediaFile);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request it is that we're responding to
        //super.onActivityResult(requestCode, resultCode, data);
        Log.d("Result Code"," "+resultCode);
        Log.d("Request Code", " " + requestCode);
        //final String action2 = data.getAction();
        //Log.d("Action: ", action2);
        switch (requestCode) {
            case PICK_CONTACT_REQUEST: {
                // Make sure the request was successful
                if (resultCode == getActivity().RESULT_OK) {
                    // Get the URI that points to the selected contact
                    Uri contactUri = data.getData();
                    // We only need the NUMBER column, because there will be only one row in the result
                    String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

                    // Perform the query on the contact to get the NUMBER column
                    // We don't need a selection or sort order (there's only one result for the given URI)
                    // CAUTION: The query() method should be called from a separate thread to avoid blocking
                    // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
                    // Consider using CursorLoader to perform the query.
                    Cursor cursor = getActivity().getContentResolver()
                            .query(contactUri, projection, null, null, null);
                    cursor.moveToFirst();

                    // Retrieve the phone number from the NUMBER column
                    int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String number = cursor.getString(column);

                    // Do something with the phone number...
                    //Toast.makeText(getActivity(), number, Toast.LENGTH_LONG);

                    Log.d("Phone: ", number);

                    contactPhone.setText(number);
                    //then setting the contact foucusable to be true if the user wants to edit the phone number
                    contactPhone.setFocusableInTouchMode(true);
                    //contactPhone.setInputType();
                    contactPhone.requestFocus();
                }

                break;
            }

            case PHOTO_CAPTURE_REQUEST: {
                // Make sure the request was successful

                if (resultCode == getActivity().RESULT_OK) {
                    Uri selectedImageUri;
                    if(data==null)
                        selectedImageUri = outputFileUri;
                    else
                        selectedImageUri = data.getData();
                    //Log.d("Action: ", "Camera Action selected");

                    //Bitmap bitmap = getThumbnail(data.getData());
                    //previewImage.setImageBitmap(bitmap);

                    //Below line not handles clicked image due to large size
                    //previewImage.setImageURI(data.getData());

                    //21st May

                    //First select image view holder

                    Log.d("Data Received:",selectedImageUri.toString());


                    if (selectedImageUri != null && "content".equals(selectedImageUri.getScheme())) {
                        //Image is chosen from Gallery
                        Log.d("Chosen Image:", " is from Gallery");
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getActivity().getContentResolver().query(
                                selectedImageUri, filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        filePath = cursor.getString(columnIndex);
                        cursor.close();


//                        bitmap = BitmapFactory.decodeFile(filePath);
//
//                        Cursor cursor = this.getContentResolver().query(_uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
//                        cursor.moveToFirst();
//                        filePath = cursor.getString(0);
//                        cursor.close();
                    } else {
                        //Image is chosen from Camera
                        Log.d("Chosen Image:"," is from Camera");
                        filePath = selectedImageUri.getPath();
                        // Get the dimensions of the bitmap
                        //filePath = _uri.getPath();
                    }

                    //Getting the original image as bitmap object
                    BitmapFactory.Options bmOriginalOptions = new BitmapFactory.Options();
                    bmOriginalOptions.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(filePath, bmOriginalOptions);
                    bmOriginalOptions.inJustDecodeBounds = false;
                    Bitmap bitmapOriginal = BitmapFactory.decodeFile(filePath, bmOriginalOptions);
                    issueImageArrayAdapter.add(bitmapOriginal);
//                    encodedBitMapImage = getStringImage(bitmapOriginal);

                    // Add to the IssueImage Adapter
//                    tempImageView.setImageBitmap(ThumbImage);
//                    issueImageArrayAdapter.add(ThumbImage);
//                    encodedThumbnailImage = getStringImage(ThumbImage);

                    //Converting bitmap image to encoded string for server
//                    encodedBitMapImage = getStringImage(bitmap);
                    Log.d(LOG_TAG,"Encoded Original Image"+ encodedBitMapImage);
                    Log.d(LOG_TAG,"Encoded Thumbnail Image"+ encodedThumbnailImage);
                    Log.d(LOG_TAG,"File Name"+filePath);

/*                    //Getting the bitmap image to scale and show on android screen
                    ImageView tempImageView = getEmptyImageHolder();
                    int targetW = tempImageView.getWidth();
                    int targetH = tempImageView.getHeight();
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bmOptions.inJustDecodeBounds = true;

                    BitmapFactory.decodeFile(filePath, bmOptions);
                    int photoW = bmOptions.outWidth;
                    int photoH = bmOptions.outHeight;

                    // Determine how much to scale down the image
                    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

                    // Decode the image file into a Bitmap sized to fill the View
                    bmOptions.inJustDecodeBounds = false;
                    bmOptions.inSampleSize = scaleFactor;
                    bmOptions.inPurgeable = true;
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath, bmOptions);

//                    tempImageView.setLayoutParams(new GridView.LayoutParams(85, 85));
//                    tempImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

//                    tempImageView.setImageBitmap(bitmap);
                    //End of Getting the bitmap image to scale and show on android screen*/



                    //End of 21st may - completed

                    //For camera
                    // Get the dimensions of the View
                    /*InputStream inputStream = null;
                    try {
                        inputStream = getActivity().getContentResolver().openInputStream( data.getData());

                        int targetW = previewImage.getWidth();
                        int targetH = previewImage.getHeight();

                        // Get the dimensions of the bitmap
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        bmOptions.inJustDecodeBounds = true;

                        //BitmapFactory.decodeFile(data.getData().getPath(), bmOptions);
                        BitmapFactory.decodeStream(inputStream,null,bmOptions);
                        inputStream.close();
                        int photoW = bmOptions.outWidth;
                        int photoH = bmOptions.outHeight;

                        // Determine how much to scale down the image
                        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

                        // Decode the image file into a Bitmap sized to fill the View
                        bmOptions.inJustDecodeBounds = false;
                        bmOptions.inSampleSize = scaleFactor;
                        bmOptions.inPurgeable = true;

                        Bitmap bitmap = BitmapFactory.decodeFile(data.getData().getPath(), bmOptions);
                        previewImage.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getActivity().getContentResolver().query(
                                data.getData(), filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String filePath = cursor.getString(columnIndex);
                        cursor.close();


                        Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
                        previewImage.setImageBitmap(yourSelectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/


                    //Code for gallery
                    /*Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getActivity().getContentResolver().query(
                            selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();


                    Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
                    previewImage.setImageBitmap(yourSelectedImage);*/


                    //Tried with picasso .. problem is with camera captured image
                    //Picasso.with(getContext()).load(data.getData()).into(previewImage);

                    //Bundle extras = data.getExtras();
                    //Bitmap imageBitmap = (Bitmap) extras.get("data");
                    //previewImage.setImageBitmap(imageBitmap);

                    //Log.d("Action:","Hello");
                        //Bundle extras = data.getExtras();
                        //Bitmap imageBitmap = (Bitmap) extras.get("data");
                    //}else{
                        //For gallery
                      //  Uri selectedImage = data.getData();
                        //previewImage.setImageURI(selectedImage);
                    //}


                    /*final String action = data.getAction();
                    Uri selectedImageUri;
                    if (action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)){
                            selectedImageUri = outputFileUri;
                    }else{
                        selectedImageUri = data.getData();
                    }
                    previewImage.setImageURI(selectedImageUri);*/

                    /*final boolean isCamera;
                    if (data == null) {
                        isCamera = true;
                    } else {
                        final String action = data.getAction();
                        if (action == null) {
                            isCamera = false;
                        } else {
                            isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        }
                    }

                    Uri selectedImageUri;
                    if (isCamera) {
                        //selectedImageUri = outputFileUri;
                        selectedImageUri = data == null ? null : data.getData();
                    } else {
                        selectedImageUri = data == null ? null : data.getData();

                    }
                    previewImage.setImageURI(selectedImageUri);
                }*/



                    break;
                }

            }
            default:
                Toast.makeText(getActivity(),"No Image Uploaded", Toast.LENGTH_SHORT).show();

        }

    }

//    private ImageView getEmptyImageHolder() {
//        for(int i =0;i<3;i++){
//            if(previewImages[i].getDrawable() == null){
//                return previewImages[i];
//            }
//        }
//        return null;
//    }

    /*public  Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException {
        InputStream input = getActivity().getContentResolver().openInputStream(uri);

        int THUMBNAIL_SIZE = 216;

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither=true;//optional
        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
            return null;

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither=true;//optional
        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        input = getActivity().getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }*/

    /**
     * Represents an asynchronous task used to create a new ticket
    */
    public class CreateTicketTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String successStr;

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

                final String TICKET_BASE_URL = getResources().getString(R.string.base_url).concat("ticket/createTicket");
                URL url = new URL(TICKET_BASE_URL);

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder();
                Map<String, String> parameters = new HashMap<>();
                parameters.put("ticket_title",ticketTitle);
                parameters.put("client_id", ""+LoginActivity.clientId);
                parameters.put("project_id",""+selectedProjectId);
                parameters.put("issue_id",""+selectedIssueID);
                parameters.put("ticket_description",ticketDescription);
                parameters.put("contact_person_name",ticketContactPersonName);
                parameters.put("contact_person_number",ticketContactPersonNumber);
                parameters.put("contact_person_email",ticketContactPersonEmail);

                if(!issueImageArrayAdapter.isEmpty()){

                    for(int i=0;i<issueImageArrayAdapter.getCount();i++){
                        parameters.put("image"+i,issueImageArrayAdapter.getStringImage(i));
                    }
                }
//                parameters.put("image",encodedBitMapImage);
//                parameters.put("thumbnailImage",encodedThumbnailImage);


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

                successStr = buffer.toString();
//
                Log.d(LOG_TAG, successStr);

/*                if(successStr.contains("true")){
                    Log.d(LOG_TAG,"Received Success:" + successStr);
                    return true;
                }
                else{
                    Log.d(LOG_TAG,"Received not success:"+ successStr);
                    return false;
                }*/
                return isTicketCreated(successStr);


            } catch (IOException e) {
                Log.d(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return false;
            }
            catch (JSONException e){
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

            }

        }

        private Boolean isTicketCreated(String successStr) throws JSONException {
            final String isCreatedString = "isCreated";

            JSONObject successJsonObject = new JSONObject(successStr);

            String isTicketSuccessfullyCreated = successJsonObject.getString(isCreatedString);
            if(isTicketSuccessfullyCreated.contains("true")){
                return true;
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            Log.d(LOG_TAG, "Success: "+success);
            showProgress(false);

            if (success) {
                Toast.makeText(getActivity(),"New Ticket Successfully created!",Toast.LENGTH_LONG).show();

                Intent viewTickets = new Intent(getActivity(), MainActivity.class);
                viewTickets.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(viewTickets);

            } else {
                Toast.makeText(getActivity(),"Ticket not created, try again!",Toast.LENGTH_LONG).show();
            }
        }

    }

//    //Following method is used to convert the bitmap image to Base 64 and encoded string
//    public String getStringImage(Bitmap bmp){
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);     // Half of the actual quality
//        byte[] imageBytes = baos.toByteArray();
//        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
//        return encodedImage;
//    }

}
