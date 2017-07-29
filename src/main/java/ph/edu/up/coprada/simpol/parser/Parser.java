package ph.edu.up.coprada.simpol.parser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import ph.edu.up.coprada.simpol.lexer.Lexer;
import ph.edu.up.coprada.simpol.parser.tree.Add;
import ph.edu.up.coprada.simpol.parser.tree.And;
import ph.edu.up.coprada.simpol.parser.tree.BooleanLiteral;
import ph.edu.up.coprada.simpol.parser.tree.Component;
import ph.edu.up.coprada.simpol.parser.tree.Divide;
import ph.edu.up.coprada.simpol.parser.tree.Equal;
import ph.edu.up.coprada.simpol.parser.tree.GreaterThan;
import ph.edu.up.coprada.simpol.parser.tree.GreaterThanOrEqual;
import ph.edu.up.coprada.simpol.parser.tree.IntegerLiteral;
import ph.edu.up.coprada.simpol.parser.tree.LessThan;
import ph.edu.up.coprada.simpol.parser.tree.LessThanOrEqual;
import ph.edu.up.coprada.simpol.parser.tree.Modulo;
import ph.edu.up.coprada.simpol.parser.tree.Multiply;
import ph.edu.up.coprada.simpol.parser.tree.Not;
import ph.edu.up.coprada.simpol.parser.tree.Or;
import ph.edu.up.coprada.simpol.parser.tree.Subtract;
import ph.edu.up.coprada.simpol.types.Symbol;
import ph.edu.up.coprada.simpol.types.Token;
import ph.edu.up.coprada.simpol.types.TokenCategory;
import ph.edu.up.coprada.simpol.types.Value;

public class Parser {
	
    private Map<String, Symbol> symbols;
    private boolean hasVariableBlock;
    private boolean hasCodeBlock;
    
    public Parser() {
        symbols = new HashMap<String, Symbol>();
        hasVariableBlock = false;
        hasCodeBlock = false;
    }

    public void parse(Lexer lexer) throws ParserError, Exception {
        while (lexer.getTokenListSize() != 0) {
            Token next = lexer.getNextToken();
            
            if (next.getType().isType(TokenCategory.SECTION)) {
                if (lexer.hasNext())
                    handleSection(lexer, next);
                else
                    throw new ParserError("missing '{'");
            } else {
                throw new ParserError("did not start from variable or code: " + next.getLexeme());
            }
        }
    }
    
    public void printSymbols() {
		Iterator<Entry<String, Symbol>> iter = symbols.entrySet().iterator();
		
		System.out.println("--------------- SYMBOLS ---------------");
		
		while (iter.hasNext()) {
			Map.Entry<String, Symbol> pair = (Map.Entry<String, Symbol>)iter.next();
			
			System.out.println(pair.getKey() + " : " + getSymbolType(pair.getKey()) + " : " + getSymbolValue(pair.getKey()).getValue());
		}
		
		System.out.println("---------------------------------------");
    }
  
    
    private void handleSection(Lexer lexer, Token prev) throws ParserError, Exception {
        Token next = lexer.getNextToken();
        
        if (next.getType().equal(TokenCategory.OPEN)) {
            if (!lexer.hasNext()) {
                throw new ParserError("unclosed block");
            } else {
                if (prev.getType().equal(TokenCategory.VARIABLE)) {
                	if (!hasVariableBlock)
                		handleDeclarations(lexer);
                	else
                		throw new ParserError("variable section already exists!");
                } else {
                	if (!hasCodeBlock)
                		handleCode(lexer);
                	else
                		throw new ParserError("code section already exists!");
                }
            }
        } else {
            throw new ParserError("invalid sequence: " + next.getLexeme());
        }
        
        if (lexer.hasNext()) {
        	if (lexer.hasNext(TokenCategory.CLOSE))
    			next = lexer.getNextToken();
        } else {
        	throw new ParserError("unclosed block in section: " + prev.getType());
        } 
    }

