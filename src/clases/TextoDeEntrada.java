package clases;

public class TextoDeEntrada extends Entrada{
    String texto;
    StringBuffer sf;

    public TextoDeEntrada(String texto){
        this.texto=texto;
        sf=new StringBuffer(texto);
        linea=texto;
    }



    public String getCaracter(){
        if (sf.length()==0)
            return null;
        String sfAux=sf.substring(pos, pos+1);
        pos++;
        return sfAux;


    }

    public boolean eOF(){
        int caca=sf.length();

        return sf.length()==pos;

    }

    public StringBuffer getSf() {
        return sf;
    }

    public String getTexto() {
        return texto;
    }

    public void close(){

    }
}
