package typeClasses;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class CommentForPost implements Serializable {

    private String comment;
    private String user;
    private String replyingTo;
    private String tokenId;


    public CommentForPost(String comment, String user,String replying_to,String tokenId) {
        this.comment = comment;
        this.user = user;
        this.replyingTo = replying_to;
        this.tokenId = tokenId;
    }
}

