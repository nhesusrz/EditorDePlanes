/**
 *
 * @author mpacheco
 */

/*
 * El proposito de esta clase es implementar el algoritmo para validar un plan completo
 * pasado por parámetro.
 */

package editorDePlanes;

import java.util.Iterator;
import java.util.Vector;
import javax.swing.JTextArea;

public class PlanValidador {

    private Plan plan_test;    
    private Mensaje mensaje;
    private JTextArea areaDeTexto;

    public PlanValidador(JTextArea areaDeTexto){
        plan_test= null;
        this.areaDeTexto= areaDeTexto;
        mensaje= new Mensaje(areaDeTexto);
    }
    /* Este metodo es quien implementa el algoritmo.
     * Crea un plan desde cero y empieza a constuirlo en base al pasado por parametro.
     * Falla cuando encuentra la primera amenaza o alguna otra posible inconsistencia.
     * Esta es comunicada a travez de la ventana de logs.
     * Cuando pudo crear el plan satisfactoriamente devuelve true.
     */
    public boolean validarPlan(Plan plan_a_validar){
        boolean boolea= true;
        JTextArea areaTemporal= new JTextArea();
        plan_test= new Plan(areaTemporal);
        Accion est_inicial= plan_a_validar.obtenerEstInicial();
        Accion est_final= plan_a_validar.obtenerEstFinal();
        if(est_inicial != null)
            plan_test.definirEstadoInicial(est_inicial);
        if(est_final != null)
            plan_test.definirEstadoFinal(est_final);
        for(Integer i= 0; i <= plan_a_validar.cantidadNivelesAcciones();i++)
            plan_test.agregarNivel();
        Vector<Accion> acciones= plan_a_validar.obtenerAcciones();
        for(Iterator<Accion> it= acciones.iterator();it.hasNext();){
            Accion a= it.next();
            if(!plan_test.validarConPolitica(a))
                plan_test.agregarAccionSinAmenaza(a);
            else{
                mensaje.validacionPlanNoValido();
                boolea= false;
            }
        }
        Vector<Link> links= plan_a_validar.obtenerLinks();        
        for(Iterator<Link> it= links.iterator();it.hasNext();)
            if(!plan_test.agregarLink(it.next())){                
                mensaje.validacionPlanNoValido();
                boolea= false;
            }
        for(Iterator<Link> it= links.iterator();it.hasNext();)
            plan_test.removerLink2(it.next());
        if(boolea)
            mensaje.validacionPlanValido();
        return boolea;
    }

}
