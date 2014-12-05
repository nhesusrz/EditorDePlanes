package clases;

import java.*;

public class Matriz {
  private int filas;
  private int columnas;
  protected  Object[][] elementos ;
  public Matriz(int filas, int columnas) {
    this.filas= filas;
    this.columnas= columnas;
   elementos= new Object[filas][columnas];
    for(int f=0; f< this.filas(); f++){
      for(int c=0; c< this.columnas(); c++){
          elementos[f][c]= null;
              }
    }
  }

  public int filas(){
    return filas;
  }
  public int columnas(){
    return columnas;
  }
  public Object getElementoAt(int fila, int columna){

      return ((Object) elementos[fila][columna]);
  }
  public void addElementoAt(int fila,int columna, Object o){
     elementos[fila][columna]= o;
  }


}