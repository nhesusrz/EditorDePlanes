package clases;

public abstract class Entrada {
    protected  String linea;
    protected  int pos;
    public abstract String getCaracter();
    public abstract boolean eOF();
    public abstract void close();

    public Entrada(){
        pos=0;
    }
}
