package com.example.hvale.loginapp;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import typeClasses.OcurrenceData;

/**
 * A fragment representing a single Occurence detail screen.
 * This fragment is either contained in a {@link OccurrenceListActivity}
 * in two-pane mode (on tablets) or a {@link OccurrenceDetailActivity}
 * on handsets.
 */
public class OccurrenceDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    private static final String URL_SERVER = "https://my-first-project-196314.appspot.com/rest";

    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_CONTENT = "content";
    public static final String ARG_IMAGE = "imageToList";
    public static final String ARG_OWNER = "owner";
    public static final String ARG_ID = "ocu_id";
    public static final String ARG_FLAG = "flag";
    public static final String ARG_TYPE = "type";
    public static final String ARG_WORKER = "worker";


    private String id = null;
    private String content = null;
    private String flag = null;
    private String type = null;
    private String worker = null;
    private String upperFlag = null;
    private String upperType = null;
    private String ocuId = null;
    private String vote = null;


    /**
     * The dummy content this fragment is presenting.
     */
    private OcurrenceData mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OccurrenceDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            id = getArguments().getString(ARG_ITEM_ID);
            content = getArguments().getString(ARG_CONTENT);
            flag = getArguments().getString(ARG_FLAG);
            type = getArguments().getString(ARG_TYPE);
            worker = getArguments().getString(ARG_WORKER);
            ocuId = getArguments().getString(ARG_ID);
            upperFlag = flag.substring(0, 1).toUpperCase() + flag.substring(1);
            upperType = type.substring(0, 1).toUpperCase() + type.substring(1);
            vote = "";

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(id);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.occurrence_detail, container, false);
        String details = "";
        // Show the dummy content as text in a TextView.

        ((ImageView) rootView.findViewById(R.id.upVote)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vote = "upVote";
                doItInBackgroud();
            }
        });

        ((ImageView) rootView.findViewById(R.id.downVote)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vote = "downVote";
                doItInBackgroud();
            }
        });


        if (id != null) {
            if (worker.equals("")) {
                details = String.format("Descrição:\n%s\n\nEstado:\n%s\n\nSeveridade:\n%s\n", content, upperFlag, upperType);
            } else {
                details = String.format("Descrição:\n%s\n\nEstado:\n%s\n\nSeveridade:\n%s\n\nTrabalhador:\n%s", content, upperFlag, upperType, worker);
            }
            ((TextView) rootView.findViewById(R.id.occurrence_detail)).setText(details);
        }

        return rootView;
    }

    private void doItInBackgroud() {
        JSONObject sessionInfo = null;
        if (vote.equals("upVote")) {
            sessionInfo = LogOutSingleton.getInstance(getContext()).getSessionId();
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, URL_SERVER + "/user/vote/upvote/" + ocuId, sessionInfo, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    onPostExecute();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    onCancelled(error);
                }
            });
            VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonRequest);
        } else if (vote.equals("downVote")) {
            sessionInfo = LogOutSingleton.getInstance(getContext()).getSessionId();
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, URL_SERVER + "/user/vote/downvote/" + ocuId, sessionInfo, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    onPostExecute();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    onCancelled(error);
                }
            });
            VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonRequest);
        }
    }

    private void onPostExecute() {
        if (vote.equals("upVote"))
            Toast.makeText(getActivity(), "Occurrence up voted!", Toast.LENGTH_SHORT).show();
        else if (vote.equals("downVote"))
            Toast.makeText(getActivity(), "Occurrence down voted!", Toast.LENGTH_SHORT).show();

    }

    private void onCancelled(VolleyError error) {
        error.printStackTrace();
        if (error instanceof ServerError) {
            if (vote.equals("upVote"))
                Toast.makeText(getActivity(), "You already up voted!", Toast.LENGTH_SHORT).show();
            else if (vote.equals("downVote"))
                Toast.makeText(getActivity(), "You already down voted!", Toast.LENGTH_SHORT).show();
        }
    }
}
