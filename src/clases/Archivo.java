package clases;
import java.io.*;
import java.io.BufferedReader;
import java.util.logging.Level;
import java.util.logging.Logger;
/**Maneja la lectura de archivos y al pedido, entrega un caracter*/
public class Archivo extends Entrada{
 private FileInputStream fis;
 private InputStreamReader isr;
 private BufferedReader archivo;
 
 private int pos;
 public Archivo(String nomArch) throws FileNotFoundException, IOException{
    fis= new FileInputStream(nomArch);
    isr=new InputStreamReader(fis);
    archivo= new BufferedReader(isr);
    linea=archivo.readLine();
    
    }

 public String getCaracter(){
     if (this.linea.length()==pos){
            try {
                this.linea = archivo.readLine();
            } catch (IOException ex) {
                Logger.getLogger(Archivo.class.getName()).log(Level.SEVERE, null, ex);
            }
       pos=0;
       return "\n"; //para reconocer el fin de linea y terminar un s�mbolo
    }
    if (linea==null)
        return null;
    else{
      char a = linea.charAt(pos);
      String b= new String();
      pos+=1;
      return (b+a);
   }
 }
 public boolean eOF(){
  if (linea==null)
  {         try {
                archivo.close();
            } catch (IOException ex) {
                Logger.getLogger(Archivo.class.getName()).log(Level.SEVERE, null, ex);
            }
    return true;}
  return false;
  }
  public void close(){
        try {
            archivo.close();
            isr.close();
            fis.close();
        } catch (IOException ex) {
            Logger.getLogger(Archivo.class.getName()).log(Level.SEVERE, null, ex);
        }
  }
}