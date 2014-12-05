package editorDePlanes;

import javax.swing.JTextArea;

/**
 *
 * @author mpacheco
 */

public abstract class ValidacionCompuesta extends Validacion {
	
	Validacion v1, v2;
    
    public ValidacionCompuesta(Plan plan, JTextArea areaDeTexto) {
		super(plan, areaDeTexto);
	}	

}