    private void handleDeclarations(Lexer lexer) throws ParserError, Exception {
        Token next = null;
        
        if (lexer.hasNext()) {
            if (lexer.hasNext(TokenCategory.CLOSE)) {
                return;
            } else {
                next = lexer.getNextToken();
                
                if (!next.getType().isType(TokenCategory.DATA_TYPE))
                    throw new ParserError("should start with a data type: " + next.getLexeme());
                
                Symbol symbol = new Symbol();
                
                if (next.getType().equal(TokenCategory.INTEGER))
                    symbol.setValue(new Value<Integer>());
                else if (next.getType().equal(TokenCategory.BOOLEAN))
                	symbol.setValue(new Value<Boolean>());
                else if (next.getType().equal(TokenCategory.STRING))
                	symbol.setValue(new Value<String>());
                
                symbol.setType(next.getType());
                
                if (!lexer.hasNext()) 
                    throw new ParserError("missing identifier!");
                
                next = lexer.getNextToken();
                
                if (!next.getType().equal(TokenCategory.IDENTIFIER)) 
                    throw new ParserError("identifier expected: " + next.getLexeme());
                
                if (symbols.containsKey(next.getLexeme()))
                    throw new ParserError("re-declaration prohibited: " + next.getLexeme());
                
                symbols.put(next.getLexeme(), symbol);
                
                handleDeclarations(lexer);
            }
        }
    }
    
    private void handleCode(Lexer lexer) throws ParserError, Exception {
        Token next = null;
        
        if (lexer.hasNext()) {
            next = lexer.getNextToken();
            
            if (next.getType().equal(TokenCategory.CLOSE)) {
            	lexer.returnToken(next);
            	return;
            }
            
            if (next.getType().isType(TokenCategory.ASSIGNMENT) ||
                next.getType().isType(TokenCategory.IO)) {
                ;
            } else {
                throw new ParserError("invalid code start: " + next.getLexeme());
            }
                
            if (next.getType().isType(TokenCategory.IO)) {
                handleIO(lexer, next);
            } else if (next.getType().isType(TokenCategory.ASSIGNMENT)) {
				handleAssignment(lexer, next);
            }
        
            
            handleCode(lexer);
        }
    }

    private void handleAssignment(Lexer lexer, Token prev) throws ParserError, Exception {
    	Token next = null;
    	Object o = null;
    	
        if (lexer.hasNext()) {
            if (lexer.hasNext(TokenCategory.CLOSE))
            	throw new ParserError("expecting expression after " + prev.getLexeme());
         
            next = lexer.getNextToken();
            
            if (prev.getType().equal(TokenCategory.PUT)) {
            	if (next.getType().isType(TokenCategory.ARITHMETIC)) {
            		o = new Integer(evaluateArithmetic(lexer, next));
            	} else if (next.getType().isType(TokenCategory.PREDICATE)) {
            		o = new Boolean(evaluatePredicate(lexer, next));
            	} else if (next.getType().isType(TokenCategory.LOGICAL)) {
            		if (next.getType().equal(TokenCategory.NOT))
            			o = new Boolean(evaluateUnaryLogical(lexer, next));
            		else
            			o = new Boolean(evaluateBinaryLogical(lexer, next));
            	} else if (next.getType().isType(TokenCategory.LITERAL)) {
            		if (next.getType().equal(TokenCategory.LITERAL_BOOLEAN))
            			o = new Boolean(Boolean.parseBoolean(next.getLexeme()));
            		else if (next.getType().equal(TokenCategory.LITERAL_INTEGER))
            			o = new Integer(Integer.parseInt(next.getLexeme()));
            		else
            			o = new String(next.getLexeme());
            	} else if (next.getType().equal(TokenCategory.IDENTIFIER)) {
            		if (getSymbol(next.getLexeme()) == null)
            			throw new ParserError("variable '" + next.getLexeme() + "' is not declared");
            		
        			if (getSymbolType(prev.getLexeme()).equal(TokenCategory.BOOLEAN)) {
        				o = (Boolean)getSymbolValue(prev.getLexeme()).getValue();
        			} else if (getSymbolType(prev.getLexeme()).equal(TokenCategory.INTEGER)) {
        				o = (Integer)getSymbolValue(prev.getLexeme()).getValue();
        			} else {
        				o = (String)getSymbolValue(prev.getLexeme()).getValue();
        			}
            	}
            } else {
            	throw new ParserError("invalid assignment start: " + prev.getLexeme());
            }
            
            if (lexer.hasNext()) {
                if (lexer.hasNext(TokenCategory.CLOSE))
                	throw new ParserError("expecting identifier");
            }
            
            next = lexer.getNextToken();
            
            if (next.getType().equal(TokenCategory.IN)) {
            	if (lexer.hasNext()) {
                    if (lexer.hasNext(TokenCategory.CLOSE))
                    	throw new ParserError("expecting identifier");
                }
            	
            	next = lexer.getNextToken();
            	
            	if (!next.getType().equal(TokenCategory.IDENTIFIER))
            		throw new ParserError("expecting identifier, got " + next.getType());
            	
            	if (getSymbol(next.getLexeme()) == null)
                    throw new ParserError("variable '" + next.getLexeme() + "' is not declared");
            	
            	if (o instanceof Integer && getSymbolType(next.getLexeme()).equals(TokenCategory.INTEGER))
            		setSymbolValue(next.getLexeme(), (Integer)o);
            	else if (o instanceof String && getSymbolType(next.getLexeme()).equals(TokenCategory.STRING))
            		setSymbolValue(next.getLexeme(), (String)o);
            	else if (o instanceof Boolean && getSymbolType(next.getLexeme()).equals(TokenCategory.BOOLEAN))
            		setSymbolValue(next.getLexeme(), (Boolean)o);
            	else
            		throw new ParserError("unsupported value " + o + " for variable '" + next.getLexeme() + "'");
            }
        }
    }
    
