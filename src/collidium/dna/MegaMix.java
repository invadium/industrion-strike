package collidium.dna;

import collidium.mix.Mix;

public class MegaMix extends Mix {

    @Override
    public void init() {
        this.lab.attach(new Planet());
    }

}
