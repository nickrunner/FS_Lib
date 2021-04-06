package dev.firstseed.sports_reference.cbb;

import dev.firstseed.sports_reference.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;


public class ReferenceDataProvider
{
    private String baseUrl = "https://www.sports-reference.com/";
    private File cacheDir;
    private OnReferenceDataReadyListener delegate;


    private HashMap<String, Element> memCache;

    private int total;
    private int remaining;

    private final int AVG_ROSTER_SIZE = 15;

    public ReferenceDataProvider(final OnReferenceDataReadyListener delegate, File cacheDir)
    {
        this.cacheDir = cacheDir;
        this.delegate = delegate;
        this.memCache = new HashMap<String, Element>();
    }


    public Element getTable(String url, String tableId) throws Exception
    {
        Document doc;
        Connection jsoupConnection;
        File diskCacheFile;
        File diskCacheDir;

        delegate.statusUpdate(total, remaining--);
        Element retval;
        String path = url.replace(baseUrl, "");
        String fileAbsPath = cacheDir.getAbsolutePath()+"/"+path;

        if(memCache.containsKey(fileAbsPath))
        {
            retval =  memCache.get(fileAbsPath);
        }
        else
        {
            //Log.d("RDP", "Searching cache for "+fileAbsPath);
            diskCacheFile = new File(fileAbsPath);

            if(diskCacheFile.exists())
            {
                try{
                    //System.out.println("RDP Found cached html! "+fileAbsPath);
                    doc = Jsoup.parse(diskCacheFile, "UTF-8");
                    //Log.d("RDP", "HTML: "+d.outerHtml());
                    retval = doc.getElementById(tableId);
                }
                catch (Exception e)
                {
                    retval = null;
                    e.printStackTrace();
                }
                doc = null;
                diskCacheFile = null;
            }
            else{
                //System.out.println("RDP: Retrieving from network "+url);
                jsoupConnection = Jsoup.connect(url);
                try{
                    doc = jsoupConnection.get();
                }
                catch (Exception e){
                    e.printStackTrace();
                    jsoupConnection = null;
                    doc = null;
                    return null;
                }

                jsoupConnection = null;
                retval = doc.getElementById(tableId);
                doc = null;
                String dir = fileAbsPath.substring(0,fileAbsPath.lastIndexOf("/"));
                diskCacheDir = new File(dir);
                diskCacheDir.mkdirs();
                diskCacheDir = null;
                if( diskCacheFile.createNewFile())
                {
                    FileWriter writer = new FileWriter(diskCacheFile);
                    writer.write(retval.outerHtml());
                    writer.close();
                }
            }
        }

//        if(retval != null)
//        {
//            memCache.put(fileAbsPath, retval);
//        }
        if(retval == null){
            System.out.println("\nFailed getting table "+tableId+" from url "+url);
        }

        return retval;
    }

