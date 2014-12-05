package editorDePlanes;

import java.util.Vector;
import java.util.Enumeration;
import java.util.Iterator;
import javax.swing.JTextArea;

/**
 *
 * @author mpacheco
 */

public class Plan implements Cloneable{
	// Vector que contiene un vector por cada nivel y este las acciones que pertenecen al mismo.	 
	private Vector<Vector<Accion>> niveles; 
	private Vector<Link> links;
	private Accion estado_inicial;
	private Accion estado_final;
	private Validacion validar;
	private Integer cant_niveles;
	private Mensaje mensaje;
    private int cant_niveles_jgraph;
    private JTextArea areaDeTexto;
    private boolean fallo_links;

    public JTextArea getAreaDeTexto() {
        return areaDeTexto;
    }

    public void setAreaDeTexto(JTextArea areaDeTexto) {
        this.areaDeTexto = areaDeTexto;
    }

    public Integer getCant_niveles() {
        return cant_niveles;
    }

    public void setCant_niveles(Integer cant_niveles) {
        this.cant_niveles = cant_niveles;
    }

    public int getCant_niveles_jgraph() {
        return cant_niveles_jgraph;
    }

    public void setCant_niveles_jgraph(int cant_niveles_jgraph) {
        this.cant_niveles_jgraph = cant_niveles_jgraph;
    }

    public Accion getEstado_final() {
        return estado_final;
    }

    public void setEstado_final(Accion estado_final) {
        this.estado_final = estado_final;
    }

    public Accion getEstado_inicial() {
        return estado_inicial;
    }

    public void setEstado_inicial(Accion estado_inicial) {
        this.estado_inicial = estado_inicial;
    }

    public boolean isFallo_links() {
        return fallo_links;
    }

    public void setFallo_links(boolean fallo_links) {
        this.fallo_links = fallo_links;
    }

    public Vector<Link> getLinks() {
        return links;
    }

    public void setLinks(Vector<Link> links) {
        this.links = links;
    }

    public Mensaje getMensaje() {
        return mensaje;
    }

    public void setMensaje(Mensaje mensaje) {
        this.mensaje = mensaje;
    }

    public Vector<Vector<Accion>> getNiveles() {
        return niveles;
    }

    public void setNiveles(Vector<Vector<Accion>> niveles) {
        this.niveles = niveles;
    }

    public Validacion getValidar() {
        return validar;
    }

    public void setValidar(Validacion validar) {
        this.validar = validar;
    }

	// Cuando se crea el plan se define una politica por defecto(Amenaza).
	public Plan(JTextArea areaDeTexto) {
		niveles= new Vector<Vector<Accion>>();
		links= new Vector<Link>();		
		estado_inicial= null;
		estado_final= null;
		// Validaci�n por defecto es amenaza.
		validar= new Amenaza(this, areaDeTexto);
		// -1 indica que no se agregaron niveles aun.
		cant_niveles= -1;
		mensaje= new Mensaje(areaDeTexto);
        cant_niveles_jgraph=0;
        this.areaDeTexto= areaDeTexto;
        fallo_links= false;
	}

