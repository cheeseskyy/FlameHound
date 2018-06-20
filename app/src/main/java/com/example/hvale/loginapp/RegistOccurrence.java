package com.example.hvale.loginapp;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.graphics.Bitmap;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ByteArrayPool;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import typeClasses.*;

import static android.R.layout.simple_spinner_item;

public class RegistOccurrence extends AppCompatActivity implements View.OnClickListener {

    private static final String URL_SERVER = "https://my-first-project-196314.appspot.com/rest";
    private static final int RESULT_LOAD_IMAGE = 1;

    ImageView imageToUpload;
    TextInputLayout inputLoc;
    private RegisterOcurrenceTask mAuth;
    //private View mOcurrenceFormView;
    private EditText mTitleView;
    private EditText mLocationView;
    private EditText mDescriptionView;
    private Spinner mTypeView;
    private List<String> mImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist_occurrence);

        Bundle p = getIntent().getExtras();
        if (p != null) {
            String loc = p.getString("Loc", "Localização");
            Log.d("location", loc);
            inputLoc = findViewById(R.id.Localizacao);

            inputLoc.setHintEnabled(true);
            inputLoc.setHint(loc);
            inputLoc.setHintAnimationEnabled(true);

        }

        mAuth = null;
        mTitleView = findViewById(R.id.title);
        mLocationView = findViewById(R.id.location);
        mDescriptionView = findViewById(R.id.description);
        mTypeView = findViewById(R.id.spinner);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mImage = new ArrayList<>();
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent it = new Intent(RegistOccurrence.this, HomePage.class);
                // startActivity(it);
                attemptRegisterOcurrence();
            }
        });

        imageToUpload = findViewById(R.id.imageToUpload);
        imageToUpload.setOnClickListener(this);


        //Spinner Content (put in a method after)
        // Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.types_array, simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mTypeView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageToUpload:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);

            /*    if(imageToUpload != null) {
                    BitmapDrawable bitmapDrawImg = ((BitmapDrawable) imageToUpload.getDrawable());
                    Bitmap bitmapImg = bitmapDrawImg.getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmapImg.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] imageInByte = stream.toByteArray();
                    ByteArrayInputStream streamImage = new ByteArrayInputStream(imageInByte);
                    mImage.add(streamImage.toString());
                }*/
                break;
            //and other listener events (the info)
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            imageToUpload.setImageURI(selectedImage);
        }
    }

    public void attemptRegisterOcurrence() {

        if (mAuth != null)
            return;

        mTitleView.setError(null);
        mLocationView.setError(null);
        mDescriptionView.setError(null);

        String title = mTitleView.getText().toString();
        String location = mLocationView.getText().toString();
        String description = mLocationView.getText().toString();
        String type = (String) mTypeView.getSelectedItem();
        List<String > mediaURI = mImage;
        String username = LogOutSingleton.getInstance(getApplicationContext()).getUsername();
        boolean cancel;
        cancel = false;

        View focusView = mDescriptionView;

        if (cancel) {
            focusView.requestFocus();
        } else {
            //showProgress(true);
            OcurrenceData data = new OcurrenceData(title,description,username,location,type,mediaURI);
            mAuth = new RegisterOcurrenceTask(data);
            mAuth.doInBackground();
        }


    }

    public class RegisterOcurrenceTask {

        /*private String mTitle;
        private String mLocation;
        private String mDescription;
        private String mType;
        private String mUsername;
        private List<String> mMediaURI;*/
        private OcurrenceData ocurrenceData;
        private JSONObject finalResponse;

        RegisterOcurrenceTask(OcurrenceData data) {
            ocurrenceData = data;
        }

        private void doInBackground() {

            Gson gson = new Gson();
            String jsonImg = gson.toJson(ocurrenceData);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(jsonImg);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            setProgressBarVisibility(true);
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, URL_SERVER + "/occurrency/saveOccurrency", jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.print("ola");
                    finalResponse = response;
                    onPostExecute(finalResponse);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print("ola2");
                    error.printStackTrace();
                    onCancelled(error);
                }
            });
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonRequest);
            setProgressBarVisibility(false);
        }

        private void onPostExecute(final JSONObject finalResponse) {
            mAuth = null;
            //   showProgress(false);
            try {
                System.out.print("MUDAR");
                LogOutSingleton.getInstance(getApplicationContext()).setLoginToken(finalResponse);
                finish();
                Intent it = new Intent(RegistOccurrence.this, HomePage.class);
                startActivity(it);
            } catch (JSONException e) {
                onCancelled(e);
            }
        }

        private void onCancelled(Exception e) {
            mAuth = null;
            //  showProgress(false);
            if (e instanceof ServerError) {
                mDescriptionView.setError(getString(R.string.invalid_username));
                mDescriptionView.requestFocus();
            } else if (e instanceof ParseError) {
                System.out.println("Erro sv");
            }
        }

    }
}

