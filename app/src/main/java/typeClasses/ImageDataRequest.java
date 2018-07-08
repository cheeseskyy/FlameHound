package typeClasses;

public class ImageDataRequest {

    private String user;
    private String tokenID;

    public ImageDataRequest(String username, String tokenID){
        this.user = username;
        this.tokenID = tokenID;
    }
}