    public Plan(Vector<Vector<Accion>> niveles, Vector<Link> links, Accion estado_inicial, Accion estado_final, Validacion validar, Integer cant_niveles, Mensaje mensaje, int cant_niveles_jgraph, JTextArea areaDeTexto, boolean fallo_links) {
        this.niveles = niveles;
        this.links = links;
        this.estado_inicial = estado_inicial;
        this.estado_final = estado_final;
        this.validar = validar;
        this.cant_niveles = cant_niveles;
        this.mensaje = mensaje;
        this.cant_niveles_jgraph = cant_niveles_jgraph;
        this.areaDeTexto = areaDeTexto;
        this.fallo_links = fallo_links;
    }
    // Retorna el numero total de acciones menos Inicial y Final.
    public int cantAcciones() {
        int cant= 0;
        for (Iterator i= niveles.iterator(); i.hasNext();){
            cant= cant + ((Vector)i.next()).size();
        }
        return cant;
    }
     public int cantNivelesJGraph(){
            return this.cant_niveles_jgraph;
        }
	// Indica si existen acciones en un nivel determinado.
	public boolean existenAccionesEnNivel(Integer nivel){
		Vector<Accion> acciones_nivel= obtenerAccionesDeNivel(nivel);
		if((acciones_nivel == null)||(acciones_nivel.size()==0))
			return false;
		return true;
	}
	// Indica si un nivel int es correcto o no.
	public boolean nivelCorrecto(Integer nivel){
		return((nivel >=0)&&(nivel<=cant_niveles)&&(cant_niveles!=-1));		
	}
	// Devuelve el vector de acciones del plan. 
	public Vector<Accion> obtenerAccionesDeNivel(Integer nivel){
		if(nivelCorrecto(nivel))
			return niveles.elementAt(nivel);
		return null;				
	}
	// Retorna el estado inicial del plan. 
	public Accion obtenerEstInicial(){
		return estado_inicial;
	}
	// Retorna el estado final del plan. 
	public Accion obtenerEstFinal(){
		return estado_final;
	}
	// Retorna la cantidad de niveles del plan en su totalidad, incluyendo el estado inicial y final. 
	public int cantidadNivelesTotales(){
		return cant_niveles + 2; 
	}
	// Retorna la cantidad de niveles del plan con respecto a sus acciones, sin tener en cuenta los estados inicial y final. 
	public Integer cantidadNivelesAcciones(){
		return cant_niveles;
	}
	// Verifica si un link ya existe en el plan.
	public boolean existeLink(Link link){
		Accion accion_origen= link.obtenerAccionOrigen();
		Accion accion_destino= link.obtenerAccionDestino();
		Predicado predicado= link.obtenerPredicado();
		for(Enumeration<Link> e= links.elements();e.hasMoreElements();){
			Link l= e.nextElement();
			if(accion_origen.equals(l.obtenerAccionOrigen())&&accion_destino.equals(l.obtenerAccionDestino())&& predicado.equals(l.obtenerPredicado()))
					return true;
		}
		return false;
	}
	// Se encarga de agregar el link al plan.
	public boolean agregarLink(Link link){
		// Se verifica que el link no se repita. 
		if(!existeLink(link))
				if((existeAccion(link.obtenerAccionOrigen()))&&(existeAccion(link.obtenerAccionDestino()))){
					Accion a1= link.obtenerAccionOrigen();			
					Accion a2= link.obtenerAccionDestino();			
					Predicado predicado= link.obtenerPredicado();
					/* 
					 * Se verifica que el nivel de la acci�n de origen sea menor que el nivel de la acci�n de destino y que 
					 * el predicado que que las vincula sea el mismo en ambas acciones.
					 */
					if((a1.obtenerNivel() < a2.obtenerNivel()) && (a1.existeEnPostCondiciones(predicado)) && (a2.existeEnPreCondiciones(predicado))){
						links.add(link);
						a1.aniadirLinkOrigen(link);
						a2.aniadirLinkDestino(link);
                        mensaje.agregarLink(link);
                        return true;
					}
					else{
                        mensaje.predicadoNoCoincide();
                        return false;
                    }
				}
				else{
                    mensaje.accionesInexistentes();
                    return false;
                }
		else{
            mensaje.linkExistente();
            return false;
        }

	}
	// Se encarga de borrar un link.
	public void removerLink(Link link){
		// Se verifica que el link exista. 
		if(links.contains(link)&&(existeAccion(link.obtenerAccionOrigen()))&&(existeAccion(link.obtenerAccionDestino()))){
			Accion a1= link.obtenerAccionOrigen();	
			a1.RemoverLinkOrigen(link);
			Accion a2= link.obtenerAccionDestino();
			a2.RemoverLinkDestino(link);			
            mensaje.eliminarLink(link);
		}
		else
            mensaje.linkInexistente();
	}
    public void removerLink2(Link link){
		// Se verifica que el link exista.
		if(links.contains(link)&&(existeAccion(link.obtenerAccionOrigen()))&&(existeAccion(link.obtenerAccionDestino()))){
			Accion a1= link.obtenerAccionOrigen();
			a1.RemoverLinkOrigen(link);
			Accion a2= link.obtenerAccionDestino();
			a2.RemoverLinkDestino(link);
            links.remove(link);
            mensaje.eliminarLink(link);
		}
		else
            mensaje.linkInexistente();
	}
	// Dice si existe una accion en todo planning.
	public boolean existeAccion(Accion accion){		
		for(Enumeration<Vector<Accion>> e= niveles.elements(); e.hasMoreElements();){
			Vector<Accion> acciones_nivel= e.nextElement();
			for(Enumeration<Accion> e1= acciones_nivel.elements(); e1.hasMoreElements();)
				if(e1.nextElement().equals(accion))
					return true;				
		}
        if(estado_inicial!=null && accion.defineEstadoInicial())
			return estado_inicial.equals(accion);
		if(estado_final != null && accion.defineEstadoFinal())
			return estado_final.equals(accion);
		return false;		
	}
    // Agrega una nueva acción al planning, sin sin importar si se producen amenazas.
    public boolean agregarAccionSinAmenaza(Accion accion){
		if(!existeAccion(accion)){
				if(nivelCorrecto(accion.obtenerNivel()))
					// Se supone que la acci�n nueva esta libre de links o sea no tiene.
					return colocarNuevaAccionSinAmenaza(accion);
				else{
                    mensaje.nivelAccionIncorrecto(accion);
                    return false;
                }
        }
		else{
            mensaje.accionRepetida(accion);
            return false;
        }
	}
	// Agrega una nueva acci�n al planning.
	public boolean agregarAccionVerifAmenaza(Accion accion){
		if(!existeAccion(accion)){
				if(nivelCorrecto(accion.obtenerNivel()))
					// Se supone que la acci�n nueva esta libre de links o sea no tiene.
					return colocarNuevaAccionVerifAmenaza(accion);
				else{
                    mensaje.nivelAccionIncorrecto(accion);
                    return false;
                }
        }
		else{
            mensaje.accionRepetida(accion);
            return false;
        }
	}
	// Dice si la acci�n que quiero mover esta en el origen, osea la instancia.
	public boolean existeAccionOrigen(Accion accion){
		Vector<Accion> acciones_nivel= obtenerAccionesDeNivel(accion.obtenerNivel());
		if(acciones_nivel.contains(accion))
			return true;
		return false;
	}
    // Permite mover una acci�n de un nivel a otro sin verificar la existencia de amenazas.
	public boolean moverAccionSinAmenaza(Accion accion, Integer nivel_destino){
		// Se verifica que no se coloque la acci�n fuera del margen o donde se encuentran los estados.
		//if(existeAccion(accion)){
				if((accion.obtenerNivel()!= nivel_destino)){
					if((nivelCorrecto(nivel_destino))&&(nivelCorrecto(accion.obtenerNivel()))){
						if(accion.tieneLinksAsociados()){
							definirPoliticaValidacion(new ConsistenciaLinks(this, nivel_destino, areaDeTexto));
							// Se verifica la concistencia de los link's.
							if(validar.validar(accion)){
                                fallo_links= false;
								return moverAccionExistenteSinAmenaza(accion, nivel_destino);
                            }
							else{
                                mensaje.inconsistenciaLinks(accion);
                                fallo_links= true;
                                return false;
                            }
						}
						else{
                            //accion.modificarNivel(nivel_destino);
							return moverAccionExistenteSinAmenaza(accion, nivel_destino);
                        }
					}
					else{
                        mensaje.nivelesIncorrectorMoverAccion(accion,nivel_destino,this);
                        return false;
                    }
				}
				else{
                    mensaje.nivelesIgualesMoverAccion(accion, nivel_destino);
                    return false;
                }
	}
	/* Permite mover una acci�n de un nivel a otro verificando la existencia de amenazas. En caso de presentarse
     * alguna da la posibilidad de promocionar o degradar la accion a mover.
     */
	public boolean moverAccionVerifAmenaza(Accion accion, Integer nivel_destino){
		// Se verifica que no se coloque la acci�n fuera del margen o donde se encuentran los estados.
				if((accion.obtenerNivel()!= nivel_destino)){
					if((nivelCorrecto(nivel_destino))&&(nivelCorrecto(accion.obtenerNivel()))){
						if(accion.tieneLinksAsociados()){
							definirPoliticaValidacion(new ConsistenciaLinks(this, nivel_destino, areaDeTexto));
							// Se verifica la concistencia de los link's.
							if(validar.validar(accion)){
                                fallo_links= false;
								return moverAccionExistenteAmenaza(accion,nivel_destino);
                            }
							else{
                                fallo_links= true;
                                mensaje.inconsistenciaLinks(accion);
                                return false;
                            }
						}
						else
							return moverAccionExistenteAmenaza(accion,nivel_destino);
					}
					else{
                        mensaje.nivelesIncorrectorMoverAccion(accion,nivel_destino,this);
                        return false;
                    }
				}
				else{
                    mensaje.nivelesIgualesMoverAccion(accion, nivel_destino);
                    return false;
                }
	}	
	// Remueve una acci�n del plan y todos los links vinculados a ella.
	public void removerAccion(Accion accion){
		Vector<Accion> acciones_nivel= obtenerAccionesDeNivel(accion.obtenerNivel());
		// Se verifica que la acci�n que se quiere borrar exista en el nivel especificado por la misma.
		if(existeAccion(accion)){
			removerLinksAsociados(accion);
			acciones_nivel.remove(accion);
			// Cambia el elemento que esta en determinada posic�n.
			niveles.setElementAt(acciones_nivel, accion.obtenerNivel());
            mensaje.accionEliminada(accion);
		}	
		else
            mensaje.accionInexistente(accion);
	}
	// Define el estado inicial del plan usando un vector de post condiciones.
	public void definirEstadoInicial(Vector<Predicado> post_condiciones){
		estado_inicial= new Accion("Estado inicial", Integer.MIN_VALUE, null, post_condiciones);
        mensaje.estadoInicialCambiado(estado_inicial);
	}
	// Define el estado inicial de plan usando una acci�n.
	public void definirEstadoInicial(Accion estado_inicial){
		// Se verifica que la acci�n cumpla con la caracterista que debe tener el estado inicial.
		if(estado_inicial.defineEstadoInicial()){
			estado_inicial.modificarNivel(-1/*Integer.MIN_VALUE*/);
			this.estado_inicial= estado_inicial;
            mensaje.estadoInicialCambiado(estado_inicial);
		}
		else
            mensaje.noRequisitosEstadoInicial(estado_inicial);
	}
	// Define el estado final del plan usando un vector de post condiciones. 
	public void definirEstadoFinal(Vector<Predicado> pre_condiciones){
		estado_final= new Accion("Estado final", this.cant_niveles_jgraph/*Integer.MAX_VALUE*/, pre_condiciones, null);
        mensaje.estadoFinalCambiado(estado_final);
	}
	// Define el estado final de plan usando una acci�n.
	public void definirEstadoFinal(Accion estado_final){
		// Se verifica que la acci�n cumpla con la caracterista que debe tener el estado final.
		if(estado_final.defineEstadoFinal()){
			estado_final.modificarNivel(Integer.MAX_VALUE);
			this.estado_final= estado_final;
            mensaje.estadoFinalCambiado(estado_final);
		}
		else
            mensaje.noRequisitosEstadoFinal(estado_final);
	}	
	// Permite crear un nivel en el plan. Se crea por defecto al final, pero siempre antes del estado final (Graficamente).
	public void agregarNivel(){
        cant_niveles_jgraph++;
        if(estado_inicial != null && estado_final != null)
            this.estado_final.modificarNivel(cant_niveles_jgraph);
		++cant_niveles;
		niveles.add(cant_niveles, new Vector<Accion>());
        mensaje.nivelAgregado(cant_niveles);
	}
    // Permite borrar un nivel determinado del plan siempre que se encuentre vacio.
    public void borrarNivel(Integer nivel){
        Vector<Accion> acciones_nivel= niveles.elementAt(nivel);
        // Si el nivel esta vacio lo borro y hago la correccion del nivel de la acciones de los siguientes niveles.
        if(acciones_nivel.size() == 0){
            //cant_niveles= cant_niveles - 1;
            for(int i= 0;i <= cant_niveles; i++){
                if(i > nivel){
                    Vector<Accion> acciones_nivel_sig= obtenerAccionesDeNivel(i);
                    for(Enumeration<Accion> e= acciones_nivel_sig.elements();e.hasMoreElements();){
                        Accion a= e.nextElement();
                        Integer niv= a.obtenerNivel();
                        niv= niv - 1;
                        a.modificarNivel(niv);
                    }
                }
			}
            for(int i=0;i < cant_niveles; i++)
                if(i>=nivel){
                    acciones_nivel= niveles.elementAt(i+1);
                    niveles.setElementAt(acciones_nivel, i);
                }
            niveles.removeElementAt(cant_niveles);
            cant_niveles= cant_niveles - 1;
            cant_niveles_jgraph= cant_niveles_jgraph - 1;
            mensaje.nivelEliminado(nivel);
		}
        else
            mensaje.nivelNoVacio(nivel);
    }
	// Permite cambiar la politica que se desea validar en el plan.
	public void definirPoliticaValidacion(Validacion validar){
		this.validar= validar; 
	}
    // Me otorga un Vector de acciones que tienen en comun las acciones que se quieren linkear.
    public Vector<Predicado> predicosEnComun(Accion aorigen, Accion adestino){        
        Vector<Predicado> vretorno= new Vector<Predicado>();
        Vector<Predicado> posao= aorigen.obtenerPostCondiciones();
        Vector<Predicado> pread= adestino.obtenerPreCondiciones();
        if(posao != null && pread != null){
            for(Enumeration<Predicado> e1= posao.elements();e1.hasMoreElements();){
                Predicado p1= e1.nextElement();
                for(Enumeration<Predicado> e2= pread.elements();e2.hasMoreElements();)
                    if(p1.equals(e2.nextElement()))
                        vretorno.add(p1);
            }
        }
        if(vretorno.size() == 0)
            return null;
        return vretorno;
    }
    // Retorna en un solo vector todas las acciones del plan;
    public Vector<Accion> obtenerAcciones(){
        Vector Acciones= new Vector();
        for (Iterator itf= this.niveles.iterator(); itf.hasNext(); ){
            Vector accionesNivel= (Vector)itf.next();
            for (Iterator itc= accionesNivel.iterator(); itc.hasNext();){
                Acciones.add((Accion)itc.next());
            }
        }
        return Acciones;
    }
    // Retorna todos los links del plan en un vector.
    public Vector<Link> obtenerLinks(){
        return this.links;
    }
    // Busca a partir de un String dado como nombre la acción que
    public boolean validarConPolitica(Accion accion){
        if(validar.validar(accion))
            return true;
        return false;
    }
	// Remueve los links asociados a una determinada acci�n.
	private void removerLinksAsociados(Accion accion){
        /* Se crea este vector auxiliar para contener los links a borrar.
         * Esto se debe que cuando hago links.remove(link) me hace el corrimiento y entonces el for pasa por alto algunos links
         * que podrian ser borrados.
         */
        Vector<Link> links_borrar= new Vector<Link>();
		for(Enumeration<Link> e= links.elements();e.hasMoreElements();){            
			Link link= e.nextElement();            
			Accion a1= link.obtenerAccionOrigen();			
			Accion a2= link.obtenerAccionDestino();			
			if(accion.equals(a1) || accion.equals(a2)){
                removerLink(link);		
                links_borrar.add(link);
            }
		}
        for(Enumeration<Link> e= links_borrar.elements();e.hasMoreElements();){
            Link l= e.nextElement();
            links.remove(l);
        }

	}
	// Metodo que trata el tema de la amenaza cuando se quiere agregar la accion y si puede la agrega sino busca donde promocionarla o degradarla.
	private boolean colocarNuevaAccionVerifAmenaza(Accion accion){
		definirPoliticaValidacion(new Amenaza(this, areaDeTexto));
		if(!validar.validar(accion)){// Sino hay amenaza.
			Vector<Accion> acciones_nivel= obtenerAccionesDeNivel(accion.obtenerNivel());
			acciones_nivel.add(accion);
			niveles.setElementAt(acciones_nivel, accion.obtenerNivel());
            mensaje.accionAgregada(accion);
            return true;
		}
		else
            ((Amenaza)validar).mostrarAmenza(accion, false);
        return false;
	}

