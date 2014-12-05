package planificador;

import editorDePlanes.Libreria;
import editorDePlanes.Plan;
import interfaceEditordePlanes.InterfaceEditordePlanesView;

/**
 *
 * @author mpacheco
 */

public abstract class Codificador {

    protected Plan plan;
	protected InterfaceEditordePlanesView itv;    

    public Codificador(Plan plan, InterfaceEditordePlanesView itv) {
		this.plan= plan;
        this.itv= itv;          
	}

    public abstract boolean codificar();

}
