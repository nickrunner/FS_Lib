package dev.firstseed.sports_reference.cbb;



import dev.firstseed.sports_reference.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class CBB implements OnReferenceDataReadyListener
{

    private ReferenceDataProvider referenceDataProvider;

    private DownloadListener downloadListener;
    private int yearsToDownload = 0;
    private int yearsDownloaded = 0;



    private HashMap<Integer, Season> seasons;

    public static int MAX_SEASONS_TO_MEM_CACHE = 10;

    public CBB(File cacheDir)
    {
        referenceDataProvider = new ReferenceDataProvider(this, cacheDir);
        seasons = new HashMap<>();
    }

    public void onSeasonReady(int year, Season season) {

       seasons.put(year, season);
       if(downloadListener != null)
       {
           downloadListener.onDownloadComplete(season);
       }


    }

    public void statusUpdate(int total, int remaining)
    {
        if(downloadListener != null){
            downloadListener.onDownloadStatus(total, remaining);
        }
    }

    public void downloadSeasons(int startYear, int endYear, DownloadListener downloadListener)
    {

        this.downloadListener = downloadListener;
        yearsToDownload = endYear - startYear +1;
        for(int i=startYear; i<=endYear; i++)
        {
            downloadSeason(i);
        }
    }

    public void downloadSeason(int year, DownloadListener downloadListener)
    {
        this.downloadListener = downloadListener;
        downloadSeason(year);
    }

    private void downloadSeason(int year)
    {
        referenceDataProvider.getSeason(LeagueType.CBB, year);
    }

    public Season getSeason(int year)
    {
        if(!seasons.containsKey(year))
        {
            downloadSeason(year);
        }

        return seasons.get(year);
    }

    public Team getTeam(int year, String uid)
    {
        Season season = getSeason(year);
        if(season == null){
            //Todo: get team individually
            return null;
        }
        return (Team)getSeason(year).getTeamFromUid(uid);
    }

    public Player getPlayer(int year, String uid)
    {
        return getSeason(year).getPlayerFromUid(uid);
    }



    public StatModel getStatModel(final int startYear, final int endYear, LinkedHashSet<String> statNames) {
        System.out.println("Getting training data from " + startYear + " to " + endYear);
        yearsToDownload = endYear - startYear + 1;
        yearsDownloaded = 0;
        HashMap<String, Double> scoreMap = new HashMap<>();
        HashMap<String, Integer> countMap = new HashMap<>();
        HashMap<String, HashMap<String, Double>> s2Map = new HashMap<>();

        for (String statName : statNames) {

            scoreMap.put(statName, 0.0);
            countMap.put(statName, 0);
        }
        StatModel model = new StatModel(statNames);

        for (int i = startYear; i <= endYear; i++)
        {
            System.out.println("\nDownloading season for reference: " + i);
            Season season = getSeason(i);

            if (season != null) {
                season.calculateComposites(model);
                System.out.println("Download reference season: " + season.getYear() + " complete.");
            } else {
                System.out.println("Download reference season: failed.");
            }

            for (String s1 : model.getStatNames()) {

                if (season.getBracket() == null) {
                    continue;
                }
                for (int r = 0; r < season.getBracket().getNumberOfRounds(); r++)
                {
                    for (Game game : season.getBracket().getRound(r))
                    {
                        if (game == null) {
                            System.out.println("Null game in round " + r + " year " + season.getYear());
                        }
                        if (game.winner == null) {
                            System.out.println("Null winner " + r + " year " + season.getYear());
                        }

                        if (game.loser == null) {
                            System.out.println("Null loser " + r + " year " + season.getYear());
                        }
                        Statistic ws1 = game.winner.getStat(s1);
                        Statistic ls1 = game.loser.getStat(s1);
                        if (ws1 == null || ls1 == null) {
                            continue;
                        }

                        double diff = ws1.getNormalizedValue() - ls1.getNormalizedValue();
                        double score = diff * (game.winnerPts - game.loserPts) * r;


                        score = score + scoreMap.get(s1);
                        scoreMap.put(s1, score);
                        int count = countMap.get(s1) + 1;
                        countMap.put(s1, count);

/*                        for(String s2 : model.getStatNames())
                        {
                            Statistic ws2 = game.winner.getStat(s2);
                            Statistic ls2 = game.loser.getStat(s2);
                            diff = ws1.getNormalizedValue() - ls2.getNormalizedValue();
                            score = diff * (game.winnerPts - game.loserPts) * r;

                        }*/
                    }
                }
            }
        }
        //Average the score for each year it was recorded: Done because not all stats are
        //recorded every year
        for (String s1 : statNames)
        {
            double score = scoreMap.get(s1);
            int count = countMap.get(s1);
            if (count > 0) {
                scoreMap.put(s1, score / (double) count);
            } else {
                scoreMap.put(s1, 0.0);
            }
            model.setWeight(s1, scoreMap.get(s1));
 /*           for(String s2 : statNames)
            {
                model.setStatCorrelation(s1, s2, s2Map.get(s1).get(s2)/count);
            }
*/
            //System.out.println("CBB: Training Result: " + s1 + " -> " + model.getWeight(s1));
        }
        //Find min/max of scores to do normalization
       /* double min = 1;
        double max = 0;
        for (String statName : statNames)
        {
            if (scoreMap.get(statName) < min) {
                min = scoreMap.get(statName);
            }
            if (scoreMap.get(statName) > max) {
                max = scoreMap.get(statName);
            }
        }*/

/*        for (String statName : statNames)
        {
            //Normalize each score. We need normalized values to set the weight
            //double score = scoreMap.get(statName);
            //scoreMap.put(statName, Util.interpolate(score, min, max, 0.0, 1.0));

        }*/

        return model;
    }

    private void adjustModel(StatModel model, int year, ArrayList<String> statFilter, double gain)
    {
        //StatModel copyModel = new StatModel(model);
        // getSeason(year).calculateComposites(model);
        LinkedHashSet<String> statNames = getSeason(year).getFilteredTeamStatNames(statFilter);

        for (int r = 0; r < getSeason(year).getBracket().getNumberOfRounds(); r++) {
            for (int g = 0; g< getSeason(year).getBracket().getRound(r).size(); g++) {
                Game aGame = getSeason(year).getBracket().getRound(r).get(g);
                Game pGame = new Game(aGame.team1, aGame.team2);
                pGame.predictOutcome(model);
                if (! (aGame.winner.name.equals(pGame.winner.name) && aGame.loser.name.equals(pGame.loser.name) )) {
                    //We made the wrong prediction... adjust model
                    for (String s1 : statNames) {
                        Statistic ws1 = aGame.winner.getStat(s1);
                        Statistic ls1 = aGame.loser.getStat(s1);
                        try {
                            double diff = ws1.getNormalizedValue() - ls1.getNormalizedValue();
                            double adder = diff * gain;// * (aGame.winnerPts - aGame.loserPts) * r;
                            model.setWeight(s1, model.getWeight(s1) + adder);

                        } catch (Exception e) {
                            //System.out.println("..."+s1);
                        }


                        for (String s2 : model.getStatNames()) {
                            Statistic ls2 = aGame.loser.getStat(s2);
                            Statistic ws2 = aGame.winner.getStat(s2);
                            if(ls2 != null) {
                                double ls2Val = ls2.getNormalizedValue();
                                double ws1Val = ws1.getNormalizedValue();
                                if(model.getWeight(s2) < 0)
                                {
                                    ls2Val = 1.0 - ls2Val;
                                }
                                if(model.getWeight(s1) <0)
                                {
                                    ws1Val = 1.0 - ws1Val;
                                }
                                double diff = ws1Val - ls2Val;
                                double adder = diff * gain; //* (aGame.winnerPts - aGame.loserPts) * r;
                                //System.out.println("SC: "+model.getStatCorrelation(s1, s2)+" Adder: "+adder);
                                model.setStatCorrelation(s1, s2, model.getStatCorrelation(s1, s2) + adder);
                                //model.setStatCorrelation(s1, s2, adder);
                            }
                        }
                    }
                }
            }
        }
    }

    public double adjustModel(StatModel model, ArrayList<String> statFilter, double gain, int startYear, int endYear)
    {
        ArrayList<Thread> threads = new ArrayList();

        for(int i=startYear; i<=endYear; i++)
        {
            final int year = i;
            Thread t = new Thread(() -> adjustModel(model, year, statFilter, gain));
            threads.add(t);
            //adjustModel(model, i, statFilter, gain);
        }
        ThreadHelper threadHelper = new ThreadHelper(endYear-startYear+1);
        threadHelper.run(threads);

        //double correct = 0;
        double total = 0;
/*        for(Game aGame : games)
        {
            Game pGame = new Game(aGame.team1, aGame.team2);
            total++;
            pGame.predictOutcome(model);
            if (aGame.winner.name.equals(pGame.winner.name) && aGame.loser.name.equals(pGame.loser.name)) {
                correct++;
            }
        }
        return correct/total;*/

        for(int i=startYear; i<=endYear; i++)
        {
            total += getSeason(i).getPredictionAccuracy(model);

        }
        return total/(endYear-startYear+1);


    }

    public double adjustModelForScore(StatModel model, ArrayList<String> statFilter, double gain, int startYear, int endYear)
    {
        ArrayList<Thread> threads = new ArrayList();


        ArrayList<Game> games = new ArrayList();
        for(int i=startYear; i<=endYear; i++)
        {
            games.addAll(getSeason(i).getBracketGames());
            final int year = i;
            Thread t = new Thread(() -> adjustModel(model, year, statFilter, gain));
            threads.add(t);
            //adjustModel(model, i, statFilter, gain);
        }
        ThreadHelper threadHelper = new ThreadHelper(endYear-startYear+1);
        threadHelper.run(threads);

        //double correct = 0;
        double total = 0;
/*        for(Game aGame : games)
        {
            Game pGame = new Game(aGame.team1, aGame.team2);
            total++;
            pGame.predictOutcome(model);
            if (aGame.winner.name.equals(pGame.winner.name) && aGame.loser.name.equals(pGame.loser.name)) {
                correct++;
            }
        }
        return correct/total;*/

        for(int i=startYear; i<=endYear; i++)
        {
            total += getSeason(i).getPredictedBracketScore(model);

        }
        return total/(endYear-startYear+1);


    }
}
