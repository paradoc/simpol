package ph.edu.up.coprada.simpol.types;

public enum TokenCategory {
    LITERAL, LITERAL_INTEGER, LITERAL_BOOLEAN, LITERAL_STRING,
    DATA_TYPE, IDENTIFIER,
    SECTION, BLOCK,
    IO,
    ASSIGNMENT, ARITHMETIC, PREDICATE, LOGICAL,
    
    INTEGER, BOOLEAN, STRING,
    PUT, IN,
    ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULO,
    VARIABLE, CODE,
    OPEN, CLOSE,
    PRT, ASK,
    GRE, GRT, LEE, LET, EQL,
    AND, OR, NOT,
    UNKNOWN
    ;
	
	private TokenCategory base;
	
	static {
		LITERAL.base = LITERAL;
		LITERAL_INTEGER.base = LITERAL;
		LITERAL_BOOLEAN.base = LITERAL;
		LITERAL_STRING.base = LITERAL;
		DATA_TYPE.base = DATA_TYPE;
		INTEGER.base = DATA_TYPE;
		BOOLEAN.base = DATA_TYPE;
		STRING.base = DATA_TYPE;
		ASSIGNMENT.base = ASSIGNMENT;
		PUT.base = ASSIGNMENT;
		IN.base = ASSIGNMENT;
		ARITHMETIC.base = ARITHMETIC;
		ADD.base = ARITHMETIC;
		SUBTRACT.base = ARITHMETIC;
		MULTIPLY.base = ARITHMETIC;
		DIVIDE.base = ARITHMETIC;
		MODULO.base = ARITHMETIC;
		SECTION.base = SECTION;
		VARIABLE.base = SECTION;
		CODE.base = SECTION;
		BLOCK.base = BLOCK;
		OPEN.base = BLOCK;
		CLOSE.base = BLOCK;
		IO.base = IO;
		PRT.base = IO;
		ASK.base = IO;
		PREDICATE.base = PREDICATE;
		GRE.base = PREDICATE;
		GRT.base = PREDICATE;
		LEE.base = PREDICATE;
		LET.base = PREDICATE;
		EQL.base = PREDICATE;
		LOGICAL.base = LOGICAL;
		AND.base = LOGICAL;
		OR.base = LOGICAL;
		NOT.base = LOGICAL;
	}
	
	public boolean isType(TokenCategory type) {
		boolean isType = false;
		
		if (type == base)
			isType = true;
		
		return isType;
	}
	
	public TokenCategory getBaseToken(String str) {
		return base;
	}
	
	public boolean equal(TokenCategory type) {
		boolean isType = false;

		if (this == type)
			isType = true;
		
		return isType;
	}
}
