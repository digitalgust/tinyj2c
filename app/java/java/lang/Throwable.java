/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java.lang;

/**
 *
 * @author gust
 */
public class Throwable {
    String detailMessage;
    
    public Throwable(){
        detailMessage="";
    }
    
    public Throwable(String s){
        detailMessage=s;
    }
    
    public String getMessage() {
        return detailMessage;
    }
}
