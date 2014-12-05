package editorDePlanes;

/**
 *
 * @author mpacheco
 */

import javax.swing.JTextArea;

public abstract class Validacion {

	protected Plan plan;
	protected JTextArea areaDeTexto;

	public Validacion(Plan plan, JTextArea areaDeTexto) {
		this.plan= plan;
        this.areaDeTexto= areaDeTexto;
	}
	// Metodo que deben redifinir cada tipo de chequeo con la politica que llevaran a cabo.
	public abstract boolean validar(Accion accion);

}
