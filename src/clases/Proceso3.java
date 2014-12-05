package clases;
  /**Una vez formado el id, controla que tenga menos de 10 caracteres, si tiene mas,
  lo trunca y lanza el error. Si el identificador no se encuentra en la tabla de
  simbolos lo guarda.Se indica que no se debe leer proximo caracter*/
import javax.swing.JTextArea;
public class Proceso3 extends Proceso {

    public Proceso3(AnalizadorLexico al, JTextArea jtextAreaErrores) {
        super(al, jtextAreaErrores);
    }

    public Proceso3(AnalizadorLexico anLex, JTextArea jtextAreaErrores, TablaSimbolos ts) {
        super(anLex, jtextAreaErrores, ts);
    }

 
  public boolean ejecutar(Simbolo [] box, char c){
	  int lin;
	  if(((box[0].getLexema()).length()> 10)){
		  jtextAreaErrores.append("Se dectecto el identificador <"+box[0].getLexema()+"> de mas de 10 caracteres"+"\n");
		  jtextAreaErrores.append("en linea ("+anLex.getNroLinea()+"), y ha sido truncado por: "+box[0].getLexema().substring(0,10)+"\n");
		  String a= new String((String)box[0].getLexema().substring(0,10));
		  box[0].setLexema(a);
	  }
	  super.guardar(box,"ID");
	  return true;
  }
}