package clases;

import javax.swing.JTextArea;

/**Crea un simbolo con el literales tales como +,-,*,/, etc*/
public class Proceso9 extends Proceso{

    public Proceso9(AnalizadorLexico al, JTextArea jtextAreaErrores) {
        super(al, jtextAreaErrores);
    }

    public Proceso9(AnalizadorLexico anLex, JTextArea jtextAreaErrores, TablaSimbolos ts) {
        super(anLex, jtextAreaErrores, ts);
    }


  public boolean ejecutar(Simbolo [] box, char c){
	  //box[0].addChar(c);
	  box[0].setToken(String.valueOf(c));
	  return false;
  }
}