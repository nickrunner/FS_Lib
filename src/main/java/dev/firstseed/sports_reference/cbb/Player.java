package dev.firstseed.sports_reference.cbb;

import dev.firstseed.sports_reference.AbstractStatCreator;
import dev.firstseed.sports_reference.Statistic;

public class Player extends AbstractStatCreator {

    public Player(String uid, String name) {
        super(uid, name);
    }

    public Statistic getStat(String tag)
    {
        return (Statistic)super.getStat(tag);
    }
}
