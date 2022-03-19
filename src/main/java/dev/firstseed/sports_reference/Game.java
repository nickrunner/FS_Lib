package dev.firstseed.sports_reference;


import java.text.DecimalFormat;
import java.util.ArrayList;

public class Game
{
    public double confidence;
    public AbstractTeam winner;
    public AbstractTeam loser;
    public int winnerPts;
    public int loserPts;

    public AbstractTeam team1;
    public AbstractTeam team2;

    public int team1pts;
    public int team2pts;

    public int team1Line;
    public int team2Line;

    public int winnerLine;
    public int loserLine;
    public double winnerDecimalOdds;
    public double loserDecimalOdds;
    public double winnerProbability;
    public double loserProbability;

    public Game(AbstractTeam team1, AbstractTeam team2, int team1pts, int team2pts )
    {
        this.team1 = team1;
        this.team2 = team2;
        try
        {
            if(team1pts > team2pts){
                this.winner = team1;
                this.loser = team2;
                this.winnerPts = team1pts;
                this.loserPts = team2pts;
            }
            else
            {
                this.winner = team2;
                this.loser = team1;
                this.winnerPts = team2pts;
                this.loserPts = team1pts;
            }
            this.team2pts = team2pts;
            this.team1pts = team1pts;
        }
        catch (Exception e)
        {
            System.out.println("Error construction game... "+e.toString());
            e.printStackTrace();
        }
    }

    public String toString()
    {

        if(this.winner == null)
        {
            return this.winnerPts+"\t"+"N/A"+"\n"+this.loserPts+"\t"+this.loser.name+"\n";
        }
        if(this.loser == null)
        {
            return this.winnerPts+"\t"+this.winner.name+"\n"+this.loserPts+"\t"+"N/A"+"\n";
        }

        return this.winnerPts+
                "\t"+this.winner.name+
                "\t-"+this.winnerLine+
                "\t"+Math.round(this.winnerProbability*100)+"%"+
                "\n"+this.loserPts+
                "\t"+this.loser.name+
                "\t +"+this.loserLine+
                "\t"+Math.round(this.loserProbability*100)+"%"+
                "\n";

    }

    public void predictOutcome(StatModel model)
    {
        if(team1 == null){
            winner = team2;
            loser = team1;
            confidence = 100;
            winnerPts = 0;
            loserPts = 0;
            team1pts = loserPts;
            team2pts = winnerPts;
            return;
        }
        else if(team2 == null){
            winner = team1;
            loser = team2;
            confidence = 100;
            winnerPts = 0;
            loserPts = 0;
            team1pts = winnerPts;
            team2pts = loserPts;
            return;
        }

        double s1total = 0;
        double s2total = 0;
        double weightTotal = 0;
        for(String s1 : model.getStatNames())
        {

            try{
                double delta = team1.getStat(s1).getNormalizedValue() - team2.getStat(s1).getNormalizedValue();
                s1total += delta * model.getWeight(s1);
                weightTotal += Math.abs(model.getWeight(s1));
                int depth =model.calculateDepth(1);
                //System.out.println("Model depth: "+depth);
                for(int i=1; i< depth; i++)
                {
                    for(String s2 : model.getStatNames())
                    {

                        try {
                            double s11 = team1.getStat(s1).getNormalizedValue();
                            double s12 = team1.getStat(s2).getNormalizedValue();
                            double s21 = team2.getStat(s1).getNormalizedValue();
                            double s22 = team2.getStat(s2).getNormalizedValue();

                            if(model.getWeight(s1) < 0)
                            {
                                s11 = 1.0 - s11;
                                s21 = 1.0 - s21;
                            }

                            if(model.getWeight(s2) < 0)
                            {
                                s12 = 1.0 - s12;
                                s22 = 1.0 - s22;
                            }

                            double delta1 = s11 - s22;
                            double delta2 = s21 - s12;
                            ArrayList<String> s1List = new ArrayList<>();
                            s1List.add(s1);
                            double sc = model.getCorrelation(s1List,s2);
                            double x = (delta1*sc) - ((delta2)*(sc));
                            s2total += x;
                            weightTotal += model.getCorrelation(s1List,s2);
                        }
                        catch (Exception e) {}
                    }
                }

            }
            catch (Exception e) {}
        }
        double total = (s1total ) + (s2total);
        confidence = (Math.abs(total) / Math.abs(weightTotal))*100 ;
        //double x = 50;
        winnerProbability = ((100 + confidence) / 200);
        loserProbability = ((100 - confidence) / 200);
        winnerLine = (int)Math.round((100 * winnerProbability) / (1-winnerProbability));
        loserLine = winnerLine;
        if(total > 0)
        {
            winner = team1;
            loser = team2;
            team1Line = winnerLine;
            team2Line = loserLine;
        }
        else{
            winner = team2;
            loser = team1;
            team2Line = winnerLine;
            team1Line = loserLine;
        }

        try {
            double wPpg = Math.abs(winner.getStat("pts_PG").getValue());
            double wPpgW = Math.abs(model.getWeight("pts_PG"));
            double wOppPpg = Math.abs(winner.getStat("opp_pts_PG").getValue());
            double wOppPpgW = Math.abs(model.getWeight("opp_pts_PG"));
            double lPpg = Math.abs(loser.getStat("pts_PG").getValue());
            double lPpgW = Math.abs(model.getWeight("pts_PG"));
            double lOppPpg = Math.abs(loser.getStat("opp_pts_PG").getValue());
            double lOppPpgW = Math.abs(model.getWeight("opp_pts_PG"));

            winnerPts = (int)Math.round(((wPpg*wPpgW) + (lOppPpg*lOppPpgW)) / (wPpgW + lOppPpgW));
            loserPts = (int)Math.round(((lPpg*lPpgW) + (wOppPpg*wOppPpgW)) / (lPpgW + wOppPpgW));

            double diff = winnerPts - loserPts;
            winnerPts += (int)confidence;
            loserPts -= (int)confidence;

            if(loserPts >= winnerPts){
                loserPts = winnerPts - 1;
            }

            if(winner.name.equals(team1.name)){
                team1pts = winnerPts;
                team2pts = loserPts;
            }
            else{
                team2pts = winnerPts;
                team1pts = loserPts;
            }
        }
        catch (Exception e){}
    }

    public Game(AbstractTeam team1, AbstractTeam team2){
        this.team1 = team1;
        this.team2 = team2;
    }
}
