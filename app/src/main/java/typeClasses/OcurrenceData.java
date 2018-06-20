package typeClasses;

import java.util.List;

public class OcurrenceData {

    public String user;
    public String location;
    public OcurrencyType type;
    public List<String> mediaURI;
    public String title;
    public String description;
    public OcurrencyFlags flag;

    public OcurrenceData() {

    }

   public OcurrenceData(String title, String description, String user, String location, String type, List<String> mediaURI) {
        this.title = title;
        this.description = description;
        this.user = user;
        this.location = location;
        this.type = selectType(type);
        this.mediaURI = mediaURI;
        this.flag = OcurrencyFlags.unconfirmed;
    }

    public OcurrenceData(String title, String description, String user, String location, String type, List<String> mediaURI, String flag) {
        this.title = title;
        this.description = description;
        this.user = user;
        this.location = location;
        this.type = selectType(type);
        this.mediaURI = mediaURI;
        this.flag = OcurrencyFlags.valueOf(flag);
    }

    private OcurrencyType selectType(String type) {
        return OcurrencyType.valueOf(type);
    }
}
