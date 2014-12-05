package clases;

import javax.swing.JTextArea;

/**
 Se utiliza con una nueva linea: suma uno a cantidad de l�neas y retorna false.
 */

public class Proceso12 extends Proceso {

    public Proceso12(AnalizadorLexico al, JTextArea jtextAreaErrores) {
        super(al, jtextAreaErrores);
    }

    public Proceso12(AnalizadorLexico anLex, JTextArea jtextAreaErrores, TablaSimbolos ts) {
        super(anLex, jtextAreaErrores, ts);
    }
 
  public boolean ejecutar(Simbolo [] box, char c) {
	  anLex.ctdorLineas+=1;
	  anLex.addNroLinea();
	  return false;
  }

}