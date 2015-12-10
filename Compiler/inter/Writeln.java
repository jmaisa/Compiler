/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inter;

/**
 *
 * @author everton
 */
public class Writeln extends Stmt {
    Expr expr; Stmt stmt;

    public Writeln() { expr = null; stmt = null; }
    
    public void gen(String a, String b) {
      emit("syscall(" + a + "(" + b + "))");
   }
}
