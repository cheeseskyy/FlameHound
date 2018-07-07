package com.example.hvale.loginapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hvale.loginapp.dummy.DummyContent;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import typeClasses.OcurrenceData;

import static android.os.StrictMode.setThreadPolicy;

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
    public static final String ARG_IMAGE = "images_id";
    private static final String url = "https://my-first-project-196314.appspot.com/rest/";
    private static final String IMAGEURL = url + "occurrency/getImageUri/";
    private String id = null;
    private String content = null;
    private String images = null;


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

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        setThreadPolicy(policy);



        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            System.out.println(getArguments().getString(ARG_IMAGE));
            System.out.println(getArguments().getString(ARG_CONTENT));


            id = getArguments().getString(ARG_ITEM_ID);
            content = getArguments().getString(ARG_CONTENT);
            images = getArguments().getString(ARG_IMAGE);
            System.out.println(images);
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
        if (images!=null) {
            if(!images.equals("")) {
                try {
                    URL url = new URL(IMAGEURL + images);
                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    ((ImageView) rootView.findViewById(R.id.imageToList)).setImageBitmap(bmp);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return rootView;
    }
}
