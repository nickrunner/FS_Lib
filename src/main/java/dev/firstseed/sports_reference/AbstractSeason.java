package dev.firstseed.sports_reference;

import dev.firstseed.sports_reference.cbb.NcaaBracket;
import dev.firstseed.sports_reference.cbb.Player;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public abstract class AbstractSeason
{
    public ArrayList<? extends AbstractTeam> teams;
    private LinkedHashSet<String> teamStatNames;
    private LinkedHashSet<String> playerStatNames;
    private int year;
    private  ArrayList<AbstractStatCreator> players = new ArrayList<>();
    private NcaaBracket bracket;


    public AbstractSeason(int year, ArrayList<? extends AbstractTeam> teams)
    {
        this.year = year;
        this.teams = teams;
        this.teamStatNames = getTeamStatNames();
        this.playerStatNames = getPlayerStatNames();

        for(AbstractTeam team : teams)
        {
            if(team == null){
                continue;
            }
            players.addAll(team.getRoster().getPlayers());
        }

        for(String statName : teamStatNames){
            //Log.d("STATS", "Stat = "+statistic.name);
            double min = getTeamMin(statName);
            double max = getTeamMax(statName);

            for(AbstractTeam team : teams){
                if(team == null){
                    continue;
                }
                Statistic stat = team.getStat(statName);
                if(stat == null){
                    continue;
                }
                stat.normalize(min, max);
            }
            //Log.d("STATS", "Min:"+min+" Max:"+max);
        }

        for(String statName : playerStatNames)
        {
            double min = getPlayerMin(statName);
            double max = getPlayerMax(statName);
            for(AbstractTeam team : teams)
            {
                if(team == null){
                    continue;
                }
                for(Player player : team.getRoster().getPlayers())
                {
                    Statistic stat =  player.getStat(statName);
                    if(stat != null)
                    {
                        stat.normalize(min, max);
                    }
                }
            }
        }


        calculateComposites(new StatModel(teamStatNames));

    }

    public void setBracket(NcaaBracket bracket)
    {
        this.bracket = bracket;
    }

    public NcaaBracket getBracket()
    {
        return this.bracket;
    }

    public LinkedHashSet<String> getPlayerStatNames()
    {
        LinkedHashSet<String> statNames = new LinkedHashSet<>();
        for(AbstractTeam team : teams)
        {
            if(team == null){
                continue;
            }
            statNames.addAll(team.getPlayerStatNames());
        }
        return statNames;
    }

    public LinkedHashSet<String> getTeamStatNames()
    {
        LinkedHashSet<String> statNames = new LinkedHashSet<>();
        for(AbstractTeam team : teams)
        {
            if(team == null){
                continue;
            }
            statNames.addAll(team.getTeamStatNames());
        }
        return statNames;
    }

    public LinkedHashSet<String> getFilteredTeamStatNames(ArrayList<String> blackList)
    {
        LinkedHashSet<String> statNames = getTeamStatNames();

        for(String statName : blackList)
        {
            statNames.remove(statName);
        }

        return statNames;
    }

    public void calculateComposites(StatModel model)
    {
        teamStatNames.add("CompleteComp");
        teamStatNames.add("OffComp");
        teamStatNames.add("DefComp");
        teamStatNames.add("SosComp");
        teamStatNames.add("GenComp");

        playerStatNames.add("CompleteComp");
        playerStatNames.add("OffComp");
        playerStatNames.add("DefComp");
        playerStatNames.add("GenComp");

        for(AbstractTeam team : teams){
            if(team == null){
                continue;
            }
            team.calculateOffensiveComposite(model);
            team.calculateDefensiveComposite(model);
            team.calculateGeneralComposite(model);
            team.calculateSosComposite(model);
            for(Player player : team.getRoster().getPlayers())
            {
                if(player == null){
                    continue;
                }
                player.calculateOffensiveComposite(model);
                player.calculateDefensiveComposite(model);
                player.calculateGeneralComposite(model);
            }
        }

        double minOff = getTeamMin("OffComp");
        double maxOff = getTeamMax("OffComp");
        double minDef = getTeamMin("DefComp");
        double maxDef = getTeamMax("DefComp");
        double minGen = getTeamMin("GenComp");
        double maxGen = getTeamMax("GenComp");
        double minSos = getTeamMin("SosComp");
        double maxSos = getTeamMax("SosComp");

        double minPlayerOff = getPlayerMin("OffComp");
        double maxPlayerOff = getPlayerMax("OffComp");
        double minPlayerDef = getPlayerMin("DefComp");
        double maxPlayerDef = getPlayerMax("DefComp");
        double minPlayerGen = getPlayerMin("GenComp");
        double maxPlayerGen = getPlayerMax("GenComp");

        for(AbstractTeam team : teams){
            if(team == null){
                continue;
            }
            team.getStat("OffComp").normalize(minOff, maxOff);
            team.getStat("DefComp").normalize(minDef, maxDef);
            team.getStat("GenComp").normalize(minGen, maxGen);
            team.getStat("SosComp").normalize(minSos, maxSos);
            team.calculateCompleteComposite(model);

            for(Player player : team.getRoster().getPlayers())
            {
                if(player == null){
                    continue;
                }
                player.getStat("OffComp").normalize(minPlayerOff, maxPlayerOff);
                player.getStat("DefComp").normalize(minPlayerDef, maxPlayerDef);
                player.getStat("GenComp").normalize(minPlayerGen, maxPlayerGen);
                player.calculateCompleteComposite(model);
            }
        }

        double minComplete = getTeamMin("CompleteComp");
        double maxComplete = getTeamMax("CompleteComp");

        double minPlayerComplete = getPlayerMin("CompleteComp");
        double maxPlayerComplete = getPlayerMax("CompleteComp");


        for(AbstractTeam team : teams){
            if(team == null){
                continue;
            }

            team.getStat("CompleteComp").normalize(minComplete, maxComplete);
            for(Player player : team.getRoster().getPlayers())
            {
                player.getStat("CompleteComp").normalize(minPlayerComplete, maxPlayerComplete);
            }
        }

//        if(!teamRankings.isEmpty()){
//            teamRankings.clear();
//        }
//
//        for(String statName : teamStatNames){
//            teamRankings.add(new Ranking(statName, teams));
//        }
//
//        if(!playerRankings.isEmpty()){
//            playerRankings.clear();
//        }
//
//        for(String statName : playerStatNames)
//        {
//            playerRankings.add(new Ranking(statName, players));
//        }
    }

    public Ranking getTeamRanking(String statName)
    {
        if(teamStatNames.contains(statName)){
            return new Ranking(statName, teams);
        }
        return null;
    }

    public Ranking getPlayerRanking(String statName)
    {
        if(playerStatNames.contains(statName)){
            return new Ranking(statName, players);
        }
        else{
            return null;
        }

    }

//    public void autoSetWeight(String tag)
//    {
//        for(Ranking ranking : teamRankings){
//            double total = 0;
//            double count = 0;
//             for(Team team : teams){
//                 Statistic stat = team.getStat(ranking.tag);
//                 Statistic cmpStat = team.getStat(tag);
//                 double coorelation = 1.0 - (Math.abs(stat.normalizedValue - cmpStat.normalizedValue));
//                 if(ranking.tag.equals("SosComp")){
//                     coorelation = (Math.abs(stat.normalizedValue - cmpStat.normalizedValue));
//                 }
//                 total += coorelation;
//                 count++;
//             }
//             ranking.coorelation = total/count;
//
//        }
//        normalizeRankingCorrelations();
//        for(Ranking ranking : teamRankings){
//            setWeight(ranking.tag, ranking.normalizedCoorelation);
//        }
//        calculateComposites();
//    }
//
//    public void normalizeRankingCorrelations()
//    {
//        double min = 1;
//        double max = 0;
//        for(Ranking ranking : teamRankings){
//            if(ranking.coorelation < min){
//                min = ranking.coorelation;
//            }
//            if(ranking.coorelation > max){
//                max = ranking.coorelation;
//            }
//        }
//
//        for(Ranking ranking : teamRankings){
//            ranking.normalizedCoorelation = Statistic.interpolate(ranking.coorelation, min, max, 0.1, 1.0);
//        }
//    }

    public ArrayList<String> getTeamNames(){
        ArrayList<String> teamNames = new ArrayList<String>();
        for(AbstractTeam team : teams){
            teamNames.add(team.name);
        }
        return teamNames;
    }

    public ArrayList<String> getTeamsInTournament(){
        ArrayList<String> teamNames = new ArrayList<String>();
        for(AbstractTeam team : teams){
            if(team.isInTournament) {
                teamNames.add(team.name);
            }
        }
        return teamNames;
    }

//    public Ranking getTeamRanking(String tag){
//        for(Ranking ranking : teamRankings){
//            if(ranking.tag.equals(tag)){
//                return ranking;
//            }
//        }
//        return null;
//    }
//
//    public Ranking getPlayerRanking(String tag)
//    {
//        for(Ranking ranking : playerRankings){
//            if(ranking.tag.equals(tag)){
//                return ranking;
//            }
//        }
//        return null;
//    }

/*

    public void applyModel(HashMap<String, Double> model, ArrayList<String> statFilter)
    {
        for(String statName : getFilteredTeamStatNames(statFilter))
        {
            try
            {
                setWeight(statName, model.get(statName));
            }
           catch (Exception e)
           {
               System.out.println("Could not set weight of stat: "+statName);
           }
        }
    }
*/

/*    public void setWeight(String statName, double weight){
        if(teamStatNames.contains(statName))
        {
            for(AbstractTeam team : teams){
                if(team == null){
                    continue;
                }
                Statistic stat = team.getStat(statName);
                if(stat == null){
                    continue;
                }
                stat.setWeight(weight);
            }
        }
        else if(playerStatNames.contains(statName))
        {
            for(AbstractTeam team : teams)
            {
                if(team == null){
                    continue;
                }
                for(Player player : team.getRoster().getPlayers())
                {
                    Statistic stat = player.getStat(statName);
                    if(stat == null){
                        continue;
                    }
                    stat.setWeight(weight);
                }
            }
        }

    }*/

    public double getTeamMin(String tag){
        double min = 100000000;
        for(AbstractTeam team : teams){
            if(team == null){
                continue;
            }
            Statistic stat = team.getStat(tag);
            if(stat == null ){
                continue;
            }
            if(stat.getValue() < min){
                min = stat.getValue();
            }
        }
        return min;
    }

    public double getTeamMax(String tag){
        double max = -1000000;
        for(AbstractTeam team : teams){
            if(team == null){
                continue;
            }
            Statistic stat = team.getStat(tag);
            if(stat == null ){
                continue;
            }
            if(stat.getValue() > max){
                max = stat.getValue();
            }
        }
        return max;
    }

    public double getPlayerMin(String tag)
    {
        double min = 1000000000;
        AbstractRoster roster;
        for(AbstractTeam team : teams){
            if(team == null){
                continue;
            }
            roster = team.getRoster();
            if(roster == null){
                continue;
            }

            for(Player player : roster.getPlayers())
            {
                if(player == null){
                    continue;
                }
                Statistic stat = player.getStat(tag);
                if(stat != null) {
                    if (stat.getValue() < min) {
                        min = stat.getValue();
                    }
                }
            }
        }
        return min;
    }

    public double getPlayerMax(String tag)
    {
        double max = -1000000000;
        AbstractRoster roster;
        for(AbstractTeam team : teams){
            if(team == null){
                continue;
            }
            roster = team.getRoster();
            if(roster == null){
                continue;
            }
            for(Player player : roster.getPlayers())
            {
                if(player == null){
                    continue;
                }
                Statistic stat = player.getStat(tag);
                if(stat != null){
                    if(stat.getValue() > max){
                        max = player.getStat(tag).getValue();
                    }
                }
            }
        }
        return max;
    }



    public int getYear()
    {
        return this.year;
    }


    public AbstractTeam getTeam(String name){
        for(AbstractTeam team : teams){
            if(team.name.equals(name)){
                return team;
            }
        }
        return null;
    }

    public AbstractTeam getTeamFromUid(String uid)
    {
        for(AbstractTeam team : teams)
        {
            if(team == null){
                continue;
            }
            if(team.getUid().equals(uid))
            {
                return team;
            }
        }
        return null;
    }

    public Player getPlayerFromUid(String uid)
    {
        for(AbstractTeam team : teams)
        {
            if(team == null){
                continue;
            }
            for(Player player : team.getRoster().getPlayers())
            {
                if(player.getUid().equals(uid)){
                    return player;
                }
            }
        }
        return null;
    }

    public NcaaBracket getPredictedBracket(StatModel model) {
        NcaaBracket predictedBracket = new NcaaBracket(getBracket());
        predictedBracket.resolve(model);
        return predictedBracket;
    }

    public int getPredictedBracketScore(StatModel model)
    {
        int score = 0;

        NcaaBracket predictedBracket =  getPredictedBracket(model);
        for (int r = 0; r < getBracket().getNumberOfRounds(); r++) {
            for (int g = 0; g< getBracket().getRound(r).size(); g++) {

                Game aGame = getBracket().getRound(r).get(g);
                Game pGame = predictedBracket.getRound(r).get(g);
                if (aGame.winner.name.equals(pGame.winner.name)) {
                    switch (r) {
                        case 0:
                            score += 10;
                            break;
                        case 1:
                            score += 20;
                            break;
                        case 2:
                            score += 40;
                            break;
                        case 3:
                            score += 80;
                            break;
                        case 4:
                            score += 160;
                            break;
                        case 5:
                            score += 320;
                            break;

                    }
                }
            }
        }

        return score;
    }

    public double getPredictionAccuracy(StatModel model)
    {
        double correct = 0;
        double total = 0;
        NcaaBracket predictedBracket =  getPredictedBracket(model);
        for (int r = 0; r < getBracket().getNumberOfRounds(); r++) {
            for (int g = 0; g< getBracket().getRound(r).size(); g++) {
                ArrayList<Game> aGames = getBracket().getRound(r);
                Game actualGame = aGames.get(g);
                ArrayList<Game> pGames = predictedBracket.getRound(r);
                total++;

                Game predictedGame = pGames.get(g);
                if (actualGame.winner.name.equals(predictedGame.winner.name))
                {
                    correct++;
                }


            }
        }
        return correct/total;
    }

    public ArrayList<Game> getBracketGames()
    {
        ArrayList<Game> games = new ArrayList();
        for (int r = 0; r < getBracket().getNumberOfRounds(); r++) {
            games.addAll(getBracket().getRound(r));
        }
        return games;
    }



}
