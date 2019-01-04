/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 *
 * @author Darkness
 */
public class Graph {

    protected ArrayList<Vertex> arr;//添加一个节点并返回该节点
    protected int start;//记录开始顶点的数组索引值
    protected Set<Integer> end;//记录终止顶点的数组索引值

    protected Vertex addVertex(ArrayList<Vertex> array) {
        XlexFrame.vState++;
        Vertex v = new Vertex(XlexFrame.vState);
        array.add(v);
        return v;
    }

    protected Vertex addOneVertex(int state) {
        Vertex v = new Vertex(state);
        arr.add(v);
        return v;
    }

    //v1和v2连接，并赋予边的权值w
    protected void vertexLink(Vertex v1, Vertex v2, char w, ArrayList<Vertex> array) {
        //找v2点在顶点表中的索引
        Edge e1 = new Edge(array.indexOf(v2), w);
        //采用头插入
        e1.eNext = v1.eNode;
        //改头
        v1.eNode = e1;
    }

    //得到所有DFA状态，参数为初始状态及纯字符串
    protected Graph reflectoDFA(Set<Integer> set, String rex,Set<Integer> end) {
        Graph g=new Graph();
        //排序规则
        Comparator<Vertex> comparator = new Comparator<Vertex>() {
            public int compare(Vertex s1, Vertex s2) {
                return s1.vId - s2.vId;
            }
        };
        //把状态集合跑完记录下来成为新图的顶点
        Map<Set<Integer>, Integer> map = new HashMap<Set<Integer>, Integer>();
        Set<Integer> tmpSet = set;
        int num = 1;
        Stack<Set<Integer>> panding = new Stack<>();
        panding.add(tmpSet);
        while (!panding.isEmpty()) {
            tmpSet = panding.pop();
            if(!map.containsKey(tmpSet))
                    map.put(tmpSet, num++);
            //横向跑完
            for (int i = 0; i < rex.length(); i++) {
                Set<Integer> tSet = move(tmpSet, rex.charAt(i));
                if (tSet.isEmpty()) {
                    continue;
                }
                //有新状态就存入栈内暂存
                if (!map.containsKey(tSet)) {
                    panding.push(tSet);
                }
            }
        }
        //成为新图顶点
        for (Integer i: map.values()) {
            g.addOneVertex(i);
        }
        //排序，确保value-1=顶点索引号
        Collections.sort(g.arr,comparator);
        //顶点连接
        for (Set<Integer> s: map.keySet()) {
            //取集合对应的value
            int value = map.get(s) - 1;
            //找到对应的顶点
            Vertex state1 = g.arr.get(value);
            for (int j = 0; j < rex.length(); j++) {
                //横向检查对应的闭包集合
                Set<Integer> st = move(s, rex.charAt(j));
                if (st.isEmpty()) {
                    continue;
                }
                //根据对应闭包从map中得到value然后找到对应顶点
                Vertex state2 = g.arr.get(map.get(st) - 1);
                //进行连接
                g.vertexLink(state1, state2, rex.charAt(j), g.arr);
            }
        }
        //添加图的start状态
        g.start = map.get(set)-1;
        //添加图的end状态
        Iterator<Integer> it = end.iterator();
        Set<Integer> newend = new HashSet<>();
        while (it.hasNext()) {
            newend.add(it.next()+1);
        }
        for (Set<Integer> s: map.keySet()) {
            Set<Integer> result = new HashSet<Integer>();
            result.addAll(s);
            result.retainAll(newend);
            if (!result.isEmpty()) {
                g.end.add(map.get(s)-1);
            }
        }
        return g;
    }
    //得到闭包操作
    protected Set<Integer> move(Set<Integer> set, char c) {
        Set<Integer> sIt = new HashSet<Integer>();
        for (int i : set) {
            //求一次对应字符的闭包
            Set<Integer> tmp1 = closure(i, c);
            sIt.addAll(tmp1);
            //再求一次ε闭包
            Set<Integer> tmp2 = closure(tmp1, 'ε');
            sIt.addAll(tmp2);
        }
        return sIt;
    }
    //重载输入一个集合得到其指定闭包

    protected Set<Integer> closure(Set<Integer> set, char c) {
        Set<Integer> sIt = new HashSet<Integer>();
        for (int i : set) {
            Set<Integer> tmpresult = closure(i, c);
            sIt.addAll(tmpresult);
        }
        return sIt;
    }

    //输入一个状态得到其指定闭包
    protected Set<Integer> closure(int state, char c) {
        boolean[] visited = new boolean[arr.size()];
        Arrays.fill(visited, false);
        Set<Integer> st = new HashSet<Integer>();
        String[] str = getclosure(state, visited, c).split(",");
        for (int i = 0; i < str.length; i++) {
            if (!str[i].equals("")) {
                st.add(Integer.parseInt(str[i]));
            }
        }
        return st;
    }

    //从某个状态开始深度搜索，state-1为对应的索引，state状态的ε闭包，未加上state
    protected String getclosure(int state, boolean[] visited, char c) {
        String s = "";
        visited[state - 1] = true;
        Edge p = new Edge();
        //p为状态的第一条边
        p = arr.get(state - 1).eNode;
        //深度搜索
        while (p != null) {
            if (visited[p.eId] != true) {
                //边的权值和要求值相等时加上，否则回退
                if (p.eWeight == c) {
                    //此处+=是加上逗号
                    s += arr.get(p.eId).vId + ",";
                    //eId指的是索引号，正确状态要+1，此处+=是累计值
                    s += getclosure(p.eId + 1, visited, c);
                }
            }
            p = p.eNext;
        }
        return s;
    }

    //找到图的终态
    public Vertex findNFAlastState() {
        return XlexFrame.vList.get(end.iterator().next());
    }

    //找到图的初态
    public Vertex findfirstState() {
        return XlexFrame.vList.get(start);
    }

    public Graph() {
        arr = new ArrayList<>();
        start = 0;
        end = new HashSet<Integer>();
    }

    public Graph(ArrayList<Vertex> a) {
        arr = a;
        start = 0;
        end = new HashSet<Integer>();
    }

    @Override
    public String toString() {
        return "(arr: " + String.valueOf(arr) + "; start: " + start + "; end: " + end + ") ";
    }
}
