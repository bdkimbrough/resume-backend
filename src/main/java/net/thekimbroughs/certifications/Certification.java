package net.thekimbroughs.certifications;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class Certification implements JsonBasedObject<Certification> {

    private String id;
    private String title;
    private String summary;
    private String dateAchieved;
    private String imageLocation;

    public Certification(JsonObject json) {
        CertificationConverter.fromJson(json, this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDateAchieved() {
        return dateAchieved;
    }

    public void setDateAchieved(String dateAchieved) {
        this.dateAchieved = dateAchieved;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        CertificationConverter.toJson(this, json);
        return json;
    }

    public static Certification fromJson(JsonObject json, Certification certification) {
        CertificationConverter.fromJson(json, certification);
    }
}
