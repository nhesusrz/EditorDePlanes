package clases;
import java.awt.Color;
import java.io.*;

import javax.swing.JTextArea;

import parser.ParserTokens;
import parser.ParserVal;

public class AnalizadorLexico implements ParserTokens{
  private Matriz matProc, matProxEstado;
  private int estadoActual, entrada;
  private String a;
  private Entrada entra;
  private boolean devolver;
  private int cantLineas;
  private int contLineas = 0;
  //Para el sintactico
  public int ctdorLineas;
  public int tokenAnt;
  public static ParserVal yylval;
  public boolean wrongassign=false;
  private String linea;
  //private JTextArea errores= null;
  private JTextArea errores= null;
  private StringBuffer texto;
  private boolean sepuedegenerar=true;
  

  public AnalizadorLexico(Entrada nomArch,JTextArea errores) throws FileNotFoundException,Exception, IOException{
    this.errores = errores;
	cantLineas=1;
    ctdorLineas=0;
    tokenAnt=0;
    estadoActual=0;
    entrada=0;
    linea=null;
    a=null;
    if (nomArch==null)
    	throw new Exception("Debe abrir un archivo para compilar.");
    entra=nomArch;               //<-- NO OLVIDARSE DE HACER EL NEW AFUERA;
    devolver=false;
    matProxEstado=new Matriz(9,9);
    matProc=new Matriz(9,9);
    cargarMatProxEstado();
    cargarMatProc();
  }
  public AnalizadorLexico(StringBuffer texto){
    this.texto=texto;
	cantLineas=1;
    ctdorLineas=0;
    tokenAnt=0;
    estadoActual=0;
    entrada=0;
    linea=null;
    a=null;
    devolver=false;
    matProxEstado=new Matriz(9,9);
    matProc=new Matriz(9,9);
    cargarMatProxEstado();
    cargarMatProc();

  }
  
  
  public int getNroLinea(){
    return cantLineas;
  }
  public void addNroLinea(){
  cantLineas+=1;
  }
  
