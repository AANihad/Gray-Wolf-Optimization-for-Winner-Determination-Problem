package sample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class GWO {
    ArrayList<Wolf> wolfPack;
    Solution solve(double a, int popSize, int maxIterations, FormuleWDP WDP, Controller.AlgorithmeTask task) {


        // init for popSize with random key encoding
        wolfPack = new ArrayList<>();
        for (int i =0; i<popSize; i++) {
            Wolf w = new Wolf();
            w.genererRandom(WDP);
            w.setPosition(Math.random());
            wolfPack.add(w);
        }
        double r1, r2, alpha_ij, beta_ij, delta_ij, x_ij;
        double A[] = new double[3];
        double C[] = new double[3];

        //Wolf alpha = getBestSol(WDP), beta = getBestSol(WDP), delta = getBestSol(WDP);
        Collections.sort(wolfPack);
        Wolf alpha = Collections.max(wolfPack);
        wolfPack.remove(alpha);
        Wolf beta = Collections.max(wolfPack);
        wolfPack.remove(beta);
        Wolf delta = Collections.max(wolfPack);
        wolfPack.remove(delta);

// cancel button pressed
        if (task.isCancelled() ) {
            System.out.println("Canceling...");
            //TODO : Change the solution to the real result
            return alpha;
        }
/*
        System.out.println(alpha.getGain());
        System.out.println(beta.getGain());
        System.out.println(delta.getGain());
*/

        int nbIteration=0;
        while(nbIteration<maxIterations){
            //ArrayList<Wolf> wolves=null;
            a = 2 - nbIteration * ((2.0) / maxIterations);
            for(int i=0; i<3; i++) {//wolfPack.size() TODO

                for (int k = 0; k < A.length; k++) {
                    r1 = Math.random();
                    r2 = Math.random();

                    A[k] = 2 * a * r1 - a;
                    C[k] = 2 * r2;
                }

                //Update the positions

                x_ij = wolfPack.get(i).getPosition();

                double d1 = Math.abs(C[0] * alpha.getPosition() - x_ij);
                double d2 = Math.abs(C[1] * beta.getPosition() - x_ij);
                double d3 = Math.abs(C[2] * delta.getPosition() - x_ij);

                double x1 = alpha.getPosition() - A[0] * d1;
                double x2 = beta.getPosition() - A[1] * d2;
                double x3 = delta.getPosition() - A[2] * d3;

                wolfPack.get(i).setPosition((x1 + x2 + x3) / 3.0);
                //TODO : update the solution using the position
                wolfPack.get(i).updateSolution(WDP);

                // Update the best solutions
                if (wolfPack.get(i).compareTo(alpha)>0){
                    wolfPack.add(delta);
                    delta = beta;
                    beta = alpha;
                    alpha = wolfPack.get(i);
                } else if (wolfPack.get(i).compareTo(beta)>0){
                    wolfPack.add(delta);
                    delta = beta;
                    beta = wolfPack.get(i);
                } else if (wolfPack.get(i).compareTo(delta)>0){
                    wolfPack.add(delta);
                    delta = wolfPack.get(i);
                }
            }
            nbIteration++;
        }

        //TODO : Change the solution to the real result
        return alpha;
    }


    public Solution getBestSol(FormuleWDP WDP){
        Solution s=new Solution();
        ArrayList<Bid> bidsPrime=new ArrayList<Bid>();
        bidsPrime.addAll(WDP.getBids());

        while(!bidsPrime.isEmpty()){
            double maxA = 0;
            Iterator<Bid> bidsIterator=bidsPrime.iterator();
            Bid best=null;
            while(bidsIterator.hasNext()){
                Bid tmpBid=bidsIterator.next();
                if(tmpBid.getGain()>maxA){
                    maxA = tmpBid.getGain();
                    best = tmpBid;
                }
            }

            if(!best.isInConflict(s.getBids())){
                s.addBid(best);
            }
            bidsPrime.remove(best);
        }
        return s;
    }

    public Solution init(FormuleWDP WDP){
        Solution s=new Solution();
        ArrayList<Bid> bidsPrime=new ArrayList<Bid>();
        bidsPrime.addAll(WDP.getBids());
        while(!bidsPrime.isEmpty()){
            double maxGain=0;
            Iterator<Bid> bidsIterator=bidsPrime.iterator();
            Bid best=null;
            while(bidsIterator.hasNext()){
                Bid tmpBid=bidsIterator.next();
                if(tmpBid.getGain()>maxGain){
                    maxGain=tmpBid.getGain();
                    best=tmpBid;
                }
            }
            if(!best.isInConflict(s.getBids())){
                s.addBid(best);
            }
            bidsPrime.remove(best);
        }
        return s;
    }
}
