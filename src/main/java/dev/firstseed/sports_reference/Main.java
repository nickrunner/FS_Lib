package dev.firstseed.sports_reference;

import dev.firstseed.sports_reference.cbb.CBB;
import dev.firstseed.sports_reference.cbb.Season;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Main {

    public static final String CACHE_DIR = "C:/git/FS_Lib/cache";
    public static String MODELS_DIR = "C:/git/FS_Lib/models";

    public static void main(String[] args)
    {
        SportsReference.cbb(new File(CACHE_DIR));
        CBB cbb = SportsReference.cbb();

        ArrayList<String> statFilter = new ArrayList<>();
        statFilter.add("g");
        //statFilter.add("off_rtg_PG");
        //statFilter.add("def_rtg_PG");
        //statFilter.add("opp_off_rtg_PG");
        //statFilter.add("srs");
        statFilter.add("mp_PG");
        statFilter.add("wins_PG");
        statFilter.add("losses_PG");
        statFilter.add("opp_mp_PG");
        statFilter.add("OffComp");
        statFilter.add("DefComp");
        statFilter.add("GenComp");
        statFilter.add("CompleteComp");
        //statFilter.add("opp_pace_PG");

        Season season = cbb.getSeason(2022);


        int start = 2010;
        int end = 2021;
        double minThresh = 1640;
        double maxThresh = 1700; //1580 seems good
        double thresh = 0.93;
        int depth = 1;
        double gain = 0.001;

        //StatModel model = new StatModel(season.getFilteredTeamStatNames(statFilter));
        StatModel model =  SportsReference.cbb().getStatModel(start, end, season.getFilteredTeamStatNames(statFilter));

//        for(int i=start; i<end; i++){
//            model =  SportsReference.cbb().getStatModel(start, end, season.getFilteredTeamStatNames(statFilter));
//            System.out.println("Optimizing "+i+" to "+end+" for "+thresh);
//            optimizeModel(model, statFilter, i, end, thresh, gain, depth);
//        }

        try{
            printSimTournament(season);
        }
        catch (Exception err){
            err.printStackTrace();
        }

    }

    public static void printSimTournament(AbstractSeason season) throws IOException {
        File modelsDir = new File(MODELS_DIR+"/v3");
        ArrayList<StatModel> models = new ArrayList<>();
        for(File file : modelsDir.listFiles()){
            String filename = file.getName();
            String[] split = filename.split("_");
            String pct = split[split.length-1].replace(".json", "");
            if(Integer.parseInt(pct) > 90 || Integer.parseInt(pct) < 82){
                continue;
            }
            models.add(new StatModel(Files.readString(Path.of(file.getAbsolutePath()))));
        }
        season.getPredictedBracket(models).print();
    }

    public static void optimizeModel(StatModel model, ArrayList<String> statFilter, final int startYear, final int endYear, double threshold, double gain, int depth)
    {
        int c = 0;
        double acc = SportsReference.cbb().adjustModel(model, statFilter, gain, startYear, endYear, depth);
        double sc = 0;
        System.out.println("... Count: "+(c)+" thresh:"+threshold+" score = "+acc);
        while( acc  < threshold)
        {
            c++;
            sc = SportsReference.cbb().adjustModel(model, statFilter, gain, startYear, endYear, depth);
            if(sc > acc)
            {
                try
                {
                    model.save(new File(MODELS_DIR+"/v3/"+startYear+"_"+endYear+"_"+depth+"_"+Math.round(sc*100)+".json"));
                }
                catch (Exception e) {
                    System.out.println("Failed to save model");
                    e.printStackTrace();
                }
                SportsReference.cbb().getSeason(2022).getPredictedBracket(model).print();
                System.out.println("\rOptimized! Count: "+(c)+" thresh:"+threshold+" score = "+sc);

                acc = sc;


            }
            System.out.println("... Count: "+(c)+" "+startYear+"-"+endYear+" thresh:"+threshold+" score = "+sc);
           /* if(c > 500)
            {
                System.out.println("Failed: Count: "+(c)+" thresh:"+threshold+" score = "+sc);
                return;
            }*/

        }


    }

    public static void optimizeModelForScore(StatModel model, ArrayList<String> statFilter, final int startYear, final int endYear, double threshold, double gain, int depth)
    {
        int c = 0;
        double score = SportsReference.cbb().adjustModelForScore(model, statFilter, gain, startYear, endYear, depth);
        double sc = 0;
        System.out.println("... Count: "+(c)+" thresh:"+threshold+" score = "+score);
        while( score  < threshold)
        {
            c++;
            sc = SportsReference.cbb().adjustModelForScore(model, statFilter, gain, startYear, endYear, depth);
            if(sc > score)
            {
                try
                {
                    model.save(new File(MODELS_DIR+"/v3/"+startYear+"_"+endYear+"_"+depth+"_"+Math.round(sc)+".json"));
                }
                catch (Exception e) {
                    System.out.println("Failed to save model");
                    e.printStackTrace();
                }
                SportsReference.cbb().getSeason(2022).getPredictedBracket(model).print();
                System.out.println("\rOptimized! Count: "+(c)+" thresh:"+threshold+" score = "+Math.round(sc));

                score = sc;


            }
            System.out.println("... Count: "+(c)+" thresh:"+threshold+" score = "+sc);
           /* if(c > 500)
            {
                System.out.println("Failed: Count: "+(c)+" thresh:"+threshold+" score = "+sc);
                return;
            }*/

        }

    }

}
