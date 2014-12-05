package editorDePlanes;

import javax.swing.tree.DefaultMutableTreeNode;
/**
 *
 * @author mpacheco
 */
public interface NodoElemento {

/**
 *
 * @author mpacheco
 */
//Esta interface es implementada por aquellos elementos que forman parte de un Arbol de Libreria

    //Retorna el nombre de un elemento del arbol de Librerias.
    public String Obtener_Nombre();
    //Agrega elementos a un Arbol de Librerias, estos elementos pueden ser 
    //Acciones, Pre y Pos condiciones de todo tipo (Variable, instanciar Variable, Predicados Compuestos, Not de prediacdos.
    public void agregarElemento(Object e, Object c);
    //Dado un elemento de Libreria retorna el nodo contenido por el y todo su subArbol que lo contiene como raiz.
    public DefaultMutableTreeNode Obtener_Nodo();
    //Borra elementos de Libreria del Arbol que los contiene.
    public void borrarElemento(Object e, Object c);
    //Retorna el Nombre juntos a los argumentos que contiene el elemento por ejemplo: "mover ( A:hola, B:mundo)
    //donde mover es un predicado compuesto con dos variable A y B instanciadas en Hola y Mundo respectivamente.
    public String Obtener_Nombre_y_Argumentos();
      
}
