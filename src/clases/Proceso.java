package clases;

import javax.swing.JTextArea;

import parser.ParserTokens;

//import clasesVisuales.Principal;

public abstract class Proceso implements ParserTokens{
  protected AnalizadorLexico anLex;
  protected JTextArea jtextAreaErrores = null;
 // protected JTextArea jtextAreaTokens = null;
  protected TablaSimbolos ts = null;

  public Proceso(AnalizadorLexico anLex, JTextArea jtextAreaErrores,TablaSimbolos ts) {
	super();
	this.anLex = anLex;
	this.jtextAreaErrores = jtextAreaErrores;
	//this.jtextAreaTokens = jtextAreaTokens;
	this.ts = ts;

   // getJTextAreaErrores();

}

  public Proceso(AnalizadorLexico al, JTextArea jtextAreaErrores) {
    anLex=al;
    this.jtextAreaErrores = jtextAreaErrores;

  }

  public abstract boolean ejecutar(Simbolo [] box, char c);

  public void guardar(Simbolo box[], String token){
    if (TablaSimbolos.existeSimbolo(box[0].getLexema())){
    	box[0]= TablaSimbolos.getSimbolo(box[0].getLexema());
     }
    else{
    	 box[0].setToken(token);
         TablaSimbolos.putSimbolo(box[0]);
        };
  }
}