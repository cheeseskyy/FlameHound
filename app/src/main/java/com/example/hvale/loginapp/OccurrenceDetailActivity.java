package com.example.hvale.loginapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import typeClasses.Comments;

/**
 * An activity representing a single Occurence detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link OccurrenceListActivity}.
 */
public class OccurrenceDetailActivity extends AppCompatActivity {
    private static final String url = "https://my-first-project-196314.appspot.com/rest/";
    private ImageView image;
    private static final String URL = "https://my-first-project-196314.appspot.com/rest/occurrency/getImageUri/";
    private String ocuID;
    private String owner;
    private List<Comments> comments = new ArrayList<>();


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
                doItInBackgroud();
            }
        });
        image = findViewById(R.id.defaultImageToShow);
        String imagem = getIntent().getStringExtra(OccurrenceDetailFragment.ARG_IMAGE);
        ocuID = getIntent().getStringExtra(OccurrenceDetailFragment.ARG_ID);
        owner = getIntent().getStringExtra(OccurrenceDetailFragment.ARG_OWNER);

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(URL + imagem, image);

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
            arguments.putString(OccurrenceDetailFragment.ARG_OWNER, getIntent().getStringExtra(OccurrenceDetailFragment.ARG_OWNER));
            arguments.putString(OccurrenceDetailFragment.ARG_IMAGE, imagem);
            arguments.putString(OccurrenceDetailFragment.ARG_ID, getIntent().getStringExtra(OccurrenceDetailFragment.ARG_ID));
            arguments.putString(OccurrenceDetailFragment.ARG_FLAG, getIntent().getStringExtra(OccurrenceDetailFragment.ARG_FLAG));
            arguments.putString(OccurrenceDetailFragment.ARG_TYPE, getIntent().getStringExtra(OccurrenceDetailFragment.ARG_TYPE));
            arguments.putString(OccurrenceDetailFragment.ARG_WORKER, getIntent().getStringExtra(OccurrenceDetailFragment.ARG_WORKER));
            OccurrenceDetailFragment fragment = new OccurrenceDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.occurence_detail_container, fragment)
                    .commit();
        }
    }

    private void doItInBackgroud() {
        final JSONObject loginInfo = LogOutSingleton.getInstance(getApplicationContext()).getSessionId();
        JSONArray ocurence = new JSONArray();
        ocurence.put(loginInfo);
        setProgressBarVisibility(true);
        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.POST, url + "social/" + ocuID + "/getAllAndroid", ocurence, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                onPostExecute(response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                onCancelled(error);
            }
        });

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonRequest);
        setProgressBarVisibility(false);
    }

    public void onPostExecute(JSONArray finalResponse) {
        String comment;
        String user;
        String date_created;
        String replying_to;
        String id;
        long upVotes;
        long downVotes;
        JSONObject jsonObject;
        comments.clear();
        for (int i = 0; i < finalResponse.length(); i++) {
            try {
                jsonObject = finalResponse.getJSONObject(i);
                comment = jsonObject.getString("comment");
                user = jsonObject.getString("username");
                date_created = jsonObject.getString("postDate");
                upVotes = jsonObject.getLong("upvotes");
                downVotes = jsonObject.getLong("downvotes");
                id = jsonObject.getString("id");
                replying_to = jsonObject.getString("replyingTo");
                Comments com = new Comments(comment, user, date_created, replying_to, id, upVotes, downVotes);
                comments.add(com);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ViewCommentFragment fragment = new ViewCommentFragment();
        Bundle arg = new Bundle();
        arg.putString("ocuID", ocuID);
        arg.putString("replyTo", owner);
        arg.putSerializable("comments", (Serializable) comments);
        fragment.setArguments(arg);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.occurence_detail_container, fragment);
        transaction.addToBackStack(String.valueOf((R.string.view_fragment_comments)));
        transaction.commit();

    }

    public void onCancelled(VolleyError error) {
        error.printStackTrace();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, OccurrenceListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<Comments> getComments() {
        return comments;
    }
}
