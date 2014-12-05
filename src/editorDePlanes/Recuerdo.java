package editorDePlanes;

/**
 *
 * @author mpacheco
 */

class Recuerdo {
    private Object state;

    public Recuerdo(Object stateToSave)
    {
        state = stateToSave;
    }

    public Object getSavedState()
    {
        return state;
    }
    public void clean() {
        state=null;
    }

}
