package editorDePlanes;

/**
 *
 * @author mpacheco
 */

public class Originador {

    
    private Object state;
    /* lots of memory consumptive private data that is not necessary to define the
     * state and should thus not be saved. Hence the small memento object. */

    public void set(Object state)
    {
      // System.out.println("Originator: Setting state to "+state.toString());
       this.state = state;
    }

    public Recuerdo salvarEnRecuerdo()
    {
        System.out.println("Originator: Saving to Memento.");
        return new Recuerdo(state);
    }

    public void reestablecerDeRecuerdo(Recuerdo m)
    {
        state = m.getSavedState();
       // System.out.println("Originator: State after restoring from Memento: "+state.toString());
    }

    public Object getState() {
        return state;
    }

    public void clean() {
        set(null);
    }

}
