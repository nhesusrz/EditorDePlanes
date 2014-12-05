package editorDePlanes;

import java.util.Vector;
import java.util.Enumeration;
import java.util.Iterator;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author mpacheco
 */

public class Accion implements TreeModel, NodoElemento{
	
	private String nombre;
	private Integer nivel=-1;
	private Vector<Predicado> pre_condiciones;
	private Vector<Predicado> post_condiciones;
	private Vector<Link> links_destino;
	private Vector<Link> links_origen;

    public Accion() {
        nombre=new String();
        pre_condiciones= new Vector<Predicado>();
		post_condiciones= new Vector<Predicado>();
		links_destino= new Vector<Link>();
		links_origen= new Vector<Link>();
        nivel=new Integer(-1);

    }
    public int cantidadPreCondicionesLikeadas(){
        return links_destino.size();
    }
    public int cantidadPostCondicionesLikeadas(){
        return links_origen.size();
    }
	// Permite crear la acci�n con determinadas caracteristicas.
	public Accion(String nombre, Integer nivel, Vector<Predicado> pre_condiciones, Vector<Predicado> post_condiciones) {
		this.nombre= nombre.toUpperCase();		
		this.nivel= nivel;
		this.pre_condiciones= pre_condiciones;
		this.post_condiciones= post_condiciones;
		links_destino= new Vector<Link>();
		links_origen= new Vector<Link>();
	}
	// Permite crear una acci�n solo con su nivel.
	public Accion(String nombre, Integer nivel) {
		this.nombre= nombre.toUpperCase();	
		this.nivel= nivel;
		pre_condiciones= new Vector<Predicado>();
		post_condiciones= new Vector<Predicado>();
		links_destino= new Vector<Link>();
		links_origen= new Vector<Link>();
	}
        /*MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM*/
        public Accion (String nombre){
            this.nombre= nombre;
            this.nivel=-1;
            this.pre_condiciones=new Vector(0);
            this.post_condiciones=new Vector(0);
          }
         public String Obtener_Nombre() {
                return this.nombre;
        }

        public void agregarElemento(Object e, Object condicion) {
            if (this.post_condiciones == null){
                this.post_condiciones= new Vector(1);
            }
            if (this.pre_condiciones == null){
                this.pre_condiciones= new Vector(1);
            }
            if (e!=null && condicion!=null){
                String c= (String) condicion;
                    if (c.equalsIgnoreCase("pre")||c.equalsIgnoreCase("pre_condiciones")){
                        this.Anadir_Pre_Condicion((Predicado)e);
                    }else
                        if (c.equalsIgnoreCase("pos")||c.equalsIgnoreCase("pos_condiciones")){
                            this.Anadir_Post_Condicion((Predicado)e);
                        }else if (c.equalsIgnoreCase("preAll")){
                                 this.pre_condiciones.addAll((Vector)e);
                              }else if(c.equalsIgnoreCase("posAll")){
                                      this.post_condiciones.addAll((Vector)e);
                                    }
            }
        }

        public void borrarElemento(Object e, Object condicion){
        String c = "";
        if (condicion != null){
            c= (String) condicion;
        }
        if (e!=null){
            if (c.equalsIgnoreCase("pre")||c.equalsIgnoreCase("pre_condiciones")){
                this.Borrar_Pre_Condicion((Predicado)e);
                }else if (c.equalsIgnoreCase("pos")||c.equalsIgnoreCase("pos_condiciones")){
                         this.Borrar_Post_Condicion((Predicado)e);
                         }
            }else{
                  if (c.equalsIgnoreCase("preAll")){
                      this.pre_condiciones.removeAllElements();
                     }else{
                            if (c.equalsIgnoreCase("posAll")){
                                this.post_condiciones.removeAllElements();
                                }
                          }
                }
        }

        public String Obtener_Nombre_y_Argumentos() {
            return this.Obtener_Nombre();
        }

        public void Anadir_Pre_Condicion(Predicado p){
		pre_condiciones.add(p);
        }

        public void Anadir_Post_Condicion(Predicado p){
		post_condiciones.add(p);
        }

        public void Borrar_Pre_Condicion(Predicado p){
            /*
            Enumeration<Predicado> e= pre_condiciones.elements();
		Predicado elemento = null;
                while(e.hasMoreElements()){
                    elemento = (Predicado) e.nextElement();
                    if(elemento.equals(p))
				pre_condiciones.remove(p);
                }
        */
            
            Predicado elemento = null;
            for (Iterator ite = this.pre_condiciones.iterator(); ite.hasNext();){
                elemento = (Predicado) ite.next();
                if (elemento.equals(p)){
                    ite.remove();
                }
            }
            
             }

       public void Borrar_Post_Condicion(Predicado p){
           //Enumeration<Predicado> e= post_condiciones.elements();
		Predicado elemento = null;
              /*  while(e.hasMoreElements()){
                    elemento = (Predicado) e.nextElement();
                    if(elemento.equals(p))
				post_condiciones.remove(p);
                }
       
              */
                for (Iterator itp = this.post_condiciones.iterator(); itp.hasNext();){
                    elemento = (Predicado) itp.next();
                    if (elemento.equals(p)){
                        itp.remove();
                    }
                }
       
       }
        
