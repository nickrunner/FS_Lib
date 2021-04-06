package dev.firstseed.sports_reference;

import com.google.gson.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class StatModel
{

    private class StatModelElement
    {
        private double weight;
        private HashMap<String, Double> correlations;
        private LinkedHashSet<String> correlationStatNames;

        public StatModelElement(double weight, LinkedHashSet<String> correlationStats)
        {
            this.correlationStatNames = correlationStats;
            this.weight = weight;
            this.correlations = new HashMap<>();
            for(String  s : correlationStats)
            {
                correlations.put(s, 0.0);
            }
        }
        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }

        public HashMap<String, Double> getCorrelations() {
            return correlations;
        }

        public void setCorrelations(HashMap<String, Double> correlations) {
            this.correlations = correlations;
        }

        public JsonObject toJson()
        {
            JsonObject json = new JsonObject();
            json.addProperty("weight", weight);
            JsonArray jsonArr = new JsonArray();
            for(String s : correlationStatNames)
            {
                JsonObject cJson = new JsonObject();
                cJson.addProperty(s, correlations.get(s));
                jsonArr.add(cJson);
            }
            json.add("correlations", jsonArr);
            return json;
        }
    }

    private HashMap<String, StatModelElement> statModelElements;
    private LinkedHashSet<String> statNames;

    public StatModel(LinkedHashSet<String> statNames)
    {
        statModelElements = new HashMap<>();
        this.statNames = statNames;


        for(String statName : statNames) {
            addStat(statName, 0.00001);
        }
    }

    public StatModel(String string)
    {
        JsonArray jsonArr = JsonParser.parseString(string).getAsJsonArray();
        //System.out.println("JsonArr: "+jsonArr.toString());
        statNames = new LinkedHashSet();
        statModelElements = new HashMap<>();
        for(JsonElement jsonE : jsonArr)
        {
            JsonObject json = jsonE.getAsJsonObject();
            statNames.addAll(json.keySet());
        }

        int i= 0;
        for(String statName : statNames)
        {
            System.out.println("StatName: "+statName+" Json: "+jsonArr.get(i).getAsJsonObject().toString());
            double weight = jsonArr.get(i).getAsJsonObject().get(statName).getAsJsonObject().get("weight").getAsDouble();
            statModelElements.put(statName, new StatModelElement(weight, new LinkedHashSet<>()));
            i++;
        }
    }

    public StatModel(StatModel statModel)
    {
        statModelElements = (HashMap<String, StatModelElement>)statModel.statModelElements.clone();
        statNames = (LinkedHashSet<String>)statModel.statNames.clone();
    }

    public JsonArray toJson()
    {
        JsonArray jsonArr = new JsonArray();
        for(String statName : statNames)
        {
            JsonObject json = new JsonObject();
            json.add(statName, statModelElements.get(statName).toJson());
            jsonArr.add(json);
        }
        return jsonArr;
    }

    public void save(File file) throws  Exception
    {
        if(!file.exists())
        {
            file.createNewFile();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(toJson().toString().getBytes());
    }

    public LinkedHashSet<String> getStatNames()
    {
        return this.statNames;
    }

    public double getWeight(String statName)
    {
        if(statModelElements.containsKey(statName))
        {
            return statModelElements.get(statName).getWeight();
        }

        return 0;
    }

    public void addStat(String statName, double weight)
    {
        LinkedHashSet<String> correlationStats = (LinkedHashSet<String>) statNames.clone();
        statModelElements.put(statName, new StatModelElement(weight, correlationStats));
    }

    public void setWeight(String statName, double weight)
    {
        statModelElements.get(statName).setWeight(weight);
    }

    public double getStatCorrelation(String s1, String s2)
    {
        if(statModelElements == null)
        {
            return 0;
        }
        if(statModelElements.get(s1) == null)
        {
            return 0;
        }
        if(statModelElements.get(s1).getCorrelations() == null)
        {
            return 0;
        }
        if(statModelElements.get(s1).getCorrelations().get(s2) == null)
        {
            return 0;
        }
        return statModelElements.get(s1).getCorrelations().get(s2);
    }

    public void setStatCorrelation(String s1, String s2, double correlation)
    {
        if(statModelElements.get(s1) == null)
        {
            statModelElements.put(s1,new StatModelElement(0.0001, statNames));
        }

        statModelElements.get(s1).getCorrelations().put(s2, correlation);
    }
}
