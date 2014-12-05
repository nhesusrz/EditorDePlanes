package editorDePlanes;

import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JTextArea;

/**
 *
 * @author mpacheco
 */

/*
 * Esta clase se usa para mostrar los mensajes que emite la clase plan.
 */
public class Mensaje {

    private JTextArea areaDeTexto;

    public Mensaje(JTextArea areaDeTexto) {
        this.areaDeTexto = areaDeTexto;
    }

	public void agregarLink(Link link){
		String envio= new String("Se agrego el link: ''"+link.obtenerAccionOrigen().obtenerNombre()+" -- ");
		envio= envio + link.obtenerPredicado().toString();
		envio= envio + " -> "+link.obtenerAccionDestino().obtenerNombre()+"''.";
        mostrarMensaje(envio);
	}

	public void predicadoNoCoincide(){
        mostrarMensaje ("El link no es valido. El predicado entre ambas acciones no coincide o el nivel de las acciones es incorrecto.");
	}

	public void linkExistente(){
		mostrarMensaje ("El link ya existe.");
	}

	public void accionesInexistentes(){
	 	mostrarMensaje ("Las acciones del link no fueron colocadas en el plan.");
	}

	public void eliminarLink(Link link){
        String envio= new String("Se elimino el link: ''"+link.obtenerAccionOrigen().obtenerNombre()+" -- ");
        envio= envio + link.obtenerPredicado().toString();
		envio= envio + " -> "+link.obtenerAccionDestino().obtenerNombre()+"''.";
        mostrarMensaje(envio);
	}

	public void linkInexistente(){
		mostrarMensaje ("El link no existe.");
	}

	public void nivelAccionIncorrecto(Accion accion){
		mostrarMensaje ("La accion ''"+accion.obtenerNombre()+"''  tiene un nivel incorrecto.");
	}

	public void nivelAgregado(Integer nivel){
		mostrarMensaje ("Fue agredado un nuevo nivel: ''"+nivel+"''.");
	}

	public void accionRepetida(Accion accion){
		mostrarMensaje ("La accion ''"+accion.obtenerNombre()+"'' ya existe en el plan.");
	}

	public void inconsistenciaLinks(Accion accion){
		mostrarMensaje ("No se puede mover la accion ''"+accion.obtenerNombre()+"'' debido a que corrompe la consistencia de los link's que tiene asociados.");
	}

	public void nivelesIncorrectorMoverAccion(Accion accion, Integer nivel_destino, Plan plan){
		mostrarMensaje ("Niveles incorrectos - Nivel Origen: (0<= "+accion.obtenerNivel()+" <= "+plan.cantidadNivelesAcciones()+") - Nivel Destino: (0 <= "+nivel_destino+" <= "+plan.cantidadNivelesAcciones()+").");
	}

	public void nivelesIgualesMoverAccion(Accion accion, Integer nivel_destino){
		mostrarMensaje ("Intenta mover la accion ''"+accion.obtenerNombre()+"'' en el mismo nivel. Del nivel: ''"+accion.obtenerNivel()+"'' al nivel ''"+nivel_destino+"''.");
	}

	public void accionInexistente(Accion accion){
		mostrarMensaje ("La accion "+accion.obtenerNombre()+" no existe.");
	}

	public void accionEliminada(Accion accion){
		mostrarMensaje ("La accion ''"+accion.obtenerNombre()+"'' fue eliminada.");
	}

	public void noRequisitosEstadoInicial(Accion accion){
		mostrarMensaje ("La accion ''"+accion.obtenerNombre()+"'' no tiene los requisitos para se definida como estado inicial.");
	}

	public void noRequisitosEstadoFinal(Accion accion){
		mostrarMensaje ("La accion ''"+accion.obtenerNombre()+"'' no tiene los requisitos para se definida como estado final.");
	}

	public void accionAgregada(Accion accion){
		mostrarMensaje ("La accion ''"+accion.obtenerNombre()+"'' a sido colocada en el nivel ''"+accion.obtenerNivel()+"''.");
	}

