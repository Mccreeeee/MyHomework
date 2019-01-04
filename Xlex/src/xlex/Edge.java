/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlex;

/**
 *
 * @author Darkness
 */
public class Edge {
    protected int eId;
    protected char eWeight;
    protected Edge eNext;
    public Edge(){
        eId=0;
        eWeight=' ';
        eNext=null;
    }
    public Edge(int e,char w){
        eId=e;
        eWeight=w;
        eNext=null;
    }
        @Override
    public String toString(){
        return "(eid: "+ String.valueOf(eId) + "; weight: " +eWeight+": next:"+eNext+ ")";
    }
}
