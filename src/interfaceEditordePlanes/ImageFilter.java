package interfaceEditordePlanes;

import java.io.File;
import javax.swing.filechooser.*;

/**
 *
 * @author mpacheco
 */

public class ImageFilter extends FileFilter {
    final static String jpeg = "jpeg";
    final static String jpg = "jpg";
    final static String gif = "gif";
    final static String tiff = "tiff";
    final static String tif = "tif";
    
    public boolean accept(File f) {

        if (f.isDirectory()) {
            return true;
        }
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 &&  i < s.length() - 1) {
            String extension = s.substring(i+1).toLowerCase();
            if (tiff.equals(extension) ||
                tif.equals(extension) ||
                gif.equals(extension) ||
                jpeg.equals(extension) ||
                jpg.equals(extension)) {
                    return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public String getDescription() {
        return "Solo Imagenes";
    }
}
