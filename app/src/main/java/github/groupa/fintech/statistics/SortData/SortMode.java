package github.groupa.fintech.statistics.SortData;

import java.util.Comparator;

import github.groupa.fintech.statistics.StatisticsModelClass;

public class SortMode implements Comparator<StatisticsModelClass> {
    //value used to determine which method to use for sorting
    byte option = 0;

    public SortMode(byte option){
        this.option = option;
    }

    @Override
    public int compare(StatisticsModelClass obj1, StatisticsModelClass obj2) {
        //sorting by amount in descended mode
        if(option==0){
            float comp = ((StatisticsModelClass)obj2).getTvAmount();
            return (int) (comp-obj1.getTvAmount());
        }

        //sorting by amount in ascended mode
        if(option==1){
            float comp = ((StatisticsModelClass)obj2).getTvAmount();
            return -(int) (comp-obj1.getTvAmount());
        }

        //sorting by date and time in ascended mode
        if(option==2){
            int i = obj1.getDate().compareTo(obj2.getDate());
            if (i!=0) return i;
            return obj1.getTime().compareTo(obj2.getTime());
        }

        //sorting by date and time in descended mode
        if(option==3){
            int i = -obj1.getDate().compareTo(obj2.getDate());
            if (i!=0) return i;
            return -obj1.getTime().compareTo(obj2.getTime());
        }

        //sorting by date and time in ascended mode
        if(option==4) return -obj1.getTvType().compareTo(obj2.getTvType());

        //sorting by date and time in descended mode
        if(option==5) return obj1.getTvType().compareTo(obj2.getTvType());

        return 0;
    }
}
