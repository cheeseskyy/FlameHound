package com.example.hvale.loginapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import com.example.hvale.loginapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.List;

import typeClasses.ImageData;
import typeClasses.UniversalImageLoader;


public class EditProfileFragment extends Fragment{

    private static final String TAG = "EditProfileFragment";
    private static final String baseurl = "https://my-first-project-196314.appspot.com/rest/";
    private static final String URL_SERVER = "https://my-first-project-196314.appspot.com/rest";
    private static LogOutSingleton mInstance;
    private ImageView mProfilePhoto;
    private static final int RESULT_LOAD_IMAGE = 1;
    public static final int RESULT_OK = -1;
    private List<String> mImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);
        mProfilePhoto = (ImageView) view.findViewById(R.id.profile_photo);

        setProfileImage();

        //back arrow for navigating back to "ProfileActivity"
        ImageView backArrow = (ImageView) view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to ProfileActivity");
                getActivity().finish();
            }
        });
        mProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final String username = LogOutSingleton.getInstance(getContext()).getUsername();
        if (requestCode == RESULT_LOAD_IMAGE)
            if (resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                mProfilePhoto.setImageURI(selectedImage);
                BitmapDrawable bitmapDrawImg = ((BitmapDrawable) mProfilePhoto.getDrawable());
                Bitmap bitmapImg = bitmapDrawImg.getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmapImg.compress(Bitmap.CompressFormat.JPEG, 10, stream);
                byte[] imageInByte = stream.toByteArray();

                ImageData image =  new ImageData(imageInByte);
                Gson gson = new Gson();
                String jsonImg = gson.toJson(image);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(jsonImg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, URL_SERVER + "/user/saveProfileImageAndroid/" + username, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            String imageId = (String) response.get("message");
                            /*
                            String imageIdFinal = imageId.substring(1,imageId.length()-1);
                            mImage.add(imageIdFinal);
                            */
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
                VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonRequest);
            }
    }

    private void setProfileImage(){
        Log.d(TAG, "setProfileImage: setting profile photo.");
        final String username = LogOutSingleton.getInstance(getActivity().getApplicationContext()).getUsername();
        String imgURL = baseurl + "user/getImageUri/" + username;
        System.out.println(imgURL);
        UniversalImageLoader.setImage(imgURL, mProfilePhoto, null, "");
    }
    /*
    private void goingBack(String url) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", url);
        getActivity().setResult();
        getActivity().finish();

    }
    */
}

