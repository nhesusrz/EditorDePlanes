package editorDePlanes;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Vector;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author mpacheco
 */

/**
 * Aclaraci�n: Esta clase se separa de compuesto por la necesidad de implementar la l�liga del 
 * metodo Niega, que es necesario para verificar el tema de las amenazas.
 */

public class Not extends Predicado {

	private Predicado predicado;
	
	public Not(Predicado p) {
		super("Not");
		this.predicado= p;
	}
	// Permite cambiar el predicado que niega.	
	public void setPredicado(Predicado p){
		this.predicado= p;
	}
	// Devuelve true si Not niega a predicado.
	public boolean niega(Predicado predicado){
	
            if ((predicado != null) && (this.predicado != null)){
                
                if(this.predicado.equals(predicado)){
                    	return true;
                }
                else{
                    return false;
                }
            }else{
                    return ((predicado == null) && (this.predicado == null));
            }
            
        }
	
	public Predicado obtenerPredicadoQueNiega(){
		return predicado;
	}
	
	public boolean equals(Predicado predicado) {	 		
		if(predicado instanceof Not)
			return this.niega(((Not) predicado).obtenerPredicadoQueNiega());
		return false;
	}							
								
	public Vector<Predicado> obtenerPredicados() {
		Vector<Predicado> vector_aux = new Vector<Predicado>();
		vector_aux.add(predicado);
		return vector_aux;
	}
	@Override
    public String toString(){            
        String envio= new String(obtenerNombre()+"(");        
        if (this.predicado != null){
            envio= envio + obtenerPredicadoQueNiega().toString();
            }
        envio = envio + ")";
        return envio;
	}

    public String toStringProlog(){
        String envio= new String(obtenerNombre().toLowerCase());
        if (this.predicado != null){
            envio= envio + obtenerPredicadoQueNiega().toStringProlog();
        }
        return envio;
    }

    public DefaultMutableTreeNode Obtener_Nodo(){

            DefaultMutableTreeNode NodoRaiz= new DefaultMutableTreeNode(this);
            if (predicado!=null){
                NodoRaiz.add(predicado.Obtener_Nodo());
             }
            return NodoRaiz;
        }


    public Object getRoot() {
        return this;
    }

    public Object getChild(Object parent, int index) {
        return predicado;
    }

    public int getChildCount(Object parent) {
        return 1;
    }

    public boolean isLeaf(Object node) {
        return false;
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    public int getIndexOfChild(Object parent, Object child) {
        return 0;
    }

    public void addTreeModelListener(TreeModelListener l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeTreeModelListener(TreeModelListener l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    //MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
    public Not(){
            super("Not");
        }
    /*MM*/
        public Not (String nombre){
            super("Not");
        }
        /*MM*/
        public Predicado Obtener_Predicado(){
            return predicado;
	}
        /*MM*/
        public void Cambiar_Predicado(Predicado p){
		this.predicado= p;
	}
     public boolean AgregarPredicado(Vector args) {
            this.agregarElemento(args.firstElement(), null);
            return true;
        }
        /*MM*/
        public void agregarElemento(Object e, Object c) {
            if (e!=null){
                predicado=(Predicado)e;
               }else{
  /*vv*/                super.nombre="Not";
                    }
    }
         @Override
        public void AgregarArgumentos(String arg) {

            Predicado P = null;

            String aux = arg.trim();
            arg = aux;

            int pos1Parentesis= arg.indexOf("(");
            int pos2Parentesis= 0;

            if(pos1Parentesis < 0){ 
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
              this.predicado = P; 
           }    

        }/*Fin AgregarArgumentos*/
    @Override
    public Vector Obtener_instancias() {
       throw new UnsupportedOperationException("Not supported yet.");
    }

        @Override
    public String Obtener_Nombre_y_Argumentos() {
        
        return (super.Obtener_Nombre_y_Argumentos().concat("(" + predicado.Obtener_Nombre_y_Argumentos() + ")"));
       
    }
        
        @Override
    public String Obtener_Nombre(){
        return Obtener_Nombre_y_Argumentos();
       
    }
        
            
    public void borrarElemento(Object e, Object c){
        if (e!=null){
            this.predicado = null;
        }
    }
    
    //MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
    
    
}
