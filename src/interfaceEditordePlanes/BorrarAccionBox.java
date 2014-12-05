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

public class BorrarAccionBox extends javax.swing.JFrame {

    /** Creates new form BorrarAccionBox */
    private Plan plan;
    private Accion accion_borrar;
    private InterfaceEditordePlanesView itv;

    public BorrarAccionBox(Plan plan, InterfaceEditordePlanesView itv) {
        initComponents();
        this.plan= plan;
        this.itv= itv;
        accion_borrar= null;
        borrar.setEnabled(false);
        accionesBox.removeAllItems();
        Vector<Accion> acciones= plan.obtenerAcciones();
        for(Enumeration<Accion> e= acciones.elements();e.hasMoreElements();)
            accionesBox.addItem(e.nextElement());
        if(accionesBox.getItemCount() <= 0){
            borrar.setEnabled(false);
            accionesBox.setEnabled(false);
        }
        else{
            borrar.setEnabled(true);
            accionesBox.setEnabled(true);

        }
    }
    @Action
    public void borrar() throws CloneNotSupportedException{
        plan.removerAccion(accion_borrar);
        accionesBox.removeAllItems();
        Vector<Accion> acciones= plan.obtenerAcciones();
        for(Enumeration<Accion> e= acciones.elements();e.hasMoreElements();)
            accionesBox.addItem(e.nextElement());
        if(accionesBox.getItemCount() <= 0){
            borrar.setEnabled(false);
            accionesBox.setEnabled(false);
        }
        itv.reDibujar();
        //---- recuerdo el estado nuevo del plan
        itv.setPlanClonado((Plan) plan.clone());
        itv.getOriginador().set(itv.getPlanClonado());
        itv.getPortero().addMemento( itv.getOriginador().salvarEnRecuerdo());
        //----
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

        accionesBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        borrar = new javax.swing.JButton();
        aceptar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(interfaceEditordePlanes.IntefaceEditordePlanes.class).getContext().getResourceMap(BorrarAccionBox.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        accionesBox.setFont(resourceMap.getFont("accionesBox.font")); // NOI18N
        accionesBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        accionesBox.setName("accionesBox"); // NOI18N
        accionesBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                accionesBoxActionPerformed(evt);
            }
        });

        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(interfaceEditordePlanes.IntefaceEditordePlanes.class).getContext().getActionMap(BorrarAccionBox.class, this);
        borrar.setAction(actionMap.get("borrar")); // NOI18N
        borrar.setFont(resourceMap.getFont("borrar.font")); // NOI18N
        borrar.setText(resourceMap.getString("borrar.text")); // NOI18N
        borrar.setName("borrar"); // NOI18N

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
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(accionesBox, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(borrar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(144, Short.MAX_VALUE)
                .addComponent(aceptar)
                .addGap(136, 136, 136))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(accionesBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(borrar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(aceptar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        aceptar();
    }//GEN-LAST:event_formWindowClosing

    private void accionesBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_accionesBoxActionPerformed
        // TODO add your handling code here:
        JComboBox cb = (JComboBox)evt.getSource();
        accion_borrar= (Accion)cb.getSelectedItem();
        if(accion_borrar !=null && accionesBox.getItemCount() > 0)
            borrar.setEnabled(true);
    }//GEN-LAST:event_accionesBoxActionPerformed

    /**
    * @param args the command line arguments
    */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox accionesBox;
    private javax.swing.JButton aceptar;
    private javax.swing.JButton borrar;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables

}