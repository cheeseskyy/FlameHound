package com.example.hvale.loginapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import java.text.ParseException;

import typeClasses.Comments;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.CommentsViewHolder> {

    public class CommentsViewHolder extends RecyclerView.ViewHolder {

        TextView comment;
        TextView username;
        TextView timestamp;

        CommentsViewHolder(View itemView) {
            super(itemView);
            comment = (TextView) itemView.findViewById(R.id.comment);
            username = (TextView) itemView.findViewById(R.id.comment_username);
            timestamp = (TextView) itemView.findViewById(R.id.comment_time_posted);

        }

    }


    private List<Comments> mComments;



    public CommentListAdapter(List<Comments> comments) {
        mComments = comments;
    }


    private static final String TAG = "CommentListAdapter";

    @Override
    public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comment, parent, false);
        return new CommentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentsViewHolder holder, int position) {
        Comments com = mComments.get(position);

        holder.comment.setText(com.getComment());
        holder.username.setText(com.getUser());
        holder.timestamp.setText(com.getDate_created());


    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private String getTimestampDifference(Comments comment) {

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.UK);
        sdf.setTimeZone(TimeZone.getTimeZone("Portugal"));//google 'android list of timezones'
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = comment.getDate_created();
        try {
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24)));
        } catch (ParseException e) {
            System.out.println("ola");
            difference = "0";
        }
        return difference;
    }

}

