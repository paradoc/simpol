package ph.edu.up.coprada.simpol;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ph.edu.up.coprada.simpol.lexer.Lexer;
import ph.edu.up.coprada.simpol.parser.Parser;
import ph.edu.up.coprada.simpol.parser.ParserError;
import ph.edu.up.coprada.simpol.types.Token;

public class Simpol 
{
	private final Lexer lexer;
	private final Parser parser;
	private List<Token> tokens;
    
    public Simpol() {
    	lexer = new Lexer();
    	parser = new Parser();
    	tokens = new ArrayList<Token>();
    }

	public void run(final String string) throws Exception {
		if (string.endsWith(".sim"))
			this.readFile(string);
		else
			throw new Exception("invalid file extension " + string);
	}
	
	private void readFile(String string) throws Exception {
		try {
			Scanner sc = new Scanner(new FileReader(string));
			boolean isString = false;
			String data = "";
			
			while (sc.hasNext()) {
				String tmp = sc.next();
				
				if (data == "" && tmp.startsWith("$"))
					isString = true;
				
				if (data != "")
					data += " ";
				data += tmp;
				
				if (data.startsWith("$") && data.endsWith("$") && data.length() > 1)
					isString = false;
				
				if (isString) {
					continue;
				} else {
					lexer.scan(data);
					data = "";
				}
				
			}
					
			if (isString) {
				sc.close();
				throw new Exception("unterminated string literal");
			}
			
			sc.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		parse();
	}
	
	private void parse() {
		// create copy
		tokens = new ArrayList<Token>(lexer.getTokens());
		
		try {
			parser.parse(lexer);
		} catch (ParserError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void printTokens() {
		lexer.printTokens();
	}
	
	public void printSymbols() {
		parser.printSymbols();
	}

	public void printTokenSequence() {
		System.out.println("---------- TOKENS & LEXEMES -----------");
		
		for (Token t : tokens) {
			System.out.println(t.getLexeme() + " : " + t.getType());
		}
		
		System.out.println("---------------------------------------");
	}

}