        public void Modificar_Nombre(String nombre){
            this.nombre= nombre;
        }

        public Vector<Predicado> Obtener_Pre_Condiciones(){
            return pre_condiciones;
        }
	
        public Vector<Predicado> Obtener_Post_Condiciones(){
            return post_condiciones;
        }
        /*MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM*/
	// Verifica si la acci�n tiene link's asociados a ella.
	public boolean tieneLinksAsociados(){
		if((links_origen.size() == 0)&&(links_destino.size() == 0))
			return false;
		return true;		
	}
	// Agrega el link en donde la acci�n juega el rol de origen.
	public void aniadirLinkOrigen(Link link){
		links_origen.add(link);
	}
	// Permite eliminar el links en el cual la acci�n juega el rol de origen.
	public void RemoverLinkOrigen(Link link){
		links_origen.remove(link);
	}
	// Agrega el link en donde la acci�n juega el rol de destino.
	public void aniadirLinkDestino(Link link){
		links_destino.add(link);
	}
	// Permite eliminar el links en el cual la acci�n juega el rol de destino.
	public void RemoverLinkDestino(Link link){
		links_destino.remove(link);
	}
	// Devuelve el vector de link's asociados en donde la acci�n es origen.
	public Vector<Link> obtenerLinksOrigen(){
		return links_origen;
	}
	// Devuelve el vector de link's asociados en donde la acci�n es destino.
	public Vector<Link> obtenerLinksDestino(){
		return links_destino;
	}
	// Verifica si un determina predicado es pre condici�n en la acci�n.
	public boolean existeEnPreCondiciones(Predicado predicado){
        for(Enumeration<Predicado> e= pre_condiciones.elements();e.hasMoreElements();)
            if(e.nextElement().equals(predicado))
                return true;
        return false;
		//return(pre_condiciones.contains(predicado));
	}
	// Verifica si un determina predicado es post condici�n en la acci�n.
	public boolean existeEnPostCondiciones(Predicado predicado){
        for(Enumeration<Predicado> e= post_condiciones.elements();e.hasMoreElements();)
            if(e.nextElement().equals(predicado))
                return true;
        return false;
		//return (post_condiciones.contains(predicado));
	}
	// Devuelve el vector con las pre condiciones de la acci�n.
	public Vector<Predicado> obtenerPreCondiciones(){
		return pre_condiciones;
	}
	// Devuelve el vector con las post condiciones de la acci�n.
	public Vector<Predicado> obtenerPostCondiciones(){
		return post_condiciones;
	}
	// Dice si la acci�n reune requisitos de estado inicial.
	public boolean defineEstadoInicial(){
		return(pre_condiciones.size()==0 && post_condiciones.size()>0);
	}
	// Dice si la acci�n reune requisitos de estado final.
	public boolean defineEstadoFinal(){
		return(pre_condiciones.size()>0 && post_condiciones.size()==0);
	}
	// Devuelve el nivel en donde se encuentra la acci�n.
	public Integer obtenerNivel(){
		return nivel;
	}
	// Devuelve el nombre de la acci�n. 
	public String obtenerNombre(){
		return nombre;
	}
	// Permite modificar el nombre de la acci�n.
	public void modificarNombre(String nombre){
		this.nombre= nombre;
	}
	// Modifica el nivel de la acci�n.
	public void modificarNivel(Integer nivel){
		this.nivel= nivel;		
	}
	// Permite promocionar la acci�n aumentando su nivel en el plan.
	public void promocionar(){
		++nivel;
	}
	// Permite degradar la acci�n disminuyendo su nivel en el plan.
	public void degradar(){
		--nivel;
	}
    /* Permite agregar un predicado a las pre/post condiciones de la accion.
     * el vector de pre/post condiciones se pasa por parametro
     */
   	public boolean agregarPredicado(Predicado predicado, Vector<Predicado> PrePos){
        /* Se lo sacamos para que tenga consistencia con la libreria.
		if(existePredicadoEqualsVector(predicado, PrePos)){
			System.out.println("El predicado "+predicado.obtenerNombre()+" ya existe en las pre/post condiciones de "+this.obtenerNombre()+".");
            return false;
        }*/
		PrePos.add(predicado);
        return true;
	}
	// Permite agregar un predicado a las pre condiciones de la acci�n.	
	public void agregarPreCondicion(Predicado predicado){
		if(existePredicadoEqualsVector(predicado, pre_condiciones))
			System.out.println("El predicado "+predicado.obtenerNombre()+" ya existe en las precondicones de "+this.obtenerNombre()+".");
        else
            pre_condiciones.add(predicado);
        
	}
	// Permite agregar un predicado a las post condiciones de la acci�n.
	public void agregarPostCondicion(Predicado predicado){
		if(existePredicadoEqualsVector(predicado, post_condiciones))
			System.out.println("El predicado "+predicado.obtenerNombre()+" ya existe en las postcondicones de "+this.obtenerNombre()+".");
        else
            post_condiciones.add(predicado);
	}
	// Permite borrar un predicado a las pre condiciones de la acci�n. 
	public void borrarPreCondicion(Predicado predicado){
		for (Enumeration<Predicado> e= pre_condiciones.elements();e.hasMoreElements();)
			if(e.nextElement().equals(predicado))
				pre_condiciones.remove(predicado);
	}
	// Permite borrar un predicado a las post condiciones de la acci�n.
	public void borrarPostCondicion(Predicado predicado){
		for (Enumeration<Predicado> e= post_condiciones.elements();e.hasMoreElements();)
			if(e.nextElement().equals(predicado))
				post_condiciones.remove(predicado);
	}
	/* 
	 * Se redefine el equals en Acci�n. El nivel no se verifica para que no se agregue dos veces la misma acci�n en cualquier lado.
	 * Eso teniendo en cuenta que si se intancia una misma accion en el plan diferiran justamente en la instanciaci�n siendo la misma acci�n.
	 */
	public boolean equals(Accion accion){		
		if(this.nombre.equals(accion.obtenerNombre())){// Se verifica que tengan el mismo nombre.			
			Vector<Predicado> pre_condiciones= accion.obtenerPreCondiciones();
			Vector<Predicado> post_condiciones= accion.obtenerPostCondiciones();
			if((mismosPredicados(this.pre_condiciones, pre_condiciones))&&(mismosPredicados(this.post_condiciones,post_condiciones)))
				return true;			
		}
		return false;
	}
	/*
	 *  Se encarga de ver que cada elemento de un vector exista en el otro y viceversa ya que pueden tener
	 *   los mismos predicados pero en distinto orden.
	 */
	private boolean mismosPredicados(Vector<Predicado> vector1, Vector<Predicado> vector2){
		for(Enumeration<Predicado> e= vector1.elements(); e.hasMoreElements();)
			if(!existePredicadoEqualsVector(e.nextElement(), vector2))
				return false;
		for(Enumeration<Predicado> e1= vector2.elements(); e1.hasMoreElements();)
			if(!existePredicadoEqualsVector(e1.nextElement(), vector1))
				return false;
		return true;
	}
	/* 
	 * Se verifica que un predicado exista en un vector usando el metodo equals de predicado, 
	 * ya que puede haber varias instancias de predicado repartidas por varias acciones pero que son iguales totalmente.
	 */	
	private boolean existePredicadoEqualsVector(Predicado predicado, Vector<Predicado> vector){
		for(Enumeration<Predicado> e= vector.elements(); e.hasMoreElements();)
			if(predicado.equals( e.nextElement()))			
				return true;
		return false;
	}
	
