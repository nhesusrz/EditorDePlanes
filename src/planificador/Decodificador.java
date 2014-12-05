package planificador;

import editorDePlanes.Plan;
import interfaceEditordePlanes.InterfaceEditordePlanesView;

/**
 *
 * @author mpacheco
 */

public abstract class Decodificador {

    protected Plan plan;
	protected InterfaceEditordePlanesView itv;

    public Decodificador(Plan plan, InterfaceEditordePlanesView itv) {
		this.plan= plan;
        this.itv=  itv;
	}

    public abstract boolean decodificar(String resultado);
}
