package ph.edu.up.coprada.simpol.lexer;

import java.util.ArrayList;
import java.util.List;

import ph.edu.up.coprada.simpol.types.TokenCategory;
import ph.edu.up.coprada.simpol.types.Token;

public class Lexer {
	
	private List<Token> tokens;

	public Lexer() {
		tokens = new ArrayList<Token>();
	}
	
	public void printTokens() {
		System.out.println("--------------- TOKENS ---------------");
		for (Token token : tokens)
			System.out.println(token.getLexeme() + " : " + token.getType());
		System.out.println("--------------------------------------");
	}

	public void scan(String str) {
		Token token = new Token(str);
	
		token.setType(evaluate(str));

		tokens.add(token);
	}
	
	public int getTokenListSize() {
		return tokens.size();
	}
	
	public Token getNextToken() {
		if (this.getTokenListSize() > 0)
			return tokens.remove(0);
		else
			return null;
	}
	
	public void returnToken(Token token) {
		this.tokens.add(0, token);
	}
	
	public boolean hasNext() {
		boolean hasNext = false;
		Token t = null;
		
		if (this.getTokenListSize() > 0)
			t = tokens.get(0);
		
		if (t != null)
			hasNext = true;
		
		return hasNext;
	}
	
	public boolean hasNext(TokenCategory cat) {
		boolean hasNext = false;
		Token t = null;
		
		if (this.getTokenListSize() > 0)
			t = tokens.get(0);
		
		if (t != null && cat != null) {
			if (t.getType() == cat)
				hasNext = true;
		}
		
		return hasNext;
	}

	public static TokenCategory evaluate(String string) {
		TokenCategory ret = TokenCategory.UNKNOWN;

		if (string.startsWith("$"))
			ret = TokenCategory.LITERAL_STRING;
		else if (string.matches("^(-|\\+)?[0-9]{1,}$"))
			ret = TokenCategory.LITERAL_INTEGER;
		else if (string.matches("^true$") || string.matches("^false$"))
			ret = TokenCategory.LITERAL_BOOLEAN;
		else if (string.equals("variable"))
			ret = TokenCategory.VARIABLE;
		else if (string.equals("code"))
			ret = TokenCategory.CODE;
		else if (string.equals("{"))
			ret = TokenCategory.OPEN;
		else if (string.equals("}"))
			ret = TokenCategory.CLOSE;
		else if (string.equals("INT"))
			ret = TokenCategory.INTEGER;
		else if (string.equals("STG")) 
			ret = TokenCategory.STRING;
		else if (string.equals("BLN"))
			ret = TokenCategory.BOOLEAN;
		else if (string.equals("ASK"))
			ret = TokenCategory.ASK;
		else if (string.equals("PRT"))
			ret = TokenCategory.PRT;
		else if (string.equals("PUT"))
			ret = TokenCategory.PUT;
		else if (string.equals("IN"))
			ret = TokenCategory.IN;
		else if (string.equals("ADD"))
			ret = TokenCategory.ADD;
		else if (string.equals("SUB"))
			ret = TokenCategory.SUBTRACT;
		else if (string.equals("MUL"))
			ret = TokenCategory.MULTIPLY;
		else if (string.equals("DIV"))
			ret = TokenCategory.DIVIDE;
		else if (string.equals("MOD"))
			ret = TokenCategory.MODULO;
		else if (string.equals("GRT"))
			ret = TokenCategory.GRT;
		else if (string.equals("GRE"))
			ret = TokenCategory.GRE;
		else if (string.equals("LET"))
			ret = TokenCategory.LET;
		else if (string.equals("LEE"))
			ret = TokenCategory.LEE;
		else if (string.equals("EQL"))
			ret = TokenCategory.EQL;
		else if (string.equals("AND"))
			ret = TokenCategory.AND;
		else if (string.equals("OHR"))
			ret = TokenCategory.OR;
		else if (string.equals("NON"))
			ret = TokenCategory.NOT;
		else if (string.matches("^[a-zA-Z]{1,}[0-9]{0,}$"))
			ret = TokenCategory.IDENTIFIER;
			
		return ret;
	}
	
	public List<Token> getTokens() {
		return tokens;
	}

}
