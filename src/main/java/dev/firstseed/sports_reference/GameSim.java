package dev.firstseed.sports_reference;


import java.util.ArrayList;

public class GameSim {
    public Game sim;
    public GameSim(ArrayList<StatModel> models, Game game, AbstractSeason season){
        sim = game;
        int t1Count = 0;
        int t2Count = 0;
        int t1Score = 0;
        int t2Score = 0;
        int t1LineTotal = 0;
        int t2LineTotal = 0;
//        int total = 0;
//        int team1Pts = 0;
//        int team2Pts = 0;
//        int team1Line = 0;
//        int team2Line = 0;
//        int winnerPts = 0;
//        int loserPts = 0;
//        int winnerLine = 0;
//        int loserLine = 0;
//        AbstractTeam winner;
//        AbstractTeam loser;
        int total = 0;
        double confidence;
        final int size = models.size();
        for(StatModel model : models)
        {
            try
            {
                season.calculateComposites(model);
                game.predictOutcome(model);
                total++;
                t1Score += game.team1pts;
                t2Score += game.team2pts;
                t1LineTotal += game.team1Line;
                t2LineTotal += game.team2Line;
                if(game.winner.name.equals(game.team1.name))
                {
                    t1Count++;
                }
                else
                {
                    t2Count++;
                }


                final int tt = total;
            }
            catch (Exception e)
            {
                continue;
            }
        }
        sim.team1pts = (int)Math.round( (double)t1Score/(double)total);
        sim.team2pts = (int)Math.round((double)t2Score/(double)total);
        sim.team1Line = (int)Math.round((double)t1LineTotal/(double)total);
        sim.team2Line = (int)Math.round((double)t2LineTotal/(double)total);
        confidence = t1Count > t2Count ? (double)t1Count/(double)total : (double)t2Count/(double)total;
        sim.winner = t1Count > t2Count ? game.team1: game.team2;
        sim.loser = t1Count > t2Count ? game.team2 : game.team1;
        sim.winnerPts = t1Count > t2Count ? sim.team1pts : sim.team2pts;
        sim.loserPts = t1Count > t2Count ? sim.team2pts : sim.team1pts;
        sim.winnerLine = t1Count > t2Count ? sim.team1Line : sim.team2Line;
        sim.loserLine = t1Count > t2Count ? sim.team2Line : sim.team1Line;

        if(sim.loserPts >= sim.winnerPts){
            sim.loserPts = sim.winnerPts -1;
        }
    }

}
