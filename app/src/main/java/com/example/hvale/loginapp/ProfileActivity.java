package com.example.hvale.loginapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonIOException;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import typeClasses.GridImageAdapter;
import typeClasses.UniversalImageLoader;
import typeClasses.UserData;


public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private static final int NUM_GRIG_COLUMNS = 3;
    private static final int RESULT_FROM_EDIT = 1;
    private static final String URL = "https://my-first-project-196314.appspot.com/rest/occurrency/getImageUri/";
    private static final String baseurl = "https://my-first-project-196314.appspot.com/rest/";
    private Context mContext = ProfileActivity.this;
    public final List<UserData> userDataList = new LinkedList<>();
    private ProgressBar mProgressBar;
    private ImageView profilePhoto;
    private RelativeLayout profile;
    private String[] ocurrencys = new String[20];
    private ArrayList<String> urls;
    JSONArray finalResponse = null;
    JSONObject statsFinalResponse = null;

    private TextView profileName;
    private TextView displayName;
    private TextView displayAddress;
    private TextView displayEmail;
    private TextView nOcurr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        urls = new ArrayList<>();
        Log.d(TAG, "onCreate: started.");
        setupActivityWidgets();
        showProgress(true);
        userStatsRest();
        doInBackGround();
        //showProgress(false);
        setupToolbar();
        setProfileImage();

    }

    private void tempGridSetup(){
        ArrayList<String> imgURLs = new ArrayList<>();
        System.out.println(urls.size());
        for(int i = 0; i < urls.size(); i++) {
            System.out.println(URL + urls.get(i));
            String bananas = URL.concat(urls.get(i));
            imgURLs.add(bananas);
        }
        /*
        imgURLs.add("https://my-first-project-196314.appspot.com/rest/occurrency/getImageUri/f3927eb4-143a-432a-8275-c03392104a52.png");
        imgURLs.add("https://i.redd.it/9bf67ygj710z.jpg");
        imgURLs.add("https://c1.staticflickr.com/5/4276/34102458063_7be616b993_o.jpg");
        imgURLs.add("http://i.imgur.com/EwZRpvQ.jpg");
        imgURLs.add("http://i.imgur.com/JTb2pXP.jpg");
        imgURLs.add("https://i.redd.it/59kjlxxf720z.jpg");
        imgURLs.add("https://i.redd.it/pwduhknig00z.jpg");
        imgURLs.add("https://i.redd.it/clusqsm4oxzy.jpg");
        imgURLs.add("https://i.redd.it/svqvn7xs420z.jpg");
        imgURLs.add("http://i.imgur.com/j4AfH6P.jpg");
        imgURLs.add("https://i.redd.it/89cjkojkl10z.jpg");
        imgURLs.add("https://i.redd.it/aw7pv8jq4zzy.jpg");
        */

        setupImageGrid(imgURLs);
    }

    private void setupImageGrid(ArrayList<String> imgURLs){
        GridView gridView = (GridView) findViewById(R.id.gridView);
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRIG_COLUMNS;
        gridView.setColumnWidth(imageWidth);
        GridImageAdapter adapter = new GridImageAdapter(mContext, R.layout.layout_grid_imageview, "", imgURLs);
        gridView.setAdapter(adapter);
    }

    private void setProfileImage(){

        Log.d(TAG, "setProfileImage: setting profile photo.");
        final String username = LogOutSingleton.getInstance(getApplicationContext()).getUsername();
        String imgURL = baseurl + "user/getImageUri/" + username;
        DiskCacheUtils.removeFromCache(imgURL, ImageLoader.getInstance().getDiskCache());
        System.out.println(imgURL);
        UniversalImageLoader.setImage(imgURL, profilePhoto, mProgressBar, "");
        /*
        ImageLoader imageLoader = ImageLoader.getInstance();
        final String username = LogOutSingleton.getInstance(getApplicationContext()).getUsername();
        String imgURL = baseurl + "user/getImageUri/" + username;
        imageLoader.displayImage(imgURL, profilePhoto);
        */
    }

    private void setupActivityWidgets(){
        profile = (RelativeLayout) findViewById(R.id.profile);
        mProgressBar = (ProgressBar) findViewById(R.id.profileProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        profilePhoto = (ImageView) findViewById(R.id.profile_photo);
        profileName = findViewById(R.id.profileName);
        displayName = findViewById(R.id.display_name);
        displayAddress = findViewById(R.id.display_address);
        displayEmail = findViewById(R.id.display_email);
        nOcurr = (TextView) findViewById(R.id.occurrencysPost);

    }
    private void setupUserInfo() {
        UserData user = userDataList.get(0);
        profileName.setText(user.getUsername());
        displayName.setText(user.getName());
        displayAddress.setText(user.getAddress());
        displayEmail.setText(user.getEmail());
        nOcurr.setText(String.valueOf(urls.size()));
    }

    /**
     * Responsible for setting up the profile toolbar
     */
    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.profileToolBar);
        setSupportActionBar(toolbar);

        ImageView profileMenu = (ImageView) findViewById(R.id.profileMenu);

        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to account settings.");
                Intent intent = new Intent(mContext, AccountSettingsActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }


    private void doInBackGround() {

        final JSONObject loginInfo = LogOutSingleton.getInstance(getApplicationContext()).getSessionId();
        final String username = LogOutSingleton.getInstance(getApplicationContext()).getUsername();
        JSONArray ocurence = new JSONArray();
        ocurence.put(loginInfo);
       // setProgressBarVisibility(true);
        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.POST, baseurl + "occurrency/getByUserAndroid/" + username, ocurence, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                finalResponse = response;
                onPostExecute(finalResponse);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                onCancelled(error);
            }
        });

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonRequest);
        //setProgressBarVisibility(false);
    }

    private void onPostExecute(JSONArray finalResponse) {

        for (int i = 0; i < finalResponse.length(); i++) {
            String[] coord = null;
            String title = null;
            String url;
            try {
                JSONObject jsonObject = finalResponse.getJSONObject(i);
                ocurrencys[i] = finalResponse.getJSONObject(i).toString();
                //System.out.println(ocurrencys[i]);
                url = jsonObject.getString("mediaURI").replace("[", "");
                url = url.replace("]", "");

                int jpg = 0;
                if((jpg = url.lastIndexOf(",")) != -1) {
                    String[] middle = url.split(",");
                    url = middle[0].substring(1, url.lastIndexOf(",") -1 );
                }
                else {
                    url = url.substring(1, url.length()-1);
                }


                System.out.println(url);
                if(url.length() != 0) {
                    urls.add(url);
                }

            } catch (JSONException e) {
                onCancelled(e);
            }
        }
        showProgress(false);
        tempGridSetup();
    }

    private void onCancelled(Exception e) {

        if (e instanceof ParseError) {
            System.out.println("Erro sv");
        }
    }

    /**
     * Shows the progress UI and hides the LoginData form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            profile.setVisibility(show ? View.GONE : View.VISIBLE);
            profile.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    profile.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            profile.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void userStatsRest() {

        final JSONObject loginInfo = LogOutSingleton.getInstance(getApplicationContext()).getSessionId();
        final String username = LogOutSingleton.getInstance(getApplicationContext()).getUsername();

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, baseurl + "user/getUserInfo/" + username, loginInfo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                statsFinalResponse = response;
                onPostExecuteUserInfo(statsFinalResponse);
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                onCancelled(error);
            }
        });

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonRequest);

    }

    private void onPostExecuteUserInfo(JSONObject finalResponse) {


            String name;
            String username;
            String email;
            String homeNumber;
            String phoneNumber;
            String address;
            String nif;
            String cc;

             /*
            {"name":"aloo name","username":"aloo","email":"aloo@","homeNumber":"","phoneNumber":"","address":"alooAdr","nif":"","cc":""}
             */
            try {
                JSONObject jsonObject = finalResponse;
                System.out.println(jsonObject);
                name = jsonObject.getString("name");
                username = jsonObject.getString("username");
                email = jsonObject.getString("email");
                homeNumber = jsonObject.getString("homeNumber");
                phoneNumber= jsonObject.getString("phoneNumber");
                address = jsonObject.getString("address");
                nif =  jsonObject.getString("nif");
                cc = jsonObject.getString("cc");

                userDataList.add(new UserData(name, username, email, homeNumber, phoneNumber, address, nif, cc));
                setupUserInfo();


            } catch (JSONException e) {
                onCancelled(e);
            }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_FROM_EDIT) {
                setProfileImage();
            }
        }
    }



}
