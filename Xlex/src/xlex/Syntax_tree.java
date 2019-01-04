/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlex;

import static xlex.XlexFrame.isletter;

/**
 *
 * @author Darkness
 */
/*
rA->rA"|"rB|rB
rB->rB"-"rC|rC
rC->rC"*"|rD
rD->"("rA")"|letter


rA->rB{"|"rB}
rB->rC{"-"rC}
rC->rD{"*"}
rD->"("rA")"|letter
 */
public class Syntax_tree {

    private TreeNode root;
    //获得下一字符
    private char token;
    //存储正则表达式
    private String s;
    //记录字符串中的字符位置
    private int i = 0;

    private void getToken() {
        char ch;
        if (i < s.length()) {
            ch = s.charAt(i);
            i++;
            token = ch;
        } else {
            token = ' ';
        }
    }

    private void error() {
        System.out.println("error!!!!!!!!!");
    }

    private void match(char expect) {
        if (token == expect) {
            getToken();
        } else {
            error();
        }
    }

    private TreeNode rA() {
        TreeNode temp, newtemp;
        //保存第一个|左边优先级高的子树
        temp = rB();
        while (token == '|') {
            //创建新节点
            newtemp = new TreeNode('|');
            match(token);
            //左孩子存前面得到的左子树
            newtemp.lcNode = temp;
            //右孩子存第一个|右边的优先级高的子树
            newtemp.rcNode = rB();
            //把得到的新树作为一个新的子树赋给temp，最终temp就是树根
            temp = newtemp;
        }
        return temp;
    }

    private TreeNode rB() {
        TreeNode temp, newtemp;
        temp = rC();
        while (token == '-') {
            newtemp = new TreeNode('-');
            match(token);
            newtemp.lcNode = temp;
            newtemp.rcNode = rC();
            temp = newtemp;
        }
        return temp;
    }

    private TreeNode rC() {
        TreeNode temp, newtemp;
        temp = rD();
        while (token == '*') {
            newtemp = new TreeNode('*');
            match(token);
            newtemp.lcNode = temp;
            temp = newtemp;
        }
        return temp;
    }

    private TreeNode rD() {
        //什么字符串都不输入即为空
        TreeNode temp = null;
        if (token == '(') {//输入的是括号
            match('(');
            //没有优先级比他高的了，直接调用rA()建树
            temp = rA();
            match(')');
        } else if (isletter(token)) {//输入的是字符
            temp = new TreeNode(token);
            getToken();
        }
        return temp;
    }



    public TreeNode getRoot() {
        return root;
    }

    public Graph toNFA(TreeNode t) {
        Graph g1 = null;
        Graph g2 = null;
        Vertex v1 = null;
        Vertex v2 = null;
        //刚开始时XlexFrame.vList为空，创建一个图的局部变量
        Graph temp = new Graph(XlexFrame.vList);
        if (t != null) {
            if (isletter(t.text)) {
                //添加2个顶点并连起来
                v1 = temp.addVertex(XlexFrame.vList);
                v2 = temp.addVertex(XlexFrame.vList);
                temp.vertexLink(v1, v2, t.text, XlexFrame.vList);
                temp.start = XlexFrame.vList.indexOf(v1);
                temp.end.add(XlexFrame.vList.indexOf(v2));
            } else {
                switch (t.text) {
                    case '|':
                        //使用后序遍历递归进行NFA的构造
                        g1 = toNFA(t.lcNode);
                        g2 = toNFA(t.rcNode);
                        //多加一个顶点
                        v1 = temp.addVertex(XlexFrame.vList);
                        v2 = temp.addVertex(XlexFrame.vList);
                        //对2个图进行连接
                        temp.vertexLink(v1, g1.findfirstState(), 'ε', XlexFrame.vList);
                        temp.vertexLink(v1, g2.findfirstState(), 'ε', XlexFrame.vList);
                        temp.vertexLink(g1.findNFAlastState(), v2, 'ε', XlexFrame.vList);
                        temp.vertexLink(g2.findNFAlastState(), v2, 'ε', XlexFrame.vList);
                        //设置开始、终止状态的索引值
                        temp.start = XlexFrame.vList.indexOf(v1);
                        temp.end.add(XlexFrame.vList.indexOf(v2));
                        break;
                    case '-':
                        //使用后序遍历递归进行NFA的构造
                        g1 = toNFA(t.lcNode);           //System.err.println("g1: "+g1);
                        g2 = toNFA(t.rcNode);
                        //System.err.println("g2: "+g2);
                        //对2个图进行连接
                        temp.vertexLink(g1.findNFAlastState(), g2.findfirstState(), 'ε', XlexFrame.vList);
                        //设置开始、终止状态的索引值
                        temp.start = XlexFrame.vList.indexOf(g1.findfirstState());
                        temp.end.add(XlexFrame.vList.indexOf(g2.findNFAlastState()));
                        break;
                    case '*':
                        //使用后序遍历递归进行NFA的构造
                        g1 = toNFA(t.lcNode);
                        //暂存叶子的终态，避免添加节点连接后终态改变
                        Vertex vt=g1.findNFAlastState();
                        v1 = temp.addVertex(XlexFrame.vList);
                        v2 = temp.addVertex(XlexFrame.vList);
                        //先连前面的节点
                        temp.vertexLink(vt, g1.findfirstState(), 'ε', XlexFrame.vList);
                        temp.vertexLink(v1, g1.findfirstState(), 'ε', XlexFrame.vList);
                        temp.vertexLink(v1, v2, 'ε', XlexFrame.vList);
                        temp.vertexLink(vt, v2, 'ε', XlexFrame.vList);
                        //设置开始、终止状态的索引值
                        temp.start = XlexFrame.vList.indexOf(v1);
                        temp.end.add(XlexFrame.vList.indexOf(v2));
                        
                }
            }
        }
        return temp;
    }

    public Syntax_tree(String rex) {
        s = rex;
        getToken();
        root = rA();
    }
}
