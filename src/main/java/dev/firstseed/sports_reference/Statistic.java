package dev.firstseed.sports_reference;

public class Statistic
{
    protected double value;
    protected double normalizedValue;
    public String name;
    protected boolean invert = false;
    private StatType type;


    public Statistic(String name, double value){
        this.name = name;
        this.value = value;
        if(
            name.contains("tov") ||
            name.contains("pf") ||
            name.contains("losses")
        ){
            invert = true;
        }

        if(name.contains("opp")){
            invert = !(invert);
        }

        if(
            name.contains("opp") ||
                    name.startsWith("blk") ||
                    name.startsWith("stl")
        )
        {
            setType(StatType.DEFENSE);
        }
        else if(name.startsWith("g") ||
                name.startsWith("win_loss_pct") ||
                name.startsWith("srs") ||
                name.startsWith("wins") ||
                name.startsWith("losses") ||
                name.startsWith("mp"))
        {
            setType(StatType.GENERAL);
        }
        else if(name.startsWith("sos")){
            setType(StatType.SOS);
        }
        else if(name.equals("GenComp"))
        {
            setType(StatType.GEN_COMP);
        }
        else if(name.equals("OffComp"))
        {
            setType(StatType.OFF_COMP);
        }
        else if(name.equals("DefComp"))
        {
            setType(StatType.DEF_COMP);
        }
        else if(name.equals("SosComp"))
        {
            setType(StatType.SOS_COMP);
        }
        else if(name.equals("CompleteComp"))
        {
            setType(StatType.COMPLETE_COMP);
        }
        else{
            setType(StatType.OFFENSE);
        }

    }

    public StatType getType()
    {
        return this.type;
    }

    public void setType(StatType type)
    {
        this.type = type;
    }

    public double getNormalizedValue(){
        return this.normalizedValue;
    }

/*    public double getWeight()
    {
        return this.weight;
    }

    public boolean getEnabled()
    {
        return this.enabled;
    }*/

    public double getValue()
    {
        return this.value;
    }

    public void setValue(double value)
    {
        this.value = value;
    }

    public void normalize(double min, double max){
        normalizedValue = Util.interpolate(value, min, max, 0.0, 1.0);
/*        if(invert){
            normalizedValue = 1.0 - normalizedValue;
        }*/
    }
/*
    public void setWeight(double weight){
        this.weight = weight;
    }


    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }
    public String toString(){
        return name +"\tValue:"+value+"\tWeight:"+weight+"\t"+"NormVal:"+normalizedValue+"\tInvert:"+invert;
    }*/
}
