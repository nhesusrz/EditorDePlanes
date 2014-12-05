package interfaceEditordePlanes;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 *
 * @author mpacheco
 */

public class GuardarRecuperarEnXML {

public static  void writeObject(Object o, File file) {
        XStream xs = new XStream();
		try {
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file+".xml"));
            xs.toXML(o, out);
			out.flush();
			out.close();
		} catch (Exception e) {
            System.out.println("---->ERRROR AL GRABAR PLAN EN XML<-----");
			e.printStackTrace();
		}

	}

	public static Object readObject(File file) {
        XStream xs = new XStream(/*new DomDriver()*/);
		try {
            FileInputStream in2= new FileInputStream(file.getAbsoluteFile());
			return xs.fromXML(in2);
		} catch (Exception e) {
            System.out.println("---->ERRROR AL LEER PLAN DESDE XML<-----");
			e.printStackTrace();
		}
		return null;
	}
}
