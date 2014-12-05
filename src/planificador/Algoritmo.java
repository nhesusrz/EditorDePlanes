package planificador;

import editorDePlanes.Plan;
import interfaceEditordePlanes.InterfaceEditordePlanesView;

/**
 *
 * @author mpacheco
 */

public abstract class Algoritmo {

    protected Plan plan;
	protected InterfaceEditordePlanesView itv;
    protected Codificador codificador;
    protected Decodificador decodificador;

    public Algoritmo(Plan plan, InterfaceEditordePlanesView itv) {
		this.plan= plan;
        this.itv= itv;        
	}

    public abstract void correr();
}