    private boolean colocarNuevaAccionSinAmenaza(Accion accion){
        Vector<Accion> acciones_nivel= obtenerAccionesDeNivel(accion.obtenerNivel());
        acciones_nivel.add(accion);
        niveles.setElementAt(acciones_nivel, accion.obtenerNivel());
        mensaje.accionAgregada(accion);
        return true;
    }
    private boolean moverAccionExistenteSinAmenaza(Accion accion, Integer nivel_destino){
        Integer nivel_original= accion.obtenerNivel();
        accion.modificarNivel(nivel_destino);
        Vector<Accion> acciones_nivel= obtenerAccionesDeNivel(nivel_destino);
        acciones_nivel.add(accion);
        niveles.setElementAt(acciones_nivel, nivel_destino);
        acciones_nivel= obtenerAccionesDeNivel(nivel_original);
		accion.modificarNivel(nivel_original);
		acciones_nivel.remove(accion);
		niveles.setElementAt(acciones_nivel, nivel_original);
        mensaje.accionRemovidaMover(accion, nivel_original);
		accion.modificarNivel(nivel_destino);
        mensaje.accionMovida(accion);
        return true;
    }
	// Metodo que trata el tema de la amenaza cuando se quiere mover la accion y si puede la agrega sino busca donde promocionarla o degradarla.
	private boolean moverAccionExistenteAmenaza(Accion accion, Integer nivel_destino){
		Integer nivel_original= accion.obtenerNivel();
		// Se le cambia momentaneamente el nivel a la acci�n para poder verificar la amenaza en el nivel de destino.
		accion.modificarNivel(nivel_destino);
		definirPoliticaValidacion(new Amenaza(this, areaDeTexto));
		// Se verifica que no exista amenaza en el nivel donde se desea colocar la acci�n. 
		if(!validar.validar(accion)){
			// Saco las acciones del nivel de destino para meter la acci�n ahi.
			Vector<Accion> acciones_nivel= obtenerAccionesDeNivel(nivel_destino);
			// Si es null quiere decir que ese nivel no existe entonces lo que se hace es colocarla en un nuevo nivel y obtengo el nivel nuevo.
			acciones_nivel.add(accion);			
			// El metodo setElementAt reemplaza al existente en esa posici�n.
			niveles.setElementAt(acciones_nivel, nivel_destino);			
			// Saco las acciones del nivel donde estaba la acci�n antes para eliminarla de ahi.			
			acciones_nivel= obtenerAccionesDeNivel(nivel_original);
			accion.modificarNivel(nivel_original);
			acciones_nivel.remove(accion);
			niveles.setElementAt(acciones_nivel, nivel_original);
            mensaje.accionRemovidaMover(accion, nivel_original);
			accion.modificarNivel(nivel_destino);
            mensaje.accionMovida(accion);
            return true;
		}
		else{
            accion.modificarNivel(nivel_original);
            ((Amenaza)validar).mostrarAmenza(accion, true);
		}
        return false;
	}
	// Retorna todos loas niveles a los cuales se puede promocionar la accion.
    // Siempre y cuando no se corrrompan los links y no haya amenaza.
	public Vector<Integer> promociones(Accion accion){
        Vector<Integer> posibilidades= new Vector<Integer>();
		Integer nivel_original= accion.obtenerNivel();
		Integer nivel= nivel_original;
		++nivel;
		definirPoliticaValidacion(new Amenaza(this, areaDeTexto));
		// Se chequea que una sola condicion de limite ya que el nivel se aumenta.
		while(nivelCorrecto(nivel)){
            definirPoliticaValidacion(new Amenaza(this, areaDeTexto));
            accion.modificarNivel(nivel);
			if(!validar.validar(accion)){
                accion.modificarNivel(nivel_original);
                definirPoliticaValidacion(new ConsistenciaLinks(this, nivel, areaDeTexto));
                if(validar.validar(accion))
                    posibilidades.add(nivel);
            }
			++nivel;
		}
		accion.modificarNivel(nivel_original);
        return posibilidades;
	}
	// Idem promociones.
	public Vector<Integer> degradaciones(Accion accion){
        Vector<Integer> posibilidades= new Vector<Integer>();
		Integer nivel_original= accion.obtenerNivel();
		Integer nivel= accion.obtenerNivel();
		--nivel;
		definirPoliticaValidacion(new Amenaza(this, areaDeTexto));
		// Se chequea que una sola condicion de limite ya que el nivel se disminuye.
		while(nivelCorrecto(nivel)){
			accion.modificarNivel(nivel);
            definirPoliticaValidacion(new Amenaza(this, areaDeTexto));
			if(!validar.validar(accion)){
                accion.modificarNivel(nivel_original);
                definirPoliticaValidacion(new ConsistenciaLinks(this, nivel,areaDeTexto));
                if(validar.validar(accion))
                    posibilidades.add(nivel);
            }
			--nivel;
		}
		accion.modificarNivel(nivel_original);
        return posibilidades;
	}
    public boolean falloDeLinks(){
        return fallo_links;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
              Plan obj=null;
              obj=(Plan) super.clone();
              //obj.niveles= (Vector<Vector<Accion>>) niveles.clone();
              //obj.links=(Vector<Link>) links.clone();
              //obj.estado_inicial=(Accion) estado_inicial.clone();
              //obj.estado_final=(Accion) estado_final.clone();
             // obj.validar=(Validacion) validar.clone();
             // obj.cant_niveles=(Integer) cant_niveles.clone();
             // obj.mensaje=(Mensaje) mensaje.clone();
              return obj;
        }

