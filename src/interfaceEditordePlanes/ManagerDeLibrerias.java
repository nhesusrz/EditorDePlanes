package interfaceEditordePlanes;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.persistence.FileStreamStrategy;
import com.thoughtworks.xstream.persistence.StreamStrategy;
import editorDePlanes.Libreria;
import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mpacheco
 */

public class ManagerDeLibrerias extends javax.swing.JFrame {
    
    /**
     * Creates new form ManagerDeLibrerias
     */
    public ManagerDeLibrerias() {
        this.LibreriaAguardar = null;
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();

        jFileChooser1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jFileChooser1, javax.swing.GroupLayout.DEFAULT_SIZE, 623, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addComponent(jFileChooser1, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jFileChooser1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser1ActionPerformed
        jFileChooser1.setCurrentDirectory(new File(".//librerias//"));
        if (this.jFileChooser1.getApproveButtonText().equalsIgnoreCase("Abrir")){
                XStream traductor = new XStream(new DomDriver());
                traductor.setMode(XStream.ID_REFERENCES);

                File FuenteSeleccionada= jFileChooser1.getSelectedFile();
                if (FuenteSeleccionada == null){
                    return;
                }
                String nombreSeleccionado= FuenteSeleccionada.getName();
                StreamStrategy strategy = new FileStreamStrategy(new File(".\\librerias\\"),traductor);//usar file persistence strategy
                FileInputStream f = null;
            try {
                f = new FileInputStream(".\\librerias\\" + nombreSeleccionado);
                
                
            } catch (FileNotFoundException ex) {
                    
                Logger.getLogger(ManagerDeLibrerias.class.getName()).log(Level.SEVERE, null, ex);
                    
            }
                this.elementoSeleccionado = (Libreria) traductor.fromXML(f);
                String nombreLibreriaSeleccionada = elementoSeleccionado.obtenerNombre();
                System.out.println(nombreLibreriaSeleccionada);
        }else if (this.jFileChooser1.getApproveButtonText().equalsIgnoreCase("Guardar")){
                    if (this.jFileChooser1.APPROVE_OPTION == this.resultado){
                        XStream traductor = new XStream();
                        traductor.setMode(XStream.ID_REFERENCES);
                        String nombreSeleccionado= this.LibreriaAguardar.Obtener_Nombre();
                        nombreSeleccionado = this.jFileChooser1.getName(this.jFileChooser1.getSelectedFile());
                        FileOutputStream f;
                        try {
                            if(nombreSeleccionado!=null){
                                f = new FileOutputStream(".\\librerias\\" + nombreSeleccionado + ".xml");
                                traductor.toXML(this.LibreriaAguardar,f);
                            }
                            
                         } catch (FileNotFoundException ex) {
                                    
                                    Logger.getLogger(ManagerDeLibrerias.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
        }
    }//GEN-LAST:event_jFileChooser1ActionPerformed
    
    Libreria elementoSeleccionado(){
        return elementoSeleccionado;
    }
    
 
    Libreria abrirLibreria(Component padre) {
       resultado= jFileChooser1.showDialog(padre, "Abrir");
       return elementoSeleccionado;
    }
    
    void guardar(Component padre, Libreria L){
        this.LibreriaAguardar = L;
        resultado = jFileChooser1.showDialog(padre, "Guardar");
    }
    
    void setearDirectorio(String ruta){
        jFileChooser1.setCurrentDirectory(new File(ruta));
    }
    
    void setLibreriaAguardar(Libreria L){
         LibreriaAguardar = L;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFileChooser jFileChooser1;
    // End of variables declaration//GEN-END:variables
    private Libreria elementoSeleccionado;
    private int resultado;
    private Libreria LibreriaAguardar;
}
