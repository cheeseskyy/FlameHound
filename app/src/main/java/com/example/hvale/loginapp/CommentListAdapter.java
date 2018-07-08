package com.example.hvale.loginapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import typeClasses.Comments;

public class CommentListAdapter extends ArrayAdapter{

    private static final String TAG = "CommentListAdapter";

    private LayoutInflater mInflater;
    private int layoutResource;
    private Context context;

    public CommentListAdapter(@NonNull Context context, int resource, @NonNull List<Comments> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResource = resource;
    }

    private static class ViewHolder{

        TextView comment;
        TextView username;
        TextView timestamp;
        //CircleImageView profileImage;
        TextView reply;
        ImageView like;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

       final ViewHolder holder;

       if(convertView.equals(null)) {
           convertView = mInflater.inflate(layoutResource, parent, false);
           holder = new ViewHolder();
           holder.comment = (TextView) convertView.findViewById(R.id.comment);
           holder.username = (TextView) convertView.findViewById(R.id.username);
           holder.timestamp = (TextView) convertView.findViewById(R.id.comment_time_posted);
           holder.reply = (TextView) convertView.findViewById(R.id.comment_reply);
        //   holder.like = (ImageView) convertView.findViewById(R.id.comment_like);
         //  holder.profileImage = (CircleImageView) convertView.findViewById(R.id.comment_profile_image);

           convertView.setTag(holder);
       }
       else{
           holder = (ViewHolder) convertView.getTag();
       }
       Comments com =  (Comments) getItem(position);
       holder.comment.setText(com.getComment());
       holder.timestamp.setText(com.getDate_created());
       holder.username.setText(com.getUser());

       return convertView;
    }


}
