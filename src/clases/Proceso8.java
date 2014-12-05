package clases;
/**Aviso de error de caracter no valido:! indica que no se debe leer proximo caracter.*/
import javax.swing.JTextArea;
public class Proceso8 extends Proceso{

    public Proceso8(AnalizadorLexico al, JTextArea jtextAreaErrores) {
        super(al, jtextAreaErrores);
    }

    public Proceso8(AnalizadorLexico anLex, JTextArea jtextAreaErrores, TablaSimbolos ts) {
        super(anLex, jtextAreaErrores, ts);
    }

  
  public boolean ejecutar(Simbolo [] box, char c){
	  jtextAreaErrores.append("Caracter Invalido <!> en linea: "+anLex.getNroLinea()+"\n");
	  box[0].setLexema("");//vacia el string lexema porque como no sale del gettoken no se inicializa el simbolo, por lo que sino que da el '!' y se agrega a lo que viene
	  anLex.setsepuedegenerar(false);
      return true;
  }
}
