package editorDePlanes;

import com.thoughtworks.xstream.*;
import java.util.Vector;
import java.util.Iterator;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author mpacheco
 */

public class Libreria implements NodoElemento {
    
    /**        Variables de Instancia      */
    private Vector acciones=null;
    private String nombre=null;
    
    /** Creates a new instance of Libreria */
    public Libreria(Vector Acciones, String Nombre) {
        acciones= Acciones;    
        nombre= Nombre;
    }
    public Libreria (String nombre){
        this.nombre=nombre;
        this.acciones = new Vector(0);
    }
        
    /**         Metodos de la Clase        */
    public String obtenerNombre(){
        return nombre;
    }

    // ver este equals que pase prueba de calidad...
    public boolean equals(String nombreX){
        return (nombre.equalsIgnoreCase(nombreX));
    }
    
     
    public void guardar(){
        XStream traductor= new XStream();
        traductor.alias("Lib1_PRUEBA_UNO",Libreria.class);         
        String ArchivoXML2= traductor.toXML(this);
        System.out.println("El archivo queda con alias:");
        System.out.println("El archivo queda como:" + ArchivoXML2);
        traductor.setMode(XStream.ID_REFERENCES);
        String ArchivoXML5= traductor.toXML(this);
        System.out.println("El archivo queda con XStream.ID_REFERENCES");
        System.out.println("El archivo queda como:" + ArchivoXML5);
    }

    public boolean AgregarAccion(Accion A){
        boolean EstadoDeOperacion= false;
        if (A!=null){
            acciones.add(A);
            EstadoDeOperacion= true;
        }
        return EstadoDeOperacion;
    }
    
    public boolean AgregarAcciones(Vector ListaAcciones){
        boolean estadoDeOperacion= false;
        boolean resultado= false;
        if (ListaAcciones!=null){
            resultado= acciones.addAll(ListaAcciones);
            estadoDeOperacion= true;
        }
        return (resultado && estadoDeOperacion);
    }
    
    public boolean BorrarAccion(Accion A){
        boolean encontrada= false;
        for (Iterator it= acciones.iterator(); (it.hasNext() && !(encontrada));){
            Accion AccionBuscada= (Accion)it.next();
            if (A.equals(AccionBuscada)){
                encontrada = true;
                it.remove();
            }
        }
        return encontrada;
    }
    
    public Vector obtenerAcciones(){
       return acciones;
    }
    
    public DefaultMutableTreeNode Obtener_Nodo(){
        DefaultMutableTreeNode NodoRaiz= new DefaultMutableTreeNode(this);
        for(Iterator it = acciones.iterator(); it.hasNext(); ) {
            		Accion AccionN = (Accion) it.next();
			NodoRaiz.add(AccionN.Obtener_Nodo());
         }
        return NodoRaiz;
    }
    @Override
    public String toString() {
        return obtenerNombre();
    }

    public String Obtener_Nombre() {
        return nombre;
    }

    public void agregarElemento(Object e, Object c) {
        if (e!=null){
            boolean resultado = this.AgregarAccion((Accion) e);
           }
    }
    
   public void borrarElemento(Object e, Object c){
       if (e!=null){
            this.BorrarAccion((Accion)e);
           }
   } 
    
   public String Obtener_Nombre_y_Argumentos(){
       return nombre;
   }

   public void cambiarNombre(String n) {
      this.nombre = n;
    }
    
    public void clean(){
        acciones.removeAllElements();
    }
}
