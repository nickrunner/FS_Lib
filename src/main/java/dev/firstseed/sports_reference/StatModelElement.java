package dev.firstseed.sports_reference;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class StatModelElement
{
    private double weight;
    private StatModel statModel;
    private String statName;

    public StatModelElement(String statName, double weight, StatModel statModel)
    {
        this.statModel = statModel;
        this.setStatName(statName);
        this.weight = weight;
    }

    public StatModelElement(String statName, double weight)
    {
        this.setStatName(statName);
        this.weight = weight;
    }

    public void setStatModel(StatModel statModel){
        this.statModel = statModel;
    }
    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }


    public StatModel getStatModel(){
        if(statModel == null){
            this.statModel = new StatModel();
        }
        return this.statModel;
    }

    public boolean isStatModelNull(){
        return this.statModel == null;
    }

    public JsonObject toJson()
    {
        JsonObject json = new JsonObject();
        json.addProperty("weight", weight);
        if(statModel != null){
            JsonArray jsonArr = statModel.toJson();
            json.add("correlations", jsonArr);
        }

        return json;
    }

    public String getStatName() {
        return statName;
    }

    public void setStatName(String statName) {
        this.statName = statName;
    }
}