    /* Devuelve true si dest puede linkear una de sus precondiones. */
    public boolean gestionaLinkPre(Accion dest){
        Vector<Accion> acciones= niveles.elementAt(getCant_niveles());
        for(Enumeration<Accion> e= acciones.elements();e.hasMoreElements();){
            Accion or= e.nextElement();
            if(gestiona(or,dest))
                return true;
        }
        return false;
    }

    /* Metodo privado que usa el anterior. Compara todoas las post condiones de 
     * la accion orcontra todas las pre condiones de la accion dest.
     */
    private boolean gestiona(Accion or, Accion dest){
        Vector<Predicado> predsor= or.Obtener_Post_Condiciones();
        for(Enumeration<Predicado> e= predsor.elements();e.hasMoreElements();){
            Predicado por= e.nextElement();
            Vector<Predicado> predest= dest.Obtener_Pre_Condiciones();
            for(Enumeration<Predicado> e1= predest.elements();e1.hasMoreElements();){
                Predicado pdest= e1.nextElement();
                if(por.equals(pdest))
                    return true;
            }
        }
        return false;
    }

    /* Trata de linkear todo el plan. */
    public void ramificar(){
        Vector<Accion> vi= new Vector<Accion>();
        for(int i= 0; i < niveles.size(); i++){
            if(i == 0){
                vi.addElement(estado_inicial);
                siguientes(i, vi);
            }
            Vector<Accion> ac1= niveles.elementAt(i);
            siguientes(i+1, ac1);            
        }
        siguientesEstadoFinal();
    }

