package com.example.hvale.loginapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import typeClasses.*;


public class ViewCommentFragment extends Fragment {

    private static final String TAG = "ViewCommentsFragment";
    private static final String url = "https://my-first-project-196314.appspot.com/rest/";


    public ViewCommentFragment() {
        super();
        setArguments(new Bundle());
    }


    //widgets
    private ImageView mBackArrow, mCheckMark;
    private EditText mComment;
    private RecyclerView mListView;
    private Comments newCommentObj;
    private CommentListAdapter commentListAdapter;

    //vars
    private List<Comments> mComments = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mComments = (ArrayList<Comments>) getArguments().getSerializable("comments");
        newCommentObj = null;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_comment, container, false);
        mBackArrow = (ImageView) view.findViewById(R.id.backArrow);
        mCheckMark = (ImageView) view.findViewById(R.id.ivPostComment);
        mComment = (EditText) view.findViewById(R.id.comment);
        mListView = (RecyclerView) view.findViewById(R.id.recycle);


        assert mListView != null;
        mListView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        List<Comments> recycleComments = getComments();







        commentListAdapter = new CommentListAdapter(recycleComments);
        mListView.setAdapter(commentListAdapter);

        mCheckMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mComment.getText().toString().equals("")) {
                    Log.d(TAG, "onClick: attempting to submit new comment.");
                    addNewComment(mComment.getText().toString());

                    mComment.setText("");
                    closeKeyboard();
                } else {
                    Toast.makeText(getActivity(), "you can't post a blank comment", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void addNewComment(String newComment) {
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);

        String username = LogOutSingleton.getInstance(getContext()).getUsername();
        String tokenId = LogOutSingleton.getInstance(getContext()).getLoginToken();
        String owner = getArguments().getString("replyTo");
        String ocuId = getArguments().getString("ocuID");
        CommentForPost comment = new CommentForPost(newComment, username, owner, tokenId);
        Comments newCom = new Comments(newComment, username, getTimestamp(), owner, ocuId, 0, 0);
        newCommentObj = newCom;
        Gson gson = new Gson();
        String jsonComment = gson.toJson(comment);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonComment);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "social/" + ocuId + "/post", jsonObject, new Response.Listener<JSONObject>() {
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
        VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void onPostExecute() {
        mComments.add(newCommentObj);
        commentListAdapter.notifyDataSetChanged();
    }

    private void onCancelled(VolleyError error) {
        error.printStackTrace();
    }


    private String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.UK);
        sdf.setTimeZone(TimeZone.getTimeZone("Portugal"));
        return sdf.format(new Date());
    }

    private List<Comments> getComments() {
        return mComments;
    }
}
