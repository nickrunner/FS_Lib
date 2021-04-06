package dev.firstseed.sports_reference.cbb;

import dev.firstseed.sports_reference.AbstractRoster;
import dev.firstseed.sports_reference.Statistic;

import java.util.ArrayList;

public class Roster extends AbstractRoster
{
    public Roster(ArrayList<Player> players)
    {
        super(players);
    }

    @Override
    public void addPlayer(Player player)
    {
        //only add players averaging >= 10 min per game and played in at least 25 games
        Statistic mpg = player.getStat("mp_per_g");
        Statistic g = player.getStat("g");

        if(g == null){
            return;
        }

        if(mpg == null )
        {
            if(g.getValue() >= 25) {
                players.add(player);
            }
        }
        else if( (mpg.getValue() >= 10) && (g.getValue() >= 25) ) {
            players.add(player);
        }
    }


}
