package interfaceEditordePlanes;

import clases.Entrada;
import clases.Sintactico;
import clases.TextoDeEntrada;
import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;
import com.jgraph.layout.graph.JGraphSimpleLayout;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;
import com.jgraph.layout.organic.JGraphFastOrganicLayout;
import com.jgraph.layout.organic.JGraphOrganicLayout;
import editorDePlanes.*;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.tree.MutableTreeNode;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.UndoableEditEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.jgraph.graph.AbstractCellView;
import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.CellHandle;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphCellEditor;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.EdgeRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphContext;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.GraphUndoManager;
import org.jgraph.graph.PortRenderer;
import org.jgraph.graph.PortView;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;
import org.jgraph.*;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.CellView;
import org.jgraph.graph.Edge;
import org.jgraph.graph.Port;
import planificador.Algoritmo;
import planificador.Progresion;
import visualPlan.Mostrar;

/**
 *
 * @author mpacheco
 */

public class InterfaceEditordePlanesView extends FrameView implements GraphSelectionListener,
		KeyListener{

    public InterfaceEditordePlanesView(SingleFrameApplication app) throws NullPointerException {
        super(app);
        initComponents();
        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
       
        planvalidador= new PlanValidador(jTextArea1);
        cambio= false;
        jTextArea1.setEnabled(false);

        guardarPlan.setEnabled(false);
        insertar.setEnabled(false);
        nuevoNivel.setEnabled(false);
        nuevaAccion.setEnabled(false);
        moverAccion.setEnabled(false);
        nuevoLink.setEnabled(false);        
        nivel.setEnabled(false);
        borrarNivel.setEnabled(false);
        borrarAccion.setEnabled(false);
        borrarLink.setEnabled(false);
        accion.setEnabled(false);
        link.setEnabled(false);
        validarPlan.setEnabled(false);
        checkAmenazaOnline.setEnabled(false);
        cerrarPlan.setEnabled(false);
        editarLibreria.setEnabled(false);
        solucion.setEnabled(false);
        libreria.setEnabled(false);

        abrirPlanMenu.setEnabled(true);
        guardarPlanMenu.setEnabled(false);
        exportarMenu.setEnabled(false);
        cerrarPlanMenu.setEnabled(false);

        areaGrafica.setEnabled(false);        
        connect.setEnabled(false);
        remove.setEnabled(false);
        botonHorizontal.setEnabled(false);
        zoomIn.setEnabled(false);
        zoomOut.setEnabled(false);
        zoomStd.setEnabled(false);

    }

    public void borrarArchivo(){
        File fichero = new File(".//planes//tempPL.pl");
        if (fichero.delete())
           System.out.println("El fichero ha sido borrado satisfactoriamente");
        else
           System.out.println("El fichero no puede ser borrado");
    }


    @Action
    public void algoritmoPlanificacion(){        
        Algoritmo alg= new Progresion(plan, this);
        alg.correr();        
    }
    /* NOTA: CUANDO SE CREA UN NUEVO PLAN, LA LIBRERIA CON LA QUE SE ESTA TRABAJANDO
     *       PERMANECE ABIERTA. SI SE ABRE UN PLAN, LAS ACCIONES DE LA LIBRERIA 
     *       ACTUAL SE BORRAN Y SE REEMPLAZAN POR LAS ACCIONES DEL PLAN ABIERTO.
     */
    @Action
    public void nuevoPlan() throws CloneNotSupportedException{
        exportarMenu.setEnabled(false);
        zoomStd.setEnabled(false);
        zoomIn.setEnabled(false);
        zoomOut.setEnabled(false);
        guardarPlan.setEnabled(true);
        insertar.setEnabled(false);
        connect.setEnabled(false);
        remove.setEnabled(false);
        botonHorizontal.setEnabled(false);
        guardarPlanMenu.setEnabled(true);

        plan= new Plan(jTextArea1);
        M= new Mostrar(plan, grafo);
        destroy();
        zoomOriginal();
        reDibujar();

        jTextArea1.setEnabled(true);
        jTextArea3.setEnabled(true);
        jTextArea1.setText(null);
        jTextArea3.setText(null);
        mostrarMensaje("Se ha creardo un nuevo plan.");
        
        guardarPlan.setEnabled(true);
        nuevoNivel.setEnabled(true);
        nuevaAccion.setEnabled(true);
        nuevoLink.setEnabled(false);
        moverAccion.setEnabled(false);
        nuevoComplonenteMenu.setEnabled(true);
            nivel.setEnabled(true);
            accion.setEnabled(true);
            link.setEnabled(false);
        borrarNivel.setEnabled(false);
        borrarAccion.setEnabled(false);
        borrarLink.setEnabled(false);
        validarPlan.setEnabled(false);
        checkAmenazaOnline.setEnabled(false);
        cerrarPlan.setEnabled(true);
        editarLibreria.setEnabled(true);        
        libreria.setEnabled(true);

        abrirPlanMenu.setEnabled(true);
        guardarPlanMenu.setEnabled(true);
        cerrarPlanMenu.setEnabled(true);        
        areaGrafica.setEnabled(true);      

        getOriginador().clean();
        getPortero().clean();
        setPlanClonado((Plan) plan.clone());
        getOriginador().set(getPlanClonado());
        getPortero().addMemento( getOriginador().salvarEnRecuerdo());
    }
    @Action
    public void zoomOriginal(){
        grafo.setScale(1.0);
    }
    @Action
    public void zoomIn(){
        grafo.setScale(2 * grafo.getScale());
    }
    @Action
    public void zoomOut(){
        grafo.setScale(grafo.getScale()/2);
    }
    @Action
    public void exportarPlan(){
            exportarPlan.addChoosableFileFilter(new ImageFilter());
            int returnVal = exportarPlan.showSaveDialog(exportarMenu);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = exportarPlan.getSelectedFile();
                ExportarJGraph eg = new ExportarJGraph();
                eg.writeObject(grafo, file);
                mostrarMensaje("Se exporto el plan actual a "+file.getPath());
            }
            if (returnVal == JFileChooser.CANCEL_OPTION) {
                mostrarMensaje("Se cancelo la exportación.");
            }
    }

    public void cargarLib(Vector<Accion> vec,JTree jTree,Libreria lib){
        DefaultMutableTreeNode raux = (DefaultMutableTreeNode) jTree.getModel().getRoot();
        DefaultMutableTreeNode nodoNuevo =null;
        Accion aux= null;
        for (int i=0;i < vec.size(); i++){
          aux= vec.elementAt(i);
          lib.AgregarAccion(aux);
          nodoNuevo= aux.Obtener_Nodo();
          raux.add(nodoNuevo);
        }
    }

    public void borrarArbol(JTree jTree){
        TreeModel modeloLibAc=  jTree.getModel();
        DefaultMutableTreeNode libAct=(DefaultMutableTreeNode) modeloLibAc.getRoot();
        libAct.removeAllChildren();
    }
    @Action
    public void  abrirPlan() throws FileNotFoundException, CloneNotSupportedException{
        jFileChooser1.setCurrentDirectory(new File(".//planes//"));
        int returnVal = jFileChooser1.showOpenDialog(abrirPlanMenu);
        boolean ok= false;
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = jFileChooser1.getSelectedFile();
            Plan plan_test= (Plan) GuardarRecuperarEnXML.readObject(file);
            if(plan_test != null){

                plan_test.setAreaDeTexto(jTextArea1);
                plan_test.setValidar(new Amenaza(plan_test, jTextArea1));
                plan_test.setMensaje(new Mensaje(jTextArea1));

                planvalidador= new PlanValidador(jTextArea1);
                if(planvalidador.validarPlan(plan_test)){
                    ok=true;
                    
                    jTextArea1.setEnabled(true);
                    plan= plan_test;
                    M= new Mostrar(plan, grafo);
                    destroy();
                    zoomOriginal();
                    reDibujar();

                    borrarArbol(jTree1);
                    lib_actual.clean();
                    cargarLib(plan.obtenerAcciones(),jTree1,lib_actual);
                    jTree1.updateUI();

                    jTextArea1.setEnabled(true);
                    jTextArea3.setEnabled(true);
                    jTextArea1.setText(null);
                    jTextArea3.setText(null);
                    
                    mostrarMensaje("Se abrio el plan con exito de "+file.getPath());
                    exportarMenu.setEnabled(true);
                    zoomStd.setEnabled(true);
                    zoomIn.setEnabled(true);
                    zoomOut.setEnabled(true);
                    guardarPlan.setEnabled(true);
                    insertar.setEnabled(true);
                    connect.setEnabled(true);
                    remove.setEnabled(true);
                    botonHorizontal.setEnabled(true);
                    guardarPlanMenu.setEnabled(true);
                    guardarPlan.setEnabled(true);
                    nuevoNivel.setEnabled(true);
                    nuevaAccion.setEnabled(true);
                    nuevoLink.setEnabled(true);
                    moverAccion.setEnabled(true);
                    nuevoComplonenteMenu.setEnabled(true);
                    nivel.setEnabled(true);
                    accion.setEnabled(true);
                    link.setEnabled(true);
                    borrarNivel.setEnabled(true);
                    borrarAccion.setEnabled(true);
                    borrarLink.setEnabled(true);
                    validarPlan.setEnabled(true);
                    checkAmenazaOnline.setEnabled(true);
                    cerrarPlan.setEnabled(true);
                    editarLibreria.setEnabled(true);                    
                    libreria.setEnabled(true);
                    abrirPlanMenu.setEnabled(true);
                    guardarPlanMenu.setEnabled(true);
                    cerrarPlanMenu.setEnabled(true);
                    areaGrafica.setEnabled(true);
                    areaGrafica.setEnabled(true);

                    getOriginador().clean();
                    getPortero().clean();
                    setPlanClonado((Plan) plan.clone());
                    getOriginador().set(getPlanClonado());
                    getPortero().addMemento( getOriginador().salvarEnRecuerdo());
                }
            }
            if (!ok){
                jTextArea1.setEnabled(true);
                mostrarMensaje("El plan no se abrio.");                
            }           
        }
    }    
    @Action
    public void guardarPlan() throws FileNotFoundException{
        jFileChooser1.setCurrentDirectory(new File(".//planes//"));
        int returnVal = jFileChooser1.showSaveDialog(guardarPlanMenu);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = jFileChooser1.getSelectedFile();
            Plan ppp1= new Plan(plan.getNiveles(), plan.getLinks(), plan.getEstado_inicial(), plan.getEstado_final(), null, plan.getCant_niveles(), null, plan.getCant_niveles_jgraph(), null, plan.isFallo_links());
            GuardarRecuperarEnXML.writeObject(ppp1, file);
            mostrarMensaje("El plan se guardo con exito en "+file.getPath());
        }
        else {
            jTextArea1.setEnabled(true);
            mostrarMensaje("El plan no se guardo.");
        }
    }
    @Action
    public void cerrarPlan(){
        plan= null;
        destroy();
        exportarMenu.setEnabled(false);
        zoomStd.setEnabled(false);
        zoomIn.setEnabled(false);
        zoomOut.setEnabled(false);
        guardarPlan.setEnabled(true);
        insertar.setEnabled(false);
        connect.setEnabled(false);
        remove.setEnabled(false);
        undo.setEnabled(false);
        botonHorizontal.setEnabled(false);
        guardarPlanMenu.setEnabled(true);

        jTextArea1.setEnabled(false);
        jTextArea3.setEnabled(false);
        jTextArea1.setText(null);
        jTextArea3.setText(null);

        mostrarMensaje("Se ha cerrado el plan actual.");

        guardarPlan.setEnabled(false);
        nuevoNivel.setEnabled(false);
        nuevaAccion.setEnabled(false);
        nuevoLink.setEnabled(false);
        moverAccion.setEnabled(false);
        nuevoComplonenteMenu.setEnabled(false);
            nivel.setEnabled(false);
            accion.setEnabled(false);
            link.setEnabled(false);
        exportarMenu.setEnabled(false);
        borrarNivel.setEnabled(false);
        borrarAccion.setEnabled(false);
        borrarLink.setEnabled(false);
        validarPlan.setEnabled(false);
        checkAmenazaOnline.setEnabled(false);
        cerrarPlan.setEnabled(false);
        editarLibreria.setEnabled(false);
        solucion.setEnabled(false);
        libreria.setEnabled(false);

        abrirPlanMenu.setEnabled(true);
        guardarPlanMenu.setEnabled(false);
        cerrarPlanMenu.setEnabled(false);
        areaGrafica.setEnabled(true);

        getOriginador().clean();
        getPortero().clean();
    }
    @Action
    public void editarLibreria(){
        JFrame mainFrame= IntefaceEditordePlanes.getApplication().getMainFrame();                
        edicionLibreria= new EdicionDeLibrerias(lib_actual, this);
        edicionLibreria.setLocationRelativeTo(mainFrame);       
        IntefaceEditordePlanes.getApplication().show(edicionLibreria);
        mainFrame.setEnabled(false);
    }
    @Action
    public void nuevoNivel(){
        JFrame mainFrame= IntefaceEditordePlanes.getApplication().getMainFrame();                
        nuevoNivelBox= new NuevoNivelBox(plan, this);
        nuevoNivelBox.setLocationRelativeTo(mainFrame);       
        IntefaceEditordePlanes.getApplication().show(nuevoNivelBox);
        mainFrame.setEnabled(false);
    }
    @Action
    public void borrarNivel(){
        JFrame mainFrame= IntefaceEditordePlanes.getApplication().getMainFrame();
        borrarNivelBox= new BorrarNivelBox(plan, this);
        borrarNivelBox.setLocationRelativeTo(mainFrame);
        IntefaceEditordePlanes.getApplication().show(borrarNivelBox);
        mainFrame.setEnabled(false);
    }
    @Action
    public void borrarAccion(){
        JFrame mainFrame= IntefaceEditordePlanes.getApplication().getMainFrame();
        borrarAccionBox= new BorrarAccionBox(plan, this);
        borrarAccionBox.setLocationRelativeTo(mainFrame);
        IntefaceEditordePlanes.getApplication().show(borrarAccionBox);
        mainFrame.setEnabled(false);
    }
    @Action
    public void borrarLink(){
        JFrame mainFrame= IntefaceEditordePlanes.getApplication().getMainFrame();
        borrarLinkBox= new BorrarLinkBox(plan, this);
        borrarLinkBox.setLocationRelativeTo(mainFrame);
        IntefaceEditordePlanes.getApplication().show(borrarLinkBox);
        mainFrame.setEnabled(false);
    }
    @Action
    public void nuevoLink(){
        JFrame mainFrame= IntefaceEditordePlanes.getApplication().getMainFrame();
        nuevoLinkBox= new NuevoLinkBox(plan, this);
        nuevoLinkBox.setLocationRelativeTo(mainFrame);
        IntefaceEditordePlanes.getApplication().show(nuevoLinkBox);
        mainFrame.setEnabled(false);
    }

    public void reDibujar(){
            destroy();
            M.destroy();
            M.reDibujar();
            if(!getBotonHorizontal().getText().equals("sin alineacion"))
                if(isGrafohorizontal())
                   setGrafohorizontal(false);
                else
                   setGrafohorizontal(true);
                botonHorizontal2(null);
    }
    @Action
    public void menuAyuda() throws FileNotFoundException, IOException{
        JFrame mainFrame = IntefaceEditordePlanes.getApplication().getMainFrame();
        if (menuAyuda == null)
            menuAyuda = new MenuAyuda();
        menuAyuda.setLocationRelativeTo(mainFrame);
        IntefaceEditordePlanes.getApplication().show(menuAyuda);
        mainFrame.setEnabled(false);
    }
    @Action
    public void acercaDe() {
        JFrame mainFrame = IntefaceEditordePlanes.getApplication().getMainFrame();
        if (aboutBox == null)            
            aboutBox = new AcercaDeBox(mainFrame);
        aboutBox.setLocationRelativeTo(mainFrame);
        IntefaceEditordePlanes.getApplication().show(aboutBox);
    }
    @Action
    public void validarPlan(){
       planvalidador= new PlanValidador(jTextArea1);
       planvalidador.validarPlan(plan);
    }
    @Action
    public void moverAccion(){
        JFrame mainFrame= IntefaceEditordePlanes.getApplication().getMainFrame();
        moverAccionBox= new MoverAccionBox(plan, this);
        moverAccionBox.setLocationRelativeTo(mainFrame);
        IntefaceEditordePlanes.getApplication().show(moverAccionBox);
        mainFrame.setEnabled(false);
    }
    // Metodo que pasa una accion de la libreria por default al plan y se re dibuja.
    @Action
    public void insertar(){
        JFrame mainFrame= IntefaceEditordePlanes.getApplication().getMainFrame();
        insertarAccionGrafoBox= new InsertarAccionGrafoBox(plan, this);
        insertarAccionGrafoBox.setLocationRelativeTo(mainFrame);
        IntefaceEditordePlanes.getApplication().show(insertarAccionGrafoBox);
        mainFrame.setEnabled(false);
    }

    public JCheckBox getCheckAmenazaOnline() {
        return checkAmenazaOnline;
    }

    public void aplicarSolucionAlgoritmica(Plan plan){
        this.plan= plan;
        M= new Mostrar(this.plan, grafo);
        destroy();
        zoomOriginal();
        reDibujar();        
    }

    public JTextArea getAreaDeTextoPrincipal(){
        return jTextArea1;
    }

    public void setCheckAmenazaOnline(JCheckBox checkAmenazaOnline) {
        this.checkAmenazaOnline = checkAmenazaOnline;
    }

    public void mostrarMensaje(String mensaje){
        jTextArea1.append("Msj Id: "+jTextArea1.getLineCount()+" - "+mensaje+"\n");
    }

    public void mostrarAmenazas(Accion accion, boolean surge_al_mover){
        JFrame mainFrame= IntefaceEditordePlanes.getApplication().getMainFrame();
        amenazaBox= new AmenazaBox(plan, accion, surge_al_mover, this);
        amenazaBox.setLocationRelativeTo(mainFrame);
        IntefaceEditordePlanes.getApplication().show(amenazaBox);
        mainFrame.setEnabled(false);
    }

    public void insertarAccionDeLibreria(Accion accion) throws CloneNotSupportedException{
        boolean repetida= false;
        if(checkAmenazaOnline.isSelected()){
           if(plan.agregarAccionVerifAmenaza(accion)){
                reDibujar();
                setPlanClonado((Plan) plan.clone());
                originador.set(planClonado);
                portero.addMemento( originador.salvarEnRecuerdo());
           }
           else{
               if(!plan.existeAccion(accion))
                    if(plan.promociones(accion).size()!=0 || plan.degradaciones(accion).size() !=0){
                        mostrarAmenazas(accion, false);
                    }
                    else{
                        plan.getMensaje().amenazaInsalvable(accion);
                    }
           }
        }
        else{
            if(plan.agregarAccionSinAmenaza(accion)){
                reDibujar();
                setPlanClonado((Plan) plan.clone());
                originador.set(planClonado);
                portero.addMemento( originador.salvarEnRecuerdo());
            }
        }
    }

    public void habilitarComandos(){
        exportarMenu.setEnabled(true);
        zoomStd.setEnabled(true);
        zoomIn.setEnabled(true);
        zoomOut.setEnabled(true);
        guardarPlan.setEnabled(true);
        insertar.setEnabled(true);
        connect.setEnabled(true);
        remove.setEnabled(true);
        botonHorizontal.setEnabled(true);
        guardarPlanMenu.setEnabled(true);
        nuevoComplonenteMenu.setEnabled(true);
        exportarMenu.setEnabled(true);     
        
        guardarPlan.setEnabled(true);
        insertar.setEnabled(true);        
        nuevaAccion.setEnabled(true);
        moverAccion.setEnabled(true);
        nuevoLink.setEnabled(true);        
        nivel.setEnabled(true);
        borrarNivel.setEnabled(true);
        borrarAccion.setEnabled(true);
        borrarLink.setEnabled(true);
        accion.setEnabled(true);
        link.setEnabled(true);
        validarPlan.setEnabled(true);
        checkAmenazaOnline.setEnabled(true);
        cerrarPlan.setEnabled(true);

        abrirPlanMenu.setEnabled(true);
        guardarPlanMenu.setEnabled(true);
        exportarMenu.setEnabled(true);
        cerrarPlanMenu.setEnabled(true);
        
        areaGrafica.setEnabled(true);       

        connect.setEnabled(true);
        remove.setEnabled(true);
        botonHorizontal.setEnabled(true);
        zoomIn.setEnabled(true);
        zoomOut.setEnabled(true);
        zoomStd.setEnabled(true);
    }

    public void desabilitarComandos(){
       
        insertar.setEnabled(false);        
        moverAccion.setEnabled(false);
        nuevoLink.setEnabled(false);
        nivel.setEnabled(true);
        borrarNivel.setEnabled(false);
        borrarAccion.setEnabled(false);
        borrarLink.setEnabled(false);
        link.setEnabled(false);
        validarPlan.setEnabled(false);
        checkAmenazaOnline.setEnabled(false);

        connect.setEnabled(false);
        remove.setEnabled(false);
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelPrincipal = new javax.swing.JPanel();
        barradeHerramientas = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        abrirPlan = new javax.swing.JButton();
        guardarPlan = new javax.swing.JButton();
        cerrarPlan = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        insertar = new javax.swing.JButton();
        connect = new javax.swing.JButton();
        remove = new javax.swing.JButton();
        botonHorizontal = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        undo = new javax.swing.JButton();
        redo = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        zoomStd = new javax.swing.JButton();
        zoomIn = new javax.swing.JButton();
        zoomOut = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        nuevoNivel = new javax.swing.JButton();
        borrarNivel = new javax.swing.JButton();
        nuevaAccion = new javax.swing.JButton();
        moverAccion = new javax.swing.JButton();
        borrarAccion = new javax.swing.JButton();
        nuevoLink = new javax.swing.JButton();
        borrarLink = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        validarPlan = new javax.swing.JButton();
        checkAmenazaOnline = new javax.swing.JCheckBox();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        editarLibreria = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        solucion = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        jButton3 = new javax.swing.JButton();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        jPanel1 = new javax.swing.JPanel();
        /*GraphModel model = new DefaultGraphModel();
        GraphLayoutCache view = new GraphLayoutCache(model,new DefaultCellViewFactory());
        grafo= new JGraph(model, view);
        areaGrafica = new javax.swing.JScrollPane(grafo);*/ //<--anda JOYA!!!
        GraphEdition();
        areaGrafica = new javax.swing.JScrollPane(grafo);
        jPanel4 = new javax.swing.JPanel();
        libreriaPorDefectoArbol = new javax.swing.JScrollPane();
        this.lib_actual = new Libreria("LibreriaActual");
        this.RaizLib = this.lib_actual.Obtener_Nodo();
        this.modeloLib = new DefaultTreeModel(this.RaizLib);
        jTree1 = new javax.swing.JTree();
        this.jTree1.setModel(this.modeloLib);
        jPanel8 = new javax.swing.JPanel();
        informeDeOperaciones = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        barraDeMenu = new javax.swing.JMenuBar();
        javax.swing.JMenu archivo = new javax.swing.JMenu();
        nuevoPlanMenu = new javax.swing.JMenuItem();
        abrirPlanMenu = new javax.swing.JMenuItem();
        guardarPlanMenu = new javax.swing.JMenuItem();
        nuevoComplonenteMenu = new javax.swing.JMenu();
        nivel = new javax.swing.JMenuItem();
        accion = new javax.swing.JMenuItem();
        link = new javax.swing.JMenuItem();
        exportarMenu = new javax.swing.JMenuItem();
        cerrarPlanMenu = new javax.swing.JMenuItem();
        javax.swing.JMenuItem salirMenu = new javax.swing.JMenuItem();
        libreria = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        javax.swing.JMenu ayuda = new javax.swing.JMenu();
        ayudaMenu = new javax.swing.JMenuItem();
        acercaDeMenu = new javax.swing.JMenuItem();
        panelDeEstado = new javax.swing.JPanel();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        jFileChooser1 = new javax.swing.JFileChooser();
        cargarAccion = new javax.swing.JFrame();
        jPanel3 = new javax.swing.JPanel();
        jTextField2 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        botonborrarPre = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        DefaultMutableTreeNode RaizPre= new javax.swing.tree.DefaultMutableTreeNode("PreCondiciones");

        //-------------------Creacion del Modelo-------------------------
        DefaultTreeModel modelo01= new DefaultTreeModel(RaizPre);
        //modelo01.addTreeModelListener(new MiArbolListener());
        Arbol_PreCondicionAmodificar = new javax.swing.JTree(modelo01);
        botonborrarPost = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        DefaultMutableTreeNode RaizPost= new javax.swing.tree.DefaultMutableTreeNode("PostCondiciones");

        //-------------------Creacion del Modelo-------------------------
        DefaultTreeModel modelo02= new DefaultTreeModel(RaizPost);
        Arbol_PostCondicionAmodificar = new javax.swing.JTree(modelo02);
        jRadioButtonPreC = new javax.swing.JRadioButton();
        jRadioButtonPostC = new javax.swing.JRadioButton();
        nombreNuevaAccion = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jButton10 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        estadoComboBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        buttonGroup1 = new javax.swing.ButtonGroup();
        exportarPlan = new javax.swing.JFileChooser();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(interfaceEditordePlanes.IntefaceEditordePlanes.class).getContext().getResourceMap(InterfaceEditordePlanesView.class);
        panelPrincipal.setToolTipText(resourceMap.getString("panelPrincipal.toolTipText")); // NOI18N
        panelPrincipal.setMaximumSize(new java.awt.Dimension(1024, 768));
        panelPrincipal.setName("panelPrincipal"); // NOI18N
        panelPrincipal.setPreferredSize(new java.awt.Dimension(1024, 768));

        barradeHerramientas.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        barradeHerramientas.setRollover(true);
        barradeHerramientas.setName("barradeHerramientas"); // NOI18N
        barradeHerramientas.setPreferredSize(new java.awt.Dimension(1024, 61));

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(interfaceEditordePlanes.IntefaceEditordePlanes.class).getContext().getActionMap(InterfaceEditordePlanesView.class, this);
        jButton1.setAction(actionMap.get("nuevoPlan")); // NOI18N
        jButton1.setIcon(resourceMap.getIcon("jButton1.icon")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setName("jButton1"); // NOI18N
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        barradeHerramientas.add(jButton1);

        abrirPlan.setAction(actionMap.get("abrirPlan")); // NOI18N
        abrirPlan.setIcon(resourceMap.getIcon("abrirPlan.icon")); // NOI18N
        abrirPlan.setText(resourceMap.getString("abrirPlan.text")); // NOI18N
        abrirPlan.setToolTipText(resourceMap.getString("abrirPlan.toolTipText")); // NOI18N
        abrirPlan.setFocusable(false);
        abrirPlan.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        abrirPlan.setName("abrirPlan"); // NOI18N
        abrirPlan.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        barradeHerramientas.add(abrirPlan);

        guardarPlan.setAction(actionMap.get("guardarPlan")); // NOI18N
        guardarPlan.setIcon(resourceMap.getIcon("guardarPlan.icon")); // NOI18N
        guardarPlan.setText(resourceMap.getString("guardarPlan.text")); // NOI18N
        guardarPlan.setToolTipText(resourceMap.getString("guardarPlan.toolTipText")); // NOI18N
        guardarPlan.setFocusable(false);
        guardarPlan.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        guardarPlan.setName("guardarPlan"); // NOI18N
        guardarPlan.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        barradeHerramientas.add(guardarPlan);

        cerrarPlan.setAction(actionMap.get("cerrarPlan")); // NOI18N
        cerrarPlan.setIcon(resourceMap.getIcon("cerrarPlan.icon")); // NOI18N
        cerrarPlan.setText(resourceMap.getString("cerrarPlan.text")); // NOI18N
        cerrarPlan.setToolTipText(resourceMap.getString("cerrarPlan.toolTipText")); // NOI18N
        cerrarPlan.setFocusable(false);
        cerrarPlan.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cerrarPlan.setName("cerrarPlan"); // NOI18N
        cerrarPlan.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        barradeHerramientas.add(cerrarPlan);

        jSeparator1.setName("jSeparator1"); // NOI18N
        barradeHerramientas.add(jSeparator1);

        insertar.setAction(actionMap.get("insertar")); // NOI18N
        insertar.setIcon(resourceMap.getIcon("insertar.icon")); // NOI18N
        insertar.setText(resourceMap.getString("insertar.text")); // NOI18N
        insertar.setToolTipText(resourceMap.getString("insertar.toolTipText")); // NOI18N
        insertar.setFocusable(false);
        insertar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        insertar.setName("insertar"); // NOI18N
        insertar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        barradeHerramientas.add(insertar);

        connect.setIcon(resourceMap.getIcon("connect.icon")); // NOI18N
        connect.setText(resourceMap.getString("connect.text")); // NOI18N
        connect.setToolTipText(resourceMap.getString("connect.toolTipText")); // NOI18N
        connect.setEnabled(false);
        connect.setFocusable(false);
        connect.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        connect.setName("connect"); // NOI18N
        connect.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        connect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectActionPerformed(evt);
            }
        });
        barradeHerramientas.add(connect);

        remove.setIcon(resourceMap.getIcon("remove.icon")); // NOI18N
        remove.setText(resourceMap.getString("remove.text")); // NOI18N
        remove.setToolTipText(resourceMap.getString("remove.toolTipText")); // NOI18N
        remove.setEnabled(false);
        remove.setFocusable(false);
        remove.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        remove.setName("remove"); // NOI18N
        remove.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        remove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeActionPerformed(evt);
            }
        });
        barradeHerramientas.add(remove);

        botonHorizontal.setIcon(resourceMap.getIcon("botonHorizontal.icon")); // NOI18N
        botonHorizontal.setText(resourceMap.getString("botonHorizontal.text")); // NOI18N
        botonHorizontal.setToolTipText(resourceMap.getString("botonHorizontal.toolTipText")); // NOI18N
        botonHorizontal.setEnabled(false);
        botonHorizontal.setFocusable(false);
        botonHorizontal.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonHorizontal.setName("botonHorizontal"); // NOI18N
        botonHorizontal.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonHorizontal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonHorizontalActionPerformed(evt);
            }
        });
        barradeHerramientas.add(botonHorizontal);

        jSeparator5.setName("jSeparator5"); // NOI18N
        barradeHerramientas.add(jSeparator5);

        undo.setIcon(resourceMap.getIcon("undo.icon")); // NOI18N
        undo.setText(resourceMap.getString("undo.text")); // NOI18N
        undo.setToolTipText(resourceMap.getString("undo.toolTipText")); // NOI18N
        undo.setEnabled(false);
        undo.setFocusable(false);
        undo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        undo.setName("undo"); // NOI18N
        undo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        undo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoActionPerformed(evt);
            }
        });
        barradeHerramientas.add(undo);

        redo.setIcon(resourceMap.getIcon("redo.icon")); // NOI18N
        redo.setText(resourceMap.getString("redo.text")); // NOI18N
        redo.setToolTipText(resourceMap.getString("redo.toolTipText")); // NOI18N
        redo.setEnabled(false);
        redo.setFocusable(false);
        redo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        redo.setName("redo"); // NOI18N
        redo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        redo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redoActionPerformed(evt);
            }
        });
        barradeHerramientas.add(redo);

        jSeparator4.setName("jSeparator4"); // NOI18N
        barradeHerramientas.add(jSeparator4);

        zoomStd.setAction(actionMap.get("zoomOriginal")); // NOI18N
        zoomStd.setIcon(resourceMap.getIcon("zoomStd.icon")); // NOI18N
        zoomStd.setText(resourceMap.getString("zoomStd.text")); // NOI18N
        zoomStd.setToolTipText(resourceMap.getString("zoomStd.toolTipText")); // NOI18N
        zoomStd.setFocusable(false);
        zoomStd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoomStd.setName("zoomStd"); // NOI18N
        zoomStd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        barradeHerramientas.add(zoomStd);

        zoomIn.setAction(actionMap.get("zoomIn")); // NOI18N
        zoomIn.setIcon(resourceMap.getIcon("zoomIn.icon")); // NOI18N
        zoomIn.setText(resourceMap.getString("zoomIn.text")); // NOI18N
        zoomIn.setToolTipText(resourceMap.getString("zoomIn.toolTipText")); // NOI18N
        zoomIn.setFocusable(false);
        zoomIn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoomIn.setName("zoomIn"); // NOI18N
        zoomIn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        barradeHerramientas.add(zoomIn);

        zoomOut.setAction(actionMap.get("zoomOut")); // NOI18N
        zoomOut.setIcon(resourceMap.getIcon("zoomOut.icon")); // NOI18N
        zoomOut.setText(resourceMap.getString("zoomOut.text")); // NOI18N
        zoomOut.setToolTipText(resourceMap.getString("zoomOut.toolTipText")); // NOI18N
        zoomOut.setFocusable(false);
        zoomOut.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoomOut.setName("zoomOut"); // NOI18N
        zoomOut.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        barradeHerramientas.add(zoomOut);

        jSeparator2.setName("jSeparator2"); // NOI18N
        barradeHerramientas.add(jSeparator2);

        nuevoNivel.setAction(actionMap.get("nuevoNivel")); // NOI18N
        nuevoNivel.setIcon(resourceMap.getIcon("nuevoNivel.icon")); // NOI18N
        nuevoNivel.setText(resourceMap.getString("nuevoNivel.text")); // NOI18N
        nuevoNivel.setToolTipText(resourceMap.getString("nuevoNivel.toolTipText")); // NOI18N
        nuevoNivel.setFocusable(false);
        nuevoNivel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        nuevoNivel.setName("nuevoNivel"); // NOI18N
        nuevoNivel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        barradeHerramientas.add(nuevoNivel);

        borrarNivel.setAction(actionMap.get("borrarNivel")); // NOI18N
        borrarNivel.setIcon(resourceMap.getIcon("borrarNivel.icon")); // NOI18N
        borrarNivel.setText(resourceMap.getString("borrarNivel.text")); // NOI18N
        borrarNivel.setToolTipText(resourceMap.getString("borrarNivel.toolTipText")); // NOI18N
        borrarNivel.setFocusable(false);
        borrarNivel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        borrarNivel.setName("borrarNivel"); // NOI18N
        borrarNivel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        barradeHerramientas.add(borrarNivel);

        nuevaAccion.setIcon(resourceMap.getIcon("nuevaAccion.icon")); // NOI18N
        nuevaAccion.setText(resourceMap.getString("nuevaAccion.text")); // NOI18N
        nuevaAccion.setToolTipText(resourceMap.getString("nuevaAccion.toolTipText")); // NOI18N
        nuevaAccion.setFocusable(false);
        nuevaAccion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        nuevaAccion.setName("nuevaAccion"); // NOI18N
        nuevaAccion.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        nuevaAccion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevaAccionActionPerformed(evt);
            }
        });
        barradeHerramientas.add(nuevaAccion);

        moverAccion.setIcon(resourceMap.getIcon("moverAccion.icon")); // NOI18N
        moverAccion.setText(resourceMap.getString("moverAccion.text")); // NOI18N
        moverAccion.setToolTipText(resourceMap.getString("moverAccion.toolTipText")); // NOI18N
        moverAccion.setFocusable(false);
        moverAccion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        moverAccion.setName("moverAccion"); // NOI18N
        moverAccion.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        moverAccion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moverAccionActionPerformed(evt);
            }
        });
        barradeHerramientas.add(moverAccion);

        borrarAccion.setAction(actionMap.get("borrarAccion")); // NOI18N
        borrarAccion.setIcon(resourceMap.getIcon("borrarAccion.icon")); // NOI18N
        borrarAccion.setText(resourceMap.getString("borrarAccion.text")); // NOI18N
        borrarAccion.setToolTipText(resourceMap.getString("borrarAccion.toolTipText")); // NOI18N
        borrarAccion.setFocusable(false);
        borrarAccion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        borrarAccion.setName("borrarAccion"); // NOI18N
        borrarAccion.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        barradeHerramientas.add(borrarAccion);

        nuevoLink.setAction(actionMap.get("nuevoLink")); // NOI18N
        nuevoLink.setIcon(resourceMap.getIcon("nuevoLink.icon")); // NOI18N
        nuevoLink.setText(resourceMap.getString("nuevoLink.text")); // NOI18N
        nuevoLink.setToolTipText(resourceMap.getString("nuevoLink.toolTipText")); // NOI18N
        nuevoLink.setFocusable(false);
        nuevoLink.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        nuevoLink.setName("nuevoLink"); // NOI18N
        nuevoLink.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        barradeHerramientas.add(nuevoLink);

        borrarLink.setAction(actionMap.get("borrarLink")); // NOI18N
        borrarLink.setIcon(resourceMap.getIcon("borrarLink.icon")); // NOI18N
        borrarLink.setText(resourceMap.getString("borrarLink.text")); // NOI18N
        borrarLink.setToolTipText(resourceMap.getString("borrarLink.toolTipText")); // NOI18N
        borrarLink.setFocusable(false);
        borrarLink.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        borrarLink.setName("borrarLink"); // NOI18N
        borrarLink.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        barradeHerramientas.add(borrarLink);

        jSeparator3.setName("jSeparator3"); // NOI18N
        barradeHerramientas.add(jSeparator3);

        validarPlan.setAction(actionMap.get("validarPlan")); // NOI18N
        validarPlan.setIcon(resourceMap.getIcon("validarPlan.icon")); // NOI18N
        validarPlan.setText(resourceMap.getString("validarPlan.text")); // NOI18N
        validarPlan.setToolTipText(resourceMap.getString("validarPlan.toolTipText")); // NOI18N
        validarPlan.setFocusable(false);
        validarPlan.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        validarPlan.setName("validarPlan"); // NOI18N
        validarPlan.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        barradeHerramientas.add(validarPlan);

        checkAmenazaOnline.setText(resourceMap.getString("checkAmenazaOnline.text")); // NOI18N
        checkAmenazaOnline.setToolTipText(resourceMap.getString("checkAmenazaOnline.toolTipText")); // NOI18N
        checkAmenazaOnline.setFocusable(false);
        checkAmenazaOnline.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        checkAmenazaOnline.setName("checkAmenazaOnline"); // NOI18N
        checkAmenazaOnline.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        checkAmenazaOnline.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                checkAmenazaOnlineMouseClicked(evt);
            }
        });
        barradeHerramientas.add(checkAmenazaOnline);

        jSeparator6.setName("jSeparator6"); // NOI18N
        barradeHerramientas.add(jSeparator6);

        editarLibreria.setAction(actionMap.get("editarLibreria")); // NOI18N
        editarLibreria.setIcon(resourceMap.getIcon("editarLibreria.icon")); // NOI18N
        editarLibreria.setText(resourceMap.getString("editarLibreria.text")); // NOI18N
        editarLibreria.setToolTipText(resourceMap.getString("editarLibreria.toolTipText")); // NOI18N
        editarLibreria.setFocusable(false);
        editarLibreria.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        editarLibreria.setName("editarLibreria"); // NOI18N
        editarLibreria.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        barradeHerramientas.add(editarLibreria);

        jSeparator7.setName("jSeparator7"); // NOI18N
        barradeHerramientas.add(jSeparator7);

        solucion.setAction(actionMap.get("algoritmoPlanificacion")); // NOI18N
        solucion.setIcon(resourceMap.getIcon("solucion.icon")); // NOI18N
        solucion.setText(resourceMap.getString("solucion.text")); // NOI18N
        solucion.setToolTipText(resourceMap.getString("solucion.toolTipText")); // NOI18N
        solucion.setFocusable(false);
        solucion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        solucion.setName("solucion"); // NOI18N
        solucion.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        barradeHerramientas.add(solucion);

        jSeparator8.setName("jSeparator8"); // NOI18N
        barradeHerramientas.add(jSeparator8);

        jButton3.setAction(actionMap.get("menuAyuda")); // NOI18N
        jButton3.setIcon(resourceMap.getIcon("jButton3.icon")); // NOI18N
        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setToolTipText(resourceMap.getString("jButton3.toolTipText")); // NOI18N
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setName("jButton3"); // NOI18N
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        barradeHerramientas.add(jButton3);

        jSeparator9.setName("jSeparator9"); // NOI18N
        barradeHerramientas.add(jSeparator9);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), resourceMap.getString("jPanel1.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("jPanel1.border.titleFont"), resourceMap.getColor("jPanel1.border.titleColor"))); // NOI18N
        jPanel1.setToolTipText(resourceMap.getString("jPanel1.toolTipText")); // NOI18N
        jPanel1.setName("jPanel1"); // NOI18N

        areaGrafica.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        areaGrafica.setAutoscrolls(true);
        areaGrafica.setDoubleBuffered(true);
        areaGrafica.setEnabled(false);
        areaGrafica.setName("areaGrafica"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(areaGrafica, javax.swing.GroupLayout.DEFAULT_SIZE, 682, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(areaGrafica, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), resourceMap.getString("jPanel4.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("jPanel4.border.titleFont"), resourceMap.getColor("jPanel4.border.titleColor"))); // NOI18N
        jPanel4.setToolTipText(resourceMap.getString("jPanel4.toolTipText")); // NOI18N
        jPanel4.setName("jPanel4"); // NOI18N

        libreriaPorDefectoArbol.setName("libreriaPorDefectoArbol"); // NOI18N

        jTree1.setFont(resourceMap.getFont("jTree1.font")); // NOI18N
        jTree1.setDragEnabled(true);
        jTree1.setName("jTree1"); // NOI18N
        libreriaPorDefectoArbol.setViewportView(jTree1);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(libreriaPorDefectoArbol, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(libreriaPorDefectoArbol, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), resourceMap.getString("jPanel8.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("jPanel8.border.titleFont"), resourceMap.getColor("jPanel8.border.titleColor"))); // NOI18N
        jPanel8.setToolTipText(resourceMap.getString("jPanel8.toolTipText")); // NOI18N
        jPanel8.setName("jPanel8"); // NOI18N

        informeDeOperaciones.setName("informeDeOperaciones"); // NOI18N

        jTextArea1.setBackground(resourceMap.getColor("jTextArea1.background")); // NOI18N
        jTextArea1.setColumns(20);
        jTextArea1.setEditable(false);
        jTextArea1.setFont(resourceMap.getFont("jTextArea1.font")); // NOI18N
        jTextArea1.setRows(5);
        jTextArea1.setBorder(null);
        jTextArea1.setName("jTextArea1"); // NOI18N
        informeDeOperaciones.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(informeDeOperaciones, javax.swing.GroupLayout.DEFAULT_SIZE, 682, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(informeDeOperaciones, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), resourceMap.getString("jPanel9.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("jPanel9.border.titleFont"), resourceMap.getColor("jPanel9.border.titleColor"))); // NOI18N
        jPanel9.setToolTipText(resourceMap.getString("jPanel9.toolTipText")); // NOI18N
        jPanel9.setName("jPanel9"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTextArea3.setBackground(resourceMap.getColor("jTextArea3.background")); // NOI18N
        jTextArea3.setColumns(20);
        jTextArea3.setFont(resourceMap.getFont("jTextArea3.font")); // NOI18N
        jTextArea3.setRows(5);
        jTextArea3.setToolTipText(resourceMap.getString("jTextArea3.toolTipText")); // NOI18N
        jTextArea3.setBorder(null);
        jTextArea3.setName("jTextArea3"); // NOI18N
        jScrollPane1.setViewportView(jTextArea3);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout panelPrincipalLayout = new javax.swing.GroupLayout(panelPrincipal);
        panelPrincipal.setLayout(panelPrincipalLayout);
        panelPrincipalLayout.setHorizontalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(barradeHerramientas, javax.swing.GroupLayout.DEFAULT_SIZE, 976, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        panelPrincipalLayout.setVerticalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                .addComponent(barradeHerramientas, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        barraDeMenu.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        barraDeMenu.setToolTipText(resourceMap.getString("barraDeMenu.toolTipText")); // NOI18N
        barraDeMenu.setName("barraDeMenu"); // NOI18N

        archivo.setText(resourceMap.getString("archivo.text")); // NOI18N
        archivo.setName("archivo"); // NOI18N

        nuevoPlanMenu.setAction(actionMap.get("nuevoPlan")); // NOI18N
        nuevoPlanMenu.setIcon(resourceMap.getIcon("nuevoPlanMenu.icon")); // NOI18N
        nuevoPlanMenu.setText(resourceMap.getString("nuevoPlanMenu.text")); // NOI18N
        nuevoPlanMenu.setName("nuevoPlanMenu"); // NOI18N
        archivo.add(nuevoPlanMenu);

        abrirPlanMenu.setAction(actionMap.get("abrirPlan")); // NOI18N
        abrirPlanMenu.setIcon(resourceMap.getIcon("abrirPlanMenu.icon")); // NOI18N
        abrirPlanMenu.setText(resourceMap.getString("abrirPlanMenu.text")); // NOI18N
        abrirPlanMenu.setName("abrirPlanMenu"); // NOI18N
        archivo.add(abrirPlanMenu);

        guardarPlanMenu.setAction(actionMap.get("guardarPlan")); // NOI18N
        guardarPlanMenu.setIcon(resourceMap.getIcon("guardarPlanMenu.icon")); // NOI18N
        guardarPlanMenu.setText(resourceMap.getString("guardarPlanMenu.text")); // NOI18N
        guardarPlanMenu.setName("guardarPlanMenu"); // NOI18N
        archivo.add(guardarPlanMenu);

        nuevoComplonenteMenu.setIcon(resourceMap.getIcon("nuevoComplonenteMenu.icon")); // NOI18N
        nuevoComplonenteMenu.setText(resourceMap.getString("nuevoComplonenteMenu.text")); // NOI18N
        nuevoComplonenteMenu.setEnabled(false);
        nuevoComplonenteMenu.setName("nuevoComplonenteMenu"); // NOI18N

        nivel.setAction(actionMap.get("nuevoNivel")); // NOI18N
        nivel.setText(resourceMap.getString("nivel.text")); // NOI18N
        nivel.setName("nivel"); // NOI18N
        nuevoComplonenteMenu.add(nivel);

        accion.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        accion.setText(resourceMap.getString("accion.text")); // NOI18N
        accion.setName("accion"); // NOI18N
        accion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevaAccionActionPerformed(evt);
            }
        });
        nuevoComplonenteMenu.add(accion);

        link.setAction(actionMap.get("nuevoLink")); // NOI18N
        link.setText(resourceMap.getString("link.text")); // NOI18N
        link.setName("link"); // NOI18N
        nuevoComplonenteMenu.add(link);

        archivo.add(nuevoComplonenteMenu);

        exportarMenu.setAction(actionMap.get("exportarPlan")); // NOI18N
        exportarMenu.setText(resourceMap.getString("exportarMenu.text")); // NOI18N
        exportarMenu.setName("exportarMenu"); // NOI18N
        archivo.add(exportarMenu);

        cerrarPlanMenu.setAction(actionMap.get("cerrarPlan")); // NOI18N
        cerrarPlanMenu.setText(resourceMap.getString("cerrarPlanMenu.text")); // NOI18N
        cerrarPlanMenu.setName("cerrarPlanMenu"); // NOI18N
        archivo.add(cerrarPlanMenu);

        salirMenu.setAction(actionMap.get("quit")); // NOI18N
        salirMenu.setIcon(resourceMap.getIcon("salirMenu.icon")); // NOI18N
        salirMenu.setText(resourceMap.getString("salirMenu.text")); // NOI18N
        salirMenu.setName("salirMenu"); // NOI18N
        archivo.add(salirMenu);

        barraDeMenu.add(archivo);

        libreria.setAction(actionMap.get("editarLibreria")); // NOI18N
        libreria.setText(resourceMap.getString("libreria.text")); // NOI18N
        libreria.setName("libreria"); // NOI18N

        jMenuItem1.setAction(actionMap.get("editarLibreria")); // NOI18N
        jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        libreria.add(jMenuItem1);

        barraDeMenu.add(libreria);

        ayuda.setAction(actionMap.get("ayuda")); // NOI18N
        ayuda.setText(resourceMap.getString("ayuda.text")); // NOI18N
        ayuda.setName("ayuda"); // NOI18N

        ayudaMenu.setAction(actionMap.get("menuAyuda")); // NOI18N
        ayudaMenu.setText(resourceMap.getString("ayudaMenu.text")); // NOI18N
        ayudaMenu.setName("ayudaMenu"); // NOI18N
        ayuda.add(ayudaMenu);

        acercaDeMenu.setAction(actionMap.get("acercaDe")); // NOI18N
        acercaDeMenu.setText(resourceMap.getString("acercaDeMenu.text")); // NOI18N
        acercaDeMenu.setName("acercaDeMenu"); // NOI18N
        ayuda.add(acercaDeMenu);

        barraDeMenu.add(ayuda);

        panelDeEstado.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panelDeEstado.setName("panelDeEstado"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout panelDeEstadoLayout = new javax.swing.GroupLayout(panelDeEstado);
        panelDeEstado.setLayout(panelDeEstadoLayout);
        panelDeEstadoLayout.setHorizontalGroup(
            panelDeEstadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDeEstadoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(367, 367, 367)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(449, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDeEstadoLayout.createSequentialGroup()
                .addContainerGap(953, Short.MAX_VALUE)
                .addComponent(statusAnimationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 9, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelDeEstadoLayout.setVerticalGroup(
            panelDeEstadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(panelDeEstadoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel))
            .addComponent(statusAnimationLabel)
        );

        jFileChooser1.setName("jFileChooser1"); // NOI18N

        cargarAccion.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        cargarAccion.setTitle(resourceMap.getString("cargarAccion.title")); // NOI18N
        cargarAccion.setName("cargarAccion"); // NOI18N
        cargarAccion.setResizable(false);
        analizadorSintactico=new Sintactico(jTextArea2);
        cargarAccion.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                cargarAccionWindowClosing(evt);
            }
        });

        jPanel3.setName("jPanel3"); // NOI18N

        jTextField2.setFont(resourceMap.getFont("nombreNuevaAccion.font")); // NOI18N
        jTextField2.setToolTipText(resourceMap.getString("jTextField2.toolTipText")); // NOI18N
        jTextField2.setEnabled(false);
        jTextField2.setName("jTextField2"); // NOI18N
        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField2KeyPressed(evt);
            }
        });

        jLabel4.setFont(resourceMap.getFont("nombreNuevaAccion.font")); // NOI18N
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), resourceMap.getString("jPanel7.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("jPanel7.border.titleFont"), resourceMap.getColor("jPanel7.border.titleColor"))); // NOI18N
        jPanel7.setName("jPanel7"); // NOI18N

        botonborrarPre.setFont(resourceMap.getFont("botonborrarPost.font")); // NOI18N
        botonborrarPre.setText(resourceMap.getString("botonborrarPre.text")); // NOI18N
        botonborrarPre.setToolTipText(resourceMap.getString("botonborrarPre.toolTipText")); // NOI18N
        botonborrarPre.setEnabled(false);
        botonborrarPre.setName("botonborrarPre"); // NOI18N
        botonborrarPre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonborrarPreActionPerformed(evt);
            }
        });

        jScrollPane7.setName("jScrollPane7"); // NOI18N

        Arbol_PreCondicionAmodificar.setFont(resourceMap.getFont("botonborrarPost.font")); // NOI18N
        Arbol_PreCondicionAmodificar.setToolTipText(resourceMap.getString("Arbol_PreCondicionAmodificar.toolTipText")); // NOI18N
        Arbol_PreCondicionAmodificar.setFocusTraversalPolicyProvider(true);
        Arbol_PreCondicionAmodificar.setName("Arbol_PreCondicionAmodificar"); // NOI18N
        jScrollPane7.setViewportView(Arbol_PreCondicionAmodificar);

        botonborrarPost.setFont(resourceMap.getFont("botonborrarPost.font")); // NOI18N
        botonborrarPost.setText(resourceMap.getString("botonborrarPost.text")); // NOI18N
        botonborrarPost.setToolTipText(resourceMap.getString("botonborrarPost.toolTipText")); // NOI18N
        botonborrarPost.setEnabled(false);
        botonborrarPost.setName("botonborrarPost"); // NOI18N
        botonborrarPost.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonborrarPostActionPerformed(evt);
            }
        });

        jScrollPane8.setName("jScrollPane8"); // NOI18N

        Arbol_PostCondicionAmodificar.setFont(resourceMap.getFont("botonborrarPost.font")); // NOI18N
        Arbol_PostCondicionAmodificar.setToolTipText(resourceMap.getString("Arbol_PostCondicionAmodificar.toolTipText")); // NOI18N
        Arbol_PostCondicionAmodificar.setFocusTraversalPolicyProvider(true);
        Arbol_PostCondicionAmodificar.setName("Arbol_PostCondicionAmodificar"); // NOI18N
        jScrollPane8.setViewportView(Arbol_PostCondicionAmodificar);

        buttonGroup1.add(jRadioButtonPreC);
        jRadioButtonPreC.setFont(resourceMap.getFont("jRadioButtonPreC.font")); // NOI18N
        jRadioButtonPreC.setSelected(true);
        jRadioButtonPreC.setText(resourceMap.getString("jRadioButtonPreC.text")); // NOI18N
        jRadioButtonPreC.setName("jRadioButtonPreC"); // NOI18N

        buttonGroup1.add(jRadioButtonPostC);
        jRadioButtonPostC.setFont(resourceMap.getFont("jRadioButtonPreC.font")); // NOI18N
        jRadioButtonPostC.setText(resourceMap.getString("jRadioButtonPostC.text")); // NOI18N
        jRadioButtonPostC.setName("jRadioButtonPostC"); // NOI18N

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonborrarPre)
                    .addComponent(jRadioButtonPreC))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRadioButtonPostC)
                    .addComponent(botonborrarPost))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButtonPreC, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRadioButtonPostC, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane8, 0, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botonborrarPre)
                    .addComponent(botonborrarPost))
                .addContainerGap())
        );

        nombreNuevaAccion.setFont(resourceMap.getFont("nombreNuevaAccion.font")); // NOI18N
        nombreNuevaAccion.setToolTipText(resourceMap.getString("nombreNuevaAccion.toolTipText")); // NOI18N
        nombreNuevaAccion.setName("nombreNuevaAccion"); // NOI18N
        nombreNuevaAccion.setText("");
        nombreNuevaAccion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                nombreNuevaAccionKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                nombreNuevaAccionKeyReleased(evt);
            }
        });

        jLabel3.setFont(resourceMap.getFont("nombreNuevaAccion.font")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), resourceMap.getString("jPanel2.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("jPanel2.border.titleFont"), resourceMap.getColor("jPanel2.border.titleColor"))); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N

        jScrollPane6.setName("jScrollPane6"); // NOI18N

        jTextArea2.setBackground(resourceMap.getColor("jTextArea2.background")); // NOI18N
        jTextArea2.setColumns(20);
        jTextArea2.setEditable(false);
        jTextArea2.setFont(resourceMap.getFont("jTextArea2.font")); // NOI18N
        jTextArea2.setRows(5);
        jTextArea2.setToolTipText(resourceMap.getString("jTextArea2.toolTipText")); // NOI18N
        jTextArea2.setBorder(null);
        jTextArea2.setName("jTextArea2"); // NOI18N
        jScrollPane6.setViewportView(jTextArea2);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField2)
                            .addComponent(nombreNuevaAccion, javax.swing.GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE))))
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(nombreNuevaAccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );

        jButton10.setText(resourceMap.getString("jButton10.text")); // NOI18N
        jButton10.setName("jButton10"); // NOI18N
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton9.setText(resourceMap.getString("jButton9.text")); // NOI18N
        jButton9.setEnabled(false);
        jButton9.setName("jButton9"); // NOI18N
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        estadoComboBox.setFont(resourceMap.getFont("jRadioButtonPreC.font")); // NOI18N
        estadoComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        estadoComboBox.setName("estadoComboBox"); // NOI18N
        estadoComboBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                estadoComboBoxMouseClicked(evt);
            }
        });

        jLabel2.setFont(resourceMap.getFont("jRadioButtonPreC.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        javax.swing.GroupLayout cargarAccionLayout = new javax.swing.GroupLayout(cargarAccion.getContentPane());
        cargarAccion.getContentPane().setLayout(cargarAccionLayout);
        cargarAccionLayout.setHorizontalGroup(
            cargarAccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cargarAccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.CENTER, cargarAccionLayout.createSequentialGroup()
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton10))
                .addGroup(cargarAccionLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel2)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(estadoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(cargarAccionLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        cargarAccionLayout.setVerticalGroup(
            cargarAccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cargarAccionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 458, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(cargarAccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(estadoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton10)
                    .addComponent(jButton9))
                .addContainerGap())
        );

        exportarPlan.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        exportarPlan.setName("exportarPlan"); // NOI18N

        setComponent(panelPrincipal);
        setMenuBar(barraDeMenu);
        setStatusBar(panelDeEstado);
        setToolBar(barradeHerramientas);
    }// </editor-fold>//GEN-END:initComponents

    public void agregarPredicado(Vector<Predicado> Final,DefaultTreeModel modelotree,Vector<Predicado> tempPredicado,JButton PrePos){
        for (int i=0;i<Final.size();i++){ //recorro todos los predicados
        // si no existe la pre condicion en la accion la agrego
            if(accionactual.agregarPredicado(Final.elementAt(i),tempPredicado)){
                modelotree.insertNodeInto(Final.elementAt(i).Obtener_Nodo(),(MutableTreeNode) modelotree.getRoot(), modelotree.getChildCount(modelotree.getRoot()));
                //si entro aca significa que pude agregar la precondicion, entonces habilito para borrar
                PrePos.setEnabled(true);
            }
        }
    }

    private void jTextField2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode()==KeyEvent.VK_ENTER){
            String text = jTextField2.getText();            
            Entrada e = new TextoDeEntrada(jTextField2.getText());
            Vector<Predicado> Final=analizadorSintactico.analizar(e);
            if (Final!=null){ // si esta bien construida la pre o pos
                DefaultTreeModel modelotree = null;
                Vector<Predicado> tempPredicado = null;
                if (jRadioButtonPreC.isSelected()){
                    modelotree= (DefaultTreeModel) Arbol_PreCondicionAmodificar.getModel();
                    tempPredicado=accionactual.obtenerPreCondiciones();
                    agregarPredicado( Final,modelotree,tempPredicado,botonborrarPre);
                }
                else{
                    if (jRadioButtonPostC.isSelected()){
                        modelotree= (DefaultTreeModel) Arbol_PostCondicionAmodificar.getModel();
                        tempPredicado=accionactual.obtenerPostCondiciones();
                        agregarPredicado( Final,modelotree,tempPredicado,botonborrarPost);
                    }
                }
                
            }
            jTextField2.setText("");
        }        
}//GEN-LAST:event_jTextField2KeyPressed

    private void nombreNuevaAccionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nombreNuevaAccionKeyPressed
        // TODO add your handling code here:      
}//GEN-LAST:event_nombreNuevaAccionKeyPressed

    private void nombreNuevaAccionKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nombreNuevaAccionKeyReleased
        // TODO add your handling code here:
        if (nombreNuevaAccion.getText().isEmpty()){
            jTextField2.setEnabled(false);            
            jButton9.setEnabled(false);  
        }
        else{
            jTextField2.setEnabled(true);
            jButton9.setEnabled(true);
        }
}//GEN-LAST:event_nombreNuevaAccionKeyReleased

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        // TODO add your handling code here:        
        accionNuevaFinal=null;
        accionactual=null;
        nombreNuevaAccion.setText("");
        jTextField2.setText("");

        TreeModel modeloAPre=  Arbol_PreCondicionAmodificar.getModel();
        DefaultMutableTreeNode Pre=(DefaultMutableTreeNode) modeloAPre.getRoot();
        Pre.removeAllChildren();
        Arbol_PreCondicionAmodificar.updateUI();

        DefaultTreeModel modeloAPost=(DefaultTreeModel) Arbol_PostCondicionAmodificar.getModel();
        DefaultMutableTreeNode Post=(DefaultMutableTreeNode) modeloAPost.getRoot();
        Post.removeAllChildren();
        Arbol_PostCondicionAmodificar.updateUI();

        jTextArea2.setText("");
        jTextArea2.removeAll();

        cargarAccion.dispose();
        JFrame mainFrame = IntefaceEditordePlanes.getApplication().getMainFrame();
        mainFrame.setEnabled(true);       
        IntefaceEditordePlanes.getApplication().show(mainFrame);
    }//GEN-LAST:event_jButton10ActionPerformed

    private void cargarAccionWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_cargarAccionWindowClosing
        // TODO add your handling code here:
        IntefaceEditordePlanes.getApplication().getMainFrame().setEnabled(true);
}//GEN-LAST:event_cargarAccionWindowClosing

    private void nuevaAccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevaAccionActionPerformed
        // TODO add your handling code here:
        JFrame mainFrame = IntefaceEditordePlanes.getApplication().getMainFrame();
        cargarAccion.setLocationRelativeTo(mainFrame);
        accionactual= new Accion();
        botonborrarPre.setEnabled(false);
        botonborrarPost.setEnabled(false);
        jRadioButtonPreC.setSelected(true);
        estadoComboBox.removeAllItems();
        estadoComboBox.addItem(new String("Normal"));
        estadoComboBox.addItem(new String("Estado Inicial"));
        estadoComboBox.addItem(new String("Estado Final"));
        IntefaceEditordePlanes.getApplication().show(cargarAccion);
        mainFrame.setEnabled(false);
    }//GEN-LAST:event_nuevaAccionActionPerformed

    public void borrarPredicadosAccionNueva(JTree arbolPredicado,JButton PrePosDell, boolean cond){
        TreePath currentSelection =  arbolPredicado.getSelectionPath();
        DefaultTreeModel modelotree= (DefaultTreeModel) arbolPredicado.getModel();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (currentSelection.getLastPathComponent());
            MutableTreeNode parent = (MutableTreeNode)(currentNode.getParent());
            if (parent != null) {
               Object padre=  ((DefaultMutableTreeNode) parent).getUserObject();
               int indice=((DefaultMutableTreeNode) parent).getIndex(currentNode);
               if(((DefaultMutableTreeNode) parent).getUserObject().getClass().getName().equals("java.lang.String")){//si el padre es string significa que es la raiz
                   if(cond)
                       accionactual.obtenerPreCondiciones().removeElementAt(indice);
                   else
                       accionactual.obtenerPostCondiciones().removeElementAt(indice);
                   modelotree.removeNodeFromParent(currentNode);
               }/*
                else
                    if(currentNode.getUserObject().getClass().getName().equals("java.lang.String")){//si es string significa que es un valor
                        ((Variable) padre).cambiarValor(null);
                    }
                    else
                        ((Predicado)padre).obtenerPredicados().removeElementAt(indice);

                //modelotree.removeNodeFromParent(currentNode);*/
            }
            MutableTreeNode root=(MutableTreeNode) modelotree.getRoot();
            if (root!=null)
                if(modelotree.getChildCount((MutableTreeNode) modelotree.getRoot())<=0)
                    PrePosDell.setEnabled(false);
        }
    }
    private void botonborrarPreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonborrarPreActionPerformed
        // TODO add your handling code here:
        borrarPredicadosAccionNueva(Arbol_PreCondicionAmodificar,botonborrarPre,true);
}//GEN-LAST:event_botonborrarPreActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:       
        String text = nombreNuevaAccion.getText();
        accionactual.modificarNombre(text);
         if(estadoComboBox.getSelectedItem() != null && !((String)estadoComboBox.getSelectedItem()).equals("Normal")){
            if(((String)estadoComboBox.getSelectedItem()).equals("Estado Inicial")){
                accionNuevaFinal= accionactual;
                plan.definirEstadoInicial(accionNuevaFinal);
                reDibujar();
            }
            else{
                accionNuevaFinal= accionactual;
                accionNuevaFinal.modificarNivel(plan.cantNivelesJGraph()+1);
                plan.definirEstadoFinal(accionNuevaFinal);
                reDibujar();
            }
        }
        else{
            lib_actual.AgregarAccion(accionactual);  
            DefaultMutableTreeNode nodoNuevo = accionactual.Obtener_Nodo();
            DefaultMutableTreeNode raux = (DefaultMutableTreeNode) this.jTree1.getModel().getRoot();
            raux.add(nodoNuevo);
            this.jTree1.updateUI();
        }
        try {
            setPlanClonado((Plan) plan.clone());
            originador.set(getPlanClonado());
            portero.addMemento(originador.salvarEnRecuerdo());
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(InterfaceEditordePlanesView.class.getName()).log(Level.SEVERE, null, ex);
        }
        accionactual=null;
        nombreNuevaAccion.setText("");
        jTextField2.setText("");

        TreeModel modeloAPre=  Arbol_PreCondicionAmodificar.getModel();
        DefaultMutableTreeNode Pre=(DefaultMutableTreeNode) modeloAPre.getRoot();
        Pre.removeAllChildren();
        Arbol_PreCondicionAmodificar.updateUI();

        DefaultTreeModel modeloAPost=(DefaultTreeModel) Arbol_PostCondicionAmodificar.getModel();
        DefaultMutableTreeNode Post=(DefaultMutableTreeNode) modeloAPost.getRoot();
        Post.removeAllChildren();
        Arbol_PostCondicionAmodificar.updateUI();
        
        jTextArea2.setText("");
        jTextArea2.removeAll();

        cargarAccion.dispose();
        JFrame mainFrame = IntefaceEditordePlanes.getApplication().getMainFrame();
        mainFrame.setEnabled(true);
        IntefaceEditordePlanes.getApplication().show(mainFrame);
    }//GEN-LAST:event_jButton9ActionPerformed

    private void botonborrarPostActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonborrarPostActionPerformed
        // TODO add your handling code here:
        borrarPredicadosAccionNueva(Arbol_PostCondicionAmodificar,botonborrarPost,false);
    }//GEN-LAST:event_botonborrarPostActionPerformed

    private void undoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoActionPerformed
        // TODO add your handling code here:
        undo();
    }//GEN-LAST:event_undoActionPerformed

    private void redoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redoActionPerformed
        // TODO add your handling code here:
        redo();
    }//GEN-LAST:event_redoActionPerformed

    private void removeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeActionPerformed
        // TODO add your handling code here:
        if (!grafo.isSelectionEmpty()) {
            Hashtable mapAccion= M.getNodoAccion();
            Hashtable mapArcos= M.getLinksArcos();
            Hashtable mapLinks= M.getArcosLinks();
            Object[] cells = grafo.getSelectionCells();
            cells = grafo.getDescendants(cells);
            for(int i=0; i < cells.length; i++){
                Accion a= (Accion) mapAccion.get(cells[i]);
                if(a != null){
                    Object[] borrar= new Object[a.obtenerLinksOrigen().size()+a.obtenerLinksDestino().size()];
                    int j= 0;
                    for(Enumeration e= mapArcos.keys();e.hasMoreElements();){
                        Link l1= (Link) e.nextElement();
                        if(l1 != null)
                        if(l1.obtenerAccionOrigen().equals(a) || l1.obtenerAccionDestino().equals(a)){
                            borrar[j]= mapArcos.get(l1);
                            mapLinks.remove(mapArcos.get(l1));
                            mapArcos.remove(l1);
                            j++;
                        }
                    }
                    grafo.getModel().remove(borrar);
                    plan.removerAccion(a);
                }
                Link l= (Link) mapLinks.get(cells[i]);
                if(l != null && plan.existeLink(l)){
                   plan.removerLink2(l);
                   DefaultEdge de= (DefaultEdge) mapArcos.get(l);
                   mapArcos.remove(l);
                   mapLinks.remove(de);
                }
            }
            grafo.getModel().remove(cells);
            try {
                setPlanClonado((Plan) plan.clone());
                originador.set(planClonado);
                portero.addMemento( originador.salvarEnRecuerdo());
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(InterfaceEditordePlanesView.class.getName()).log(Level.SEVERE, null, ex);
            }
		}
    }//GEN-LAST:event_removeActionPerformed

    private void connectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectActionPerformed
        // TODO add your handling code here:
		grafo.setPortsVisible(!grafo.isPortsVisible());
		URL connectUrl;
        if (grafo.isPortsVisible())
			connectUrl = getClass().getClassLoader().getResource("interfaceEditordePlanes/resources/connection.png");
		else
			connectUrl = getClass().getClassLoader().getResource("interfaceEditordePlanes/resources/connectionoff.png");
        ImageIcon connectIcon = new ImageIcon(connectUrl);
        connect.setIcon(connectIcon);
    }//GEN-LAST:event_connectActionPerformed

    private void botonHorizontalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonHorizontalActionPerformed
        // TODO add your handling code here:
        URL connectUrl;
        if(grafohorizontal) {
            //botonHorizontal.setText("Vertical");
            grafohorizontal=false;
            connectUrl = getClass().getClassLoader().getResource("interfaceEditordePlanes/resources/vertical.png");
        }
        else {
           //botonHorizontal.setText("Horizontal");
            grafohorizontal=true;
            connectUrl = getClass().getClassLoader().getResource("interfaceEditordePlanes/resources/horizontal.png");
        }
        ImageIcon connectIcon = new ImageIcon(connectUrl);
        botonHorizontal.setIcon(connectIcon);
        layout(grafo, false, grafohorizontal);
}//GEN-LAST:event_botonHorizontalActionPerformed

    public void botonHorizontal2(java.awt.event.ActionEvent evt){
        botonHorizontalActionPerformed(evt);
    }
    private void checkAmenazaOnlineMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checkAmenazaOnlineMouseClicked
        // TODO add your handling code here:
         if(plan != null){
            if(checkAmenazaOnline.isSelected()){
                if(planvalidador.validarPlan(plan)){
                    mostrarMensaje("Se activo el chequeo de Amenazas.");
                    solucion.setEnabled(true);
                }
                else{                    
                    mostrarMensaje("No se pudo activar el chequeo de amenazas.");
                    checkAmenazaOnline.setSelected(false);                    
                }
            }
            else{
                mostrarMensaje("Se desactivo el chequeo de Amenazas");
                solucion.setEnabled(false);
            }
        }
    }//GEN-LAST:event_checkAmenazaOnlineMouseClicked

    private void estadoComboBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_estadoComboBoxMouseClicked
        // TODO add your handling code here:
}//GEN-LAST:event_estadoComboBoxMouseClicked

    private void moverAccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moverAccionActionPerformed
        // TODO add your handling code here:
      moverAccion();
    }//GEN-LAST:event_moverAccionActionPerformed

   
    public static void layout(JGraph graph, boolean organic, boolean horizontal) {
		JGraphFacade facade = createFacade(graph);
		if (!organic) {
			JGraphHierarchicalLayout layout = new JGraphHierarchicalLayout();
			if (horizontal) {
				layout.setOrientation(SwingConstants.WEST);
			}
            else{
                layout.setOrientation(SwingConstants.NORTH);

            }
			layout.setInterRankCellSpacing(50);
			layout.setIntraCellSpacing(60);
			try {
				layout.run(facade);
			} catch (Exception e) {
				System.err.println(e.getMessage());
				organic = true;
			}
		}
		if (organic) {
			JGraphLayout layout = new JGraphSimpleLayout(
			JGraphSimpleLayout.TYPE_CIRCLE);
			layout.run(facade);
			JGraphFastOrganicLayout fastOrganicLayout = new JGraphFastOrganicLayout();
			fastOrganicLayout.setForceConstant(80);
			fastOrganicLayout.run(facade);
			JGraphOrganicLayout organicLayout = new JGraphOrganicLayout();
			organicLayout.setRadiusScaleFactor(0.9);
			organicLayout.setNodeDistributionCostFactor(8000000.0);
			organicLayout.setOptimizeBorderLine(false);
			organicLayout.setDeterministic(true);
			organicLayout.run(facade);
		}
		Map map = facade.createNestedMap(true, true);
		graph.getGraphLayoutCache().edit(map, null, null, null);
	}
    /**
	 * Creates a {@link JGraphFacade}.
	 *
	 * @param graph
	 *            The graph to use for the facade.
	 * @return Returns a new facade for the specified graph.
	 */
	protected static JGraphFacade createFacade(JGraph graph) {
		JGraphFacade facade = new JGraphFacade(graph);
		facade.setIgnoresUnconnectedCells(true);
		facade.setIgnoresCellsInGroups(true);
		facade.setIgnoresHiddenCells(true);
		facade.setDirected(true);
		return facade;
	}
    //-------FIN DE TODO LAYOUT------------------
    protected GraphUndoManager undoManager;    
	protected int cellCount = 0;
    public void GraphEdition() {     
		grafo = createGraph(); 
     	grafo.setMarqueeHandler(createMarqueeHandler());		
		undoManager = new GraphUndoManager() {			
			public void undoableEditHappened(UndoableEditEvent e) {				
				super.undoableEditHappened(e);				
				updateHistoryButtons();
			}
		};
        installListeners(grafo);
    }

    public void destroy() {
		PortView.renderer = new PortRenderer();
		EdgeView.renderer = new EdgeRenderer();
		AbstractCellView.cellEditor = new DefaultGraphCellEditor();
		VertexView.renderer = new VertexRenderer();
        DefaultGraphModel ob = null;
        ob= (DefaultGraphModel) getGrafo().getModel();
        ob.remove(getGrafo().getRoots());
        getGrafo().clearOffscreen();
	}
    
	protected JGraph createGraph() {       
        MyModel m= new MyModel();
  		JGraph graph = new MyGraph(m);
		graph.getGraphLayoutCache().setFactory(new DefaultCellViewFactory() {
		protected EdgeView createEdgeView(Object cell) {
				return new EdgeView(cell) {

					public CellHandle getHandle(GraphContext context) {
						return new MyEdgeHandle(this, context);
					}
				};
			}
		});
		return graph;
	}
    
	protected void installListeners(JGraph graph) {		
		graph.getModel().addUndoableEditListener(undoManager);
		graph.getSelectionModel().addGraphSelectionListener(this);		
		graph.addKeyListener(this);
	}
    
	protected BasicMarqueeHandler createMarqueeHandler() {
		return new MyMarqueeHandler();
	}

	public void insert(Point2D point) {
        insertar();
	}


    public void insert_2(Point2D point){
        if(plan.cantidadNivelesAcciones() == -1)
            habilitarComandos();
        plan.agregarNivel();
        reDibujar();
    }

    public void remove_nivel(Point2D point){
        plan.borrarNivel(plan.cantidadNivelesAcciones());
        if(plan.cantidadNivelesAcciones() == -1)
           desabilitarComandos();
        reDibujar();
    }

    public void insert_link(Point2D point){
        nuevoLink();
    }

	public Map createCellAttributes(Point2D point) {
		Map map = new Hashtable();
		if (grafo != null) {
			point = grafo.snap((Point2D) point.clone());
		} else {
			point = (Point2D) point.clone();
		}
		GraphConstants.setBounds(map, new Rectangle2D.Double(point.getX(), point.getY(), 0, 0));
		GraphConstants.setResize(map, true);
		GraphConstants.setGradientColor(map, Color.blue);
		GraphConstants.setBorderColor(map, Color.black);
		GraphConstants.setBackground(map, Color.white);
		GraphConstants.setOpaque(map, true);
		return map;
	}

	protected DefaultGraphCell createDefaultGraphCell() {
		DefaultGraphCell cell = new DefaultGraphCell("Cell "+ new Integer(cellCount++));
		cell.addPort();
		return cell;
	}

	public void connect(Port source, Port target) {
        nuevoLink();
	}

	protected DefaultEdge createDefaultEdge() {
		return new DefaultEdge();
	}

	public Map createEdgeAttributes() {
		Map map = new Hashtable();
		GraphConstants.setLineEnd(map, GraphConstants.ARROW_SIMPLE);
		GraphConstants.setLabelAlongEdge(map, true);
		return map;
	}

	public void group(Object[] cells) {
		cells = grafo.order(cells);
		if (cells != null && cells.length > 0) {
			DefaultGraphCell group = createGroupCell();
			grafo.getGraphLayoutCache().insertGroup(group, cells);
		}
	}

	protected DefaultGraphCell createGroupCell() {
		return new DefaultGraphCell();
	}

	protected int getCellCount(JGraph graph) {
		Object[] cells = graph.getDescendants(graph.getRoots());
		return cells.length;
	}

	public void ungroup(Object[] cells) {
		grafo.getGraphLayoutCache().ungroup(cells);
	}

	public boolean isGroup(Object cell) {
		CellView view = grafo.getGraphLayoutCache().getMapping(cell, false);
		if (view != null)
			return !view.isLeaf();
		return false;
	}

    public void reestablecerPLan(){
        if(portero.getPosActual()<0)
                originador.reestablecerDeRecuerdo(portero.getMemento(0));
            else
                if(portero.getPosActual()>portero.getEstadosSalvados().size()-1)
                    originador.reestablecerDeRecuerdo(portero.getMemento(portero.getEstadosSalvados().size()));
                else
                    originador.reestablecerDeRecuerdo(portero.getMemento(portero.getPosActual()));
             setPlan((Plan) originador.getState());
             M=new Mostrar(plan, grafo);
    }

    public void undo() {
		try {
			undoManager.undo(grafo.getGraphLayoutCache());

            portero.decrementarPosActual();
            reestablecerPLan();

		} catch (Exception ex) {
			System.err.println(ex);
		} finally {
			updateHistoryButtons();
           
		}
	}
	
	public void redo() {
		try {
			undoManager.redo(grafo.getGraphLayoutCache());

            portero.incrementarPosActual();
            reestablecerPLan();

		} catch (Exception ex) {
			System.err.println(ex);
		} finally {
			updateHistoryButtons();
           
		}
	}

	protected void updateHistoryButtons() {
		undo.setEnabled(undoManager.canUndo(grafo.getGraphLayoutCache()));
		redo.setEnabled(undoManager.canRedo(grafo.getGraphLayoutCache()));
	}

    public void valueChanged(GraphSelectionEvent e) {
		boolean enabled = !grafo.isSelectionEmpty();
		remove.setEnabled(enabled);
	}

    public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_DELETE)
			removeActionPerformed(null);
	}

    public static class MyGraph extends JGraph {
		public MyGraph(GraphModel model) {
			this(model, null);
		}
		public MyGraph(GraphModel model, GraphLayoutCache cache) {
			super(model, cache);
            setBackground(Color.white);
			setPortsVisible(true);
			setGridEnabled(false);
            setGridColor(Color.darkGray);
			setGridSize(8);
			setTolerance(4);
			setInvokesStopCellEditing(true);
			setCloneable(true);
			setJumpToDefaultPort(true);
		}

	}
	public static class MyEdgeHandle extends EdgeView.EdgeHandle {
		public MyEdgeHandle(EdgeView edge, GraphContext ctx) {
			super(edge, ctx);
		}
		public boolean isAddPointEvent(MouseEvent event) {
			return event.isShiftDown();
		}

        public boolean isRemovePointEvent(MouseEvent event) {
			return event.isShiftDown();
		}

	}

    public static class MyModel extends DefaultGraphModel {
		public boolean acceptsSource(Object edge, Object port) {
			return (((Edge) edge).getTarget() != port);
		}
		public boolean acceptsTarget(Object edge, Object port) {
			return (((Edge) edge).getSource() != port);
		}
	}

    public class MyMarqueeHandler extends BasicMarqueeHandler {
		protected Point2D start, current;
		protected PortView port, firstPort;
		public boolean isForceMarqueeEvent(MouseEvent e) {
			if (e.isShiftDown())
				return false;
			if (SwingUtilities.isRightMouseButton(e))
				return true;
			port = getSourcePortAt(e.getPoint());
			if (port != null && grafo.isPortsVisible())
				return true;
			return super.isForceMarqueeEvent(e);
		}

        public void mousePressed(final MouseEvent e) {
            if(areaGrafica.isEnabled())
                if (SwingUtilities.isRightMouseButton(e)) {
                    Object cell = grafo.getFirstCellForLocation(e.getX(), e.getY());
                    JPopupMenu menu = createPopupMenu(e.getPoint(), cell);
                    menu.show(grafo, e.getX(), e.getY());                    
                } 
                else
                    if (port != null && grafo.isPortsVisible()) {
                        start = grafo.toScreen(port.getLocation());
                        firstPort = port;
                    }
                    else{
                        super.mousePressed(e);
                    }
            
             if (SwingUtilities.isMiddleMouseButton(e)){
                 jTextArea3.setText(null);
                 Object[] cells = grafo.getSelectionCells();
                 Hashtable ha= M.getNodoAccion();
                 Hashtable hl= M.getArcosLinks();
                 for(int i=0; i < cells.length; i++){
                     Accion a= (Accion) ha.get(cells[i]);
                     Link l= (Link) hl.get(cells[i]);
                     if(a!=null){
                        jTextArea3.append("Accion\n");
                        jTextArea3.append("------\n");
                        jTextArea3.append("Nombre: "+a.obtenerNombre()+"\n");
                        jTextArea3.append("Nivel: "+a.obtenerNivel()+"\n");
                        jTextArea3.append("Precondiciones:\n");
                        Vector<Predicado> vp= a.obtenerPreCondiciones();
                        for(Enumeration<Predicado> e2= vp.elements();e2.hasMoreElements();)
                            jTextArea3.append("- "+e2.nextElement().toString()+"\n");
                        jTextArea3.append("Postcondiciones:\n");
                        vp= a.obtenerPostCondiciones();
                        for(Enumeration<Predicado> e2= vp.elements();e2.hasMoreElements();)
                            jTextArea3.append("- "+e2.nextElement().toString()+"\n");
                    }
                    if(l!=null){
                        jTextArea3.append("Link\n");
                        jTextArea3.append("----\n");
                        jTextArea3.append("Accion: "+l.obtenerAccionOrigen().toString()+"\n");
                        jTextArea3.append("Predicado: "+l.obtenerPredicado().toString()+"\n");
                        jTextArea3.append("Accion: "+l.obtenerAccionDestino().toString()+"\n");
                    }
                 }
             }
		}

		public void mouseDragged(MouseEvent e) {
			if (start != null) {
				Graphics g = grafo.getGraphics();
				PortView newPort = getTargetPortAt(e.getPoint());
				if (newPort == null || newPort != port) {
					paintConnector(Color.black, grafo.getBackground(), g);
					port = newPort;
					if (port != null)
						current = grafo.toScreen(port.getLocation());
					else
						current = grafo.snap(e.getPoint());
					paintConnector(grafo.getBackground(), Color.black, g);
				}
			}
			super.mouseDragged(e);
		}

		public PortView getSourcePortAt(Point2D point) {
			grafo.setJumpToDefaultPort(false);
			PortView result;
			try {
				result = grafo.getPortViewAt(point.getX(), point.getY());
			} finally {
				grafo.setJumpToDefaultPort(true);
			}
			return result;
		}

		protected PortView getTargetPortAt(Point2D point) {
			return grafo.getPortViewAt(point.getX(), point.getY());
		}

		public void mouseReleased(MouseEvent e) {
			if (e != null && port != null && firstPort != null
					&& firstPort != port) {
				connect((Port) firstPort.getCell(), (Port) port.getCell());
				e.consume();
			} else
				grafo.repaint();
			firstPort = port = null;
			start = current = null;
			super.mouseReleased(e);
		}

		public void mouseMoved(MouseEvent e) {
			if (e != null && getSourcePortAt(e.getPoint()) != null
					&& grafo.isPortsVisible()) {
				grafo.setCursor(new Cursor(Cursor.HAND_CURSOR));
				e.consume();
			} else
				super.mouseMoved(e);
		}

		protected void paintConnector(Color fg, Color bg, Graphics g) {
			g.setColor(fg);
			g.setXORMode(bg);
			paintPort(grafo.getGraphics());
			if (firstPort != null && start != null && current != null)
				g.drawLine((int) start.getX(), (int) start.getY(),
						(int) current.getX(), (int) current.getY());
		}

		protected void paintPort(Graphics g) {
			if (port != null) {
				boolean o = (GraphConstants.getOffset(port.getAllAttributes()) != null);
				Rectangle2D r = (o) ? port.getBounds() : port.getParentView()
						.getBounds();
				r = grafo.toScreen((Rectangle2D) r.clone());
				r.setFrame(r.getX() - 3, r.getY() - 3, r.getWidth() + 6, r
						.getHeight() + 6);
				grafo.getUI().paintCell(g, port, r, true);
			}
		}
	}

    public JPopupMenu createPopupMenu(final Point pt, final Object cell) {
		JPopupMenu menu = new JPopupMenu();
		if (!grafo.isSelectionEmpty()) {
			menu.addSeparator();
			menu.add(new AbstractAction("Remover Elemento") {
				public void actionPerformed(ActionEvent e) {
                    removeActionPerformed(e);
				}
			});
		}
        if(plan.cantidadNivelesAcciones() >= 0){
            menu.addSeparator();
            menu.add(new AbstractAction("Insertar Accion") {
                public void actionPerformed(ActionEvent ev) {
                    insert(pt);
                }
            });
            menu.addSeparator();
            menu.add(new AbstractAction("Insertar Link") {
                public void actionPerformed(ActionEvent ev) {
                    insert_link(pt);
                }
            });
        }
        menu.addSeparator();
		menu.add(new AbstractAction("Insertar Nivel") {
			public void actionPerformed(ActionEvent ev) {
				insert_2(pt);
			}
		});
        if(plan.cantidadNivelesAcciones() >= 0){
            menu.add(new AbstractAction("Remover Nivel") {
                public void actionPerformed(ActionEvent ev) {
                    remove_nivel(pt);
                }
            });
        }
        menu.addSeparator();
		return menu;
	}

    public  JTextArea getJTextAreaErrores() {
		return jTextArea2;
    }

        public Originador getOriginador() {
        return originador;
    }

    public void setOriginador(Originador originador) {
        this.originador = originador;
    }

    public Plan getPlanClonado() {
        return planClonado;
    }

    public void setPlanClonado(Plan planClonado) {
        this.planClonado = planClonado;
    }

    public Portero getPortero() {
        return portero;
    }

    public void setPortero(Portero portero) {
        this.portero = portero;
    }

     public JGraph getGrafo() {
        return grafo;
    }

    public void setGrafo(JGraph grafo) {
        this.grafo = grafo;
    }

    public Mostrar getM() {
        return M;
    }

    public void setM(Mostrar M) {
        this.M = M;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public Accion getAccionNuevaFinal() {
        return accionNuevaFinal;
    }

    public void setAccionNuevaFinal(Accion accionNuevaFinal) {
        this.accionNuevaFinal = accionNuevaFinal;
    }

    public Accion getAccionactual() {
        return accionactual;
    }

    public void setAccionactual(Accion accionactual) {
        this.accionactual = accionactual;
    }

    public boolean isGrafohorizontal() {
        return grafohorizontal;
    }

    public void setGrafohorizontal(boolean grafohorizontal) {
        this.grafohorizontal = grafohorizontal;
    }
    public JButton getBotonHorizontal() {
        return botonHorizontal;
    }

    public void setBotonHorizontal(JButton botonHorizontal) {
        this.botonHorizontal = botonHorizontal;
    }
    
    public void setLibreria(Libreria L){
        lib_actual = L;
    }
    //Esto es para probar cuando el usuario en edicion de libreria hace cargarLibreriaDeTrabajo, 
    //es como si le pasaramos la libreria que esta utilizando para trabajar
    public Libreria getLibreriaDeTrabajo(){
        return lib_actual;
    }

    public JTree getJTree1() {
        return jTree1;
    }

    public void setJTree1(JTree jTree1) {
        this.jTree1 = jTree1;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTree Arbol_PostCondicionAmodificar;
    private javax.swing.JTree Arbol_PreCondicionAmodificar;
    private javax.swing.JButton abrirPlan;
    private javax.swing.JMenuItem abrirPlanMenu;
    private javax.swing.JMenuItem accion;
    private javax.swing.JMenuItem acercaDeMenu;
    private javax.swing.JScrollPane areaGrafica;
    private javax.swing.JMenuItem ayudaMenu;
    private javax.swing.JMenuBar barraDeMenu;
    private javax.swing.JToolBar barradeHerramientas;
    private javax.swing.JButton borrarAccion;
    private javax.swing.JButton borrarLink;
    private javax.swing.JButton borrarNivel;
    private javax.swing.JButton botonHorizontal;
    private javax.swing.JButton botonborrarPost;
    private javax.swing.JButton botonborrarPre;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JFrame cargarAccion;
    private javax.swing.JButton cerrarPlan;
    private javax.swing.JMenuItem cerrarPlanMenu;
    private javax.swing.JCheckBox checkAmenazaOnline;
    private javax.swing.JButton connect;
    private javax.swing.JButton editarLibreria;
    private javax.swing.JComboBox estadoComboBox;
    private javax.swing.JMenuItem exportarMenu;
    private javax.swing.JFileChooser exportarPlan;
    private javax.swing.JButton guardarPlan;
    private javax.swing.JMenuItem guardarPlanMenu;
    private javax.swing.JScrollPane informeDeOperaciones;
    private javax.swing.JButton insertar;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton9;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JRadioButton jRadioButtonPostC;
    private javax.swing.JRadioButton jRadioButtonPreC;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparator9;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTree jTree1;
    private javax.swing.JMenu libreria;
    private javax.swing.JScrollPane libreriaPorDefectoArbol;
    private javax.swing.JMenuItem link;
    private javax.swing.JButton moverAccion;
    private javax.swing.JMenuItem nivel;
    private javax.swing.JTextField nombreNuevaAccion;
    private javax.swing.JButton nuevaAccion;
    private javax.swing.JMenu nuevoComplonenteMenu;
    private javax.swing.JButton nuevoLink;
    private javax.swing.JButton nuevoNivel;
    private javax.swing.JMenuItem nuevoPlanMenu;
    private javax.swing.JPanel panelDeEstado;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JButton redo;
    private javax.swing.JButton remove;
    private javax.swing.JButton solucion;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JButton undo;
    private javax.swing.JButton validarPlan;
    private javax.swing.JButton zoomIn;
    private javax.swing.JButton zoomOut;
    private javax.swing.JButton zoomStd;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;
    private JFrame menuAyuda;

    private Plan plan,planClonado=null;
    private JFrame nuevoNivelBox;
    private JFrame borrarNivelBox;
    private JFrame nuevoLinkBox;
    private JFrame amenazaBox;;
    private JFrame moverAccionBox;
    private JFrame borrarAccionBox;
    private JFrame borrarLinkBox;
    private JFrame insertarAccionGrafoBox;
    private JFrame edicionLibreria;

    private PlanValidador planvalidador;

    private Portero portero = new Portero();
    private Originador originador = new Originador();

    DefaultListModel modeloJList1 = new DefaultListModel();
    DefaultListModel modeloJList2 = new DefaultListModel();
    private Accion accionactual;
    private Accion accionNuevaFinal;
    private Sintactico analizadorSintactico;
    private JGraph grafo;
    private boolean grafohorizontal=true;
    private Mostrar M;
    boolean cambio;
    private Libreria lib_actual;
    
    private DefaultMutableTreeNode RaizLib;
    private DefaultTreeModel modeloLib;
}