    public String toString(){
       return " "+obtenerNombre()+" ";
    }

    public String toStringProlog(){
        return nombre.toLowerCase();
    }

    public DefaultMutableTreeNode Obtener_Nodo(){

        DefaultMutableTreeNode NodoRaiz= new DefaultMutableTreeNode(this);
        DefaultMutableTreeNode NodoInternoPre= new DefaultMutableTreeNode("Pre_Condiciones");
        DefaultMutableTreeNode NodoInternoPos= new DefaultMutableTreeNode("Pos_Condiciones");


        for (Iterator it= pre_condiciones.iterator(); it.hasNext();){
            Predicado PreCondicionN= (Predicado) it.next();
            DefaultMutableTreeNode NodoInterno= PreCondicionN.Obtener_Nodo();
            NodoInternoPre.add(NodoInterno);
        }
        NodoRaiz.add(NodoInternoPre);

        for (Iterator it= post_condiciones.iterator(); it.hasNext();){
            Predicado PosCondicionN= (Predicado) it.next();
            DefaultMutableTreeNode NodoInterno= PosCondicionN.Obtener_Nodo();
            NodoInternoPos.add(NodoInterno);
        }
        NodoRaiz.add(NodoInternoPos);


        return NodoRaiz;

    }

    public Object getRoot() {
        return this.Obtener_Nodo();
    }

    public Object getChild(Object parent, int index) {
        DefaultMutableTreeNode p= (DefaultMutableTreeNode)parent;
        return p.getChildAt(index);
    }

    public int getChildCount(Object parent) {
        DefaultMutableTreeNode p= (DefaultMutableTreeNode)parent;
        return p.getChildCount();
    }

    public boolean isLeaf(Object node) {
        DefaultMutableTreeNode nodo= (DefaultMutableTreeNode)node;
        return nodo.isLeaf();
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    public int getIndexOfChild(Object parent, Object child) {        
        DefaultMutableTreeNode p= (DefaultMutableTreeNode)parent;
        return p.getIndex((TreeNode) child);
    }

    public void addTreeModelListener(TreeModelListener l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeTreeModelListener(TreeModelListener l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void anularLinkOrYDst(){
        links_destino= new Vector<Link>();
		links_origen= new Vector<Link>();
    }

}
