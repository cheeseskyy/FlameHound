package typeClasses;

import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class OccurrenceDataRegister {

    private static final int FIVE = 5;
    public String user;
    public String location;
    public OcurrencyType type;
    public List<String> mediaURI;
    public String title;
    public String description;
    public OcurrencyFlags flag;

    public OccurrenceDataRegister() {

    }

    public OccurrenceDataRegister(String title, String description, String user, String location, String type, List<String> mediaURI) {
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
    }

    private OcurrencyType selectType(String type) {
        return OcurrencyType.valueOf(type);
    }

    public String[] getOcuInfo() {
        String[] ocuInfo = new String[FIVE + mediaURI.size()];
        String tempLocation;
        ocuInfo[0] = title;
        ocuInfo[1] = description;
        ocuInfo[2] = user;
        tempLocation = location.replace(", ", ":");
        tempLocation = location.replace("(", "");
        tempLocation = location.replace(")", "");
        ocuInfo[3] = tempLocation;
        ocuInfo[4] = type.toString();
        for (String image : mediaURI) {
            int i = 5;
            ocuInfo[i] = image;
            i++;
        }
        ocuInfo[FIVE + mediaURI.size() - 1] = flag.toString();
        return ocuInfo;
    }
}

