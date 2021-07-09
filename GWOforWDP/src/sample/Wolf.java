package sample;

public class Wolf extends Solution implements Comparable<Wolf>{
    double position;

    public Wolf() {
        super();
        this.position = Math.random();
    }

    public void setPosition(double position) {
        this.position = position;
    }

    public double getPosition() {
        return position;
    }

    @Override
    public int compareTo(Wolf w) {
        return (int)(this.getGain() - w.getGain());
    }

    public static Wolf div(Wolf x, int a){
        int l = (int)(x.getBids().size() /a);
        Wolf w = new Wolf();
        for (int i=0; i<l; i++)
            w.addBid(x.getBids().get(i));

        w.setConflict();
        return w;
    }

    public void updatePosition(WDPinstance f) {
        int b = (int)( Math.abs(this.position) % this.getBids().size());
        b= (b==0)?1:b;

        Wolf w = div(this, b);

        for (int bb = b, i=0; bb<w.getBids().size() && i<f.getRandomBids().size(); i++){
            int index =  f.getRandomBids().get(i);
            Bid tmp = f.getBids().get(index);
            if (tmp.isInConflict(w.getBids())) {
                w.getBids().add(tmp);
                bb--;
            }
        }

        if (this.getGain() < w.getGain()){
            this.setBids(w.getBids());
            this.setConflict(w.getConflict());
            this.setGain(w.getGain());
            this.setPosition(w.getPosition());
        }
        else {
            Wolf s = new Wolf();
            s.genererRandom(f);
            this.setBids(s.getBids());
            this.setConflict(s.getConflict());
            this.setGain(s.getGain());
            this.setPosition(s.getPosition());
        }
    }
}
