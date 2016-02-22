package ua.nau.edu.RecyclerViews.NewsActivity;

public class NewsDataModel {

    int id;
    String author;
    String authorUniqueId;
    String authorPhotoUrl;
    String message;
    String createTime;
    boolean isExpanded;

    public NewsDataModel(int id, String author, String authorUniqueId, String authorPhotoUrl, String message, String createTime) {
        this.id = id;
        this.author = author;
        this.authorUniqueId = authorUniqueId;
        this.authorPhotoUrl = authorPhotoUrl;
        this.message = message;
        this.createTime = createTime;
        this.isExpanded = false;
    }

    public int getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getAuthorUniqueId() {
        return authorUniqueId;
    }

    public String getAuthorPhotoUrl() {
        return authorPhotoUrl;
    }

    public String getMessage() {
        return message;
    }

    public String getCreateTime() {
        return createTime;
    }

    public boolean getExpandedState() {
        return isExpanded;
    }

    public void setExpandedState(boolean state) {
        isExpanded = state;
    }
}
