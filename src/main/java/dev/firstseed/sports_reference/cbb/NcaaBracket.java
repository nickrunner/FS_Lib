package dev.firstseed.sports_reference.cbb;

import dev.firstseed.sports_reference.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NcaaBracket extends Bracket
{
    public class FirstFourGame{
        int region;
        int seed;
        Game game;
        public FirstFourGame(int region, int seed, Game game){
            this.region = region;
            this.seed = seed;
            this.game = game;
        }
    }

    private HashMap<Integer, FirstFourGame> firstFourGameMap = new HashMap<Integer, FirstFourGame>();
    public NcaaBracket()
    {
        super(64);
    }

    public NcaaBracket(NcaaBracket bracket)
    {
        super(bracket);
        this.firstFourGameMap = (HashMap<Integer, FirstFourGame>) bracket.firstFourGameMap.clone();
    }

    public void addTeam(Team team, int region, int seed)
    {
        if(region > 1 && region < 4)
        {
            super.addTeam(team, region*seed);
        }
    }

    public void addFirstFourGame(int region, int seed, Game game){
        System.out.println("Adding First Four game: "+region+" seed: "+seed+" game: "+game.team1.name+" vs. "+game.team2.name);
        firstFourGameMap.put(region, new FirstFourGame(region, seed, game));
    }

    @Override
    public void resolve(StatModel model) {
        ArrayList<Game> round = getRound(0);
        for(int region : firstFourGameMap.keySet()){
            int seed = getOverallSeed(region+1, firstFourGameMap.get(region).seed);
            Game game = firstFourGameMap.get(region).game;
            System.out.println("Game: "+game.team1.name+" vs. "+game.team2.name);
            game.predictOutcome(model);
            AbstractTeam team = game.winner;
            System.out.println("Handling first four with overall seed: "+seed+" in region "+region+" seed: "+firstFourGameMap.get(region).seed+" Game: "+game);
            addTeam(team, seed);
            for(Game rGame : round){
                if(rGame.team2 == null){
                    rGame.team2 = team;
                    break;
                }
            }
        }
        super.resolve(model);
    }

    @Override
    public void resolve(ArrayList<StatModel> models, AbstractSeason season) {
        ArrayList<Game> round = getRound(0);
        for(int region : firstFourGameMap.keySet()){
            int seed = getOverallSeed(region+1, firstFourGameMap.get(region).seed);
            Game game = new GameSim(models,firstFourGameMap.get(region).game, season).sim;
            AbstractTeam team = game.winner;
            System.out.println("Handling first four with overall seed: "+seed+" in region "+region+" seed: "+firstFourGameMap.get(region).seed+" Game: "+game);
            addTeam(team, seed);
            for(Game rGame : round){
                if(rGame.team2 == null){
                    rGame.team2 = team;
                    break;
                }
            }
        }
        super.resolve(models, season);
    }

    private int getOverallSeed(int region, int seed)
    {
        boolean invertAdder = seed % 2 == 0;

        int adder = region;
        if(invertAdder)
        {
            adder = 5 - region;
        }

        return (4*(seed-1)) + adder;
    }

    public void addRegionalGame(Game game, int round, int region, int seed1, int seed2)
    {
        super.addGame(game, round, getOverallSeed(region, seed1), getOverallSeed(region, seed2));
    }

    public void addNationalGame(Game game, int round)
    {
        super.addGame(game, round);
    }

    public ArrayList<Game> getRound1()
    {
        return super.getRound(0);
    }

    public ArrayList<Game> getRound2()
    {
        return super.getRound(1);
    }

    public ArrayList<Game> getSweetSixteen()
    {
        return super.getRound(2);
    }

    public ArrayList<Game> getEliteEight()
    {
        return super.getRound(3);
    }

    public ArrayList<Game> getFinalFour()
    {
        return super.getRound(4);
    }

    public Game getChampionship()
    {
        return super.getRound(5).get(0);
    }

    public Team getChampion()
    {
        return (Team)getChampionship().winner;
    }

    @Override
    public int getSeed(AbstractTeam team)
    {
        int s =  super.getSeed(team);
        if(s%4 == 0){
            return s/4;
        }
        return (s/4) +1;
    }

    public void print()
    {

        System.out.println("***** First Round *****");
        for(Game game : getRound1())
        {
            System.out.println(game.toString());
        }

        System.out.println("\n\n***** Second Round *****");
        for(Game game : getRound2())
        {
            System.out.println(game.toString());
        }

        System.out.println("\n\n***** Sweet Sixteen *****");
        for(Game game : getSweetSixteen())
        {
            System.out.println(game.toString());
        }

        System.out.println("\n\n***** Elite Eight *****");
        for(Game game : getEliteEight())
        {
            System.out.println(game.toString());
        }

        System.out.println("\n\n***** Final Four *****");
        for(Game game : getFinalFour())
        {
            System.out.println(game.toString());
        }

        System.out.println("\n\n***** National Championship *****");
        System.out.println(getChampionship().toString());

        System.out.println("\n\n***** Champion *****");
        System.out.println(getChampionship().winner.name);
    }

}
