package editorDePlanes;

import javax.swing.JTextArea;

/**
 *
 * @author mpacheco
 */

public class ValidacionOr extends ValidacionCompuesta {

	public ValidacionOr(Plan plan, JTextArea areaDeTexto) {
		super(plan, areaDeTexto);
	}
	
	public boolean validar(Accion accion) {		
		return v1.validar(accion) || v2.validar(accion);
	}

}
