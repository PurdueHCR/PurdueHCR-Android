package Models;

public class Link {
    private String linkId;
    private String description;
    private boolean singleUse;
    private int pointTypeId;
    private boolean isEnabled;
    private boolean isArchived;

    public Link(String linkId, String description, boolean singleUse, int pointTypeId, boolean isEnabled, boolean isArchived) {
        this.linkId = linkId;
        this.description = description;
        this.singleUse = singleUse;
        this.pointTypeId = pointTypeId;
        this.isEnabled = isEnabled;
        this.isArchived = isArchived;
    }

    public Link(String description, boolean singleUse, int pointTypeId) {
        this.description = description;
        this.singleUse = singleUse;
        this.pointTypeId = pointTypeId;
    }

    @Override
    public String toString() {
        return "Link{" +
                "linkId='" + linkId + '\'' +
                ", description='" + description + '\'' +
                ", singleUse=" + singleUse +
                ", pointTypeId=" + pointTypeId +
                ", isEnabled=" + isEnabled +
                ", isArchived=" + isArchived +
                '}';
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public void setSingleUse(boolean singleUse) {
        this.singleUse = singleUse;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public String getLinkId() {
        return linkId;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSingleUse() {
        return singleUse;
    }

    public int getPointTypeId() {
        return pointTypeId;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public boolean isArchived() {
        return isArchived;
    }
}