    private void siguientesEstadoFinal(){
        for(int i= 0; i < niveles.size(); i++){
           Vector<Accion> ac= niveles.elementAt(i);
           for(Enumeration<Accion> e= ac.elements();e.hasMoreElements();){
               Accion a= e.nextElement();
               unir(a,estado_final);
           }
        }
    }

    private void siguientes(int i, Vector<Accion> ac1){
        for(int j= i; j < niveles.size(); j++){
                Vector<Accion> ac2= niveles.elementAt(j);
                for(Enumeration<Accion> e1= ac1.elements();e1.hasMoreElements();){
                    Accion a1= e1.nextElement();
                    for(Enumeration<Accion> e2= ac2.elements();e2.hasMoreElements();){
                        Accion a2= e2.nextElement();
                        unir(a1,a2);
                    }
                }
            }
    }

    /* Contruye todos los links posibles entre accion a1 y accion a2. */
    private void unir(Accion or, Accion dest){
        Vector<Predicado> predsor= or.Obtener_Post_Condiciones();
        for(Enumeration<Predicado> e= predsor.elements();e.hasMoreElements();){
            Predicado por= e.nextElement();
            Vector<Predicado> predest= dest.Obtener_Pre_Condiciones();
            for(Enumeration<Predicado> e1= predest.elements();e1.hasMoreElements();){
                Predicado pdest= e1.nextElement();
                if(por.equals(pdest)){
                    Link link= new Link(or, dest, pdest);
                    agregarLink(link);
                }
            }
        }
    }
}