  public Simbolo getToken(){
      Simbolo [] box= new Simbolo[1];
      box[0]=new Simbolo();
      try{
          Proceso p;
          while (!entra.eOF()&&!box[0].estaconstruido()){
            if (!(devolver))
                a=entra.getCaracter();
            entrada=getCol(a.charAt(0)); // devuelve el tipo de caracter (literal, letra...)
            p=(Proceso)matProc.getElementoAt(estadoActual,entrada);
            if (p!=null)
                devolver=p.ejecutar(box,a.charAt(0));
            estadoActual=((Integer)matProxEstado.getElementoAt(estadoActual,entrada)).intValue();
          }
          if (entra.eOF()&&!box[0].estaconstruido()){
            entrada=6;
            p=(Proceso)matProc.getElementoAt(estadoActual,entrada);
            if (p!=null)
                devolver=p.ejecutar(box,a.charAt(0));
            estadoActual=((Integer)matProxEstado.getElementoAt(estadoActual,entrada)).intValue();
            return new Simbolo("EndOfFile","$");
          }
      }
      catch(IOException e){}
      return box[0];
   
 }
  /**
   * EL procedimiento cargar matriz proximo estado me da el proximo estado dependiendo de en q estado
   * me encuentre(fila) y de que simbolo leyo(columna)
   * **/
  private void cargarMatProxEstado(){
	    //Estado 0
	    matProxEstado.addElementoAt(0,0,new Integer(1)); 
	    matProxEstado.addElementoAt(0,1,new Integer(0)); 
	    matProxEstado.addElementoAt(0,2,new Integer(0)); 
	    matProxEstado.addElementoAt(0,3,new Integer(0)); 
	    matProxEstado.addElementoAt(0,4,new Integer(0)); 
	    matProxEstado.addElementoAt(0,5,new Integer(0)); 
	    matProxEstado.addElementoAt(0,6,new Integer(0)); 
	    matProxEstado.addElementoAt(0,7,new Integer(0)); 
	    matProxEstado.addElementoAt(0,8,new Integer(0)); 
	        
	    //Estado 1
	    matProxEstado.addElementoAt(1,0,new Integer(1)); 
	    matProxEstado.addElementoAt(1,1,new Integer(5)); 
	    matProxEstado.addElementoAt(1,2,new Integer(0)); 
	    matProxEstado.addElementoAt(1,3,new Integer(0)); 
	    matProxEstado.addElementoAt(1,4,new Integer(0)); 
	    matProxEstado.addElementoAt(1,5,new Integer(0)); 
	    matProxEstado.addElementoAt(1,6,new Integer(0)); 
	    matProxEstado.addElementoAt(1,7,new Integer(1)); 
	    matProxEstado.addElementoAt(1,8,new Integer(0)); 
		
	      //Estado 2
	    matProxEstado.addElementoAt(2,0,new Integer(3)); 
	    matProxEstado.addElementoAt(2,1,new Integer(0)); 
	    matProxEstado.addElementoAt(2,2,new Integer(0)); 
	    matProxEstado.addElementoAt(2,3,new Integer(4)); 
	    matProxEstado.addElementoAt(2,4,new Integer(0)); 
	    matProxEstado.addElementoAt(2,5,new Integer(0));
	    matProxEstado.addElementoAt(2,6,new Integer(0));
	    matProxEstado.addElementoAt(2,7,new Integer(2)); 
	    matProxEstado.addElementoAt(2,8,new Integer(0)); 

	    //Estado 3
	    matProxEstado.addElementoAt(3,0,new Integer(3)); 
	    matProxEstado.addElementoAt(3,1,new Integer(5)); 
	    matProxEstado.addElementoAt(3,2,new Integer(7)); 
	    matProxEstado.addElementoAt(3,3,new Integer(6)); 
	    matProxEstado.addElementoAt(3,4,new Integer(0)); 
	    matProxEstado.addElementoAt(3,5,new Integer(0)); 
	    matProxEstado.addElementoAt(3,6,new Integer(0)); 
	    matProxEstado.addElementoAt(3,7,new Integer(3)); 
	    matProxEstado.addElementoAt(3,8,new Integer(0)); 

	    //Estado 4
	    matProxEstado.addElementoAt(4,0,new Integer(0));
	    matProxEstado.addElementoAt(4,1,new Integer(0));
	    matProxEstado.addElementoAt(4,2,new Integer(8));
	    matProxEstado.addElementoAt(4,3,new Integer(4));
	    matProxEstado.addElementoAt(4,4,new Integer(0));
	    matProxEstado.addElementoAt(4,5,new Integer(0));
	    matProxEstado.addElementoAt(4,6,new Integer(0));
	    matProxEstado.addElementoAt(4,7,new Integer(4));
	    matProxEstado.addElementoAt(4,8,new Integer(0));
	    //Estado 5
	    matProxEstado.addElementoAt(5,0,new Integer(0));
	    matProxEstado.addElementoAt(5,1,new Integer(2));
	    matProxEstado.addElementoAt(5,2,new Integer(0));
	    matProxEstado.addElementoAt(5,3,new Integer(0));
	    matProxEstado.addElementoAt(5,4,new Integer(0));
	    matProxEstado.addElementoAt(5,5,new Integer(0));
	    matProxEstado.addElementoAt(5,6,new Integer(0));
	    matProxEstado.addElementoAt(5,7,new Integer(0));
	    matProxEstado.addElementoAt(5,8,new Integer(0));
	    //Estado 6
	    matProxEstado.addElementoAt(6,0,new Integer(0));
	    matProxEstado.addElementoAt(6,1,new Integer(0));
	    matProxEstado.addElementoAt(6,2,new Integer(0));
	    matProxEstado.addElementoAt(6,3,new Integer(4));
	    matProxEstado.addElementoAt(6,4,new Integer(0));
	    matProxEstado.addElementoAt(6,5,new Integer(0));
	    matProxEstado.addElementoAt(6,6,new Integer(0));
	    matProxEstado.addElementoAt(6,7,new Integer(0));
	    matProxEstado.addElementoAt(6,8,new Integer(0));
	    //Estado 7
	    matProxEstado.addElementoAt(7,0,new Integer(0));
	    matProxEstado.addElementoAt(7,1,new Integer(0));
	    matProxEstado.addElementoAt(7,2,new Integer(8));
	    matProxEstado.addElementoAt(7,3,new Integer(0));
	    matProxEstado.addElementoAt(7,4,new Integer(0));
	    matProxEstado.addElementoAt(7,5,new Integer(0));
	    matProxEstado.addElementoAt(7,6,new Integer(0));
	    matProxEstado.addElementoAt(7,7,new Integer(0));
	    matProxEstado.addElementoAt(7,8,new Integer(0));
	    //Estado 8
	    matProxEstado.addElementoAt(8,0,new Integer(3));
	    matProxEstado.addElementoAt(8,1,new Integer(0));
	    matProxEstado.addElementoAt(8,2,new Integer(0));
	    matProxEstado.addElementoAt(8,3,new Integer(0));
	    matProxEstado.addElementoAt(8,4,new Integer(0));
	    matProxEstado.addElementoAt(8,5,new Integer(0));
	    matProxEstado.addElementoAt(8,6,new Integer(0));
	    matProxEstado.addElementoAt(8,7,new Integer(8));
	    matProxEstado.addElementoAt(8,8,new Integer(0));
		}
	 /**
	  * Funciona de igual manera que proximo estado lo unico es que este procedimiento 
	  * dependiendo de lo que llega y el estado en el que me encuentro ejecuta un proceso.
	  * **/
	 private void cargarMatProc(){
	      //Estado 0
	    matProc.addElementoAt(0,0,new Proceso2(this,errores));
	    matProc.addElementoAt(0,1,new Proceso10(this,errores));
	    matProc.addElementoAt(0,2,new Proceso10(this,errores));
	    matProc.addElementoAt(0,3,new Proceso10(this,errores));
	    matProc.addElementoAt(0,4,new Proceso10(this,errores));
	    matProc.addElementoAt(0,5,new Proceso12(this,errores));
	    matProc.addElementoAt(0,6,null);
	    matProc.addElementoAt(0,7,new Proceso11(this,errores));
	    matProc.addElementoAt(0,8,new Proceso10(this,errores));
	      //Estado 1
	    matProc.addElementoAt(1,0,new Proceso2(this,errores));
	    matProc.addElementoAt(1,1,new Proceso3(this,errores));
	    matProc.addElementoAt(1,2,new Proceso10(this,errores));
	    matProc.addElementoAt(1,3,new Proceso10(this,errores));
	    matProc.addElementoAt(1,4,new Proceso10(this,errores));
	    matProc.addElementoAt(1,5,new Proceso10(this,errores));
	    matProc.addElementoAt(1,6,new Proceso10(this,errores));
	    matProc.addElementoAt(1,7,new Proceso11(this,errores));
	    matProc.addElementoAt(1,8,new Proceso10(this,errores));
	    //Estado 2
	    matProc.addElementoAt(2,0,new Proceso2(this,errores));	
	    matProc.addElementoAt(2,1,new Proceso10(this,errores));	
	    matProc.addElementoAt(2,2,new Proceso10(this,errores));	
	    matProc.addElementoAt(2,3,new Proceso9(this,errores));	
	    matProc.addElementoAt(2,4,new Proceso10(this,errores));	
	    matProc.addElementoAt(2,5,new Proceso10(this,errores));	
	    matProc.addElementoAt(2,6,new Proceso10(this,errores));	
	    matProc.addElementoAt(2,7,new Proceso11(this,errores));	
	    matProc.addElementoAt(2,8,new Proceso10(this,errores));	
	    //Estado 3
	    matProc.addElementoAt(3,0,new Proceso2(this,errores));	
	    matProc.addElementoAt(3,1,new Proceso3(this,errores));	
	    matProc.addElementoAt(3,2,new Proceso3(this,errores));	
	    matProc.addElementoAt(3,3,new Proceso3(this,errores));  	
	    matProc.addElementoAt(3,4,new Proceso10(this,errores));	
	    matProc.addElementoAt(3,5,new Proceso10(this,errores));	
	    matProc.addElementoAt(3,6,new Proceso10(this,errores));	
	    matProc.addElementoAt(3,7,new Proceso11(this,errores));	
	    matProc.addElementoAt(3,8,new Proceso10(this,errores));	
	       //Estado 4
	    matProc.addElementoAt(4,0,new Proceso10(this,errores));	
	    matProc.addElementoAt(4,1,new Proceso10(this,errores));	
	    matProc.addElementoAt(4,2,new Proceso9(this,errores));	
	    matProc.addElementoAt(4,3,new Proceso9(this,errores));	
	    matProc.addElementoAt(4,4,new Proceso9(this,errores));	
	    matProc.addElementoAt(4,5,new Proceso12(this,errores));	
	    matProc.addElementoAt(4,6,null);	
	    matProc.addElementoAt(4,7,new Proceso11(this,errores));	
	    matProc.addElementoAt(4,8,new Proceso10(this,errores));	
	    //Estado 5
	    matProc.addElementoAt(5,0,new Proceso10(this,errores));	
	    matProc.addElementoAt(5,1,new Proceso9(this,errores));	
	    matProc.addElementoAt(5,2,new Proceso10(this,errores));	
	    matProc.addElementoAt(5,3,new Proceso10(this,errores));	
	    matProc.addElementoAt(5,4,new Proceso10(this,errores));	
	    matProc.addElementoAt(5,5,new Proceso10(this,errores));	
	    matProc.addElementoAt(5,6,new Proceso10(this,errores));	
	    matProc.addElementoAt(5,7,new Proceso10(this,errores));	
	    matProc.addElementoAt(5,8,new Proceso10(this,errores));	
	    //Estado 6
	    matProc.addElementoAt(6,0,new Proceso10(this,errores));	
	    matProc.addElementoAt(6,1,new Proceso10(this,errores));	
	    matProc.addElementoAt(6,2,new Proceso10(this,errores));	
	    matProc.addElementoAt(6,3,new Proceso9(this,errores));	
	    matProc.addElementoAt(6,4,new Proceso10(this,errores));	
	    matProc.addElementoAt(6,5,new Proceso10(this,errores));	
	    matProc.addElementoAt(6,6,new Proceso10(this,errores));	
	    matProc.addElementoAt(6,7,new Proceso10(this,errores));	
	    matProc.addElementoAt(6,8,new Proceso10(this,errores));	
	    //Estado 7
	    matProc.addElementoAt(7,0,new Proceso10(this,errores));	
	    matProc.addElementoAt(7,1,new Proceso10(this,errores));	
	    matProc.addElementoAt(7,2,new Proceso9(this,errores));	
	    matProc.addElementoAt(7,3,new Proceso10(this,errores));	
	    matProc.addElementoAt(7,4,new Proceso10(this,errores));	
	    matProc.addElementoAt(7,5,new Proceso10(this,errores));	
	    matProc.addElementoAt(7,6,new Proceso10(this,errores));	
	    matProc.addElementoAt(7,7,new Proceso10(this,errores));	
	    matProc.addElementoAt(7,8,new Proceso10(this,errores));	
		//Estado 8
	    matProc.addElementoAt(8,0,new Proceso2(this,errores));	
	    matProc.addElementoAt(8,1,new Proceso10(this,errores));	
	    matProc.addElementoAt(8,2,new Proceso10(this,errores));	
	    matProc.addElementoAt(8,3,new Proceso10(this,errores));	
	    matProc.addElementoAt(8,4,new Proceso10(this,errores));	
	    matProc.addElementoAt(8,5,new Proceso10(this,errores));	
	    matProc.addElementoAt(8,6,new Proceso10(this,errores));	
	    matProc.addElementoAt(8,7,new Proceso10(this,errores));	
	    matProc.addElementoAt(8,8,new Proceso11(this,errores));	
	 }