	public void accionNoAgreda(Accion accion){
		mostrarMensaje ("La accion ''"+accion.obtenerNombre()+"'' que quiere crear no puede ser colocada debido a una amenaza, modifiquela.");
	}

	public void accionRemovidaMover(Accion accion, Integer nivel_original){
		mostrarMensaje ("La accion ''"+accion.obtenerNombre()+"'' a sido removida del nivel ''"+nivel_original+"''.");
	}

	public void accionMovida(Accion accion){
		mostrarMensaje ("La accion ''"+accion.obtenerNombre()+"'' a sido movida al nivel ''"+accion.obtenerNivel()+"''");
	}

	public void promocionarA(Integer nivel_promocion){
		if(nivel_promocion!=null)
			mostrarMensaje ("Se puede promocionar a: "+nivel_promocion);
		else
			mostrarMensaje ("Se promociona a: No existe posibilidad.");
	}

	public void degradarA(Integer nivel_degradacion){
		if(nivel_degradacion!=null)
			mostrarMensaje ("Se puede degradar a: "+nivel_degradacion);
		else
			mostrarMensaje ("Se degrada a: No existe posibilidad.");
	}

	public void amenazaInsalvable(Accion accion){
        String envio= new String("");
		envio= envio +"No se puede salvar la amenaza anterior con la accion ''"+accion.obtenerNombre()+"''.";
        mostrarMensaje(envio);
	}

	public void mostrarAmenazas(Accion accion, Vector<Accion> acciones_negadas, Vector<Vector<Predicado>> predicados_acciones){
    	String envio= new String("");
        int tam;
        for(Enumeration<Accion> e= acciones_negadas.elements();e.hasMoreElements();){
            //envio= envio + "              ";
			Accion accion_conflicto= e.nextElement();
			envio= new String("Amenaza: - Accion: ''"+accion.obtenerNombre()+"'' con Accion: ''"+accion_conflicto.obtenerNombre()+"''\n");
            tam= predicados_acciones.size();
			for(Enumeration<Vector<Predicado>> e1= predicados_acciones.elements(); e1.hasMoreElements();){
				for(Enumeration<Predicado> e2=  e1.nextElement().elements();e2.hasMoreElements();){
                    Predicado p= e2.nextElement();
                    Predicado p1= e2.nextElement();
                    tam= tam - 2;
                    if(tam/2 == 0 && tam != -1){
                        envio= envio + "                 - ''"+p.toString()+"'' con  ''"+p1.toString()+"''\n";
                    }
                    else{
                        envio= envio + "                 - ''"+p.toString()+"'' con  ''"+p1.toString()+"''";
                    }
                }
			}
        }
        mostrarMensaje(envio);
	}

	public void nivelNoVacio(Integer nivel){
        mostrarMensaje ("El nivel ''"+nivel+"'' aun contiene acciones.");
    }

    public void nivelEliminado(Integer nivel){
        mostrarMensaje ("El nivel ''"+nivel+"'' fue eliminado.");

    }
    public void validacionPlanValido(){
        mostrarMensaje ("Validacion Satisfactoria, el plan esta libre de amenazas.");
    }

    public void validacionPlanNoValido(){
        mostrarMensaje ("Validacion No Satisfactoria, el plan presenta amenazas.");
    }

    public void estadoInicialCambiado(Accion estado_inicial){
        String envio= new String();
        envio= envio + "El Estado Inicial a sido cambiado por la accion ''";
        envio= envio + estado_inicial.obtenerNombre()+"''.";
        mostrarMensaje (envio);
    }

    public void estadoFinalCambiado(Accion estado_final){
        String envio= new String();
        envio= envio + "El Estado Final a sido cambiado por la accion ''";
        envio= envio + estado_final.obtenerNombre()+"''.";
        mostrarMensaje (envio);
    }

    public void mostrarMensaje(String mensaje){
        areaDeTexto.append("Msj Id: "+areaDeTexto.getLineCount()+" - "+mensaje+"\n");
    }

}