    public void getBracket(AbstractSeason season)
    {
        String url = baseUrl+"/cbb/postseason/"+season.getYear()+"-ncaa.html";
        NcaaBracket bracket = new NcaaBracket();
        boolean nationalFlag = false;
        try{
            Element b = getTable(url, "brackets");
            Elements regions = b.children();
            for(Element region : regions)
            {
                int regionIndex = regions.indexOf(region);
                //System.out.println("\n\nREGION ********\n"+region.outerHtml()+"\n*********** REGION\n\n");

                Element teamBr;
                if(regionIndex < 4){
                    teamBr = region.getElementsByClass("team16").get(0);
                    nationalFlag = false;
                }
                else{
                    teamBr = region.getElementsByClass("team4").get(0);
                    nationalFlag = true;
                }

                Elements rounds = teamBr.getElementsByClass("round");
                for(Element round : rounds)
                {
                    //System.out.println("\n\nROUND ********\n"+round.outerHtml()+"\n*********** ROUND\n\n");
                    int roundIndex = rounds.indexOf(round);
                    for(Element g : round.children())
                    {

                        if(g.children().size() > 1)
                        {
                            Element t1 = g.children().get(0);
                            Element t2 = g.children().get(1);
                            try{
                                int seed1 = Integer.parseInt(t1.getElementsByTag("span").get(0).text());
                                AbstractTeam team1 = season.getTeamFromUid(t1.getElementsByTag("a").get(0).attr("href"));
                                int team1pts = Integer.parseInt(t1.getElementsByTag("a").get(1).text());

                                int seed2 = Integer.parseInt(t2.getElementsByTag("span").get(0).text());
                                AbstractTeam team2 = season.getTeamFromUid(t2.getElementsByTag("a").get(0).attr("href"));
                                int team2pts = Integer.parseInt(t2.getElementsByTag("a").get(1).text());

                                //System.out.println("RBD: "+seed1+". "+team1.name+" "+team1pts+" "+seed2+". "+team2.name+" "+team2pts);
                                Game game = new Game(team1, team2, team1pts, team2pts);
                                if(nationalFlag){
                                    bracket.addNationalGame(game, roundIndex+4);
                                }
                                else{
                                    bracket.addRegionalGame(game, roundIndex, regionIndex+1, seed1, seed2);
                                }
                            }
                            catch (Exception e)
                            {

                                try
                                {
                                    int seed1 = Integer.parseInt(t1.getElementsByTag("span").get(0).text());
                                    AbstractTeam team1 = season.getTeamFromUid(t1.getElementsByTag("a").get(0).attr("href"));
                                    int seed2;
                                    try{
                                        seed2 = Integer.parseInt(t2.getElementsByTag("span").get(0).text());
                                    }
                                    catch (Exception e3)
                                    {
                                        if(seed1 == 1)
                                        {
                                            seed2 = 16;
                                        }
                                        else{
                                            seed2 = 11;
                                        }
                                    }

                                    AbstractTeam team2 = null;
                                    try{
                                        team2 = season.getTeamFromUid(t2.getElementsByTag("a").get(0).attr("href"));
                                    }
                                    catch (Exception e1)
                                    {
                                        System.out.println("Failed parsing tournament team");
                                    }

                                    Game game = new Game(team1, team2);
                                    switch(regionIndex)
                                    {
                                        case 0:
                                            bracket.addRegionalGame(game, 0, 4, seed1, seed2);
                                            break;
                                        case 1:
                                            bracket.addRegionalGame(game, 0, 3, seed1, seed2);
                                            break;
                                        case 2:
                                            bracket.addRegionalGame(game, 0, 2, seed1, seed2);
                                            break;
                                        case 3:
                                            bracket.addRegionalGame(game, 0, 1, seed1, seed2);
                                            break;
                                    }

                                   // System.out.print("Added game to region "+(regionIndex)+" Team1 = "+team1.name+" "+seed1);
                                    /*if(team2 != null)
                                    {
                                        System.out.println(" Team2 = "+team2.name+" "+seed2);
                                    }
                                    else
                                    {
                                        System.out.println(" Team2 = null");
                                    }*/
                                }
                                catch (Exception e2)
                                {
                                    //System.out.println("Could not parse tournament game:\n"+g.outerHtml());
                                }

                            }

                        }
                    }
                }
            }
            System.out.println("Completed getting bracket from year: "+season.getYear());
            season.setBracket(bracket);
        }
        catch (Exception e){
            System.out.println("\nRDP: Could not get bracket from url "+url);
            e.printStackTrace();
        }

    }

    public void getRoster(Team team)
    {
        LinkedHashSet<Statistic> perGame = new LinkedHashSet<Statistic>();
        Roster roster = new Roster(new ArrayList<Player>());
        String url = baseUrl+team.getUid();

        Element table;
        try{
            table = getTable(url, "per_game");
        }
        catch (Exception e){
            e.printStackTrace();
            return;
        }
        if(table == null){
            System.out.println("RDP: Failed getting table for url "+url);
            return;
        }
        Element tableBody;
        try{
            tableBody = table.getElementsByTag("tbody").get(0);
        }
        catch (Exception e){
            e.printStackTrace();
            return;
        }

        for(Element row : tableBody.getElementsByTag("tr"))
        {

            Elements cols = row.getElementsByTag("td");
            String playerName = cols.get(0).text();
            String uid = cols.get(0).getElementsByTag("a").get(0).attr("href");
            Player player = new Player(uid, playerName);

            for(Element col : cols)
            {
                if(cols.indexOf(col) < 1){
                    continue;
                }
                try {
                    if(col.text().equals("")){
                        continue;
                    }
                    double val = Double.parseDouble(col.text());
                    String name = col.attr("data-stat");
                    //Log.d("RDB", "Adding stat "+name+" = "+val);
                    perGame.add(new Statistic(name, val));
                }
                catch (Exception e){
                    System.out.println("RDB"+" Failed parsing statistic: "+col.text()+" "+e);
                }
            }
            player.addStats(perGame);
            roster.addPlayer(player);


        }

        team.setRoster(roster);
        System.gc();

    }

