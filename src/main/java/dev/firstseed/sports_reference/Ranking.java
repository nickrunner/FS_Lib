package dev.firstseed.sports_reference;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Ranking implements Serializable
{
    public ArrayList<AbstractStatCreator> statCreators;
    public String tag;
    public double coorelation;
    public double normalizedCoorelation;

    private Statistic stat;

    public Ranking(final String tag, ArrayList<? extends AbstractStatCreator> teams)
    {
        this.tag = new String(tag);
        this.statCreators = (ArrayList<AbstractStatCreator>)teams.clone();
        this.stat = teams.get(0).getStat(tag);
        Collections.sort(this.statCreators, (o1, o2) -> {
            Statistic stat1 = o1.getStat(tag);
            Statistic stat2 = o2.getStat(tag);
            double r1;
            double r2;


            if(stat1 == null ){
                r1 = 0;
            }
            else if(new Double(stat1.getValue()).isNaN())
            {
                r1 = 0;
            }
            else{
                r1 = stat1.normalizedValue;
            }
            if(stat2 == null){
                r2 = 0;
            }
            else if(new Double(stat2.getValue()).isNaN())
            {
                r2 = 0;
            }
            else{
                r2 = stat2.normalizedValue;
            }

            return Double.compare(r2, r1);
        });
    }

    public Statistic getStat()
    {
        return this.stat;
    }

    public String toString(){
        String string =  "\n\n\n************* "+tag+" ****************";
        int i = 1;
        for(AbstractStatCreator statCreator : statCreators){
            string += "\n"+i+++". "+statCreator.name+"\t"+statCreator.getStat(tag).normalizedValue*100;
        }
        return string;
    }

/*
    public File toCsv(String dirPath){
        String string =  "\n\n\n,"+tag;
        int i = 1;
        string += "\nRank,Name,Val,Normalized,Weight,Enabled";
        File csv = new File(dirPath+"/"+tag+".csv");



        try{
            if(!csv.exists()){
                csv.createNewFile();
            }
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(csv));
            outputStreamWriter.write(string);
            for(AbstractStatCreator statCreator : statCreators){
                String line = "\n"+i+++","+statCreator.name+","+statCreator.getStat(tag).normalizedValue+","+statCreator.getStat(tag).value+","+statCreator.getStat(tag).weight+","+statCreator.getStat(tag).enabled;
                //Log.d("RANK", "Writing Line->"+line);
                outputStreamWriter.write(line);
            }

            return csv;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
*/

    public void print(boolean onlyTournament){
        String string =  "\n\n\n************* "+tag+" ****************";
        System.out.println("RANKING: "+string);
        int i = 1;
        for(AbstractStatCreator statCreator : statCreators){
            if(onlyTournament)
            {
                if(statCreator.isInTournament)
                {
                    string = ""+i+++"|"+statCreator.name+"|"+statCreator.getStat(tag).getNormalizedValue()+"|"+statCreator.getStat(tag).getValue();
                    System.out.println(string);
                }
            }
            else{
                string = ""+i+++"|"+statCreator.name+"|"+statCreator.getStat(tag).getNormalizedValue()+"|"+statCreator.getStat(tag).getValue();
                System.out.println(string);
            }


        }
    }

}
