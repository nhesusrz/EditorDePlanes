package planificador;

import editorDePlanes.Accion;
import editorDePlanes.Plan;
import editorDePlanes.Predicado;
import interfaceEditordePlanes.InterfaceEditordePlanesView;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author mpacheco
 */

public class CodificadorProgresion extends Codificador{

    Hashtable javaProlog;

    public CodificadorProgresion(Plan plan, InterfaceEditordePlanesView itv) {
        super(plan, itv);
        javaProlog= new Hashtable();
    }
    /*
     * Este metodo volcara el plan actual en un archivo prolog(.pl) para luego
     * ser ejecutado.
     */    
    public boolean codificar() {
        itv.mostrarMensaje("Codificando.");
        Vector<Accion> acciones;
        if(plan.obtenerEstInicial()!= null){
            if(plan.obtenerEstFinal() != null){
                acciones= itv.getLibreriaDeTrabajo().obtenerAcciones();
                if(acciones.size() != 0){
                    String temp= "% Permite ver si se puede o no usar la accion en base a sus precondiciones y el estado actual del plan.\r\n\r\n";
                    for(Enumeration<Accion> e= acciones.elements();e.hasMoreElements();){
                        Accion a= e.nextElement();
                        temp= temp + codificarAcciones(a);
                        temp= temp + "\r\n";                        
                    }
                    temp= temp + "\r\n% Agrega sus postcondiciones en el estado actual del plan.\r\n\r\n";
                    for(Enumeration<Accion> e= acciones.elements();e.hasMoreElements();){
                        Accion a= e.nextElement();
                        temp= temp + codificarUsos(a);
                        temp= temp + "\r\n";
                    }
                    temp= temp + codificarAuxiliares();
                    GuardarRecuperarEnPL.writeObject(temp);
                    itv.mostrarMensaje("Plan codificado.");
                    return true;
                }
                else
                    itv.mostrarMensaje("El plan no tiene acciones.");
            }
            else
                itv.mostrarMensaje("No se definio el estado final.");
        }
        else
            itv.mostrarMensaje("No se definio el estado inicial.");
      acciones= null;
      return false;
    }

    public String codificarConsulta(){
        String texto= "planear(";
        texto= texto + generarListaPredicados(plan.obtenerEstInicial().Obtener_Post_Condiciones());
        texto= texto + "," + generarListaPredicados(plan.obtenerEstFinal().Obtener_Pre_Condiciones());
        texto= texto + "," + generarListaAcciones(itv.getLibreriaDeTrabajo().obtenerAcciones());
        texto= texto + ",5,A)";
        return texto;
    }

    public Hashtable obtenerTablaSimbolos(){
        return javaProlog;
    }
    /*
     * Genera el string de la lista con las acciones para la consulta. Ademas
     * llena la tabla de simbolos.
     */
    private String generarListaAcciones(Vector<Accion> acciones){
        String texto= "[";
        Accion a;
        for(int i=0;i < acciones.size() - 1;i++){
            a= acciones.elementAt(i);
            texto= texto + a.obtenerNombre().toLowerCase().replace(" ", "") + ",";
            javaProlog.put(a.obtenerNombre().toLowerCase().replace(" ", ""), a);
        }
        a= acciones.elementAt(acciones.size() - 1);
        texto= texto + a.obtenerNombre().toLowerCase().replace(" ", "") + "]";
        javaProlog.put(a.obtenerNombre().toLowerCase().replace(" ", ""), a);
        return texto;
    }

    private String generarListaPredicados(Vector<Predicado> predicados){
        String texto= "[";
        Predicado p;
        for(int i=0;i < predicados.size() - 1;i++){
            p= predicados.elementAt(i);
            texto= texto + p.toStringProlog()+",";
        }
        p= predicados.elementAt(predicados.size() - 1);
        texto= texto + p.toStringProlog()+"]";
        return texto;
    }

    private String codificarAcciones(Accion a){
        String nombre= a.obtenerNombre().toLowerCase().replace(" ", "");        
        String texto= "accion("+nombre+",E):- ";
        Vector<Predicado> predicados= a.Obtener_Pre_Condiciones();
        for(Enumeration<Predicado> e= predicados.elements();e.hasMoreElements();){
            Predicado p= e.nextElement();
            texto= texto + "member("+p.toStringProlog()+",E),";
        }
        String texto2= texto.substring(0,texto.length()-1);
        texto=texto2.concat(".");
        return texto;
    }

    private String codificarUsos(Accion a){
        String nombre= a.Obtener_Nombre().toLowerCase().replace(" ", "");        
        String texto= "aplicar("+nombre+",EA,ES):- ";
        Vector<Predicado> postcondiciones= a.Obtener_Post_Condiciones();                
        Predicado p;
        if(postcondiciones.size() == 1){            
            p= postcondiciones.elementAt(0);
            texto= texto + "insertar("+p.toStringProlog()+",EA,ES).";
        }
        else{
            int ind= 0;
            p= postcondiciones.elementAt(0);
            texto= texto + "insertar("+p.toStringProlog()+",EA,E0), ";
            for(int i= 1; i < postcondiciones.size()-1; i++){
                p= postcondiciones.elementAt(i);
                texto= texto + "insertar("+p.toStringProlog()+",E"+ind+",E"+(ind+1)+"), ";            
                ind++;
            }         
            p= postcondiciones.elementAt(postcondiciones.size()-1);
            texto= texto + "insertar("+p.toStringProlog()+",E"+ind+",ES).";
        }           
        return texto;       
    }

    private String codificarAuxiliares(){
        String texto= "\r\n% Predicados auxiliares y el predicado pricipal planear.\r\n\r\n";
        texto= texto + "pertenece([],_).\r\npertenece([C|L],L2):- member(C,L2), pertenece(L,L2).\r\ndel(H,[H|T],T).\r\ndel(H,[Y|T],[Y|T1]):- del(H,T,T1).\r\ninsertar(X,L,L1):- del(X,L1,L).\r\ninv([],[]).\r\ninv([H|T],L):- inv(T,Z), append(Z,[H],L).\r\nplanear(EI,EF,_,_,[]):- pertenece(EF,EI).\r\nplanear(_,_,_,0,_):- !,fail.\r\nplanear(_,_,[],_,[]):- !,fail.\r\nplanear(EI,EF,[A|LA],CONT,[A|SOL]):- accion(A,EI), aplicar(A,EI,EN), planear(EN,EF,LA,CONT,SOL).\r\nplanear(EI,EF,LA,CONT,SOL):- !, inv(LA,L), CONT1 is CONT - 1, planear(EI,EF,L,CONT1,SOL).\r\n";
        return texto;
    }
}
