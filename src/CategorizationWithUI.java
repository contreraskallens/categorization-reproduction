import sim.engine.*;
import sim.display.*;
import sim.portrayal.*;

public class CategorizationWithUI extends GUIState {
    public static void main(String[] args){
        CategorizationWithUI vid = new CategorizationWithUI();
        Console c = new Console(vid);
        c.setVisible(true);
    }
    public CategorizationWithUI() { super(new Categorization(System.currentTimeMillis()));}
    public CategorizationWithUI(SimState state) { super(state); }
    public static String getName() { return "Categorization via Social Interaction"; }

    public Object getSimulationInspectedObject() { return state; }

    public Inspector getInspector(){
        Inspector i = super.getInspector();
        i.setVolatile(true);
        return i;
    }

}
