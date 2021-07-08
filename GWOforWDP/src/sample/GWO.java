package sample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class GWO {
    ArrayList<Wolf> wolfPack;
    Solution solve(int popSize, int maxIterations, FormuleWDP WDP, Controller.AlgorithmeTask task) {
        // init for popSize with random key encoding
        wolfPack = new ArrayList<>();

        for (int i =0; i<popSize; i++) {
            Wolf w = new Wolf();
            w.genererRandom(WDP);
            w.setPosition(Math.random() * w.getBids().size());
            wolfPack.add(w);
        }

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

        int nbIteration=0;
        double a, r1, r2, alpha_ij, beta_ij, delta_ij, x_ij;
        double A[] = new double[3];
        double C[] = new double[3];

        while(nbIteration<maxIterations){
            if (task.isCancelled() ) {
                System.out.println("Canceling...");
                //TODO : Change the solution to the real result
                return alpha;
            }

            a = 2 - nbIteration * ((2.0) / maxIterations);
            for(int i=0; i<wolfPack.size(); i++) {
                for (int k = 0; k < A.length; k++) {
                    r1 = Math.random();
                    r2 = Math.random();
                    // Equation (3.3)
                    A[k] = 2 * a * r1 - a;
                    // Equation (3.4)
                    C[k] = 2 * r2;
                    C[k] = (C[k]*wolfPack.get(i).getBids().size()) % wolfPack.get(i).getBids().size();
                }
                //update the solution

                alpha_ij = alpha.getPosition();
                beta_ij = beta.getPosition();
                delta_ij = delta.getPosition();
                x_ij = wolfPack.get(i).getPosition();
                // Equation (3.5)-part 1
                double d1 = Math.abs(C[0] * alpha_ij - x_ij);
                double d2 = Math.abs(C[1] * beta_ij - x_ij);
                double d3 = Math.abs(C[2] * delta_ij - x_ij);
                // Equation 3.6
                double x1 = alpha_ij - A[0] * d1;
                double x2 = beta_ij - A[1] * d2;
                double x3 = delta_ij - A[2] * d3;
                wolfPack.get(i).setPosition((x1 + x2 + x3) / 3.0);
                wolfPack.get(i).updatePosition(WDP);
                //System.out.println(">"+wolfPack.get(i).getGain());
            }
            wolfPack.add(delta);
            wolfPack.add(beta);
            wolfPack.add(alpha);

            Collections.sort(wolfPack);
            alpha = Collections.max(wolfPack);
            wolfPack.remove(alpha);
            beta = Collections.max(wolfPack);
            wolfPack.remove(beta);
            delta = Collections.max(wolfPack);
            wolfPack.remove(delta);

            nbIteration++;
        }/*
        System.out.println(alpha.getGain());
        System.out.println(beta.getGain());
        System.out.println(delta.getGain());
        System.out.println("-----------");
*/
        //TODO : Change the solution to the real result
        return alpha;
    }
}