    private void handleIO(Lexer lexer, Token prev) throws ParserError, Exception {
        Token next = null;

        if (lexer.hasNext()) {
            next = lexer.getNextToken();

            if (next.equals(TokenCategory.CLOSE))
                throw new ParserError("expecting identifier after " + prev.getLexeme());
            
            if (prev.getType().equal(TokenCategory.ASK)) {
                if (next.getType().equal(TokenCategory.IDENTIFIER)) {
                    handleUserInput(next);
                } else {
                    throw new ParserError("expecting identifier before " + next.getLexeme());
                }
            } else {
                handleOutput(lexer, next);
            }
        }
    }
    
    private void handleOutput(Lexer lexer, Token token) throws ParserError, Exception {
    	if (getSymbol(token.getLexeme()) == null && token.getType().equal(TokenCategory.IDENTIFIER))
            throw new ParserError("variable '" + token.getLexeme() + "' is not declared");
    	
    	if (token.getType().isType(TokenCategory.IO) ||
			token.getType().isType(TokenCategory.ASSIGNMENT))
			throw new ParserError("expecting expression; found: " + token.getType());
		
    	if (token.getType().isType(TokenCategory.ARITHMETIC)) {
			System.out.println(evaluateArithmetic(lexer, token));
    	} else if (token.getType().isType(TokenCategory.PREDICATE)) {
			System.out.println(evaluatePredicate(lexer, token));
    	} else if (token.getType().isType(TokenCategory.LOGICAL)) {
			if (token.getType().equal(TokenCategory.NOT))
				System.out.println(evaluateUnaryLogical(lexer, token));
			else
				System.out.println(evaluateBinaryLogical(lexer, token));
    	} else if (token.getType().equal(TokenCategory.IDENTIFIER)) {
        	System.out.println(getSymbolValue(token.getLexeme()).getValue());
    	} else if (token.getType().isType(TokenCategory.LITERAL)) {
    		System.out.println(token.getLexeme());
    	}
    }
    
    private boolean evaluateUnaryLogical(Lexer lexer, Token prev) throws ParserError, Exception {
		boolean ret = false;
    	Token next = null;
    	Component c = null;
    	
    	if (lexer.hasNext())
    		next = lexer.getNextToken();
    	else
    		throw new ParserError("expecting one expression; got 0");
    	
    	if (prev.getType().equal(TokenCategory.NOT))
    		c = new Not();
		else
			throw new ParserError("unknown error");
    	
    	// visit left expression
    	preEvaluateLogical(lexer, next, c);
    	
    	ret = (Boolean)c.evaluate().getValue();
    	
    	return ret;
    }

