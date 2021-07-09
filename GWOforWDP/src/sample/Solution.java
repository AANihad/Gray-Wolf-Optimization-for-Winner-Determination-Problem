package sample;

import java.util.ArrayList;

public class Solution {
    private ArrayList<Bid> bids;
    private ArrayList<Bid> conflict;
    private double gain;    //fitness

    public Solution(){
        this.bids=new ArrayList<>();
        this.gain=0;
        this.conflict=new ArrayList<>();
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
        for (Bid tmp : b.getConflict()) {
            if (!this.conflict.contains(tmp)) {
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

    public String toString(){
        StringBuilder s= new StringBuilder("Solution's Value = " + this.gain + "\n");
        s.append("Solution's Bids : \n");
        for (Bid bid : this.bids) {
            s.append(bid.toString());
        }
        return s.toString();
    }

    public boolean equals(Solution s){
        if(s==null || s.getGain()!=this.gain || s.getBids().size()!=this.bids.size()){
            return false;
        }
        for (Bid bid : this.bids) {
            boolean same = false;
            for (int j = 0; j < s.getBids().size(); j++) {
                if (bid.equals(s.getBids().get(j))) {
                    same = true;
                    break;
                }
            }
            if (!same) {
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
        for (Bid bid : this.bids) {
            this.concatConflict(bid);
        }
    }

    //Random key encoding algorithme modifié
    public void genererRandom(WDPinstance WDP){
        Solution s=new Solution();
        ArrayList<Integer> r=new ArrayList<>();
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
            if(tmp.isInConflict(s.getBids())){
                s.addBid(tmp);
            }
        }
        this.bids = s.getBids();
        this.setConflict();
        this.gain = s.getGain();
    }
}
