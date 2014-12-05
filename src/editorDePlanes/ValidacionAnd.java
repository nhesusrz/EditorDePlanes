package editorDePlanes;

import javax.swing.JTextArea;

/**
 *
 * @author mpacheco
 */

public class ValidacionAnd extends ValidacionCompuesta {

	public ValidacionAnd(Plan plan, JTextArea areaDeTexto) {
		super(plan, areaDeTexto);
	}

	public boolean validar(Accion accion) {		
		return v1.validar(accion) && v2.validar(accion);
	}

}
