package com.example.hvale.loginapp;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hvale.loginapp.dummy.DummyContent;

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
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_CONTENT = "content";
    private String id = null;
    private String content = null;

    /**
     * The dummy content this fragment is presenting.
     */
    private OccurrenceListActivity.OccurrenceItem mItem;

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
            System.out.println(id + " *** " + content);


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

        // Show the dummy content as text in a TextView.
        if (id != null) {
            ((TextView) rootView.findViewById(R.id.occurrence_detail)).setText(content);
        }

        return rootView;
    }
}
