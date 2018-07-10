package typeClasses;

import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class OccurrenceDataRegister {

    private static final int EIGHT = 8;
    public String user;
    public String location;
    public OcurrencyType type;
    public List<String> mediaURI;
    public String title;
    public String description;
    public OcurrencyFlags flag;
    public String array;

    public OccurrenceDataRegister() {

    }

    public OccurrenceDataRegister(String title, String description, String user, String location, String type, List<String> mediaURI, String array) {
        this.title = title;
        this.description = description;
        this.user = user;
        location = "(" + location + ")";
        location = location.replace(":", ", ");
        this.location = location;
        this.type = selectType(type);
        this.mediaURI = new ArrayList<>();
        if (mediaURI.isEmpty())
            mediaURI.add("");
        this.mediaURI = mediaURI;
        this.flag = OcurrencyFlags.unconfirmed;
        this.array = array;

    }

    private OcurrencyType selectType(String type) {
        return OcurrencyType.valueOf(type);
    }

    public String[] getOcuInfo() {
        String[] ocuInfo = new String[EIGHT];
        String tempLocation;
        ocuInfo[0] = title;
        ocuInfo[1] = description;
        ocuInfo[2] = user;
        tempLocation = location.replace(", ", ":");
        tempLocation = location.replace("(", "");
        tempLocation = location.replace(")", "");
        ocuInfo[3] = tempLocation;
        ocuInfo[4] = type.toString();
        ocuInfo[5] = flag.toString();
        ocuInfo[6] = mediaURI.get(0);
        ocuInfo[7] = array.toString();
        return ocuInfo;
    }
}

