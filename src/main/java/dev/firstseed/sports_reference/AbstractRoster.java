package dev.firstseed.sports_reference;
import dev.firstseed.sports_reference.cbb.Player;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public abstract class AbstractRoster
{
    protected ArrayList<Player> players;

    public AbstractRoster(ArrayList<Player> players)
    {
        this.setPlayers(players);
    }


    public ArrayList<Player> getPlayers() {
        return players;
    }


    public void addPlayer(Player player)
    {
        players.add(player);
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public LinkedHashSet<String> getPlayerStatNames()
    {
        LinkedHashSet<String> statNames = new LinkedHashSet<>();
        for(Player player : players)
        {
            if(player == null){
                continue;
            }
            for(Statistic stat : player.getStatistics()){
                statNames.add(stat.name);
            }
        }
        return statNames;
    }

}
