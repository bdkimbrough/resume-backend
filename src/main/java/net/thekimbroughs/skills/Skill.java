package net.thekimbroughs.skills;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.UUID;

@DataObject(generateConverter = true)
public class Skill {

    private String id;
    private String name;
    private String description;
    private String startDate;
    private String endDate;

    public Skill(JsonObject json) {
        SkillConverter.fromJson(json, this);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        SkillConverter.toJson(this, json);
        return json;
    }

    @Override
    public String toString() {
        return "Skill: " + toJson().encodePrettily();
    }

    public static void fromJson(JsonObject json, Skill certification) {
        SkillConverter.fromJson(json, certification);
    }
}
