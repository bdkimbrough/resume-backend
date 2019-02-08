package net.thekimbroughs.positions;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.List;

@DataObject(generateConverter = true)
public class Position {

    private String id;
    private String title;
    private String summary;
    private List<String> responsibilities;
    private String startDate;
    private String endDate;

    public Position(JsonObject json) {
        PositionConverter.fromJson(json, this);
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

    public List<String> getResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(List<String> responsibilities) {
        this.responsibilities = responsibilities;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        PositionConverter.toJson(this, json);
        return json;
    }

    public static void fromJson(JsonObject json, Position certification) {
        PositionConverter.fromJson(json, certification);
    }
}
