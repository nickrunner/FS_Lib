package dev.firstseed.sports_reference;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;

public class TeamStats
{


    private HashMap<String, ArrayList<Statistic>> map;
    private ArrayList<String> names;
    private ArrayList<String> uids;



    public TeamStats(Element table)
    {
        map = new HashMap<String, ArrayList<Statistic>>();
        names = new ArrayList<String>();
        uids = new ArrayList<String>();
        if(table == null){
            System.out.println("TEAM STATS: table does not exist");
            return;
        }

        try{

            Element tableBody = table.getElementsByTag("tbody").get(0);
            for(Element tr : tableBody.getElementsByTag("tr"))
            {
                int colNum=0;
                String name = "";
                String uid = "";
                for(Element td : tr.getElementsByTag("td"))
                {
                    if(colNum==0)
                    {
                        name = td.text();
                        Elements a = td.getElementsByTag("a");
                        uid = a.get(0).attr("href");
                        names.add(name);
                        uids.add(uid);
                        map.put(uid, new ArrayList<Statistic>());
                        //Log.d("REF DATA", "Name = "+name+" UID = "+uid);
                    }
                    else if(!td.attr("data-stat").equals("x")){
                        double val;
                        String text = td.text();
                        try{
                            if(!text.equals("")){
                                val = Double.parseDouble(text);
                                map.get(uid).add(new Statistic(td.attr("data-stat"),val));
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    colNum++;
                }
            }

        }
        catch (Exception e)
        {
            System.out.println("Failed getting team stats from table "+table.html());
            e.printStackTrace();
        }
    }

    public ArrayList<Statistic> getStats(String uid)
    {
        return map.get(uid);
    }

    public ArrayList<String> getNames()
    {
        return names;
    }

    public ArrayList<String> getUids()
    {
        return uids;
    }
}
