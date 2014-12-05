package editorDePlanes;

import java.util.Vector;
import java.util.Enumeration;
import javax.swing.JFrame;
import javax.swing.JTextArea;

/**
 *
 * @author mpacheco
 */

public class Amenaza extends Validacion {

	Accion accion_nueva;	
	Mensaje mensaje;
	// Estos vectores se usan para lluego mostrar adecuadamente la amenaza completa.
	Vector<Accion> acciones_negadas;	
	Vector<Vector<Predicado>> predicados_acciones;
	Vector<Predicado> predicados_con_amenazas;
    JFrame amenazaBox;
	
	public Amenaza(Plan plan, JTextArea areaDeTexto) {
		super(plan,  areaDeTexto);
		acciones_negadas= new Vector<Accion>();		
		predicados_acciones= new Vector<Vector<Predicado>>();	
		mensaje= new Mensaje( areaDeTexto);
	}
	/* 
	 * Para verificar una posible amenaza cuando insertamos la acci�n en el plan se toma la nueva
	 * acci�n y se va cotenjando con cada una de las acciones del nivel en donde se desea colocarla,
	 * verificando que los predicados de las pre condiciones de la nueva acci�n no sean negados por ningun
	 * predicado de las post condiciones de las acciones que se ya se encuentran en el nivel.
	 * De manera similar se coteja el tema de la negaci�n entre los predicados de las post condiciones de la
	 * nueva acci�n con los predicados de las pre condiciones de las acciones que ya se encuentran en el nivel.
	 */
	public boolean validar(Accion accion) {
		boolean amenaza= false;		
		if(plan.existenAccionesEnNivel(accion.obtenerNivel())){			
			Vector<Accion> acciones_nivel= plan.obtenerAccionesDeNivel(accion.obtenerNivel());
			for(Enumeration<Accion> e= acciones_nivel.elements();e.hasMoreElements();){
				Accion accion_nivel= e.nextElement();
				if(efectoNegador(accion,accion_nivel)){
					acciones_negadas.add(accion_nivel);
					predicados_acciones.add(predicados_con_amenazas);
					amenaza= true;					
				}
			}
			if(amenaza)
				return true;
		}
		return false;		
	}
	// Verifica si se produce una negaci�n entre las pre condiones con las post condiones entre ambas acciones y viceversa.
	private boolean efectoNegador(Accion a1, Accion a2){
		boolean efecto_negador= false;		
		predicados_con_amenazas= new Vector<Predicado>();
		// Da un warning pero no moverlo porque aca siempre se tiene que iniciar un nuevo vector para contener los predicados que se pueden llegar a negar.
		//Vector<Predicado> predicados_con_amenazas= new Vector<Predicado>();
		Vector<Predicado> pos_a1= a1.obtenerPostCondiciones();
		Vector<Predicado> pres_a2= a2.obtenerPreCondiciones();		
		Vector<Predicado> pres_a1= a1.obtenerPreCondiciones();
		Vector<Predicado> pos_a2= a2.obtenerPostCondiciones();
		if(predicadoNegado(pos_a1,pres_a2)||predicadoNegado(pos_a2,pres_a1))
			efecto_negador= true;
		return efecto_negador;
	}
	// Verifica si algun predicado de un vector niega algun predicado del otro vector y viceversa.
	private boolean predicadoNegado(Vector<Predicado> v1, Vector<Predicado> v2){
		boolean predicado_negado= false;		
		for(Enumeration<Predicado> e1= v1.elements(); e1.hasMoreElements();){
			Predicado p1= e1.nextElement();			
			for(Enumeration<Predicado> e2= v2.elements(); e2.hasMoreElements();){
				Predicado p2= e2.nextElement();
				if((p1.niega(p2)) || (p2.niega(p1))){
					predicado_negado= true;
					predicados_con_amenazas.add(p1); 
					predicados_con_amenazas.add(p2);			
				}				
			}			
		}		
		return predicado_negado;
	}
	// No implementa imprimible porque no se imprime a si misma. Simplemente se muestra la amenazas.
	public void mostrarAmenza(Accion accion, boolean surge_al_mover){
        mensaje.mostrarAmenazas(accion, acciones_negadas, predicados_acciones);
   	}
}
