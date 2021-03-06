package parser;

// Teste de Commit
import inter.Access;
import inter.And;
import inter.Arith;
import inter.Break;
import inter.Constant;
import inter.Do;
import inter.Else;
import inter.Expr;
import inter.Id;
import inter.If;
import inter.Not;
import inter.Or;
import inter.Readln;
import inter.Rel;
import inter.Seq;
import inter.Set;
import inter.SetElem;
import inter.Stmt;
import inter.Unary;
import inter.While;
import inter.Writeln;
import java.awt.image.LookupTable;

import java.io.IOException;

import lexer.Lexer;
import lexer.Num;
import lexer.Tag;
import lexer.Token;
import lexer.Word;
import symbols.Array;
import symbols.Env;
import symbols.Type;

public class Parser {

	private Lexer	lex;			// lexical analyzer for this parser
	private Token	look;			// lookahead tagen
	Env				top		= null;	// current or top symbol table
	int				used	= 0;	// storage used for declarations

	public Parser(Lexer l) throws IOException {
		lex = l;
		move();
	}

	void move() throws IOException {
		look = lex.scan();
	}

	void error(String s) {
		throw new Error("near line " + lex.line + ": " + s);
	}

	void match(int t) throws IOException {
		if (look.tag == t)
			move();
		else
			error("syntax error");
	}

	public void program() throws IOException { // program -> block
		Stmt s = inicio(); // Stmt s = block();
		int begin = s.newlabel();
		int after = s.newlabel();
		s.emitlabel(begin);
		s.gen(begin, after);
		s.emitlabel(after);
	}

	/*
	 * /* Stmt block() throws IOException { // block -> { decls stmts }
	 * match('{'); // match(Tag.BEGIN); Env savedEnv = top; top = new Env(top);
	 * decls(); Stmt s = stmts(); match('}'); // match(Tag.END); top = savedEnv;
	 * return s; }
	 */
	Stmt inicio() throws IOException { // TODO TESTE block -> { decls stmts }
		match(Tag.PROGRAM);
		match(Tag.ID);
		match(';');
		Env savedEnv = top;
		top = new Env(top);
		decls();
		Stmt s = block();
		// match(Tag.END);
		match('.');
		top = savedEnv;
		return s;
	}

	Stmt block() throws IOException { // block -> { decls stmts }
		match(Tag.BEGIN);
		Env savedEnv = top;
		top = new Env(top);
		// decls();
		System.out.println("look= " + look);

		Stmt s = stmts();
		match(Tag.END);
		// match('.');
		top = savedEnv;
		return s;
	}

	void decls() throws IOException { // decls -> var
		if (look.tag == Tag.VAR) {// teste
			move();
			while (look.tag == Tag.ID) {// while (look.tag == Tag.BASIC) { // D
										// -> type ID ;
				Token tok = look;
				System.out.println("tok= " + tok);
				match(Tag.ID); // Type p = type();
				match(':'); // Token tok = look;
				Type p = type();

				match(';');
				Id id = new Id((Word) tok, p, used);
				top.put(tok, id);
				used = used + p.width;
				System.out.println("last= " + look);
			}
		}
	}

	Type type() throws IOException {

		Type p = (Type) look; // expect look.tag == Tag.BASIC

		 match(Tag.BASIC);
		 if (look.tag != '[')
		 return p; // T -> basic
		 else
		 return dims(p); // return array type

	/*	if (look.tag == Tag.ARRAY)
			return dims(p);
		else
			return p; // T -> basic  */
	}

	Type dims(Type p) throws IOException {
		// ARRAY
		match('[');
		Token tok = look;
		match(Tag.NUM);
		match(']');
		if (look.tag == '[')
			p = dims(p);

		return new Array(((Num) tok).value, p);
	}

	Stmt stmts() throws IOException {
		if (look.tag == Tag.END)
			return Stmt.Null;
		else
			return new Seq(stmt(), stmts());
	}

