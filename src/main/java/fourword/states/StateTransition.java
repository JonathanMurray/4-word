package fourword.states;

/**
 * Created by jonathan on 2015-06-23.
 */
public class StateTransition {

    public boolean changeState;
    public StateName newState;
    public Object data;

    public static StateTransition STAY_HERE = new StateTransition(false, null, null);

    private StateTransition(boolean changeState, StateName newState, Object data){
        this.changeState = changeState;
        this.newState = newState;
        this.data = data;
    }

    public static StateTransition change(StateName newState, Object data){
        return new StateTransition(true, newState, data);
    }

    public static StateTransition change(StateName newState){
        return new StateTransition(true, newState, null);
    }

    public String toString(){
        if(!changeState){
            return "STAY";
        }
        return "changeState -> " + newState + (data != null? (" (" + data + ")") : "");
    }
}
