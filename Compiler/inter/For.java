package inter;
import symbols.*;

public class For extends Stmt {
	
	Expr expr; Stmt stmt;

	   public For() {
                expr = null; stmt = null; 
	   }
           
              public void init(Stmt s, Expr x) {
      expr = x; stmt = s;
      if( expr.type != Type.Bool ) expr.error("boolean required in for");
   }

	   public void gen(int b, int a) {
               //?????
	   }
}
