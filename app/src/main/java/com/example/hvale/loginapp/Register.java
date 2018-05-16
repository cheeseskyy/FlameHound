package com.example.hvale.loginapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.net.NetworkInfo;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class Register extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private static final String URL_SERVER = "https://my-first-project-196314.appspot.com/rest";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserRegisterTask mAuthTask = null;

    // UI references.
    private EditText mNameView;
    private EditText mPasswordView;
    private EditText mPasswordConfirmationView;
    private EditText mUserNameView;
    private EditText mEmailView;
    private EditText mAddressView;
    private View mProgressView;
    private View mRegisterFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mNameView = findViewById(R.id.name);
        mUserNameView = findViewById(R.id.username);
        mEmailView = findViewById(R.id.email);
        mAddressView = findViewById(R.id.address);
        mPasswordView = findViewById(R.id.password);
        mPasswordConfirmationView = findViewById(R.id.confirmation);

        Button mEmailSignInButton = findViewById(R.id.register_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mRegisterFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mNameView.setError(null);
        mUserNameView.setError(null);
        mEmailView.setError(null);
        mAddressView.setError(null);
        mPasswordView.setError(null);
        mPasswordConfirmationView.setError(null);

        // Store values at the time of the login attempt.
        String name = mNameView.getText().toString();
        String username = mUserNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String address = mAddressView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirmation = mPasswordConfirmationView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password,confirmation)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserRegisterTask(name, username, email, address, password,confirmation);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password, String confirmation) {
        return password.length() > 4 && password.equals(confirmation);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void,JSONObject> {

        private final String mName;
        private final String mUsername;
        private final String mEmail;
        private final String mAddress;
        private final String mPassword;
        private final String mPasswordConfirmation;
        private JSONObject finalResponse;

        UserRegisterTask(String name, String username, String email, String address, String password, String confirmation) {
            mName = name;
            mUsername = username;
            mEmail = email;
            mAddress = address;
            mPassword = password;
            mPasswordConfirmation = confirmation;
            finalResponse = null;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            Map<String, String> jsonObjParam = new HashMap();
            jsonObjParam.put("name",mName);
            jsonObjParam.put("username", mUsername);
            jsonObjParam.put("email", mEmail);
            jsonObjParam.put("role","");
            jsonObjParam.put("homeNumber","");
            jsonObjParam.put("phoneNumber","");
            jsonObjParam.put("address",mAddress);
            jsonObjParam.put("nif","");
            jsonObjParam.put("cc","");
            jsonObjParam.put("password", mPassword);
            jsonObjParam.put("confirmation", mPasswordConfirmation);
            JSONObject jsonObject = new JSONObject(jsonObjParam);
            setProgressBarVisibility(true);
            RegisterRequest jsonRequest = new RegisterRequest(Request.Method.POST, URL_SERVER + "/register/v3", jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                        finalResponse = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    onCancelled();
                }
            });
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonRequest);
            setProgressBarVisibility(false);
            return finalResponse;
        }


        @Override
        protected void onPreExecute() {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected() ||
                    (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                            && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
                cancel(true);
            }
        }

        protected void onPostExecute(final JSONObject  finalResponse) {
        int statusCode;
        mAuthTask = null;
        showProgress(false);

        try {
            statusCode = (int) finalResponse.get("statusCode");
            if (statusCode == 200) {
                finish();
                Intent it = new Intent(Register.this, LoginActivity.class);
                startActivity(it);
            } else if(statusCode == 405) {
                mUserNameView.setError(getString(R.string.userName_Email_used));
                mUserNameView.requestFocus();
            }else if (statusCode == -1) {
                onCancelled();
            }
        }catch (JSONException e ){
            onCancelled();
        }
    }

    @Override
    protected void onCancelled() {
        mAuthTask = null;
        showProgress(false);
        mPasswordView.setError(getString(R.string.unable_to_register));
        mPasswordView.requestFocus();
    }
}
}



