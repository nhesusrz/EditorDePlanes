package clases;

import java.awt.Color;
import java.util.*;

import javax.swing.JTextArea;
import parser.ParserTokens;

public class TablaSimbolos implements ParserTokens{
  private static Hashtable tabla = new Hashtable();
  private JTextArea jTextAreaTabla = null;
  
  public TablaSimbolos(JTextArea jTextAreaTabla) {
		this.jTextAreaTabla = jTextAreaTabla;
	    
  }
  public TablaSimbolos() {
		//cargarPalabrasReservadas();
  }
 
  public static Simbolo getSimbolo(String cadena){
      if (tabla.containsKey(cadena)){
        return (Simbolo) tabla.get(cadena);}
      else return null;
  }

  public static boolean existeSimbolo(String pal){
    return (tabla.containsKey(pal));
  }

  public static void putSimbolo(Simbolo s){
	  if (!(tabla.containsKey(s.getLexema())))
	    tabla.put(s.getLexema(),s);
  }
  public static void removeSimbolo(String simb){
	  if (tabla.containsKey(simb))
	      tabla.remove(simb);
	  }

  public void imprimir(){  
	  Enumeration enume =tabla.elements();
	  while (enume.hasMoreElements()){
		  Simbolo s=(Simbolo)enume.nextElement();
		  jTextAreaTabla.setForeground(Color.blue);
		  jTextAreaTabla.append(TablaSimbolos.token(s.getToken())+": "+s.getLexema()+"\n");
	  }
  }  
  public static String token(String s){
      if(s.equals("ID")) return new String("IDENTIFICADOR");
      return null;


  }
	  
	  public void limpiarTabla(){  
	     this.tabla.clear();
	  }
}


