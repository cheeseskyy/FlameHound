package com.example.hvale.loginapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import typeClasses.*;

import static android.os.StrictMode.*;

/**
 * An activity representing a list of Occurences. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link OccurrenceDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class OccurrenceListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private static final String url = "https://my-first-project-196314.appspot.com/rest/";
    private static final String IMAGEURL = url + "occurrency/getImageUri/";
    private final List<OcurrenceData> ocurrencys = new LinkedList<>();
    private final List<LatLng> locations = new LinkedList<>();
    private JSONArray finalResponse = null;
    private View mProgressView;
    private FrameLayout fram_l;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_occurence_list);

        ThreadPolicy policy = new ThreadPolicy.Builder().permitAll().build();
        setThreadPolicy(policy);

        mProgressView = findViewById(R.id.list_progress);
        fram_l = findViewById(R.id.frameLayout);
        showProgress(true);
        System.out.println("Inicio");
        doInBackGround();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (findViewById(R.id.occurence_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        View recyclerView = findViewById(R.id.occurence_list);
        if (recyclerView != null)
            setupRecyclerView((RecyclerView) recyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, ocurrencys, mTwoPane));
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final OccurrenceListActivity mParentActivity;
        private final List<OcurrenceData> mValues;
        private final boolean mTwoPane;

        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OcurrenceData item = (OcurrenceData) view.getTag();
                System.out.println(mTwoPane);
                if (mTwoPane) {
                    Bundle arguments = new Bundle();

                    arguments.putString(OccurrenceDetailFragment.ARG_ITEM_ID, item.title);
                    arguments.putString(OccurrenceDetailFragment.ARG_CONTENT, item.description);
                    arguments.putString(OccurrenceDetailFragment.ARG_IMAGE,item.mediaURI.get(0));
                    OccurrenceDetailFragment fragment = new OccurrenceDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.occurence_detail_container, fragment)
                            .commit();
                } else {

                    Context context = view.getContext();
                    Intent intent = new Intent(context, OccurrenceDetailActivity.class);
                    intent.putExtra(OccurrenceDetailFragment.ARG_ITEM_ID, item.title);
                    intent.putExtra(OccurrenceDetailFragment.ARG_CONTENT, item.description);
                    System.out.println(item.mediaURI.get(0));
                    intent.putExtra(OccurrenceDetailFragment.ARG_IMAGE,item.mediaURI.get(0));
                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(OccurrenceListActivity parent,
                                      List<OcurrenceData> items,
                                      boolean twoPane) {

            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.occurrence_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mIdView.setText(mValues.get(position).title);
            holder.mContentView.setText(mValues.get(position).description);
            holder.mLocation.setText(mValues.get(position).location);
            if(!mValues.get(position).getImageURI(0).equals("")) {
                try {
                    URL url = new URL(IMAGEURL + mValues.get(position).getImageURI(0));
                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    holder.mImageView.setImageBitmap(bmp);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);


        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;
            final TextView mLocation;
            final ImageView mImageView;

            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
                mLocation = (TextView) view.findViewById(R.id.location);
                mImageView = view.findViewById(R.id.imageToList);
            }
        }
    }

    private void doInBackGround() {
        final JSONObject loginInfo = LogOutSingleton.getInstance(getApplicationContext()).getSessionId();
        JSONArray ocurence = new JSONArray();
        ocurence.put(loginInfo);
        setProgressBarVisibility(true);
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
        setProgressBarVisibility(false);
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

                ocurrencys.add(new OcurrenceData(title, description, user, address, type, images, flag));
                showProgress(false);
            }
        } catch (JSONException e) {
            onCancelled(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

            fram_l.setVisibility(show ? View.GONE : View.VISIBLE);
            fram_l.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    fram_l.setVisibility(show ? View.GONE : View.VISIBLE);
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
            fram_l.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
