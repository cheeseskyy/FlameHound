package com.example.hvale.loginapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.ImageView;
import com.example.hvale.loginapp.*;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.model.LatLng;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * An activity representing a single Occurence detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link OccurrenceListActivity}.
 */
public class OccurrenceDetailActivity extends AppCompatActivity {
    private static final String url = "https://my-first-project-196314.appspot.com/rest/";
    private JSONArray finalResponse = null;
    private View mProgressView;
    private ImageView image;
    private static final String URL = "https://my-first-project-196314.appspot.com/rest/occurrency/getImageUri/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_occurrence_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        image = findViewById(R.id.defaultImageToShow);
        String imagem = getIntent().getStringExtra(OccurrenceDetailFragment.ARG_IMAGE);
        System.out.println(imagem);

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(URL +  imagem , image);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(OccurrenceDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(OccurrenceDetailFragment.ARG_ITEM_ID));
            arguments.putString(OccurrenceDetailFragment.ARG_CONTENT, getIntent().getStringExtra(OccurrenceDetailFragment.ARG_CONTENT));
            arguments.putString(OccurrenceDetailFragment.ARG_IMAGE,imagem);
            OccurrenceDetailFragment fragment = new OccurrenceDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.occurence_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, OccurrenceListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
