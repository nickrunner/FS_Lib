package dev.firstseed.sports_reference;


import java.util.LinkedHashSet;

public abstract class AbstractStatCreator
{
    public String name;
    protected  String uid;
    protected LinkedHashSet<Statistic> stats;
    protected int year = 0;
    public boolean isInTournament = false;

    public AbstractStatCreator(String uid, String name)
    {
        this.uid = uid;
        this.name = name;
        this.stats = new LinkedHashSet<Statistic>();
    }


    public Statistic getStat(String tag){
        for(Statistic stat : stats){
            if(stat.name.equals(tag)){
                return stat;
            }
        }
        return null;
    }

    public void addStats(LinkedHashSet<Statistic> stats)
    {
        this.stats.addAll(stats);
    }

    public LinkedHashSet<Statistic> getStatistics(){
        return stats;
    }

    public void calculateGeneralComposite(StatModel model)
    {
        double valueTotal = 0;
        double weightTotal = 0;
        for(Statistic stat : stats){
            if(stat.getType() == StatType.GENERAL && !stat.name.equals("g")){
                valueTotal += stat.getNormalizedValue() * model.getWeight(stat.name);
                weightTotal += model.getWeight(stat.name);

            }
        }
        double generalComposite = valueTotal/weightTotal;
        try {
            getStat("GenComp").setValue(generalComposite);
        }
        catch(Exception e){
            stats.add(new Statistic("GenComp", generalComposite));
            //model.addStat("GenComp", 0.5);
        }

    }

    public void calculateOffensiveComposite(StatModel model){
        double valueTotal = 0;
        double weightTotal = 0;
        for(Statistic stat : stats){
            if(stat.getType() == StatType.OFFENSE) {

                //System.out.println("ASC OffComp Stat nValue = "+stat.normalizedValue);
                valueTotal += stat.getNormalizedValue() * model.getWeight(stat.name);
                weightTotal += model.getWeight(stat.name);

            }
        }


        double offensiveComposite = valueTotal/weightTotal;
        try {
            getStat("OffComp").setValue(offensiveComposite);
        }
        catch(Exception e){
            stats.add(new Statistic("OffComp", offensiveComposite));
           // model.addStat("OffComp", 0.5);
        }

    }

    public void calculateDefensiveComposite(StatModel model)
    {
        double valueTotal = 0;
        double weightTotal = 0;
        for(Statistic stat : stats){
            if(stat.getType() == StatType.DEFENSE){

                valueTotal += stat.getNormalizedValue() * model.getWeight(stat.name);
                weightTotal += model.getWeight(stat.name);

            }
        }
        double defensiveComposite = valueTotal/weightTotal;

        try {
            getStat("DefComp").setValue(defensiveComposite);
        }
        catch(Exception e){
            stats.add(new Statistic("DefComp", defensiveComposite));
            //model.addStat("DefComp", 0.5);
        }

    }

    public void calculateSosComposite(StatModel model)
    {
        double valueTotal = 0;
        double weightTotal = 0;
        for(Statistic stat : stats){
            if(stat.getType() == StatType.SOS){

                valueTotal += stat.getNormalizedValue() * model.getWeight(stat.name);
                weightTotal += model.getWeight(stat.name);

            }
        }
        double sosComposite = valueTotal/weightTotal;

        try {
            getStat("SosComp").setValue(sosComposite);
        }
        catch(Exception e){
            stats.add(new Statistic("SosComp", sosComposite));
            //model.addStat("SosComp", 0.5);
        }

    }


    public void calculateCompleteComposite(StatModel model){
        double total = 0;
        double weightTotal = 0;
        Statistic oc = getStat("OffComp");
        Statistic gc = getStat("GenComp");
        Statistic dc = getStat("DefComp");
        Statistic sc = getStat("SosComp");

        if(oc != null){

            //Log.d("TEAM", "Off Comp");
            total += (oc.getNormalizedValue() * model.getWeight(oc.name));
            weightTotal += model.getWeight(oc.name);

        }

        if(dc != null)
        {

            //Log.d("TEAM", "Def Comp");
            total += (dc.getNormalizedValue() * model.getWeight(dc.name));
            weightTotal += model.getWeight(dc.name);

        }

        if(gc != null){

            //Log.d("TEAM", "Gen Comp");
            total += gc.getNormalizedValue() * model.getWeight(gc.name);
            weightTotal += model.getWeight(gc.name);

        }

        if(sc != null){

            //Log.d("TEAM", "Sos Comp");
            total += sc.getNormalizedValue() * model.getWeight(sc.name);
            weightTotal += model.getWeight(sc.name);

        }


        double completeComposite = total/weightTotal;

        try {
            getStat("CompleteComp").setValue(completeComposite);
        }
        catch(Exception e){
            stats.add(new Statistic("CompleteComp", completeComposite));
            //model.addStat("CompleteComp", 0.5);
        }
    }

    public String getUid()
    {
        return this.uid;
    }

    public void setYear(int year)
    {
        this.year = year;
    }

    public int getYear()
    {
        return  this.year;
    }
}
