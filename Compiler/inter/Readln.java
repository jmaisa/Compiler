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
public class Readln extends Stmt {
        Expr expr; Stmt stmt;

    public Readln() { expr = null; stmt = null; }
    
    public void gen(String a) {
      emit("syscall(readln(" + a + "))");
   }
}
