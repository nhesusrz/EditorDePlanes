package visualPlan;

import editorDePlanes.Accion;
import editorDePlanes.Link;
import editorDePlanes.Plan;
import editorDePlanes.Predicado;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.geom.Rectangle2D;

import java.awt.geom.Point2D;
import javax.swing.BorderFactory;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import java.util.*;

/**
 *
 * @author mpacheco
 */

public class Mostrar {

private     JGraph graph;
private     DefaultGraphCell  NodoInicio;
private     DefaultGraphCell NodoFin;
private     Hashtable accionNodo;
private     Hashtable puertoPredicado;
private     Hashtable puertoNodo;
private     Hashtable nodoAccion;
private     Hashtable arcosLinks;
private     Hashtable linksArcos;
private     Hashtable arcosDeAccion;
private     Vector Aristas;
private     Plan plan;
private     Hashtable predicadoPuerto;
private     DefaultGraphCell [] cells;
private     DefaultGraphCell [] grafo;
private     DefaultGraphCell [] niveles;
private     DefaultGraphCell [] AristasNiveles;
private     Object [] roots;
private     Font fontNodo;
private     Font fontLink;

public  Mostrar (Plan plan, JGraph graph_principal){
    this.plan= plan;
    graph = graph_principal;
    accionNodo= new Hashtable();
    puertoPredicado= new Hashtable();
    puertoNodo= new Hashtable();
    predicadoPuerto= new Hashtable();
    nodoAccion= new Hashtable();
    arcosLinks= new Hashtable();
    linksArcos= new Hashtable();
    arcosDeAccion= new Hashtable();
    Aristas= new Vector();
    fontNodo= new Font("Nodo", Font.BOLD, 10);
    fontLink= new Font("Link", Font.ROMAN_BASELINE, 9);
}
// Este metodo borra lo que hay en el grafo de acuerdo al parametro y siempre vuelve a redibujar el plan.
public void reDibujar(){
    if(plan!=null){
        String no_def= new String(" NO DEFINIDO ");
        Accion AccionInicial= plan.obtenerEstInicial();
        if(AccionInicial != null)
            NodoInicio = new DefaultGraphCell(AccionInicial);
        else{
            AccionInicial= new Accion(no_def, -1);
            NodoInicio = new DefaultGraphCell(AccionInicial);
        }
        // Propiedades del Estado Inicial.
        /* Permite setearle un icono al estado inicial.
        URL connectUrl;
     	connectUrl = getClass().getClassLoader().getResource("interfaceEditordePlanes/resources/connection.png");
        ImageIcon connectIcon = new ImageIcon(connectUrl);
        GraphConstants.setIcon(NodoInicio.getAttributes(), connectIcon);
        */
        GraphConstants.setBounds( NodoInicio.getAttributes(),new Rectangle2D.Double(150,30,150,30));
        GraphConstants.setBackground(NodoInicio.getAttributes(), Color.blue);
        GraphConstants.setGradientColor(NodoInicio.getAttributes(),Color.blue);
        GraphConstants.setBorder(NodoInicio.getAttributes(), BorderFactory.createRaisedBevelBorder());
        GraphConstants.setBorder(NodoInicio.getAttributes(), BorderFactory.createLineBorder(Color.blue, 2));
        GraphConstants.setOpaque(NodoInicio.getAttributes(), true);
        GraphConstants.setAutoSize(NodoInicio.getAttributes(), true);
        GraphConstants.setSelectable(NodoInicio.getAttributes(), true);
        GraphConstants.setEditable(NodoInicio.getAttributes(), false);
        GraphConstants.setFont(NodoInicio.getAttributes(), fontNodo);
        GraphConstants.setForeground(NodoInicio.getAttributes(), Color.white);
        this.accionNodo.put(AccionInicial, NodoInicio);
        this.nodoAccion.put(NodoInicio,AccionInicial);
        Accion AccionFinal=plan.obtenerEstFinal();
        if(AccionFinal!=null)
            NodoFin = new DefaultGraphCell(AccionFinal);
        else{
            AccionFinal= new Accion(no_def, plan.cantNivelesJGraph()+1);
             NodoFin = new DefaultGraphCell(AccionFinal);
        }
        // Propiedades del Estado Final.
        GraphConstants.setBounds( NodoFin.getAttributes(), new Rectangle2D.Double(150,30,150,30));
        GraphConstants.setBackground(NodoFin.getAttributes(), Color.blue);
        GraphConstants.setGradientColor(NodoFin.getAttributes(), Color.blue);
        GraphConstants.setBorder(NodoFin.getAttributes(), BorderFactory.createRaisedBevelBorder());
        GraphConstants.setBorder(NodoFin.getAttributes(), BorderFactory.createLineBorder(Color.blue, 2));
        GraphConstants.setOpaque(NodoFin.getAttributes(), true);
        GraphConstants.setAutoSize(NodoFin.getAttributes(), true);
        GraphConstants.setSelectable(NodoFin.getAttributes(), true);
        GraphConstants.setEditable(NodoFin.getAttributes(), false);
        GraphConstants.setForeground(NodoFin.getAttributes(), Color.white);
        GraphConstants.setFont(NodoFin.getAttributes(), fontNodo);
        this.accionNodo.put(AccionFinal, NodoFin);
        this.nodoAccion.put(NodoFin,AccionFinal);
        // Tengo los estados y todas las acciones del plan.
        cells = new DefaultGraphCell[plan.cantAcciones()+2];
        crearNodos(plan.obtenerAcciones(), cells);
        cells[cells.length-2]= NodoInicio;
        cells[cells.length-1]= NodoFin;
        //version nivelar
        //Aca iria el crearNodosNivel o sea el arbol del cual cuelgan las acciones para nivelarce
        this.crearNodosNivel();
        //Aca iria el nivelar.......o sea cuelga las acciones del arbol de niveles para que el planning quede ordenado por niveles
        this.nivelar();
        for(int i=0; i < cells.length ; i++){
            if(!AccionInicial.obtenerNombre().equals("NO DEFINIDO") && i==cells.length-2){
                crearPuertos(cells[i]);
            }
            else{
                if(!AccionFinal.obtenerNombre().equals("NO DEFINIDO") && i==cells.length-1)
                    crearPuertos(cells[i]);
                else
                    if(i!=cells.length-2 && i!=cells.length-1)
                        crearPuertos(cells[i]);
            }

        }
        //Antes de llamar a este ver de que los puertos para las pre y pos de la acciones empiezan en 1
        crearArco();
        this.grafo= new DefaultGraphCell [cells.length+this.Aristas.size()+ this.plan.cantNivelesJGraph()+ 2 + 1 + this.AristasNiveles.length];
        //this.grafo= new DefaultGraphCell [cells.length+this.Aristas.size()];
        for (int i=0; i<cells.length; i++)
            grafo[i]= cells[i];
        int j= cells.length;
        for (Iterator itAristas=Aristas.iterator(); itAristas.hasNext();){
            grafo[j]=(DefaultGraphCell)itAristas.next();
            j++;
        }
        int h= cells.length + this.Aristas.size();
        for (int k=0; k<this.niveles.length; k++){
            grafo[h]= this.niveles[k];
            h++;
        }
        int a=cells.length + this.Aristas.size()+ this.niveles.length;
        for (int l=0; l<this.AristasNiveles.length; l++){
            grafo[a]=this.AristasNiveles[l];
            a++;
        }
        //hasta aca tengo los elemento visuales del grafo
        // Insert the cells via the cache, so they get selected
        graph.setAutoResizeGraph(true);
        graph.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        graph.getGraphLayoutCache().insert(grafo);
        graph.setCloneable(true);
        graph.setInvokesStopCellEditing(true);
        DefaultGraphCell[] cellsRoot = new DefaultGraphCell[2];
        cellsRoot [0]= this.niveles[0];
        cellsRoot [1]= this.cells[this.cells.length-2];
        roots = cellsRoot;
        graph.setPortsVisible(!graph.isPortsVisible());
    }
}

public void destroy(){
    accionNodo.clear();
    puertoPredicado.clear();
    puertoNodo.clear();
    predicadoPuerto.clear();
    Aristas.removeAllElements();
    nodoAccion.clear();
    arcosLinks.clear();
    linksArcos.clear();
    arcosDeAccion.clear();
    grafo= null;
    cells= null;
    niveles= null;
    AristasNiveles= null;
    roots= null;
}

public  void crearNodos(Vector Acciones, DefaultGraphCell [] Nodos){
    int i=0;
    for (Iterator itAccion= Acciones.iterator(); itAccion.hasNext();){
        Accion A= (Accion)itAccion.next();
        DefaultGraphCell Nodo = new DefaultGraphCell(A);
        // Propiedades de los nodos de Acciones.
        GraphConstants.setBounds( Nodo.getAttributes(), new Rectangle2D.Double(150,30,150,30));
        GraphConstants.setGradientColor(Nodo.getAttributes(), Color.lightGray);
        GraphConstants.setBorder(Nodo.getAttributes(), BorderFactory.createRaisedBevelBorder());
        GraphConstants.setBorder(Nodo.getAttributes(), BorderFactory.createLineBorder(Color.lightGray, 1));
        GraphConstants.setOpaque(Nodo.getAttributes(), true);
        GraphConstants.setAutoSize(Nodo.getAttributes(), true);
        GraphConstants.setEditable(Nodo.getAttributes(), false);
        GraphConstants.setFont(Nodo.getAttributes(), fontNodo);
        Nodos[i] = Nodo;
        i++;
        accionNodo.put(A, Nodo);
        nodoAccion.put(Nodo, A);
    }
}

void crearPuertos(DefaultGraphCell Nodo){
    Accion A= (Accion)Nodo.getUserObject();
    Vector Pre= A.obtenerPreCondiciones();
    Vector Pos= A.obtenerPostCondiciones();
    for(Iterator itPre=Pre.iterator(); itPre.hasNext();){
        Predicado PrediPre= (Predicado)itPre.next();
        DefaultPort Ppre = new DefaultPort(PrediPre);
        Nodo.add(Ppre);
        puertoPredicado.put( Ppre, PrediPre );
        puertoNodo.put( Ppre, Nodo );
        predicadoPuerto.put( PrediPre, Ppre);
    }
    for(Iterator itPos=Pos.iterator(); itPos.hasNext();){
        Predicado PrediPos= (Predicado) itPos.next();
        DefaultPort Ppos = new DefaultPort(PrediPos);
        Nodo.add(Ppos);
    }
}

void crearArco(){
    Vector Acciones= this.plan.obtenerAcciones();
    if(plan.obtenerEstInicial()!=null)
        Acciones.add(this.plan.obtenerEstInicial());
    if(plan.obtenerEstFinal()!=null)
        Acciones.add(this.plan.obtenerEstFinal());
    // Por cada Accion
    for(Iterator itAcciones= Acciones.iterator(); itAcciones.hasNext();){
        Accion A= (Accion) itAcciones.next();
        //Obtengo el Nodo asociado a la accion
        DefaultGraphCell NodoOri= (DefaultGraphCell) this.accionNodo.get(A);
        //Obtiene todos los links donde figura como Origen
        if(A.obtenerLinksOrigen() != null){
        Vector LinkO= A.obtenerLinksOrigen();
        //Por cada links
        for (Iterator itLink= LinkO.iterator(); itLink.hasNext();){
            //Obtengo la instancia del link
            Link L= (Link) itLink.next();
            //Obtiene los puertos
            List LPort= NodoOri.getChildren();
            //Obtengo el predicado asociado al puerto
            Predicado Predi= L.obtenerPredicado();
            //Defino el arco correspondiente al link
            DefaultEdge Arco= new DefaultEdge(Predi);
            int arrow = GraphConstants.ARROW_TECHNICAL;
            // Propiedades de los Links del plan.
            //Object[] preds = {new String(Predi.toString()),new String(Predi.toString())};
            //Point2D[] labelPositions = {new Point2D.Double(GraphConstants.PERMILLE*7/8, -20), new Point2D.Double(GraphConstants.PERMILLE/8, -20)};
            //GraphConstants.setExtraLabels(Arco.getAttributes(), labelPositions);
            GraphConstants.setForeground(Arco.getAttributes(), Color.blue);
            GraphConstants.setLineEnd(Arco.getAttributes(), arrow);
            GraphConstants.setEndFill(Arco.getAttributes(), true);
            GraphConstants.setEditable(Arco.getAttributes(), false);
            GraphConstants.setLabelPosition(Arco.getAttributes(),new Point2D.Double(GraphConstants.PERMILLE/2, -6) );
            GraphConstants.setFont(Arco.getAttributes(), fontLink);
            boolean listo= false;
            //Busca en los puertos cual es el que tiene el predicado en cuestion o sea el del link
            for (Iterator PortX= LPort.iterator(); PortX.hasNext() && !listo;){
                //obengo la instancia del puerto
                DefaultPort P= (DefaultPort) PortX.next();
                //tengo que chequear que no sea el puerto 0
                String nombre = P.toString();
                String puertoDelNivel= "0";
                //String puertoDelNivel= "puertoNivel";
                if (!(nombre.equalsIgnoreCase(puertoDelNivel)))  {
                    //obtengo el predicado asociado el puerto
                    Predicado PrediX= (Predicado) P.getUserObject();
                    if (PrediX.equals(L.obtenerPredicado())){
                        Arco.setSource(NodoOri.getChildAt(NodoOri.getIndex(P)));
                        listo=true;
                    }
                }
            }
            //obtengo el destino del arco o link
            Accion AccionDestino= L.obtenerAccionDestino();
            DefaultGraphCell NodoDes= (DefaultGraphCell) this.accionNodo.get(AccionDestino);
            //Obtengo los puertos de la accion destino
            List LPortDes= NodoDes.getChildren();
            boolean termino= false;
            //Por cada puerto busco el que tiene el predicado igual al del link que los asocia
            for (Iterator PortY= LPortDes.iterator(); PortY.hasNext() && !termino;){
                DefaultPort Pdes= (DefaultPort) PortY.next();
                //tengo que chequear que no sea el puerto 0
                String nombrePuerto = Pdes.toString();
                String puertoDelNiveldeGrilla= "0";
                //String puertoDelNiveldeGrilla= "puertoNivel";
                if (!(nombrePuerto.equalsIgnoreCase(puertoDelNiveldeGrilla))){
                    Predicado PrediY= (Predicado) Pdes.getUserObject();
                    if (PrediY.equals(L.obtenerPredicado())){
                        Arco.setTarget(NodoDes.getChildAt(NodoDes.getIndex(Pdes)));
                        termino=true;
                    }
                }
            }

            if ((Arco)!= null){
                this.Aristas.add(Arco);
                arcosLinks.put(Arco, L);
                linksArcos.put(L,Arco);
            }
        }
    }}

}

public JGraph getGrafo(){
   return graph;
}
// creo la regla...
public void crearNodosNivel(){
    int cantNiveles= this.plan.cantNivelesJGraph()+ 2;
    this.niveles= new DefaultGraphCell[cantNiveles+1];
    for (int i=0; i<this.niveles.length; i++){
        //Por cada nivel se va creando un nodo del cual colgaran las acciones del nivel siguiente....
        DefaultGraphCell Nivel= new DefaultGraphCell(new String(" "+(i-2)+" "));
        GraphConstants.setBounds( Nivel.getAttributes(), new Rectangle2D.Double(50,30,50,30));
        // Propiedades de los Nodos de la regla.
        GraphConstants.setBackground(Nivel.getAttributes(), Color.LIGHT_GRAY);
        GraphConstants.setGradientColor(Nivel.getAttributes(), Color.green);
        GraphConstants.setBorder(Nivel.getAttributes(), BorderFactory.createRaisedBevelBorder());
        GraphConstants.setBorder(Nivel.getAttributes(), BorderFactory.createLineBorder(Color.green, 2));
        GraphConstants.setOpaque(Nivel.getAttributes(), true);
        GraphConstants.setAutoSize(Nivel.getAttributes(), true);
        GraphConstants.setFont(Nivel.getAttributes(),fontNodo);
        this.niveles[i]=Nivel;
        GraphConstants.setSelectable(niveles[i].getAttributes(), false);
        GraphConstants.setEditable(niveles[i].getAttributes(), false);
    }
    // Seteo el color para que no se vean los nodos de la regla que no corresponden.

    GraphConstants.setBackground(niveles[0].getAttributes(), Color.white);
    GraphConstants.setGradientColor(niveles[0].getAttributes(), Color.white);
    GraphConstants.setForeground(niveles[0].getAttributes(), Color.white);
    GraphConstants.setBorder(niveles[0].getAttributes(), BorderFactory.createLineBorder(Color.white, 0));
    GraphConstants.setOpaque(niveles[0].getAttributes(), false);

    GraphConstants.setBackground(niveles[1].getAttributes(), Color.white);
    GraphConstants.setGradientColor(niveles[1].getAttributes(), Color.white);
    GraphConstants.setForeground(niveles[1].getAttributes(), Color.white);
    GraphConstants.setBorder(niveles[1].getAttributes(), BorderFactory.createLineBorder(Color.white, 0));
    GraphConstants.setOpaque(niveles[1].getAttributes(), false);

    GraphConstants.setBackground(niveles[plan.cantNivelesJGraph()+ 2].getAttributes(), Color.white);
    GraphConstants.setGradientColor(niveles[plan.cantNivelesJGraph()+ 2].getAttributes(), Color.white);
    GraphConstants.setForeground(niveles[plan.cantNivelesJGraph()+ 2].getAttributes(), Color.white);
    GraphConstants.setBorder(niveles[plan.cantNivelesJGraph()+ 2].getAttributes(), BorderFactory.createLineBorder(Color.white, 0));
    GraphConstants.setOpaque(niveles[plan.cantNivelesJGraph()+ 2].getAttributes(), false);

    int cantAristasNiveles= (this.plan.cantAcciones()+ 2)*2 + cantNiveles - 1;
    this.AristasNiveles= new DefaultEdge[cantAristasNiveles];
    int n=0;
    for (int j=0; j<this.niveles.length-1; j++){
        DefaultEdge aristaNivel= new DefaultEdge(j-(cantNiveles-3));
        int arrow = GraphConstants.ARROW_CLASSIC;
        // Color flechas de la regla.
        GraphConstants.setLineColor(aristaNivel.getAttributes(), Color.red);
        // Para ver las lineas de la regla cambiarle el float de 0 a 1. y cambiar el color de white a red.
            GraphConstants.setForeground(aristaNivel.getAttributes(), Color.white);
            GraphConstants.setLineWidth(aristaNivel.getAttributes(), 0);
        //
        GraphConstants.setLineEnd(aristaNivel.getAttributes(), arrow);
        GraphConstants.setEndFill(aristaNivel.getAttributes(), true);
        DefaultGraphCell nodoNivelOrigen= this.niveles[j];
        DefaultGraphCell nodoNivelDestino= this.niveles[j+1];
        DefaultPort po=new DefaultPort("0");
        DefaultPort pd=new DefaultPort("0");
        nodoNivelOrigen.add(po);
        nodoNivelDestino.add(pd);
        //es cero si se respeto el orden, osea primero se creo el arbol de nivel
        aristaNivel.setSource(nodoNivelOrigen.getChildAt(nodoNivelOrigen.getIndex(po)));
        aristaNivel.setTarget(nodoNivelDestino.getChildAt(nodoNivelDestino.getIndex(pd)));
        this.AristasNiveles[n]=aristaNivel;
        n++;
        GraphConstants.setSelectable(aristaNivel.getAttributes(), false);
        GraphConstants.setEditable(aristaNivel.getAttributes(), false);
    }
}

public Hashtable getLinksArcos() {
    return linksArcos;
}

public Hashtable getArcosLinks() {
       return arcosLinks;
}

public Hashtable getNodoAccion() {
       return nodoAccion;
}
//creo los arcos entre la regla de niveles y las acciones
public void nivelar(){
    int pos;
    boolean termina = false;
    //buso la posicion del primer espacio en el arreglo para guardar un elemento
    for(pos=0; pos<this.AristasNiveles.length && !termina ; pos++)
        if (this.AristasNiveles[pos] == null)
            termina=true;
    int i=pos-1;
    for(int n=0; n<this.cells.length-2; n++){
        DefaultGraphCell nodo= this.cells[n];
        Accion A=(Accion) nodo.getUserObject();
        int nivelAccion=A.obtenerNivel();
        //porque empieza en -2 o sea 0 para el arreglo
        int indiceDelNodoDelNivel=nivelAccion+1;
        DefaultEdge aristaNivel= new DefaultEdge(nivelAccion);
        int arrow = GraphConstants.ARROW_CLASSIC;
        // Propiedades de las flechas de nivelacion que van hacia abajo.
        GraphConstants.setLineColor(aristaNivel.getAttributes(), Color.red);
        // Para ver las lineas cambiarle el float de 0 a 1. y cambiar el color de white a red.
            GraphConstants.setForeground(aristaNivel.getAttributes(), Color.white);
            GraphConstants.setLineWidth(aristaNivel.getAttributes(), 0);
        //
        GraphConstants.setSelectable(aristaNivel.getAttributes(), false);
        GraphConstants.setEditable(aristaNivel.getAttributes(), false);
        GraphConstants.setLineEnd(aristaNivel.getAttributes(), arrow);
        GraphConstants.setEndFill(aristaNivel.getAttributes(), true);
        DefaultGraphCell nodoNivelOrigen= this.niveles[indiceDelNodoDelNivel];
        DefaultGraphCell nodoNivelDestino= nodo;
        DefaultPort po=new DefaultPort("0");
        DefaultPort pd=new DefaultPort("0");
        nodoNivelOrigen.add(po);
        nodoNivelDestino.add(pd);
        //ver de mejor buscar el puerto que tiene el nombre del puerto de nivel ente caso usa para el ejemplo es "0"
        aristaNivel.setSource(nodoNivelOrigen.getChildAt(0));
        aristaNivel.setTarget(nodoNivelDestino.getChildAt(0));
        this.AristasNiveles[i]=aristaNivel;
        i++;
        //conexion de abajo
        DefaultEdge aristaNivelInferior= new DefaultEdge(nivelAccion);
        // Color de las flechas de la conexcion hacia abajo.
        GraphConstants.setLineColor(aristaNivelInferior.getAttributes(), Color.red);
        // Para ver las lineas cambiarle el float de 0 a 1. y cambiar el color de white a red.
            GraphConstants.setForeground(aristaNivelInferior.getAttributes(), Color.white);
            GraphConstants.setLineWidth(aristaNivelInferior.getAttributes(), 0);
        //
        GraphConstants.setSelectable(aristaNivelInferior.getAttributes(), false);
        GraphConstants.setEditable(aristaNivelInferior.getAttributes(), false);
        GraphConstants.setLineEnd(aristaNivelInferior.getAttributes(), arrow);
        GraphConstants.setEndFill(aristaNivelInferior.getAttributes(), true);
        nodoNivelOrigen= nodoNivelDestino;
        nodoNivelDestino= this.niveles[indiceDelNodoDelNivel + 1];
        po=new DefaultPort("0");
        pd=new DefaultPort("0");
        nodoNivelOrigen.add(po);
        nodoNivelDestino.add(pd);
        aristaNivelInferior.setSource(nodoNivelOrigen.getChildAt(nodoNivelOrigen.getIndex(po)));
        aristaNivelInferior.setTarget(nodoNivelDestino.getChildAt(nodoNivelDestino.getIndex(pd)));
        this.AristasNiveles[i]=aristaNivelInferior;
        i++;
    }
    DefaultGraphCell nodoOriNivel= this.niveles[0];
    DefaultGraphCell nodoDesNivel= NodoInicio;
    DefaultPort pori= new DefaultPort("0");
    DefaultPort pdes= new DefaultPort("0");
    nodoOriNivel.add(pori);
    nodoDesNivel.add(pdes);
    //DefaultEdge aristaEI= new DefaultEdge(-2);
    int arrow = GraphConstants.ARROW_CLASSIC;

    nodoOriNivel= nodoDesNivel;
    nodoDesNivel= this.niveles[1];

    DefaultEdge aristaEIinferior= new DefaultEdge(-2);
    // Color de flecha hacia EI.
    GraphConstants.setLineColor(aristaEIinferior.getAttributes(), Color.red);
    // Para ver las lineas cambiarle el float de 0 a 1. y cambiar el color de white a red.
       GraphConstants.setForeground(aristaEIinferior.getAttributes(), Color.white);
       GraphConstants.setLineWidth(aristaEIinferior.getAttributes(), 0);
    //
    GraphConstants.setSelectable(aristaEIinferior.getAttributes(), false);
    GraphConstants.setEditable(aristaEIinferior.getAttributes(), false);
    GraphConstants.setLineEnd(aristaEIinferior.getAttributes(), arrow);
    GraphConstants.setEndFill(aristaEIinferior.getAttributes(), true);

    pori= new DefaultPort("0");
    pdes= new DefaultPort("0");

    nodoOriNivel.add(pori);
    nodoDesNivel.add(pdes);

    aristaEIinferior.setSource(nodoOriNivel.getChildAt(nodoOriNivel.getIndex(pori)));
    aristaEIinferior.setTarget(nodoDesNivel.getChildAt(nodoDesNivel.getIndex(pdes)));

    this.AristasNiveles[i]=aristaEIinferior;
    i++;

    DefaultGraphCell nodoOrigNivel= this.niveles[this.niveles.length-2];
    DefaultGraphCell nodoDestNivel= NodoFin;

    DefaultPort porig= new DefaultPort(0);
    DefaultPort pdest= new DefaultPort(0);

    nodoOrigNivel.add(porig);
    nodoDestNivel.add(pdest);

    DefaultEdge aristaEF= new DefaultEdge(this.plan.cantNivelesJGraph()-1);
    GraphConstants.setLineColor(aristaEF.getAttributes(), Color.red);
    // Para ver las lineas cambiarle el float de 0 a 1. y cambiar el color de white a red.
       GraphConstants.setForeground(aristaEF.getAttributes(), Color.white);
       GraphConstants.setLineWidth(aristaEF.getAttributes(), 0);
    //
    GraphConstants.setSelectable(aristaEF.getAttributes(), false);
    GraphConstants.setEditable(aristaEF.getAttributes(), false);
    GraphConstants.setLineEnd(aristaEF.getAttributes(), arrow);
    GraphConstants.setEndFill(aristaEF.getAttributes(), true);
    aristaEF.setSource(nodoOrigNivel.getChildAt(0));
    aristaEF.setTarget(nodoDestNivel.getChildAt(0));

    int posEF=i;
    this.AristasNiveles[posEF]=aristaEF;
    i++;
    nodoOrigNivel=nodoDestNivel;
    nodoDestNivel=this.niveles[this.niveles.length-1];
    DefaultEdge aristaEFinferior= new DefaultEdge(this.plan.cantNivelesJGraph()-1);
     // Color de flecha hacia EF.
    GraphConstants.setLineColor(aristaEFinferior.getAttributes(), Color.red);
    // Para ver las lineas cambiarle el float de 0 a 1. y cambiar el color de white a red.
       GraphConstants.setForeground(aristaEFinferior.getAttributes(), Color.white);
       GraphConstants.setLineWidth(aristaEFinferior.getAttributes(), 0);
    //
    GraphConstants.setSelectable(aristaEFinferior.getAttributes(), false);
    GraphConstants.setEditable(aristaEFinferior.getAttributes(), false);
    GraphConstants.setLineEnd(aristaEFinferior.getAttributes(), arrow);
    GraphConstants.setEndFill(aristaEFinferior.getAttributes(), true);
    porig= new DefaultPort("0");
    pdest= new DefaultPort("0");
    nodoOrigNivel.add(porig);
    nodoDestNivel.add(pdest);
    aristaEFinferior.setSource(nodoOrigNivel.getChildAt(nodoOrigNivel.getIndex(porig)));
    aristaEFinferior.setTarget(nodoDestNivel.getChildAt(nodoDestNivel.getIndex(pdest)));
    this.AristasNiveles[i]=aristaEFinferior;
    i++;

}
}