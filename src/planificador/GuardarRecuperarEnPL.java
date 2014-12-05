package planificador;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 *
 * @author mpacheco
 */

public class GuardarRecuperarEnPL {

    public static void writeObject(String o){
        PrintWriter escribir=null;
        try {
            escribir = new PrintWriter(new BufferedWriter(new FileWriter(".//planes//tempPL.pl"/*,true*/)));
            escribir.print(o);
        } catch (Exception e) {
            System.out.println("---->ERRROR AL GRABAR PLAN EN pl<-----");
			e.printStackTrace();
        }
        finally{
            try{
                if( null != escribir)
                    escribir.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static Object readObject() {
        BufferedReader leer=null;
        try {
            // Leer el Codigo Fuente de este Archivo
            leer = new BufferedReader(
                             new FileReader(".//planes//tempPL.pl"));
            String s, s2 = new String();
            while ((s = leer.readLine()) != null)
                s2 += s + "\n";
             return s2;
		} catch (Exception e) {
            System.out.println("---->ERRROR AL LEER PLAN DESDE pl<-----");
			e.printStackTrace();
		}
        finally{
            try{
                if( null != leer)
                    leer.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }
}
