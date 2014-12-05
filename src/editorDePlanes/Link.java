package editorDePlanes;

/**
 *
 * @author mpacheco
 */

public class Link{

	// Acci�n origen del link.
	private Accion accionor;
	// Acci�n destino del link.
	private Accion accionde;
	// Predicado que vincula ambas acciones.
	private Predicado predicado;   
	
	public Link(Accion accion1, Accion accion2, Predicado predicado) {
		this.accionor= accion1;
		this.accionde= accion2;
		this.predicado= predicado;
	}
	// Retorna la acci�n de origen del link.
	public Accion obtenerAccionOrigen(){
		return accionor;
	}
	// Retorna la acci�n de destino del link.
	public Accion obtenerAccionDestino(){
		return accionde;
	}
	// Retorna el predicado que vincula el link.
	public Predicado obtenerPredicado(){
		return predicado;
	}
    @Override
    public String toString(){
        String envio= new String("Link: " + obtenerAccionOrigen().obtenerNombre() + " -- ");
		envio= envio + obtenerPredicado().toString();
		envio= envio + " --> " + obtenerAccionDestino().obtenerNombre() + "\n";
        return envio;        
    }

}
