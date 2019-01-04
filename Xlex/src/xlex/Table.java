/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlex;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Darkness
 */
public class Table {

    //暂存数据
    private ArrayList<String> theadlist;
    //存储表格
    protected ArrayList<String> tdatalist;
    protected ArrayList<String> tdatalist2;
    //NFA图g中每一个顶点通过权值c的边到达的点，数据放在theadlist
    protected void NFAthrough_get(Graph g, char c) {
        theadlist.clear();
        theadlist.add(String.valueOf(c));
        Edge temp = new Edge();
        for (int i = 0; i < g.arr.size(); i++) {
            String s = "";
            temp = g.arr.get(i).eNode;
            //记录有多少个可到达的点
            int num = 0;
            while (temp != null) {
                if (temp.eWeight == c) {
                    num++;
                    if (num == 1) {
                        s = String.valueOf(g.arr.get(temp.eId).vId);//直接显示存状态而不是存索引
                    } else {
                        s += "," + String.valueOf(g.arr.get(temp.eId).vId);//同上
                    }
                }
                temp = temp.eNext;
            }
            theadlist.add(s);
        }
    }

    protected int sellength() {
        int maxlength = 0;
        Iterator<String> it = theadlist.iterator();
        while (it.hasNext()) {
            String next = it.next();
            if (next.length() >= maxlength) {
                maxlength = next.length();
            }
        }
        return maxlength;
    }
//将表头和内容依次相加

    protected void ttext(Graph g, String rex) {
        for (int i = 0; i < rex.length(); i++) {
            if (XlexFrame.isletter(rex.charAt(i)) || rex.charAt(i) == 'ε') {
                NFAthrough_get(g, rex.charAt(i));
                //进行处理放到tdatalist中
                output();
                for (int j = 0; j < tdatalist2.size(); j++) {
                    tdatalist2.set(j, tdatalist2.get(j) + tdatalist.get(j));
                }
            }
        }
        tdatalist2.add("Start State: "+(g.start+1));//g.start是开始的索引
        Iterator<Integer> it=g.end.iterator();//g.end是结束的索引
        String tmp="";
        while(it.hasNext()){
            tmp+=(it.next()+1)+" ";
        }
        tdatalist2.add("End State: "+tmp);
    }
//对theadlist格式化仍存放在tdatalist

    protected void output() {
        tdatalist.clear();
        //存改前表的最长长度
        int maxlistlength = sellength();
        String s2 = "";
        Iterator<String> it = theadlist.iterator();
        while (it.hasNext()) {
            String s0 = "";
            String s1 = "";
            String next = it.next();
            for (int i = 1; i <= (maxlistlength - next.length()); i++) {
                s0 += " ";
            }
            for (int i = 1; i <= maxlistlength; i++) {
                s1 += "-";
            }
            //处理成新的string字符串
            s2 = " " + next + s0 + "|";
            tdatalist.add(s2);
            s2 = "-" + s1 + "+";
            tdatalist.add(s2);
        }
    }
//rex为预处理后的纯字符串
    public Table(Graph g, String rex) {
        theadlist = new ArrayList<String>();
        tdatalist = new ArrayList<String>();
        tdatalist2 = new ArrayList<String>();
        theadlist.add("State");
        for (int i = 0; i < g.arr.size(); i++) {
            theadlist.add(String.valueOf(g.arr.get(i).vId));
        }
        output();
        //处理好后复制一份给tdatalist2保存
        for (int i = 0; i < tdatalist.size(); i++) {
            tdatalist2.add(tdatalist.get(i));
        }
        ttext(g, rex);
    }
}
