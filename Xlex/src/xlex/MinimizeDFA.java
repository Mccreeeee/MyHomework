/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Darkness
 */
public class MinimizeDFA {

    protected Graph resultMinDFA;

    //在图g中由状态a经过c到达的状态集
    protected Set<Integer> toState(int a, char c, Graph g) {
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < g.arr.size(); i++) {
            if (g.arr.get(i).vId == a) {
                Edge edge = new Edge();
                edge = g.arr.get(i).eNode;
                while (edge != null) {
                    if (edge.eWeight == c) {
                        set.add(g.arr.get(edge.eId).vId);
                    }
                    edge = edge.eNext;
                }
            }
        }
        return set;
    }

    //相同长度数组判断是否相等，因最后一位存的是状态所以只需比对前面的是否相同即可
    protected boolean arrayEqual(int[] a, int[] b) {
        for (int i = 0; i < a.length - 1; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }

    //判断元素是否在集合中
    protected boolean isInSet(int a, Set<Integer> s) {
        Iterator<Integer> it = s.iterator();
        while (it.hasNext()) {
            if (a == it.next()) {
                return true;
            }
        }
        return false;
    }

    //判断存储集合的集合是否相等
    protected boolean isListEqual(ArrayList<Set<Integer>> l1, ArrayList<Set<Integer>> l2) {
        if (l1 == null && l2 == null) {
            return true;
        }
        if (l1 == null || l2 == null || l1.size() != l2.size()
                || l1.isEmpty() || l2.isEmpty()) {
            return false;
        }
        for (int i = 0; i < l1.size(); i++) {
            if (!l2.contains(l1.get(i))) {
                return false;
            }
        }
        return true;
    }

    //判断V1和V2是否相连，不仅要判断状态是否相同，还要判断边的权值是否相同
    protected boolean isLinked(Vertex v1, Vertex v2, char c) {
        Edge edge = new Edge();
        edge = v1.eNode;
        while (edge != null) {
            if (v2.vId == (edge.eId + 1) && edge.eWeight == c) {
                return true;
            } else {
                edge = edge.eNext;
            }
        }
        return false;
    }

    protected Graph minimizeDFA(Graph g, String rex) {
        Graph minDFA = new Graph();
        int num = 1;
        //划分非接受态和接受态，存储状态号而不是索引
        Set<Integer> endState = new HashSet<>();
        Set<Integer> notEndState = new HashSet<>();
        for (Integer end : g.end) {
            endState.add(end + 1);
        }
        for (int i = 0; i < g.arr.size(); i++) {
            if (!isInSet(g.arr.get(i).vId, endState)) {
                notEndState.add(g.arr.get(i).vId);
            }
        }
        Map<Set<Integer>, Integer> resultMap = new HashMap<>();
        //对非接受态标记并填入ArrayList准备进行分组
        ArrayList<int[]> judge = new ArrayList<>();
        Set<Set<Integer>> result = new HashSet<>();
        ArrayList<Set<Integer>> gList = new ArrayList<>();
        ArrayList<Set<Integer>> gTemp;
        gList.add(notEndState);
        gList.add(endState);
        do {
            //浅复制一份即可
            gTemp = (ArrayList<Set<Integer>>) gList.clone();
            for (int gl = 0; gl < gList.size(); gl++) {
                Iterator<Integer> it = gList.get(gl).iterator();
                while (it.hasNext()) {
                    //多加一个位置存状态号
                    int[] flag = new int[rex.length() + 1];
                    //避免使用next函数后指针向后跳，先进行保存
                    int state = it.next();
                    //最后一位写入状态
                    flag[rex.length()] = state;
                    for (int j = 0; j < rex.length(); j++) {
                        Edge tmp = new Edge();
                        //减一因为存的是状态号而不是索引，要减一
                        tmp = g.arr.get(state - 1).eNode;
                        while (tmp != null) {
                            //找到连接的状态，填上对应的flag数组
                            if (tmp.eWeight == rex.charAt(j)) {
                                for (int i = 0; i < gList.size(); i++) {
                                    if (isInSet(g.arr.get(tmp.eId).vId, gList.get(i))) {
                                        flag[j] = i + 1;
                                    }
                                }
                            }
                            tmp = tmp.eNext;
                        }
                    }
                    judge.add(flag);
                }
                for (int i = 0; i < judge.size(); i++) {
                    Set<Integer> tmpSet = new HashSet<>();
                    for (int j = 0; j < judge.size(); j++) {
                        //相等就加入集合中，因为是集合所以加入过一次后不会再加入
                        if (arrayEqual(judge.get(i), judge.get(j))) {
                            tmpSet.add(judge.get(j)[rex.length()]);
                        }
                    }
                    //改用集合存，最终不会再有重复的值
                    result.add(tmpSet);
                }
                //重置一次
                judge.clear();
            }
            gList.clear();
            for (Set<Integer> rSet : result) {
                gList.add(rSet);
            }
            result.clear();
        } while (!isListEqual(gTemp, gList));//对每一个result中的集合添加映射关系

        for (int gl1 = 0; gl1 < gList.size(); gl1++) {
            result.add(gList.get(gl1));
        }

        for (Set<Integer> s : result) {
            resultMap.put(s, num++);
        }

        //开始构造最小化DFA图
        //把最小化DFA图的顶点表构造好
        for (Integer i : resultMap.values()) {
            minDFA.addOneVertex(i);
        }
        //处理图的连接关系
        for (Set<Integer> s : resultMap.keySet()) {
            Vertex v1 = minDFA.arr.get(resultMap.get(s) - 1);
            //判断经过a，b等符号后的元素对应哪段映射
            for (Integer i : s) {
                for (int j = 0; j < rex.length(); j++) {
                    //经过某个符号后得到的状态集合
                    Set<Integer> s1 = toState(i, rex.charAt(j), g);
                    //找状态集合中每一个元素对应哪个映射
                    for (Integer i1 : s1) {
                        for (Set<Integer> tS : resultMap.keySet()) {
                            if (isInSet(i1, tS)) {
                                //排除重复连接的情况
                                Vertex v2 = minDFA.arr.get(resultMap.get(tS) - 1);
                                //没连接过才连接
                                if (!isLinked(v1, v2, rex.charAt(j))) {
                                    minDFA.vertexLink(v1, v2, rex.charAt(j), minDFA.arr);
                                }
                            }
                        }
                    }
                }
            }
        }
        //添加图的start状态
        minDFA.start = g.start;
        //添加图的end状态
        Iterator<Integer> oldEnd = g.end.iterator();
        Set<Integer> newEnd = new HashSet<>();

        while (oldEnd.hasNext()) {
            newEnd.add(oldEnd.next() + 1);
        }
        for (Set<Integer> s : resultMap.keySet()) {
            Set<Integer> res = new HashSet<>();
            res.addAll(s);
            res.retainAll(newEnd);
            if (!res.isEmpty()) {
                minDFA.end.add(resultMap.get(s) - 1);
            }
        }
        return minDFA;
    }

    public MinimizeDFA(Graph g, String rex) {
        resultMinDFA = minimizeDFA(g, rex);
    }
}
