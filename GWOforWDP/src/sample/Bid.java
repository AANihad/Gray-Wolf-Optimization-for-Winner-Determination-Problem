package sample;

import java.util.ArrayList;

public class Bid {
    private static int nbBids=0;
    private int id;
    private final ArrayList<Short> items;
    private final ArrayList<Bid> conflict;
    private double gain;

    public Bid(){
        this.items =new ArrayList<>();
        this.gain=0;
        this.conflict=new ArrayList<>();
        this.conflict.add(this);
        nbBids++;
        this.setId(nbBids);
    }

    public ArrayList<Short> getItems() {
        return items;
    }

    public double getGain() {
        return gain;
    }

    public void setGain(double gain) {
        this.gain = gain;
    }

    public void addLot(short lot){
        this.items.add(lot);
    }

    public ArrayList<Bid> getConflict() {
        return conflict;
    }

    public void checkAddToConflict(Bid b){
        for (Short item : this.items) {
            if (b.getItems().contains(item)) {
                this.conflict.add(b);
                b.addConflict(this);
                break;
            }
        }
    }

    public void checkAddToConflict(ArrayList<Bid> bidsFormula){
        for (Bid bid : bidsFormula) {
            this.checkAddToConflict(bid);
        }
    }

    public void addConflict(Bid b){
        this.conflict.add(b);
    }

    public boolean equals(Bid b){
        if(this.gain!=b.getGain()||this.items.size()!=b.getItems().size()){
            return false;
        }
        for(int i = 0; i<this.items.size(); i++){
            if((this.items.get(i))!=(short)(b.getItems().get(i))){
                return false;
            }
        }
        return true;
    }

    public boolean equals(Object o){
        return this.equals((Bid) o);
    }

    public boolean isInConflictWith(Bid b){
        return this.conflict.contains(b);
    }

    public boolean isInConflict(ArrayList<Bid> b){
        for (Bid bid : b) {
            if (this.isInConflictWith(bid)) {
                return false;
            }
        }
        return true;
    }

    public String toString(){
        StringBuilder s= new StringBuilder("ID: " + this.id + " \tValue = " + this.gain + "\n");
        s.append(+this.getItems().size()).append(" Items :");
        for (Short item : this.items) {
            s.append(" ").append(item);
        }
        s.append("\n");
        return s.toString();
    }

    public void setId(int id) {
        this.id = id;
    }
}