 private int getCol(char c) throws IOException{
  //cualquier cosa menos (, ),;,\n
  if(((c>='a')&&(c<='z'))||((c>='A')&&(c<='Z'))
	 ||(c=='+')||(c=='-')||(c=='{')||(c=='}')
	 ||(c>='0')&&(c<='9')
	 ||(c=='*')
	 ||(c=='/')
	)
    return 0;
  //creo un pred compuesto
  if(c=='(')
	  return 1;
  if((c==','))
     return 2;
  if (c==')')
	  return 3;
  if (c==';')
	  return 4;
  if  (c=='\n'){
	  contLineas++;
	  return 5;
  }
  //fin de archivo
  if(entra.eOF())
	return 6;
  if ((c==' ')||(c=='\t'))
     return 7;
  return 8;
 }
 
 public void close(){
     entra.close();
 }

 public void resetearCont(){
	 contLineas = 0;
 }
 public int contLineas(){
     return contLineas;  
 }
 public void yyerror(String error,int tipo){
	 if (tipo == 1){

		((JTextArea) errores).append(error+"\n");
	 }
	 else if(tipo == 2){
		((JTextArea) errores).append(error+"\n");
		((JTextArea) errores).setForeground(Color.BLUE);
	 }
	 else
		((JTextArea) errores).setForeground(Color.green);
		 
	 
 }
 public void setsepuedegenerar(boolean var){
    sepuedegenerar=var;
}
public boolean getsepuedegenerar(){
    return sepuedegenerar;
}


}
