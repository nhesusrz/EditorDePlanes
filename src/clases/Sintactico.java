package clases;

import editorDePlanes.Predicado;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JTextArea;
import parser.Parser;

public class Sintactico {
    JTextArea errores;
    public Sintactico(JTextArea errores){
        this.errores=errores;
    }

    	public Vector<Predicado> analizar(Entrada fuente){
		 try{

                TablaSimbolos ts = new TablaSimbolos();
			 	AnalizadorLexico anLexico = new AnalizadorLexico(fuente,errores);
			 	Parser par = new Parser(anLexico,errores);
			    par.run();
			    par.close();
			    ts.limpiarTabla();
                if(anLexico.getsepuedegenerar()&& par.getsepuedegenerar())
                    return par.getFinal();
                return null;
		    }
		 	catch (FileNotFoundException e)
		    {
		    	mostrarMensaje(e.getMessage());
		    }
		    catch (IOException e1)
		    {
		    	mostrarMensaje(e1.getMessage());
		    }
		    catch (Exception e2) {

		    	mostrarMensaje(e2.getMessage());
			}
         return null;

	}

    private void mostrarMensaje(String message) {
        String s = new String("");
        if (message==null)
            System.out.println(s);
        else
            System.out.println(message);
    }


}
