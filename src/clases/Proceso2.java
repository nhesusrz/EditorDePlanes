package clases;

import javax.swing.JTextArea;

 /**Se agregan caracteres al simbolo que se esta construyendo*/
public class Proceso2 extends Proceso{

    public Proceso2(AnalizadorLexico al, JTextArea jtextAreaErrores) {
        super(al, jtextAreaErrores);
    }

    public Proceso2(AnalizadorLexico anLex, JTextArea jtextAreaErrores, TablaSimbolos ts) {
        super(anLex, jtextAreaErrores, ts);
    }

 
  public boolean ejecutar(Simbolo [] box, char c){
	  box[0].addChar(c);
	  return false;
  }
}