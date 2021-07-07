package sample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

public class Wolf extends Solution implements Comparable<Wolf>{

    private double position =0;

    @Override
    public int compareTo(Wolf w) {
        return (int)(this.getGain() - w.getGain());
    }

    public double getPosition() {
        return position;
    }

    public void setPosition(double position) {
        this.position = position;
    }

    public void updateSolution(FormuleWDP f) { // flip n bids, respect conflicts

        //ArrayList<Solution> ss = this.flip((int)position*this.getBids().size());
        //this = Collections.max((Wolf)ss);
        for (int i=0; i<this.getBids().size(); i++){
            System.out.println(this.getBids().get(i).getConflict());
        }
/*
        Iterator<Bid> bidsIterator=f.getBids().iterator();
        while(bidsIterator.hasNext()){
            Bid tmp=bidsIterator.next();
            if(!this.getConflict().contains(tmp)){
                this.addBid(tmp);
            }
        }
*/
        System.out.println("_______________________________");
        this.calculatePrice();
    }

}
