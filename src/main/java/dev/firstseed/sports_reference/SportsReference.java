package dev.firstseed.sports_reference;

import dev.firstseed.sports_reference.cbb.CBB;

import java.io.File;

public class SportsReference
{
    private static CBB cbb;


    public static CBB cbb(File cacheDir)
    {
        if(cbb == null){
            cbb = new CBB(cacheDir);
        }
        return cbb;
    }

    public static CBB cbb()
    {
        return cbb;
    }


}
