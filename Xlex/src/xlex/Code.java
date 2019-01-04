/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlex;

import java.util.Iterator;

/**
 *
 * @author Darkness
 */
public class Code {

    protected int column;
    protected int row;
    protected int[][] minDFATable;
    protected char[] ch;
    //在图g中由状态a经过c到达的状态

    protected int toState(int a, char c, Graph g) {
        for (int i = 0; i < g.arr.size(); i++) {
            if (g.arr.get(i).vId == a) {
                Edge edge = new Edge();
                edge = g.arr.get(i).eNode;
                while (edge != null) {
                    if (edge.eWeight == c) {
                        return g.arr.get(edge.eId).vId;
                    } else {
                        edge = edge.eNext;
                    }
                }
            }
        }
        return -1;
    }

    //参数为最小化后的DFA图和trans2方法处理后的字符串
    //同时构建好最小化DFA表格
    public Code(Graph minDFA, String rex) {
        column = rex.length() + 1;
        row = minDFA.arr.size();
        ch = new char[rex.length()];
        minDFATable = new int[row][column];
        //填第一列
        for (int i = 0; i < row; i++) {
            minDFATable[i][0] = minDFA.arr.get(i).vId;
        }
        //填第一行
        for (int i = 0; i < rex.length(); i++) {
            ch[i] = rex.charAt(i);
        }
        //填好数据
        for (int i = 0; i < row; i++) {
            for (int j = 1; j < column; j++) {
                minDFATable[i][j] = toState(minDFATable[i][0], ch[j - 1], minDFA);
            }
        }
    }
    //生成代码
    protected String toCode(Graph minDFA) {
        String temp = "bool xLex(string rex) { \r\n";
        temp += "bool f=true;\r\n";
        //初始状态
        temp += "int statement=" + (minDFA.start + 1) + ";\r\n";
        temp += "for(int i=0;i<rex.length();i++){  \r\n";
        temp += "switch(rex[i]) {\r\n";
        for (int i = 1; i < column; i++) {
            temp += "case'" + ch[i - 1] + "':\r\n";
            //固定某一列对每一行处理即处理同一case下的情况
            for (int j = 0; j < row; j++) {
                if (j != 0) {
                    temp += "else ";
                }
                temp += "if(statement==" + minDFATable[j][0] + ")";
                if (minDFATable[j][i] != -1) {
                    temp += "statement = " + minDFATable[j][i] + ";\r\n";
                } else {
                    temp += "f=false;\n";
                }
            }
            temp += "break;\n";
        }
        temp += "default:  \r\n";
        temp += "f=false;\r\n";
        temp += "break;\n";
        temp += "}\n";
        temp += "};\n";
        temp+= "if(";
        Iterator it=minDFA.end.iterator();
        while(it.hasNext()){
            temp+="statement!="+((int)it.next()+1)+"||";
        }
        temp=temp.substring(0, temp.length()-2);
        temp+="}\n";
        temp+="f=false;\n";
        temp += "return f;\n";
        temp += "};";
        return temp;
    }
}
