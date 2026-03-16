package collidium.mixer;

import java.awt.Color;

import collidium.dna.Planet;
import collidium.dna.geo.Background;
import collidium.dna.hud.Label;
import collidium.mix.Env;
import collidium.mix.Mix;

// Concrete mix examples
public class MegaMix extends Mix {

    @Override
    public void init() {
        Env env = this.env;

        this.background = new Background(Color.getHSBColor(.1f, .05f, 0.1f));
        this.background.setVisible(false);

        this.lab.attach(new Planet(env.width/2, env.height/2, 50,
            Color.getHSBColor(0.2f, 0.5f, 0.7f), this));
        this.lab.attach(new Label("This message is hard-coded!", 20, 40));
    }

}
