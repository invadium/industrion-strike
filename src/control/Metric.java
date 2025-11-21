package control;

public class Metric {
	//in this range bots can see the enemy
	static final public double enemyVicinity = 100000;
	//in this range bots can see attacking(them or their target) enemy
	static final public double attackingEnemyVicinity = 100000;
	//in this range capital ships lasers open fire
	static final public double shipLaserRange = 50000;
	//in this range fighter lasers open fire
	static final public double fighterLaserRange = 50000;

	//in this range target is reached
	//static final public double reachRange = 5000;
	//in this range ships/fighters get cruise speed. Overwise they get maximum
	static final public double cruiseRange = 40000;
	//in this range fighter increase distance with attacking target
	static final public double oppositeRange = 4000;
	//in this range fingter turn back to the attacking target
	static final public double turnBackRange = 40000;
	//in this range missile turn back to the attacking target
	static final public double missileTurnBack = 25000;
	//in this range fighter back to follow target instead of rounding
	static final public double turnFollowRange = 15000;

	//missile explosion rate
	static final public int missileExplossion = 25;
}