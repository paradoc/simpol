package ph.edu.up.coprada.simpol.types;

public class Token {
	
	private TokenCategory tokenCategory;
	private String lexeme;
	
	public Token(String lexeme) {
		this.lexeme = lexeme;
	}
	
	public String getLexeme() {
		return lexeme;
	}
	
	public void setLexeme(String lexeme) {
		this.lexeme = lexeme;
	}
	
	public TokenCategory getType() {
		return tokenCategory;
	}
	public void setType(TokenCategory tokenCategory) {
		this.tokenCategory = tokenCategory;
	}
	
}