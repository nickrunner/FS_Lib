package dev.firstseed.sports_reference;


import dev.firstseed.sports_reference.cbb.Team;

import java.util.ArrayList;

public class Bracket
{
    private ArrayList<Game>[] rounds;
    private AbstractTeam[] teams;

    private int nSeeds;
    private int roundIndex;

    public Bracket(int nSeeds){
        this.nSeeds = nSeeds;
        teams = new Team[nSeeds];
        int tmp = nSeeds;
        int nRounds = 1;
        while( (tmp = tmp/2) > 1){
            nRounds++;
        }
        //System.out.println("\nCreating bracket with "+nSeeds+" seeds and "+nRounds+" rounds.");
        rounds = new ArrayList[nRounds];
        for(int i=0; i< rounds.length; i++){
            rounds[i] = new ArrayList<>();
        }
        roundIndex = 0;
    }

    public Bracket(Bracket bracket)
    {
        this.rounds = bracket.rounds.clone();
        this.teams = bracket.teams.clone();
        this.nSeeds = bracket.nSeeds;
        this.roundIndex = bracket.roundIndex;
    }

    public void addTeam(AbstractTeam team, int seed)
    {
        try{
            teams[seed-1] = team;
        }
        catch (Exception e){
            System.out.println("Bracket Error: Tried to add invalid seed "+seed+" nSeeds = "+nSeeds);
        }
    }

    public int getNumberOfRounds()
    {
        return rounds.length;
    }

    public int getSeed(AbstractTeam team)
    {
        int i=0;
        if(team == null)
        {
            return 0;
        }
        for(AbstractTeam t : teams){
            if(t != null)
            {
                if (t.equals(team)){
                    return i+1;
                }
            }

            i++;
        }
        return 0;
    }

//    public void addRound()
//    {
//        rounds.add(new ArrayList<Game>());
//    }

    public void addGame(Game game, int round, int seed1, int seed2)
    {
        if(game.team1 == null && game.team2 == null)
        {
            System.out.println("Error: tried to add game where both teams were null");
            return;
        }
/*        if(game.team1 == null)
        {
            System.out.println("Adding game: "+seed1+"."+"N/A"+" vs. "+seed2+"."+game.team2.name+"");
        }
        else if(game.team2 == null)
        {
            System.out.println("Adding game: "+seed1+"."+game.team1.name+" vs. "+seed2+"."+"N/A");
        }

        else{
            System.out.println("Adding game: "+seed1+"."+game.team1.name+" vs. "+seed2+"."+game.team2.name+"");
        }*/

        addTeam(game.team1, seed1);
        addTeam(game.team2, seed2);
        addGame(game, round);
    }

    public void addGame(Game game, int round)
    {
        //System.out.println("Adding Game: Round: "+round+"\n"+game.toString());
        rounds[round].add(game);
    }

    public ArrayList<Game> getRound(int round)
    {
        return rounds[round];
    }

    public ArrayList<AbstractTeam> getRoundWinners(int round)
    {
        ArrayList<AbstractTeam> roundWinners = new ArrayList<>();
        for(Game game : getRound(round))
        {
            roundWinners.add(game.winner);
        }

        return roundWinners;
    }

    public ArrayList<AbstractTeam> getTeams()
    {
        ArrayList<AbstractTeam> teams = new ArrayList<>();
        for(Game game : getRound(0))
        {
            teams.add(game.team1);
            teams.add(game.team2);
        }
        return teams;
    }



    public void printTeams()
    {
        int i= 0;
        for(AbstractTeam team : teams)
        {
            if(team != null)
            {
                System.out.println((i+1)+". "+team.name);
            }
            else{
                System.out.println((i+1)+". "+"Null");
            }
            i++;
        }
    }

    public void resolve(StatModel model)
    {
        roundIndex = 0;
        resolve(rounds[0], model);

    }

    private void resolve(ArrayList<Game> games, StatModel model){
        //System.out.println("Resolving round: "+roundIndex+" with "+games.size()+" games");
        if(games.size() == 1){
            games.get(0).predictOutcome(model);
            rounds[roundIndex++] = games;
            roundIndex = 0;
            return;
        }

        ArrayList<Game> nextRound = new ArrayList<>();
        for(int i=0; i<games.size(); i+=2){

            Game game1 = games.get(i);

            Game game2 = games.get(i+1);

            game1.predictOutcome(model);
            game2.predictOutcome(model);
            //System.out.println("Round: "+roundIndex+"\n"+game1);
            //System.out.println("Round: "+roundIndex+"\n"+game2);
            nextRound.add(new Game(game1.winner, game2.winner));
        }
        rounds[roundIndex++] = games;

        resolve(nextRound, model);

    }
}
