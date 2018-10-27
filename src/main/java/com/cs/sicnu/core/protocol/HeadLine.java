package com.cs.sicnu.core.protocol;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * headline-> name: ValueDescribe,ValueDescribe,ValueDescribe
 * value->  baseval;valDisposition;valDisposition....
 *
 */

public class HeadLine {
    private String name;
    private String value;


    private List<ValueDescribe> values=new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void addValueDescribe(ValueDescribe valueDescribe){
        values.add(valueDescribe);
    }

    public Iterator<ValueDescribe> describeIterable(){
        return values.iterator();
    }

    public static class ValueDescribe{
        private String baseVal;
        private List<ValueAdorn> valueAdorns=new ArrayList<>();

        public ValueDescribe(String baseVal) {
            this.baseVal = baseVal;
        }

        public void addValueAdorn(ValueAdorn adorn){
            valueAdorns.add(adorn);
        }

        public Iterator<ValueAdorn> adornIterator(){
            return valueAdorns.iterator();
        }

        public String getBaseVal() {
            return baseVal;
        }
    }

    public static class ValueAdorn{
        private String name;
        private String vlaue;

        public ValueAdorn(String name, String vlaue) {
            this.name = name;
            this.vlaue = vlaue;
        }

        public String getName() {
            return name;
        }

        public String getVlaue() {
            return vlaue;
        }
    }

    private static String name_p="^.+: ";
    private static String value_p="[^,]+(;.+=.+){0}[,|, ]{0,1}";
    private static String adron_p="[A-Za-z]+=[^,;]+";

    private static Pattern pattern_namep=Pattern.compile(name_p);
    private static Pattern pattern_values=Pattern.compile(value_p);
    private static Pattern pattern_adron=Pattern.compile(adron_p);

    public static HeadLine createHeadLine(String line) throws ParseException{
        HeadLine headLine=new HeadLine();
        Matcher matcher=pattern_namep.matcher(line);
        if (!matcher.find()){
            throw new ParseException("error");
        }
        String name=matcher.group();
        headLine.name=name.substring(0,name.length()-2);
        headLine.value=line.substring(name.length());
        String[] values=headLine.value.split(",");
        for (String v:values){
            String[] vd=v.split(";");
            ValueDescribe describe=new ValueDescribe(vd[0]);
            if (vd.length>1){
                for (int i=1;i<vd.length;i++){
                    String dms[]=vd[i].split("=");
                    if (dms.length!=2){
                        throw new ParseException("123dadadawdwadygdawjdgaj");
                    }
                    ValueAdorn adorn=new ValueAdorn(dms[0],dms[1]);
                    describe.addValueAdorn(adorn);
                }
            }
            headLine.addValueDescribe(describe);
        }

        return headLine;
    }

}
