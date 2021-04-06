package dev.firstseed.sports_reference.cbb;

import dev.firstseed.sports_reference.AbstractRoster;
import dev.firstseed.sports_reference.AbstractTeam;
import dev.firstseed.sports_reference.Statistic;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class Team extends AbstractTeam {
    private Roster roster;

    public Team(String uid, String name)
    {
        super(uid, name);
        if(this.name.contains("NCAA")){
            isInTournament = true;
            this.name = this.name.replaceAll(" NCAA", "");
        }
        else if(this.name.contains("*")){
            isInTournament = true;
            this.name = this.name.replaceAll(" *", "");
        }
    }


    public void setRoster(AbstractRoster roster){
        this.roster = (Roster)roster;
        for(Player player : roster.getPlayers())
        {
            player.isInTournament = isInTournament;
        }
    }

    public AbstractRoster getRoster()
    {
        if(this.roster == null){
            this.roster = new Roster(new ArrayList<>());
        }
        return this.roster;
    }


    public void findPerGameAverages(LinkedHashSet<Statistic> statistics){
        //Find Per Game averages
        double nGames = getStat("g").getValue();
        for(Statistic stat : statistics){
            if(!stat.name.contains("pct") &&
                    !stat.name.startsWith("g") &&
                    !stat.name.startsWith("pace")&&
                    !stat.name.startsWith("off_Rtg")&&
                    !stat.name.startsWith("srs") &&
                    !stat.name.startsWith("sos") &&
                    !stat.name.contains("rate")
            )
            {
                stat.setValue(stat.getValue()/nGames);
                stat.name = stat.name.concat("_PG");
            }
        }
    }





}
