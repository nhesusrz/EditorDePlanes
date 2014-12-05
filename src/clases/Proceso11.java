package clases;

import javax.swing.JTextArea;

/**se utiliza para retornar false y de esta manera no devolver el caracter consumido
 * es llamado en el estado 0 entrada 9 para los casos de blanco y tabulaci�n
 * */
public class Proceso11 extends Proceso{

    public Proceso11(AnalizadorLexico al, JTextArea jtextAreaErrores) {
        super(al, jtextAreaErrores);
    }

    public Proceso11(AnalizadorLexico anLex, JTextArea jtextAreaErrores, TablaSimbolos ts) {
        super(anLex, jtextAreaErrores, ts);
    }

  
  public boolean ejecutar(Simbolo [] box, char c){
	  return false;
  }
}