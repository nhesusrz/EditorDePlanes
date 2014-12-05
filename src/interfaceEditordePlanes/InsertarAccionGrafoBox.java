package interfaceEditordePlanes;

import editorDePlanes.Accion;
import editorDePlanes.Plan;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JComboBox;
import org.jdesktop.application.Action;

/**
 *
 * @author mpacheco
 */

public class InsertarAccionGrafoBox extends javax.swing.JFrame {

    /** Creates new form InsertarAccionGrafoBox */
    private Plan plan;
    private Accion accion_elegida;
    private Integer nivel;
    private Vector<Accion> accionesLib;
    private InterfaceEditordePlanesView itv;

    public InsertarAccionGrafoBox(Plan plan, InterfaceEditordePlanesView itv) {
        initComponents();
        this.plan= plan;        
        this.itv= itv;
        accion_elegida= null;
        nivel= null;
        acciones.removeAllItems();
        niveles.removeAllItems();
        insertar.setEnabled(false);
        Integer n= plan.cantidadNivelesAcciones();
        for(Integer i=new Integer(0); i <= n; i++)
            niveles.addItem(i);
        if(itv.getLibreriaDeTrabajo() != null){
            accionesLib= itv.getLibreriaDeTrabajo().obtenerAcciones();
            for(Enumeration<Accion> e= accionesLib.elements(); e.hasMoreElements();){
                Accion accion= e.nextElement();
                acciones.addItem(accion);
            }
        }
    }
    @Action
    public void insertar() throws CloneNotSupportedException{
        if(accion_elegida != null && nivel != null){
            accion_elegida.modificarNivel(nivel);
            accion_elegida.anularLinkOrYDst();
            itv.insertarAccionDeLibreria(accion_elegida);
        }
    }
    @Action
    public void aceptar(){
        IntefaceEditordePlanes.getApplication().getMainFrame().setEnabled(true);
        dispose();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        acciones = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        niveles = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        insertar = new javax.swing.JButton();
        aceptar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(interfaceEditordePlanes.IntefaceEditordePlanes.class).getContext().getResourceMap(InsertarAccionGrafoBox.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        acciones.setFont(resourceMap.getFont("acciones.font")); // NOI18N
        acciones.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        acciones.setName("acciones"); // NOI18N
        acciones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                accionesActionPerformed(evt);
            }
        });

        jLabel1.setFont(resourceMap.getFont("acciones.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        niveles.setFont(resourceMap.getFont("acciones.font")); // NOI18N
        niveles.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        niveles.setName("niveles"); // NOI18N
        niveles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nivelesActionPerformed(evt);
            }
        });

        jLabel2.setFont(resourceMap.getFont("acciones.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(interfaceEditordePlanes.IntefaceEditordePlanes.class).getContext().getActionMap(InsertarAccionGrafoBox.class, this);
        insertar.setAction(actionMap.get("insertar")); // NOI18N
        insertar.setFont(resourceMap.getFont("aceptar.font")); // NOI18N
        insertar.setText(resourceMap.getString("insertar.text")); // NOI18N
        insertar.setName("insertar"); // NOI18N

        aceptar.setAction(actionMap.get("aceptar")); // NOI18N
        aceptar.setFont(resourceMap.getFont("aceptar.font")); // NOI18N
        aceptar.setText(resourceMap.getString("aceptar.text")); // NOI18N
        aceptar.setName("aceptar"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(acciones, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(niveles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(insertar))
                    .addComponent(aceptar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(acciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(niveles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(insertar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addComponent(aceptar)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        aceptar();
    }//GEN-LAST:event_formWindowClosing

    private void accionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_accionesActionPerformed
        // TODO add your handling code here:
        JComboBox cb = (JComboBox)evt.getSource();
        accion_elegida= (Accion)cb.getSelectedItem();
        if(accion_elegida != null && nivel != null)
            insertar.setEnabled(true);
        else
            insertar.setEnabled(false);
    }//GEN-LAST:event_accionesActionPerformed

    private void nivelesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nivelesActionPerformed
        // TODO add your handling code here:
        JComboBox cb = (JComboBox)evt.getSource();
         nivel= (Integer)cb.getSelectedItem();
        if(nivel != null &&accion_elegida != null )
            insertar.setEnabled(true);
        else
            insertar.setEnabled(false);
    }//GEN-LAST:event_nivelesActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox acciones;
    private javax.swing.JButton aceptar;
    private javax.swing.JButton insertar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JComboBox niveles;
    // End of variables declaration//GEN-END:variables

}
