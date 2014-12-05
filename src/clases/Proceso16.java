package clases;

import javax.swing.JTextArea;

/**
 Se utiliza con una nueva linea en los comentarios: suma uno a cantidad de l�neas y retorna false.
 Ademas de tener  un contador para ver cuantos \n se hicieron desde donde comenzo el comentario
 */

public class Proceso16 extends Proceso {

    public Proceso16(AnalizadorLexico al, JTextArea jtextAreaErrores) {
        super(al, jtextAreaErrores);
    }

    public Proceso16(AnalizadorLexico anLex, JTextArea jtextAreaErrores, TablaSimbolos ts) {
        super(anLex, jtextAreaErrores, ts);
    }
 
  public boolean ejecutar(Simbolo [] box, char c) {
      anLex.resetearCont();  
      return false;
  } 
}