    public void getSeason(final LeagueType leagueType, final int year)
    {
        if(year == 2020)
        {
            return;
        }

        ArrayList<Team> teams = new ArrayList<Team>();
        TeamStats basicTeamStats = getTeamStats(leagueType, year, TeamStatType.TEAM_STATS_BASIC);

        if(basicTeamStats == null)
        {
            System.out.println("RDP Skipping: Could not get basic statistics for year "+year);
            delegate.onSeasonReady(year, null);
            return;
        }
        ArrayList<String> uids = basicTeamStats.getUids();
        if(uids == null){
            System.out.println("RDP: Skipping year: No uids for year "+year);
            delegate.onSeasonReady(year, null);
            return;
        }
        if(basicTeamStats.getUids().isEmpty()){
            System.out.println("RDP: Skipping year "+year);
            delegate.onSeasonReady(year, null);
            return;
        }

        total = 4 + basicTeamStats.getUids().size();
        remaining = total -1;

        TeamStats basicOpp = getTeamStats(leagueType, year, TeamStatType.TEAM_STATS_BASIC_OPPONENT);

        TeamStats adv = getTeamStats(leagueType, year, TeamStatType.TEAM_STATS_ADVANCED);

        TeamStats advOpp = getTeamStats(leagueType, year, TeamStatType.TEAM_STATS_ADVANCED_OPPONENT);


        int i =0;
        for(String uid : basicTeamStats.getUids())
        {
            Team team = new Team(uid, basicTeamStats.getNames().get(i));

            team.addStats(basicTeamStats);
            team.addStats(basicOpp);
            team.addStats(adv);
            team.addStats(advOpp);
            team.setYear(year);
            team.findPerGameAverages(team.getStatistics());

            //Uncomment to get player statistics... runs a lot slower
            //getRoster(team);

            teams.add(team);
            i++;
        }
        total = 0;
        remaining = 0;
        Season season = new Season(year, teams);
        try
        {
            getBracket(season);
        }
        catch (Exception e)
        {
            System.out.println("Failed getting bracket for year: "+season.getYear());
            e.printStackTrace();
        }
        delegate.onSeasonReady(year, season);
    }

    private String getLeagueRoute(LeagueType leagueType)
    {
        switch(leagueType)
        {
            case CBB:
                return "cbb/";
            case CFB:
                return "cfb/";
        }
        return  "cbb/";
    }

    public void setDelegate(OnReferenceDataReadyListener delegate)
    {
        this.delegate = delegate;
    }


    public TeamStats getTeamStats(LeagueType leagueType, int year, TeamStatType type)
    {

        String tmp = "";
        String suffix = "";
        String route = "";

        switch(type)
        {
            case TEAM_STATS_BASIC:
                tmp = "basic_school_stats";
                suffix = "-school-stats";
                route = "seasons/";
                break;
            case TEAM_STATS_BASIC_OPPONENT:
                tmp = "basic_opp_stats";
                suffix = "-opponent-stats";
                route = "seasons/";
                break;
            case TEAM_STATS_ADVANCED:
                tmp = "adv_school_stats";
                suffix = "-advanced-school-stats";
                route = "seasons/";
                break;
            case TEAM_STATS_ADVANCED_OPPONENT:
                tmp = "adv_opp_stats";
                suffix = "-advanced-opponent-stats";
                route = "seasons/";
                break;
        }

        final String url = baseUrl+getLeagueRoute(leagueType)+route+year+suffix+".html";
        final String tableId = tmp;
        try {
            return new TeamStats(getTable(url, tableId));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
