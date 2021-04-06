package dev.firstseed.sports_reference;

import dev.firstseed.sports_reference.cbb.CBB;
import dev.firstseed.sports_reference.cbb.Season;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class Main {



    public static void main(String[] args)
    {
        SportsReference.cbb(new File("C:/FS/cache"));
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

        Season season = cbb.getSeason(2021);

        int start = 2016;
        int end = 2019;
        double minThresh = 1640;
        double maxThresh = 1700; //1580 seems good
        double thresh = 100;


        StatModel model =  SportsReference.cbb().getStatModel(start, end, season.getFilteredTeamStatNames(statFilter));
        //StatModel model = new StatModel(season.getFilteredTeamStatNames(statFilter));

        double gain = 0.001;
        System.out.println("Optimizing "+start+" to "+end+" for "+thresh);
        optimizeModel(model, statFilter, start, end, thresh, gain);

        season.getPredictedBracket(model).print();

    }

    public static void optimizeModel(StatModel model, ArrayList<String> statFilter, final int startYear, final int endYear, double threshold, double gain)
    {
        int c = 0;
        double acc = SportsReference.cbb().adjustModel(model, statFilter, gain, startYear, endYear);
        double sc = 0;
        System.out.println("... Count: "+(c)+" thresh:"+threshold+" score = "+acc);
        while( acc  < threshold)
        {
            c++;
            sc = SportsReference.cbb().adjustModel(model, statFilter, gain, startYear, endYear);
            if(sc > acc)
            {
                try
                {
                    model.save(new File("C:/FS/models/v2/"+startYear+"_"+endYear+"_"+Math.round(sc)+".json"));
                }
                catch (Exception e) {
                    System.out.println("Failed to save model");
                    e.printStackTrace();
                }
                SportsReference.cbb().getSeason(2021).getPredictedBracket(model).print();
                System.out.println("\rOptimized! Count: "+(c)+" thresh:"+threshold+" score = "+sc);

                acc = sc;


            }
            System.out.println("... Count: "+(c)+" thresh:"+threshold+" score = "+sc);
           /* if(c > 500)
            {
                System.out.println("Failed: Count: "+(c)+" thresh:"+threshold+" score = "+sc);
                return;
            }*/

        }


    }

    public static void optimizeModelForScore(StatModel model, ArrayList<String> statFilter, final int startYear, final int endYear, double threshold, double gain)
    {
        int c = 0;
        double score = SportsReference.cbb().adjustModelForScore(model, statFilter, gain, startYear, endYear);
        double sc = 0;
        System.out.println("... Count: "+(c)+" thresh:"+threshold+" score = "+score);
        while( score  < threshold)
        {
            c++;
            sc = SportsReference.cbb().adjustModelForScore(model, statFilter, gain, startYear, endYear);
            if(sc > score)
            {
                try
                {
                    model.save(new File("C:/FS/models/v2/"+startYear+"_"+endYear+"_"+Math.round(sc)+".json"));
                }
                catch (Exception e) {
                    System.out.println("Failed to save model");
                    e.printStackTrace();
                }
                SportsReference.cbb().getSeason(2021).getPredictedBracket(model).print();
                System.out.println("\rOptimized! Count: "+(c)+" thresh:"+threshold+" score = "+sc);

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
