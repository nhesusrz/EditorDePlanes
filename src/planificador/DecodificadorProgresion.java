package planificador;

import editorDePlanes.Accion;
import editorDePlanes.Amenaza;
import editorDePlanes.Plan;
import interfaceEditordePlanes.InterfaceEditordePlanesView;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author mpacheco
 */
public class DecodificadorProgresion extends Decodificador{

    Hashtable javaProlog;

    public DecodificadorProgresion(Plan plan, InterfaceEditordePlanesView itv, Hashtable javaProlog){
        super(plan,itv);
        this.javaProlog= javaProlog;
    }

    public boolean decodificar(String resultado){
        if(resultado == null)
            return false;
        Plan pnuevo= new Plan(itv.getAreaDeTextoPrincipal());
        Amenaza validar= new Amenaza(pnuevo, itv.getAreaDeTextoPrincipal());
        pnuevo.definirPoliticaValidacion(validar);
        Accion a= plan.getEstado_inicial();
        a.anularLinkOrYDst();
        pnuevo.definirEstadoInicial(a);
        a= plan.getEstado_final();
        a.anularLinkOrYDst();
        pnuevo.definirEstadoFinal(a);
        pnuevo.agregarNivel();
        Vector<String> aprolg= procesarString(resultado);
        for(int i=0; i < aprolg.size();i++){
            a= (Accion)javaProlog.get(aprolg.elementAt(i));
            a.anularLinkOrYDst();            
            a.modificarNivel(pnuevo.getCant_niveles());
            if((pnuevo.validarConPolitica(a))||(pnuevo.gestionaLinkPre(a))){
                pnuevo.agregarNivel();            
                a.modificarNivel(pnuevo.getCant_niveles());
                pnuevo.agregarAccionSinAmenaza(a);
            }
            else
                pnuevo.agregarAccionSinAmenaza(a);
        }
        pnuevo.ramificar();
        itv.aplicarSolucionAlgoritmica(pnuevo);
        return true;
    }

    /* Procesa el string envia por el algoritmo que contiene el resultado prolog. */

    private Vector<String> procesarString(String proc){
        //accion2 accion5 accion6 accion4 accion1
        Vector<String> retorno= new Vector<String>();
        proc= proc.replace("'.'", "");
        proc= proc.replace("(", "");
        proc= proc.replace("[]", "");
        proc= proc.replace(")", "");
        proc= proc.replace(",", "");
        proc= proc.concat("END.");
        String key= " ";
        while(key != null){
            key= obtenerNombre(proc);
            if(key.equals("END"))
                key= null;
            else{
                retorno.addElement(key);
                proc= proc.replace(key+" ", "");
            }
        }        
        return retorno;
    }

    private String obtenerNombre(String s){
        int ind= 0;
        String retorno= new String();
        while((s.charAt(ind) != ' ')&&(s.charAt(ind) != '.')){
            retorno= retorno + s.charAt(ind);
            ind++;
        }
        return retorno;
    }
}
