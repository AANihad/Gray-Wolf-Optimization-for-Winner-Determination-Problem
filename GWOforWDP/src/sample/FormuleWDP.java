package sample;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class FormuleWDP {
    private ArrayList<Bid> bids;
    private short nbBids;
    private short nbLots;

    public FormuleWDP(){
        this.bids=new ArrayList<Bid>();
        this.nbBids=0;
        this.nbLots=0;
    }

    public FormuleWDP(String cheminFichier){
        this();
        try {
            BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(cheminFichier)));
            String ligne=br.readLine();
            String[] mots=ligne.split(" ");
            this.nbLots=Short.parseShort(mots[0]);
            this.nbBids=Short.parseShort(mots[1]);
            ligne=br.readLine();
            while(ligne!=null){
                mots=ligne.split(" ");
                Bid bid=new Bid();
                bid.setGain(Double.parseDouble(mots[0]));
                for(int i=1;i<mots.length;i++){
                    bid.addLot(Short.parseShort(mots[i]));
                }
                bid.checkAddToConflict(this.bids);
                this.bids.add(bid);
                ligne=br.readLine();
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Bid> getBids() {
        return bids;
    }

    public void setBids(ArrayList<Bid> bids) {
        this.bids = bids;
    }

    public short getNbBids() {
        return nbBids;
    }

    public void setNbBids(short nbBids) {
        this.nbBids = nbBids;
    }

    public short getNbLots() {
        return nbLots;
    }

    public void setNbLots(short nbLots) {
        this.nbLots = nbLots;
    }

    public String toString(){
        String s="Nombre de lots : "+this.nbLots+"\n";
        s+="Nombre d'enchères : "+this.nbBids+"\n";
        Iterator<Bid> bids=this.bids.iterator();
        while(bids.hasNext()){
            s+=bids.next().toString();
        }
        s+="\n";
        return s;
    }

    public Bid bestBid(Solution s){
        Iterator<Bid> bids=this.bids.iterator();
        Bid bestBid= null;
        double bestGainBid=-10000;
        while(bids.hasNext()){
            Bid tmp=bids.next();
            if(!s.getBids().contains(tmp)){
                Iterator<Bid> conflicts=tmp.getConflict().iterator();
                double perte=0;
                while(conflicts.hasNext()){
                    Bid tmpConflinct=conflicts.next();
                    if(s.getBids().contains(tmpConflinct)){
                        perte+=tmpConflinct.getGain();
                    }
                }
                double gainBid=tmp.getGain()-perte;
                if(gainBid>bestGainBid){
                    bestGainBid=gainBid;
                    bestBid=tmp.clone();
                }
            }
        }
        return bestBid;
    }
/*
    //Méthode d'optimisation par essaim d'abeilles
    public Solution beeSwarmOptimisation(int nbIterationsMax,int flip,int nbChancesMax){

            //Recherche des abeilles successivement
            while(searchArea.hasNext()){
                Solution tmp=searchArea.next();
                //System.out.println("le meilleur voisin à un nombre de clasuses sat= "+bestVoisin.getNbClausesSat());
                Bid b=bestBid(tmp);
                tmp.forcedAddBid(b,this);
                danse.add(tmp);
            }
    }
 */
}
