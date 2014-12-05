package interfaceEditordePlanes;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import javax.imageio.ImageIO;
import org.jgraph.JGraph;

/**
 *
 * @author mpacheco
 */

public class ExportarJGraph {
    
	public  static void writeObject(JGraph graph, File file) {
		try {

            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
            BufferedImage image = graph.getImage(graph.getBackground(),10);
            ImageIO.write(image, getExtension(file), out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static Object readObject(String filename) {
		try {
			ObjectInputStream in = new ObjectInputStream(
					new BufferedInputStream(new FileInputStream(filename)));
			Object object = in.readObject();
			in.close();			
			return object;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

    private static String getExtension(File file) {

        String ext = null;
        String s = file.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

}
