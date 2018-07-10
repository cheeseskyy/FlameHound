package typeClasses;

import android.app.SearchManager;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class OcurrenceData {

    private static final int SEVEN = 7;
    public String user;
    public String location;
    public String id;
    public OcurrencyType type;
    public List<String> mediaURI;
    public String title;
    public String description;
    public OcurrencyFlags flag;
    public String worker;

    public OcurrenceData() {

    }

    public OcurrenceData(String title, String description, String user, String location, String type, List<String> mediaURI) {
        this.title = title;
        this.description = description;
        this.user = user;
        location = "(" + location + ")";
        location = location.replace(":", ", ");
        this.location = location;
        this.type = selectType(type);
        this.id = "";
        this.mediaURI = new ArrayList<>();
        if (mediaURI.isEmpty())
            mediaURI.add("");
        this.mediaURI = mediaURI;
        this.flag = OcurrencyFlags.unconfirmed;
        this.worker = "";
    }

    public OcurrenceData(String title, String description, String user, String location, String type, List<String> mediaURI, String flag, String id, String worker) {
        this.title = title;
        this.description = description;
        this.user = user;
        this.location = location;
        this.type = selectType(type);
        this.mediaURI = mediaURI;
        this.flag = OcurrencyFlags.valueOf(flag);
        this.id = id;
        this.worker=worker;
    }

    private OcurrencyType selectType(String type) {
        return OcurrencyType.valueOf(type);
    }

    public String getImageURI(int position){
        return mediaURI.get(position);
    }


    public String[] getOcuInfo() {
        String[] ocuInfo = new String[SEVEN];
        String tempLocation;
        ocuInfo[0] = title;
        ocuInfo[1] = description;
        ocuInfo[2] = user;
        tempLocation = location.replace(", ", ":");
        tempLocation = location.replace("(", "");
        tempLocation = location.replace(")", "");
        ocuInfo[3] = tempLocation;
        ocuInfo[4] = type.toString();
        ocuInfo[5] = mediaURI.get(0);
        ocuInfo[6] = flag.toString();
        return ocuInfo;
    }
}
