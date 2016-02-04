package ua.nau.edu.RecyclerViews.LectorsActivity;

public class LectorsDataModel {

    String name;
    String uniqueId;
    String photoUrl;
    String institute;

    public LectorsDataModel(String name, String uniqueId, String photoUrl, String institute) {
        this.name = name;
        this.uniqueId = uniqueId;
        this.photoUrl = photoUrl;
        this.institute = institute;
    }

    public String getName() {
        return name;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getInstitute() {return institute;}
}
