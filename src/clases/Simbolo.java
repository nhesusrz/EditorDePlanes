package clases;
import java.lang.*;

import parser.ParserTokens;

public class Simbolo implements ParserTokens{
private String lexema="";
public boolean constantePositiva=false;
	
	private String token;
	
	private int id_token;

	public Simbolo(){}
	
	public Simbolo(String token,String lexema){
	    this.token=token;
	    this.lexema=lexema;
		this.id_token= new Integer(Simbolo.getTipoToken(token));
	}
	
	public String getToken(){
		return this.token;
	}
	
	public int getTokenInt(){
		return id_token;
	}
	
	public String getLexema(){
	    return this.lexema;
	}
	
	public void setToken(String str){
		this.token=new String(str);
//		this.id_token= new Integer(Simbolo.getTipoToken(lexema));
		if ( (str.equals("Literal")) || (str.equals("Operador Logico")) || (str.equals("Asignacion")) ) 
		    this.id_token = new Integer(this.getTipoToken(this.lexema));
       	else
       	    this.id_token = new Integer(this.getTipoToken(this.token));
		
	}
	
	public void setLexema(String str){
	    this.lexema=str;
    }
	
	public void addChar(char caracter){
	    this.lexema=this.getLexema()+caracter;
	}
	
	public boolean estaconstruido(){
		return (this.getToken()!=null);
	}

	public String getTipoString() {
		// TODO Auto-generated method stub
		return "";
	}

	public void setTipo(String string) {
		// TODO Auto-generated method stub
		
	}
	public static int getTipoToken(String token){
		if (token.equals("ID"))
			return ID;
		if (token.equals("EndOfFile"))
			return -1;
		
		int num =((int) token.charAt(0));
		
		return num;
 
	}

}