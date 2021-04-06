package dev.firstseed.sports_reference;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public abstract class AbstractTeam extends AbstractStatCreator
{
    public AbstractTeam(String uid, String name)
    {
        super(uid, name);
    }

    public LinkedHashSet<String> getPlayerStatNames()
    {
        return getRoster().getPlayerStatNames();
    }

    public LinkedHashSet<String> getTeamStatNames()
    {
        LinkedHashSet<String> statNames = new LinkedHashSet<>();
        for(Statistic stat : stats)
        {
            statNames.add(stat.name);
        }
        return statNames;
    }

    public void addStats(TeamStats teamStats){
        LinkedHashSet<Statistic> newStats = new LinkedHashSet<Statistic>();
        ArrayList<Statistic> s = teamStats.getStats(uid);
        if(s != null){
            newStats.addAll(s);
            //findPerGameAverages(newStats);
            this.stats.addAll(newStats);
        }
    }

    public Statistic getStat(String tag)
    {
        return (Statistic)super.getStat(tag);
    }

    public abstract void setRoster(AbstractRoster roster);
    public abstract AbstractRoster getRoster();


}
