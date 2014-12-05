package planificador;

import editorDePlanes.Plan;
import interfaceEditordePlanes.InterfaceEditordePlanesView;
import jpl.Query;
import java.util.Hashtable;

/**
 *
 * @author mpacheco
 */
public class Progresion extends Algoritmo{

    public Progresion(Plan plan, InterfaceEditordePlanesView itv){
        super(plan, itv);
    }

    public void correr(){
        codificador= new CodificadorProgresion(plan, itv);
        if(codificador.codificar()){
            String res= obtenerResultado();
            if(res != null){
                itv.mostrarMensaje("Decodificando.");
                Decodificador d= new DecodificadorProgresion(plan, itv, ((CodificadorProgresion)codificador).obtenerTablaSimbolos());
                d.decodificar(res);
                itv.mostrarMensaje("Solución encontrada.");
            }
            else
                itv.mostrarMensaje("El conjunto de acciones no ofrecen solución.");
        }
        else
            itv.mostrarMensaje("No se logro codificar el plan.");
    }

    private String obtenerResultado(){
        String pl = "consult('./planes/tempPL.pl')";
		Query q = new Query(pl);
        if(q.hasSolution()){
            String t= ((CodificadorProgresion)codificador).codificarConsulta();
            System.out.print(t);
            q= new Query(t);
            if(q.hasSolution()){
                Hashtable hst= q.oneSolution();
                return hst.get("A").toString();
            }
        }
        return null;
    }
}
