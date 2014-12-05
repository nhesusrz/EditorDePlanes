package editorDePlanes;

import java.util.ArrayList;
import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

/**
 *
 * @author mpacheco
 */

public abstract class Predicado implements TreeModel, NodoElemento{
	
	protected String nombre;

    public Predicado(){}

	public Predicado(String nombre) {
		this.nombre= nombre.toUpperCase();		
	}
	// Devuelve el nombre del predicado.
	public String obtenerNombre(){
		return nombre;
	}
	// Modifica el nombre del predicado.
	public void cambiarNombre(String nombre){
		this.nombre= nombre;
	}
	
	public abstract boolean equals(Predicado p);
	// Metodo que devuelve los predicados que contiene un predicado, en caso de ser unitario se retorna asi mismo.
	public abstract Vector<Predicado> obtenerPredicados();
	// Metodo que retorna si el predicado niega, el unico que puede devolver true se Not el resto devuelven false. 
	public abstract boolean niega(Predicado p);

         public abstract DefaultMutableTreeNode Obtener_Nodo();
         
         /*MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM*/
         public abstract void AgregarArgumentos(String arg);
         public abstract boolean AgregarPredicado(Vector args);
         public abstract Vector Obtener_instancias();   
        /*MM*/
        public String Obtener_Nombre_y_Argumentos(){
            return nombre.trim();
        }
        public void Cambiar_Nombre(String nombre){
		this.nombre= nombre;
	}
        protected int posicionParentesisFinDeArgumentos(int posComienzoDeParentesis, String args){
            
            int indiceParentesis= posComienzoDeParentesis;
            int cantidadParentesis= 0;
            int indiceParentesisCierre= -1;
            
            int i= indiceParentesis;
            boolean T= false;
            
            while ((i < args.length()) && (!T)){
                
                String auxP= args.substring(i, i+1);
                
                if (auxP.equalsIgnoreCase("(")){
                    cantidadParentesis ++;
                }
                else{
                    if (auxP.equalsIgnoreCase(")")){
                        cantidadParentesis --;
                    }
                }/*else*/
                
                if (cantidadParentesis == 0){
                    indiceParentesisCierre= i;
                    T=true;
                }else{
                    if (cantidadParentesis < 0){
                        T=true;
                        indiceParentesisCierre= -2;
                    }
                }/*else*/
            
            i++;
            }/*while*/
            
            if (cantidadParentesis > 0){
                    indiceParentesisCierre= -3;
                }
            
            return indiceParentesisCierre;
        }/*posicionParentesisFinDeArgumentos*/
        
//Este metodo le permite e los predicados agregar elemntos que son pasados mediane una cadena de caracteres Stirng.
//Cada elento (predicado) sabe obtener sus propios argumentos. 
//Retorna un Arreglo de argumentos que se corresponderan con instancias.  Ej: ArrayList AreggloX= P.obtenerArgumentos(argumentos)
//quedaria AreggloX= {A:hola, mover(a,b)}
//o sea un ArrayList que tiene dos elentos A:hola y mover(a,b)        
        /*MM*/
        protected ArrayList obtenerArgumentos(String args){
            //En caso de ser mas argumentos se redimensiona solo
            ArrayList argumentos= new ArrayList(5);
     
            args= args.concat(",");
            int parentesisCierre= 0;
            int cantidadComas= 0;
            int ini=0;
            
            for (int i=0; i< args.length();i++){
                String elemento= args.substring(i, i+1);
                if (elemento.equalsIgnoreCase(",")){
                    cantidadComas ++;
                    if (parentesisCierre == 0){
                        argumentos.add(args.substring(ini, i).trim());
                        ini=i+1;
                    }
                }else{
                    if (elemento.equalsIgnoreCase("(")){
                        parentesisCierre ++;
                    }else{
                        if (elemento.equalsIgnoreCase(")")){
                            parentesisCierre --;
                            }
                        }/*else*/
                    }/*else*/
            }/*for*/
          
            return argumentos;
        }/*obtenerArgumentos*/


         
         
         
         /*MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM*/

  public abstract String toStringProlog();

}
