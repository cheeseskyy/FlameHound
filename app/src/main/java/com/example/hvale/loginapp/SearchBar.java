package com.example.hvale.loginapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toolbar;

import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.model.LatLng;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import typeClasses.OcurrenceData;

public class SearchBar extends AppCompatActivity {

    ArrayList<String> lstSource = new ArrayList<String>();
    MaterialSearchView searchView;
    ListView listView;
    private JSONArray finalResponse = null;
    private static final String url = "https://my-first-project-196314.appspot.com/rest/";
    private final List<OcurrenceData> occurrencys = new LinkedList<OcurrenceData>();
    private RelativeLayout relLay;
    private ProgressBar mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bar);


        relLay = findViewById(R.id.searchLayout);
        mProgressView = findViewById(R.id.searchListProgressBar);
        showProgress(true);
        doInBackGround();
        System.out.println(occurrencys.size());

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.searchToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Occurrency Search");
        toolbar.setTitleTextColor(Color.WHITE);

        listView = findViewById(R.id.searchListView);


        searchView = findViewById(R.id.search_view);


        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                ArrayAdapter adapter = new ArrayAdapter(SearchBar.this, android.R.layout.simple_list_item_1,lstSource);
                listView.setAdapter(adapter);
            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText != null && !newText.isEmpty()) {
                    List<String> lstFound = new ArrayList<>();
                    for(String item:lstSource) {
                        String current = item.toLowerCase();
                        if(current.contains(newText.toLowerCase()))
                            lstFound.add(item);
                    }
                    ArrayAdapter adapter = new ArrayAdapter(SearchBar.this, android.R.layout.simple_list_item_1,lstFound);
                    listView.setAdapter(adapter);
                }
                else {
                    ArrayAdapter adapter = new ArrayAdapter(SearchBar.this, android.R.layout.simple_list_item_1,lstSource);
                    listView.setAdapter(adapter);
                }
                return true;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;

    }

    private void doInBackGround() {
        final JSONObject loginInfo = LogOutSingleton.getInstance(getApplicationContext()).getSessionId();
        JSONArray ocurence = new JSONArray();
        ocurence.put(loginInfo);
        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.POST, url + "occurrency/getOccurrencyAndroid/all", ocurence, new Response.Listener<JSONArray>() {
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
    }

    private void onPostExecute(JSONArray finalResponse) {

        try {
            for (int i = 0; i < finalResponse.length(); i++) {
                String[] coord;
                String user;
                String title;
                String type;
                String description;
                String flag;
                String address = "";
                JSONArray imageArray;
                Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());

                JSONObject jsonObject = finalResponse.getJSONObject(i);
                coord = jsonObject.getString("location").split(",");
                LatLng current = new LatLng(Double.parseDouble(coord[0]), Double.parseDouble(coord[1]));
                title = jsonObject.getString("title");
                description = jsonObject.getString("description");
                user = jsonObject.getString("user");
                type = jsonObject.getString("type");
                flag = jsonObject.getString("flag");
                imageArray = jsonObject.getJSONArray("mediaURI");
                List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(coord[0]), Double.parseDouble(coord[1]), 1);

                if (addresses.size() > 0)
                    address = addresses.get(0).getAddressLine(0);

                List<String> images = new ArrayList<>();
                for (int j = 0; j < imageArray.length(); j++)
                    images.add((String) imageArray.get(0));

                occurrencys.add(new OcurrenceData(title, description, user, address, type, images, flag));
                lstSource.add(title);
                System.out.println(i);
            }
        } catch (JSONException e) {
            onCancelled(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setLstSource();
        showProgress(false);
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

            relLay.setVisibility(show ? View.GONE : View.VISIBLE);
            relLay.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    relLay.setVisibility(show ? View.GONE : View.VISIBLE);
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
            relLay.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    private void setLstSource() {
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,lstSource);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), OccurrenceDetailActivity.class);
                intent.putExtra(OccurrenceDetailFragment.ARG_ITEM_ID, occurrencys.get(position).title);
                intent.putExtra(OccurrenceDetailFragment.ARG_CONTENT, occurrencys.get(position).description);
                intent.putExtra(OccurrenceDetailFragment.ARG_IMAGE, occurrencys.get(position).mediaURI.get(0));

                startActivity(intent);
            }
        });
    }
}
