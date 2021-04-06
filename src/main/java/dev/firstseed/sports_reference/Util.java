package dev.firstseed.sports_reference;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class Util
{
    public static double interpolate(double val, double min1, double max1, double min2, double max2)
    {

        double  range1 = (max1 - min1);
        double  range2 = (max2 - min2);
        double  range3 = (val - min1);
        double retval;

        if(range2 == 0)
        {
            return min2;
        }
        else if(range3 == 0)
        {
            return min2;
        }
        else if(range1 == 0)
        {
            return min2;
        }
        else
        {
            retval = ( ( (range3 * range2) / range1 ) )+ min2;
            return retval;
        }
    }

    public static String toJsonString(Map<String, Double> map)
    {
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    public static JsonObject toJson(HashMap<String, Double> map)
    {
        return new Gson().fromJson(toJsonString(map), JsonObject.class);
    }
}