    private boolean evaluateBinaryLogical(Lexer lexer, Token prev) throws ParserError, Exception {
    	boolean ret = false;
    	Token next = null;
    	Component c = null;
    	
    	if (lexer.hasNext())
    		next = lexer.getNextToken();
    	else
    		throw new ParserError("expecting two expressions; got 0");
    	
    	if (!lexer.hasNext() || lexer.hasNext(TokenCategory.CLOSE))
    		throw new ParserError("expecting two expressions: got 1");
    	
    	if (prev.getType().equal(TokenCategory.AND))
    		c = new And();
		else if (prev.getType().equal(TokenCategory.OR))
			c = new Or();
		else
			throw new ParserError("unknown error");
    	
    	// visit left expression
    	preEvaluateLogical(lexer, next, c);
    	
    	if (lexer.hasNext())
    		next = lexer.getNextToken();
		else
    		throw new ParserError("expecting two expressions; got 1");
    	
    	// visit right expression
    	preEvaluateLogical(lexer, next, c);
    	
    	ret = (Boolean)c.evaluate().getValue();
    	
    	return ret;
    }
    
    private boolean evaluatePredicate(Lexer lexer, Token prev) throws ParserError, Exception {
    	boolean ret = false;
    	
    	Token next = null;
    	Component c = null;
    	
    	if (lexer.hasNext())
    		next = lexer.getNextToken();
    	else
    		throw new ParserError("expecting two expressions; got 0");
    	
    	if (!lexer.hasNext() || lexer.hasNext(TokenCategory.CLOSE))
    		throw new ParserError("expecting two expressions: got 1");
    	
    	if (prev.getType().equal(TokenCategory.GRE))
    		c = new GreaterThanOrEqual();
		else if (prev.getType().equal(TokenCategory.GRT))
			c = new GreaterThan();
		else if (prev.getType().equal(TokenCategory.LEE))
			c = new LessThanOrEqual();
		else if (prev.getType().equal(TokenCategory.LET))
			c = new LessThan();
		else if (prev.getType().equal(TokenCategory.EQL))
			c = new Equal();
		else
			throw new ParserError("unknown error");
    	
    	// visit left expression
    	preEvaluateInteger(lexer, next, c);
    	
    	if (lexer.hasNext())
    		next = lexer.getNextToken();
		else
    		throw new ParserError("expecting two expressions; got 1");
    	
    	// visit right expression
    	preEvaluateInteger(lexer, next, c);
    	
    	ret = (Boolean)c.evaluate().getValue();
    	
    	return ret;
    }
    
    private int evaluateArithmetic(Lexer lexer, Token prev) throws ParserError, Exception {
    	int ret = 0;
    	Token next = null;
    	Component c = null;

    	if (lexer.hasNext())
    		next = lexer.getNextToken();
    	else
    		throw new ParserError("expecting two expressions; got 0");
    	
    	if (!lexer.hasNext() || lexer.hasNext(TokenCategory.CLOSE))
    		throw new ParserError("expecting two expressions: got 1");
    		
    	if (prev.getType().equal(TokenCategory.ADD))
    		c = new Add();
		else if (prev.getType().equal(TokenCategory.SUBTRACT))
			c = new Subtract();
		else if (prev.getType().equal(TokenCategory.MULTIPLY))
			c = new Multiply();
		else if (prev.getType().equal(TokenCategory.DIVIDE))
			c = new Divide();
		else if (prev.getType().equal(TokenCategory.MODULO))
			c = new Modulo();
		else
			throw new ParserError("unknown error");

    	// visit left expression
    	preEvaluateInteger(lexer, next, c);
    	
    	if (lexer.hasNext())
    		next = lexer.getNextToken();
		else
    		throw new ParserError("expecting two expressions; got 1");
    	
    	// visit right expression
    	preEvaluateInteger(lexer, next, c);
    	
    	ret = (Integer)c.evaluate().getValue();
    	
    	return ret;
    }
    
    private void preEvaluateLogical(Lexer lexer, Token prev, Component c) throws ParserError, Exception {
    	if (prev.getType().isType(TokenCategory.LOGICAL)) {
    		if (prev.getType().equal(TokenCategory.NOT))
    			c.add(new BooleanLiteral(evaluateUnaryLogical(lexer, prev)));
    		else
    			c.add(new BooleanLiteral(evaluateBinaryLogical(lexer, prev)));
		} else if (prev.getType().isType(TokenCategory.PREDICATE)) {
			c.add(new BooleanLiteral(evaluatePredicate(lexer, prev)));
		} else if (prev.getType().equal(TokenCategory.LITERAL_BOOLEAN)) {
			c.add(new BooleanLiteral(Boolean.parseBoolean(prev.getLexeme())));
		} else if (prev.getType().equal(TokenCategory.IDENTIFIER)) {
			if (getSymbol(prev.getLexeme()) == null)
				throw new ParserError("no reference for variable: " + prev.getLexeme());
			
			if (getSymbolValue(prev.getLexeme()).getValue() == null)
				throw new ParserError("uninitialized variable: " + prev.getLexeme());
			
			if (getSymbolType(prev.getLexeme()).equal(TokenCategory.BOOLEAN)) {
				c.add(new BooleanLiteral((Boolean)getSymbolValue(prev.getLexeme()).getValue()));
			} else {
				throw new ParserError("cannot add different types!");
			}
		} else {
			throw new ParserError("invalid type: " + prev.getLexeme());
		}
    }
    
