package editorDePlanes;

import java.util.Vector;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author mpacheco
 */

/**
 * Aclaraci�n: Se entiende que cuando una variable no esta intanciada(valor == null) se toma como cte.
 */

public class Variable extends Predicado {

	private String valor = new String("");
	
	public Variable(String nombre_cte) {
		super(nombre_cte);	
		this.valor = nombre_cte.toLowerCase(); // Se vuelve a dejar en minusculas ya que se definio como cte.		
	}
	
	public Variable(String nombre_variable, String valor) {
		super(nombre_variable);
		this.valor= valor.toLowerCase();
	}
	
	public String obtenerValor(){
		return valor;
	}
	
	public boolean esCte(){
		return valor == null;
	}
	
	public void cambiarValor(String valor){
		this.valor = valor.toLowerCase();
	}
	
	public boolean equals(Predicado predicado) {
		// Puede darse el caso de que una tenga el mismo nombre que un compuesto.
		if(predicado.obtenerNombre().equals(this.obtenerNombre()))
			if((predicado instanceof Variable)){
				// Tengo cuatro casos, p cte yo cte, p cte yo !cte(o sea variable),p !cte yo cte y p !cte yo !cte.
				if(((Variable)predicado).esCte() && this.esCte())
					return(predicado.obtenerNombre().equals(this.obtenerNombre()));
				if(((Variable)predicado).esCte() && !this.esCte())
					return(predicado.obtenerNombre().equals(this.obtenerValor()));
				if(!(((Variable)predicado).esCte()) && this.esCte())
					return(((Variable)predicado).obtenerValor().equals(this.obtenerNombre()));
				if(!(((Variable)predicado).esCte()) && !this.esCte())
					return(((Variable)predicado).obtenerValor().equals(this.obtenerValor()));
			}
		return false;
	}
	
	public Vector<Predicado> obtenerPredicados(){
		Vector<Predicado> vector_aux = new Vector<Predicado>();
		vector_aux.add(this);
		return vector_aux;
	}
	
	public boolean niega(Predicado p){
		return false;
	}
	
	public String imprimir(){
		if(esCte())
			return(obtenerNombre());
		return(obtenerNombre() + "(" + obtenerValor() + ")");
	}

    @Override
    public String toString(){
		return this.Obtener_Nombre_y_Argumentos();                
	}

    public String toStringProlog(){
        return nombre.toLowerCase()+valor;
    }

    public DefaultMutableTreeNode Obtener_Nodo(){
        DefaultMutableTreeNode NodoRaiz= new DefaultMutableTreeNode(this);
        DefaultMutableTreeNode NodoHoja= new DefaultMutableTreeNode(valor);
        NodoRaiz.add(NodoHoja);
        return NodoRaiz;
    }

    public Object getRoot() {
        return this;
    }

    public Object getChild(Object parent, int index) {
        return valor;
    }

    public int getChildCount(Object parent) {
        return 1;
    }

    public boolean isLeaf(Object node) {
        if (valor==null) {return true;}
        else return false;
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    public int getIndexOfChild(Object parent, Object child) {
        if (child.toString().equals(valor)) {return 0;}
        else return -1;
    }

    public void addTreeModelListener(TreeModelListener l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeTreeModelListener(TreeModelListener l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    //MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
     /*MM*/
        /*MM*/
        public void agregarElemento(Object e, Object c) {
            if (e!=null){
                valor= (String) e;
            }else{
                  valor=nombre.toLowerCase();
                 }
        }
                
    @Override
    /*MM*/
    public void AgregarArgumentos(String arg) {

        this.Cambiar_Valor(arg.toLowerCase().trim());
  
       }

    @Override
    /*MM*/
    public Vector Obtener_instancias() {
        Vector instancia= new Vector();
        instancia.add(this.valor);
        
        return instancia;
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    
  
    @Override
    /*MM*/
    public String Obtener_Nombre_y_Argumentos() {
        return super.Obtener_Nombre_y_Argumentos().concat(": " + valor);
        
    }

    /*MM*/
    public void Cambiar_Nombre(String nombre, String v){
		this.nombre= nombre;
	}
    
    @Override
    /*MM*/
    public String Obtener_Nombre(){
   //     return Obtener_Nombre_y_Argumentos();
        return this.nombre;
    }
    /*MM*/
    public void borrarElemento(Object e, Object c){
        this.valor = nombre.toLowerCase();
    }
    /*MM*/
    public void Cambiar_Valor(String valor){
		this.valor= valor;
    }

    @Override
    public boolean AgregarPredicado(Vector args) {boolean agregoOk=false;
        /*  if (args.length > 0) {
                agregoOk=true;
                this.valor= args[0].Obtener_Nombre().toLowerCase();
            }
        */
        return agregoOk;

       }
    
    //MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM

}
