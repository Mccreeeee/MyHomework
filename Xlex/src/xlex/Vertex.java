/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlex;

import java.util.ArrayList;

/**
 *
 * @author Darkness
 */
public class Vertex {

    protected int vId;
    protected Edge eNode;
    public Vertex(int v) {
        vId = v;
        eNode = null;
    }

    @Override
    public String toString() {
        return "\r\n (vid: " + String.valueOf(vId) + "; node: " + eNode + ")";
    }
}
