package sample;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class FormuleWDP {
    private ArrayList<Bid> bids;
    private short nbBids;
    private short nbLots;
    private ArrayList<Integer> randomBids=new ArrayList<Integer>();
    public FormuleWDP(){
        this.bids=new ArrayList<Bid>();
        this.nbBids=0;
        this.nbLots=0;

    }

    public ArrayList<Integer> getRandomBids() {
        return randomBids;
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

            for(int i=0;i<this.getNbBids();i++){
                int tmp=(int)(Math.random()*10000)%this.getNbLots();
                randomBids.add(tmp);
            }
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
}
