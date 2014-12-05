package editorDePlanes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Vector;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author mpacheco
 */

public class PredicadoCompuesto extends Predicado{

	private Vector<Predicado> predicados;


    public PredicadoCompuesto(){
		predicados = new Vector<Predicado>();
	}

	public PredicadoCompuesto(String nombre,Vector<Predicado> predicados) {
		super(nombre);
		if (predicados != null){
                    this.predicados= predicados;
                }
	}
	
	public PredicadoCompuesto(String nombre) {
		super(nombre);
		predicados = new Vector<Predicado>();
	}
	
	public Vector<Predicado> obtener_Predicados(){
		return predicados;
	}

	public void aniadirPredicado(Predicado p){
            if (p != null){
                predicados.add(p);
            }
	}
	
	public boolean equals(Predicado predicado) {
		/*
		 * Si no es compuesto retorno false, pero en el caso de que p si lo sea debebos comparar sus
		 * nombres lluego cantidad de predicados que contiene c/u y lluego que c/u de ellos sean iguales.
		 * Por ejemplo: ss(Not(b), on(A,B,C)) y ss(Not(b)) son distintos.
		 */
        String S1, S2;
		Vector<Predicado> vector_local= this.obtener_Predicados();
		Vector<Predicado> vector_param= predicado.obtenerPredicados();
        S1= this.obtenerNombre();
        S2= predicado.obtenerNombre();
        if (!(predicado instanceof PredicadoCompuesto)||!(S1.equals(S2))||(vector_local.size()!=vector_param.size()))
				return false;
		boolean ok= true;
		for (int i=0;(i<vector_local.size())&&(ok); i++){
			Predicado predicado_local= vector_local.elementAt(i);
			Predicado predicado_param= vector_param.elementAt(i);
            ok= predicado_param.equals(predicado_local);
		}
		return ok;
	}
	
	public boolean niega(Predicado p){
		return false;
	}
	
	public Vector<Predicado> obtenerPredicados() {
		return predicados;
	}

    @Override
    public String toString(){
       return Obtener_Nombre_y_Argumentos();
    }

    public String toStringProlog(){
        String envio= nombre.toLowerCase();
        for (Iterator i= (Iterator)this.Obtener_Predicados().iterator(); i.hasNext();){
            Predicado P= (Predicado) i.next();
            envio= envio.concat(P.toStringProlog());
        }        
        return envio;
    }

    public DefaultMutableTreeNode Obtener_Nodo(){

            DefaultMutableTreeNode NodoRaiz= new DefaultMutableTreeNode(this);

                for (Iterator it = predicados.iterator(); it.hasNext();){
                    Predicado P= (Predicado) it.next();
                    DefaultMutableTreeNode NodoInterno= P.Obtener_Nodo();
                    NodoRaiz.add(NodoInterno);
                }

            return NodoRaiz;
     }

    public Object getRoot() {
        return this;
    }

    public Object getChild(Object parent, int index) {
        return predicados.get(index);
    }

    public int getChildCount(Object parent) {
        return predicados.size();
    }

    public boolean isLeaf(Object node) {
        return predicados.size()==0;
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getIndexOfChild(Object parent, Object child) {
        return predicados.indexOf(child);
    }

    public void addTreeModelListener(TreeModelListener l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeTreeModelListener(TreeModelListener l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    //MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
     public boolean AgregarPredicado(Vector args) {
   
        boolean t= this.predicados.addAll(args);

        return t;
    }
    
    /*MM*/
    public void agregarElemento(Object e, Object c) {
        if (predicados == null){ this.predicados= new Vector( );}
        if (e!= null){
            this.predicados.add((Predicado)e);
        }
    }
    @Override
        /*MM*/
        public void AgregarArgumentos(String arg) {
        
        Predicado P = null;
        
        String aux = arg.trim();
        arg = aux;
       
        int pos1Parentesis= arg.indexOf("(");
        int pos2Parentesis= 0;
        
        if(pos1Parentesis < 0){ 
                                if (aux.equalsIgnoreCase("")) return;
                                P=new Variable("nuevaVariable");
                                
                                int argAuxIndice= arg.indexOf(":");
                                if (argAuxIndice >= 0) { 

                                                        P.Cambiar_Nombre(arg.substring(0, argAuxIndice).toUpperCase());
                                                        
                                                        //si hay algo despues de los dos puntos es instancia de variable
                                                        if (argAuxIndice + 1 <= (arg.length()-1)){
                                                                                                    String auxArg= arg.substring(argAuxIndice + 1, arg.length());
                                                                                                    P.AgregarArgumentos(auxArg);
                                                            // caso contrario la instancia conserva el nombre de la variable   
                                                            }else{
                                                                  P.AgregarArgumentos(arg.substring(0, argAuxIndice).toLowerCase());
                                                                 }
                                                 //si no hay instancia el nombre de la variable y la instancia se llaman iguales   
                                                        }else{
                                                               P.Cambiar_Nombre(arg.toUpperCase());
                                                               P.AgregarArgumentos(arg.toLowerCase());
                                                              }



        }
        else{
             if (pos1Parentesis >= 0){
                 
                    if (arg.substring(0, 1).equalsIgnoreCase("!")){
                        
                        P=new Not();
                        
                        pos2Parentesis= posicionParentesisFinDeArgumentos(pos1Parentesis,arg);
                        String predicado= arg.substring(pos1Parentesis + 1, pos2Parentesis);
                        P.AgregarArgumentos(predicado);
                        
                    }else{
                        String nombreP= arg.substring(0, pos1Parentesis);/*vv sacer espacios con trim*/
                        P=new PredicadoCompuesto(nombreP);
                    
                        pos2Parentesis = posicionParentesisFinDeArgumentos(pos1Parentesis,arg);
                        
                        String predicados = arg.substring(pos1Parentesis + 1, pos2Parentesis);
                    
                        ArrayList argumentos= obtenerArgumentos(predicados);
                        ListIterator i= argumentos.listIterator();
 
                        while (i.hasNext()){
                            
                            String a = (String)i.next().toString();
                            if ( !(a.equalsIgnoreCase("")) ){
                                P.AgregarArgumentos(a);
                                }
                            }/*while*/

                        }/*else*/

            }/*if pos1Parentesis >= 0*/
        
        }/*else*/
       if (P != null) {
          this.predicados.add(P); 
       }    
            
            
    }/*Fin AgregarArgumentos*/
 
        
         @Override
         /*MM*/
         public Vector Obtener_instancias() {
        return this.predicados;
       // throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    /*MM*/
    public String Obtener_Nombre_y_Argumentos() {
        String nombreX= super.Obtener_Nombre_y_Argumentos().concat("( ");
        for (Iterator i= (Iterator)this.Obtener_Predicados().iterator(); i.hasNext();){
            Predicado P= (Predicado) i.next();
            nombreX= nombreX.concat(P.Obtener_Nombre_y_Argumentos() + ", ");
        }       
        if (this.Obtener_Predicados().size()>0){
            nombreX= nombreX.substring(0, nombreX.length()-2);
        }
        nombreX= nombreX.concat(" )");
        return nombreX;
    }
    
     @Override
    /*MM*/
     public String Obtener_Nombre(){
       return Obtener_Nombre_y_Argumentos(); 
    }
    /*MM*/
    public void borrarElemento(Object e, Object c){
        if (e!=null){
            this.predicados.removeElement((Predicado) e);
        }
    }
    /*MM*/
    	public Vector<Predicado> Obtener_Predicados(){
		return predicados;
	}
        

    //MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
}