	Stmt stmt() throws IOException {
		Expr x;
		Stmt s, s1, s2, s3, s4;
		Stmt savedStmt; // save enclosing loop for breaks

		switch (look.tag) {

			case ';':
				move();
				return Stmt.Null;

			case Tag.IF:
				// TODO TESTE match(Tag.IF); x = bool(); match('Tag.THEN');
				match(Tag.IF);
				x = bool();
				match(Tag.THEN);
				s1 = stmt();
				if (look.tag != Tag.ELSE)
					return new If(x, s1);
				match(Tag.ELSE);
				s2 = stmt();
				return new Else(x, s1, s2);

			case Tag.WHILE:
				While whilenode = new While();
				savedStmt = Stmt.Enclosing;
				Stmt.Enclosing = whilenode;
				match(Tag.WHILE);
				// match('(');
				x = bool();
				// match(')');
				match(Tag.DO);
				s1 = stmt();
				whilenode.init(x, s1);
				Stmt.Enclosing = savedStmt; // reset Stmt.Enclosing
				return whilenode;

			case Tag.REPEAT: // ////////Repeat
				Do donode = new Do();
				savedStmt = Stmt.Enclosing;
				Stmt.Enclosing = donode;
				match(Tag.REPEAT);
				s1 = stmt();
				match(Tag.UNTIL);
				x = bool();
				match(';');
				donode.init(s1, x);
				Stmt.Enclosing = savedStmt; // reset Stmt.Enclosing
				return donode;

			case Tag.PROCEDURE:
				match(Tag.PROCEDURE);
				match(Tag.ID);
				match(';');
				Env savedEnv = top;
				top = new Env(top);
				decls();
				Stmt aux = block();
				top = savedEnv;
				return aux;

			case Tag.FUNCTION:
				match(Tag.FUNCTION);
				match(Tag.ID);
				match(';');
				Env saveEnv = top;
				top = new Env(top);
				decls();
				Stmt block = block();
				top = saveEnv;
				return block;

			/*
			 * case Tag.FOR: 
			 * match(Tag.FOR); 
			 * Token tok = look; // variavel de incremento 
			 * assign(); 
			 * match(Tag.TO); 
			 * expr(); num
			 * match(Tag.DO); 
			 * Env saveEnv = top;
				top = new Env(top);
				decls();
				Stmt for = block();
				top = saveEnv;
				return new For("tok<num", stmt);
                          // incremento retorno do inicio do for
			 */


			case Tag.WRITELN:
                        case Tag.READLN:
                                Writeln wrln = new Writeln();
				//savedStmt = Stmt.Enclosing;
				Stmt.Enclosing = wrln;
                                String a = look.toString();
                                move();
                                match('(');
                                String b = "";
                                if (look.tag == Tag.ID) {
                                    b = look.toString();
                                    move();
                                }
                                match(')');
                                wrln.gen(a,b);
                                return wrln;
                 
			case Tag.BREAK:
				match(Tag.BREAK);
				match(';');
				return new Break();

			case Tag.BEGIN:
				return block();

			default:
				return assign();
		}
	}

	Stmt assign() throws IOException {
		Stmt stmt;
		Token t = look;
		match(Tag.ID);
		Id id = top.get(t);
		if (id == null)
			error(t.toString() + " undeclared");

		if (look.tag == ':') { // S -> id = E ;
			move();
			match('=');
			stmt = new Set(id, bool());
		} else { // S -> L = E ;
			Access x = offset(id);
			match(':');
			// move();
			match('=');
			stmt = new SetElem(x, bool());
		}
		match(';');
		return stmt;
	}

	Expr bool() throws IOException {
		Expr x = join();
		while (look.tag == Tag.OR) {
			Token tok = look;
			move();
			x = new Or(tok, x, join());
		}
		return x;
	}

	Expr join() throws IOException {
		Expr x = equality();
		while (look.tag == Tag.AND) {
			Token tok = look;
			move();
			x = new And(tok, x, equality());
		}
		return x;
	}

	Expr equality() throws IOException {
		Expr x = rel();
		while (look.tag == Tag.EQ || look.tag == Tag.NE) {
			Token tok = look;
			move();
			x = new Rel(tok, x, rel());
		}
		return x;
	}

	Expr rel() throws IOException {
		Expr x = expr();
		switch (look.tag) {
			case '<':
			case Tag.LE:
			case Tag.GE:
			case '>':
				Token tok = look;
				move();
				return new Rel(tok, x, expr());
			default:
				return x;
		}
	}

	Expr expr() throws IOException {
		Expr x = term();
		while (look.tag == '+' || look.tag == '-') {
			Token tok = look;
			move();
			x = new Arith(tok, x, term());
		}
		return x;
	}

	Expr term() throws IOException {
		Expr x = unary();
		while (look.tag == '*' || look.tag == '/') {
			Token tok = look;
			move();
			x = new Arith(tok, x, unary());
		}
		return x;
	}

	Expr unary() throws IOException {
		if (look.tag == '-') {
			move();
			return new Unary(Word.minus, unary());
		} else if (look.tag == '!') {
			Token tok = look;
			move();
			return new Not(tok, unary());
		} else
			return factor();
	}

	Expr factor() throws IOException {
		Expr x = null;
		switch (look.tag) {
			case '(':
				move();
				x = bool();
				match(')');
				return x;
			case Tag.NUM:
				x = new Constant(look, Type.Int);
				move();
				return x;
			case Tag.REAL:
				x = new Constant(look, Type.Float);
				move();
				return x;
			case Tag.TRUE:
				x = Constant.True;
				move();
				return x;
			case Tag.FALSE:
				x = Constant.False;
				move();
				return x;
			default:
				error("syntax error");
				return x;
			case Tag.ID:
				String s = look.toString();
				Id id = top.get(look);
				if (id == null)
					error(look.toString() + " undeclared");
				move();
				if (look.tag != '[')
					return id;
				else
					return offset(id);
		}
	}

	Access offset(Id a) throws IOException { // I -> [E] | [E] I
		Expr i;
		Expr w;
		Expr t1, t2;
		Expr loc; // inherit id

		Type type = a.type;
		match('[');
		i = bool();
		match(']'); // first index, I -> [ E ]
		type = ((Array) type).of;
		w = new Constant(type.width);
		t1 = new Arith(new Token('*'), i, w);
		loc = t1;
		while (look.tag == '[') { // multi-dimensional I -> [ E ] I
			match('[');
			i = bool();
			match(']');
			type = ((Array) type).of;
			w = new Constant(type.width);
			t1 = new Arith(new Token('*'), i, w);
			t2 = new Arith(new Token('+'), loc, t1);
			loc = t2;
		}

		return new Access(a, loc, type);
	}
}
