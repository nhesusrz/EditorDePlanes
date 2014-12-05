package editorDePlanes;

import java.util.ArrayList;
import java.util.Vector;

/**
 *
 * @author mpacheco
 */

public class Portero {
    private int posActual=-1;
    private ArrayList<Recuerdo> estadosSalvados = new ArrayList<Recuerdo>();

   public void addMemento(Recuerdo m) {
       incrementarPosActual();
       estadosSalvados.add(posActual, m);
       //imprimir();
       //estadosSalvados.add(m);
       if(estadosSalvados.size()>posActual+1)
        borrarConsecuentes(posActual+1);
   }

   public void borrarConsecuentes(int pos){
       while((pos>=0)&&(pos<estadosSalvados.size())&&(estadosSalvados.get(pos)!=null))
           estadosSalvados.remove(pos);
   }

   public Recuerdo getMemento(int index) { return estadosSalvados.get(index); }

    public ArrayList<Recuerdo> getEstadosSalvados() {
        return estadosSalvados;
    }

    public void setEstadosSalvados(ArrayList<Recuerdo> estadosSalvados) {
        this.estadosSalvados = estadosSalvados;
    }

    public int getPosActual() {
        return posActual;
    }

    public void setPosActual(int posActual) {
        this.posActual = posActual;
    }

    public void incrementarPosActual(){
       // if(getPosActual()<estadosSalvados.size())
            posActual++;

    }

    public void decrementarPosActual(){
       // if(getPosActual()>0)
            posActual--;
    }

    public void clean(){
        posActual=-1;
        for (int i=0;i<estadosSalvados.size();i++)
            estadosSalvados.clear();
    }

    public void imprimir() {
        Vector vec=null;
        int j=0;
        System.out.println("==========================================");
        for (;j<estadosSalvados.size();j++){
            System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            System.out.println("Acciones del estado salvado ["+j+"] :");
            vec=((Plan) estadosSalvados.get(j).getSavedState()).obtenerAcciones();
            for (int i=0;i<vec.size();i++)
                System.out.println(((Accion) vec.elementAt(i)).toString());
            System.out.println("cantidad de acciones: "+vec.size());
            System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        }
        
        System.out.println("ESTADOS SALVADOS: "+j);
        
        //for (int i=0;i<vec.size();i++)
         //   System.out.println(((Accion) vec.elementAt(i)).imprimir());
        System.out.println("==========================================");
    }

    public void imprimirPos(int pos) {
        Vector vec=null;
        vec=((Plan) estadosSalvados.get(pos).getSavedState()).obtenerAcciones();
        System.out.println("cantidad de acciones: "+vec.size());
        for (int i=0;i<vec.size();i++)
            System.out.println(((Accion) vec.elementAt(i)).toString());
    }

}
