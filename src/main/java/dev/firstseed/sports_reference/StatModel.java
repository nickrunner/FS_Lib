package dev.firstseed.sports_reference;

import com.google.gson.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class StatModel
{
    private HashMap<String, StatModelElement> statModelElements;
    private LinkedHashSet<String> statNames;

    public StatModel(){
        statModelElements = new HashMap<>();
    }

    public StatModel(LinkedHashSet<String> statNames)
    {
        statModelElements = new HashMap<>();
        this.statNames = statNames;


        for(String statName : statNames) {
            setWeight(statName, 0.00001);
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
            JsonObject statJson = jsonArr.get(i).getAsJsonObject().get(statName).getAsJsonObject();
            double weight = statJson.get("weight").getAsDouble();

            StatModelElement statModelElement = new StatModelElement(statName, weight);
            try{
                JsonArray correlations = statJson.getAsJsonArray("correlations");
                statModelElement.setStatModel(new StatModel(correlations.toString()));
            }catch (Exception e){

            }
            statModelElements.put(statName, statModelElement);
            i++;
        }
    }

    public StatModel(StatModel statModel)
    {
        statModelElements = (HashMap<String, StatModelElement>)statModel.statModelElements.clone();
        statNames = (LinkedHashSet<String>)statModel.statNames.clone();
    }

    public int calculateDepth(int fromDepth){
        StatModelElement element = null;
        for(String statName : statModelElements.keySet()){
            element = statModelElements.get(statName);
            if(element.isStatModelNull()){
                return fromDepth;
            }
            break;
        }
        if(element != null){
            return element.getStatModel().calculateDepth(fromDepth+1);
        }
        return 0;

    }

    public JsonArray toJson()
    {
        JsonArray jsonArr = new JsonArray();
        for(String statName : statModelElements.keySet())
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

//    public void addStat(String statName, double weight)
//    {
//        LinkedHashSet<String> correlationStats = (LinkedHashSet<String>) statNames.clone();
//        statModelElements.put(statName, new StatModelElement(weight, correlationStats));
//    }

    public void setWeight(String statName, double weight)
    {
        if(statModelElements.get(statName) == null){
            statModelElements.put(statName, new StatModelElement(statName, weight));
        }
        statModelElements.get(statName).setWeight(weight);
    }

    public double getCorrelation(ArrayList<String> statChain, String stat)
    {
        if(statModelElements == null)
        {
            return 0;
        }
        StatModel model = getModelAtDepth(statChain);
        if(model == null){
            return 0;
        }

        return model.getWeight(stat);
    }

    public void setCorrelation(ArrayList<String> statChain, String statName,  double correlation)
    {
//        if(statModelElements.get(s1) == null)
//        {
//            statModelElements.put(s1,new StatModelElement(s1,0.0001));
//        }
        getModelAtDepth(statChain).setWeight(statName, correlation);
    }

    public StatModel getModelAtDepth(ArrayList<String> stats){
        StatModel model = statModelElements.get(stats.get(0)).getStatModel();

        if(stats.size() > 1){
            return model.getModelAtDepth(new ArrayList<>(stats.subList(1, stats.size())));
        }
        return model;
    }
}
