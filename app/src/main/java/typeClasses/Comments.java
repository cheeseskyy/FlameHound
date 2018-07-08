package typeClasses;

public class Comments {

    private String comment;
    private String user;
    private String date_created;
    private String replying_to;
    private String id;
    private long upVotes;
    private long downVotes;


    public Comments(String comment, String user, String date_created,String replying_to,String id, long upVotes, long downVotes) {
        this.comment = comment;
        this.user = user;
        this.date_created = date_created;
        this.replying_to = replying_to;
        this.id = id;
        this.upVotes = upVotes;
        this.downVotes = downVotes;
    }

    public String getComment() {
        return comment;
    }

    public String getUser() {
        return user;
    }

    public String getDate_created() {
        return date_created;
    }
}
