package sample;

import java.util.ArrayList;
import java.util.Iterator;

public class Solution {
    private ArrayList<Bid> bids;
    private ArrayList<Bid> conflict;
    private double gain;    //fitness

    public Solution(){
        this.bids=new ArrayList<Bid>();
        this.gain=0;
        this.conflict=new ArrayList<Bid>();
    }

    public ArrayList<Bid> getBids() {
        return bids;
    }

    public void setBids(ArrayList<Bid> bids) {
        this.bids = bids;
    }

    public void addBid(Bid b){
        this.bids.add(b);
        this.gain+=b.getGain();
        this.concatConflict(b);
    }

    public void concatConflict(Bid b){
        Iterator<Bid> conflictIterator=b.getConflict().iterator();
        while(conflictIterator.hasNext()){
            Bid tmp=conflictIterator.next();
            if(!this.conflict.contains(tmp)){
                this.conflict.add(tmp);
            }
        }
    }

    public double getGain() {
        return gain;
    }

    public void setGain(double gain) {
        this.gain = gain;
    }

    public void calculatePrice(){
        this.gain=0;
        for (Bid bid : this.bids) {
            //System.out.println( bid.getGain());
            this.gain += bid.getGain();
        }
    }

    public Solution clone(){
        Solution solution=new Solution();
        Iterator<Bid> bids=this.bids.iterator();
        while(bids.hasNext()){
            solution.addBid(bids.next());
        }
        return solution;
    }

    public String toString(){
        String s="Gain de la solution = "+this.gain+"\n";
        s+="Enchères de la solutions : \n";
        Iterator<Bid> bids=this.bids.iterator();
        while(bids.hasNext()){
            s+=bids.next().toString();
        }
        return s;
    }

    public boolean equals(Solution s){
        if(s==null || s.getGain()!=this.gain || s.getBids().size()!=this.bids.size()){
            return false;
        }
        for(int i=0;i<this.bids.size();i++){
            boolean same=false;
            for(int j=0;j<s.getBids().size();j++){
                if(this.bids.get(i).equals(s.getBids().get(j))){
                    same=true;
                    break;
                }
            }
            if(!same){
                return false;
            }
        }
        return true;
    }

    public boolean equals(Object o){
        Solution s=(Solution) o;
        return this.equals(s);
    }

    public ArrayList<Bid> getConflict() {
        return conflict;
    }

    public void setConflict(ArrayList<Bid> conflict) {
        this.conflict = conflict;
    }

    public void setConflict(){
        Iterator<Bid> bidsIterator=this.bids.iterator();
        while(bidsIterator.hasNext()){
            this.concatConflict(bidsIterator.next());
        }
    }

    //Random key encoding algorithme modifié
    public void genererRandom(FormuleWDP WDP){
        Solution s=new Solution();
        //Génération du vecteur r
        ArrayList<Integer> r=new ArrayList<Integer>();
        for(int i=0;i<WDP.getNbBids();i++){
            int tmp=(int)(Math.random()*10000)%WDP.getNbLots();
            r.add(tmp);
        }

        int i=WDP.getNbBids()-1;
        while(i>=0){
            int index = r.indexOf(i);
            while(index==-1){
                i--;
                index=r.indexOf(i);
            }
            Bid tmp=WDP.getBids().get(index);
            r.set(index,-1);
            if(!tmp.isInConflict(s.getBids())){
                s.addBid(tmp);
            }
        }
        this.bids = s.getBids();
        this.setConflict();
        this.gain = s.getGain();
    }
}
