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
public class TreeNode {
    protected char text;
    protected TreeNode lcNode;
    protected TreeNode rcNode;
    public TreeNode(char c) {
        text = c;
        lcNode = null;
        rcNode = null;
    }
    @Override
    public String toString(){
        return "(t: "+ String.valueOf(text) + "; l: " + lcNode + "; r: " + rcNode + ")";
    }
}