package editorDePlanes;

import java.util.Vector;
import java.util.Enumeration;
import javax.swing.JTextArea;

/**
 *
 * @author mpacheco
 */

public class ConsistenciaLinks extends Validacion {
	
	Integer nivel_destino;	

	public ConsistenciaLinks(Plan plan, Integer nivel_destino, JTextArea areaDeTexto) {
		super(plan, areaDeTexto);
		this.nivel_destino= nivel_destino;
	}
	/* 
	 * La politica que lleva adelante esta clase es la de verificar que cuando se quiera mover una acci�n, a
	 * un determinado nivel y esta tenga link's asociados, entonces debe tenerse en cuenta que no se pierda
	 * la consistencia de estos link's.
	 * Cuando se habla de concistencia nos referimos a que cuando se degrade la acci�n ninguno de los
	 * link's entrantes apunten en un mismo nivel o superior. De la misma manera que los links salientes de la 
	 * acci�n apunten hacia arriba o al mismo nivel cuando promocione la acci�n.
	 */
	public boolean validar(Accion accion) {		
		// Quiero mover la acci�n hacia abajo, promocionarla. 
		if(accion.obtenerNivel() < nivel_destino){
			// Tomo los link's salientes de mi acci�n en donde ella es origen en ellos.
			Vector<Link> links_origen= accion.obtenerLinksOrigen();
			if(verificarNivelPromocion(accion,links_origen))
				return true;
		}
		else{
			// Tomo los link's entrantes de mi acci�n en donde ella es destino en ellos. 
			Vector<Link> links_destino= accion.obtenerLinksDestino();
			if(verificarNivelDegradacion(accion,links_destino))
				return true;
		}		
		return false;
	}
	/* 
	 * M�todo que verifica que las acciones destino, que tienen los links salientes de la accion que quiero mover
	 * para abajo (promocionar), sean su nivel siempre mayor que el nivel de destino de la acci�n.
	 */
	private boolean verificarNivelPromocion(Accion accion, Vector<Link> links_origen){
		for(Enumeration<Link> e= links_origen.elements(); e.hasMoreElements();){
			Link link= e.nextElement();
			Accion accion_destino= link.obtenerAccionDestino();
			// Si el nivel donde deseo colocarla no es menor (pensemos con los links) entonces no corrompo al link.
			if(!(nivel_destino < accion_destino.obtenerNivel()))
				return false;
		}
		return true;			
	}
	// Idem anterior pero ahora tomando los links en donde la acci�n es destino en sus link's.
	private boolean verificarNivelDegradacion(Accion accion, Vector<Link>links_destino){
		for(Enumeration<Link> e= links_destino.elements(); e.hasMoreElements();){
			Link link= e.nextElement();
			Accion accion_origen= link.obtenerAccionOrigen();
			if(!(nivel_destino > accion_origen.obtenerNivel()))
				return false;
		}
		return true;
	}
}