    private void preEvaluateInteger(Lexer lexer, Token prev, Component c) throws ParserError, Exception {
    	if (prev.getType().isType(TokenCategory.ARITHMETIC)) {
			c.add(new IntegerLiteral(evaluateArithmetic(lexer, prev)));
		} else if (prev.getType().equal(TokenCategory.LITERAL_INTEGER)) {
			c.add(new IntegerLiteral(Integer.parseInt(prev.getLexeme())));
		} else if (prev.getType().equal(TokenCategory.IDENTIFIER)) {
			if (getSymbol(prev.getLexeme()) == null)
				throw new ParserError("no reference for variable: " + prev.getLexeme());
			
			if (getSymbolValue(prev.getLexeme()).getValue() == null)
				throw new ParserError("uninitialized variable: " + prev.getLexeme());
			
			if (getSymbolType(prev.getLexeme()).equal(TokenCategory.INTEGER)) {
				c.add(new IntegerLiteral((Integer)getSymbolValue(prev.getLexeme()).getValue()));
			} else {
				throw new ParserError("cannot add different types!");
			}
		} else {
			throw new ParserError("invalid type: " + prev.getLexeme());
		}
    }
    
    private void handleUserInput(Token token) throws ParserError, Exception {
    	// find in symbol table
    	if (getSymbol(token.getLexeme()) == null)
    		throw new ParserError("variable '" + token.getLexeme() + "' is not declared");
    	
    	// get user input
        @SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
        
        System.out.println("set value for '" + token.getLexeme() + "':");
        System.out.print(">>> ");
        
        String str = sc.nextLine();
        //sc.close();
        
        // analyze input type
        TokenCategory cat = Lexer.evaluate(str);
        
        // final check for strings
        if (cat.equal(TokenCategory.LITERAL_STRING)) 
        	if (!str.endsWith("$"))
        		throw new ParserError("unterminated string literal: " + str);

        // set value in symbol table
        TokenCategory dataType = getSymbolType(token.getLexeme());
        if (dataType.equal(TokenCategory.INTEGER) && cat.equal(TokenCategory.LITERAL_INTEGER))
        	setSymbolValue(token.getLexeme(), new Integer(str));
        else if (dataType.equal(TokenCategory.BOOLEAN) && cat.equal(TokenCategory.LITERAL_BOOLEAN))
        	setSymbolValue(token.getLexeme(), new Boolean(str));
        else if (dataType.equal(TokenCategory.STRING) && cat.equal(TokenCategory.LITERAL_STRING))
        	setSymbolValue(token.getLexeme(), new String(str));
        else
        	throw new ParserError("invalid input '" + str + "' for type: " + dataType);
    }
    
    private Symbol getSymbol(String lexeme) {
    	Symbol symbol = null;
    	
    	symbol = symbols.get(lexeme);
    	
    	return symbol;
    }
    
    private TokenCategory getSymbolType(String lexeme) {
    	TokenCategory dataType = null;
    	
    	dataType = getSymbol(lexeme).getType();
    	
    	return dataType;
    }
    
    private <T> Value<T> getSymbolValue(String lexeme) {
    	Value<T> val = null;
    	
		val = getSymbol(lexeme).getValue();
		
		return val;
    }
    
    private void setSymbolValue(String lexeme, Integer value) {
    	getSymbolValue(lexeme).setValue(value);
    }
    
    private void setSymbolValue(String lexeme, Boolean value) {
    	getSymbolValue(lexeme).setValue(value);
    }
    
    private void setSymbolValue(String lexeme, String value) {
    	getSymbolValue(lexeme).setValue(value);
    }

}
