package clases;

import javax.swing.JTextArea;

/**Aviso de error de caracter no valido */
public class Proceso10 extends Proceso{

    public Proceso10(AnalizadorLexico al, JTextArea jtextAreaErrores) {
        super(al, jtextAreaErrores);
    }

    public Proceso10(AnalizadorLexico anLex, JTextArea jtextAreaErrores, TablaSimbolos ts) {
        super(anLex, jtextAreaErrores, ts);
    }

 
  public boolean ejecutar(Simbolo [] box, char c){
	  if(c=='\n')  
		  jtextAreaErrores.append("Caracter Invalido <Enter> en linea: "+anLex.getNroLinea()+"\n");
	  else
		  jtextAreaErrores.append("Caracter Invalido <"+c+"> en linea: "+anLex.getNroLinea()+"\n");
      anLex.setsepuedegenerar(false);
	  anLex.wrongassign=true;
	  box[0].setLexema("");//vaciar para poder cargar.(igual que en P8)
	  return false;
  }
}