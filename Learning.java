
public abstract class Learning {
	protected double getScore(Strategy strategy){
		return 0.0;
	}
	
	protected abstract void store();
	
	protected abstract boolean load();
}
