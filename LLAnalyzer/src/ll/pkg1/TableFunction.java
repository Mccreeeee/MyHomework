/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ll.pkg1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 *
 * @author huqingyu18
 */
public class TableFunction {

    //获得所有终结符包括$，但不包括ε
    public Set<String> getTerminal(ArrayList<String> headList,
            ArrayList<String> bodyList) {
        Set<String> nonTerminal = new HashSet<>();
        for (String str : bodyList) {
            String[] spaceSplitStr = str.split("\\s+");
            for (String strTmp : spaceSplitStr) {
                //->左边不包含这个字符串代表为终结符，同时排除ε
                if (!headList.contains(strTmp) && !strTmp.equals("ε")) {
                    nonTerminal.add(strTmp);
                }
            }
        }
        nonTerminal.add("$");
        return nonTerminal;
    }

    //获得所有非终结符
    public Set<String> getNonTerminal(ArrayList<String> headList) {
        Set<String> terminal = new HashSet<>();
        for (String str : headList) {
            terminal.add(str);
        }
        return terminal;
    }

    //判断是什么符号，1为ε或终结符,2为非终结符
    public int judgeSymbol(ArrayList<String> headList, String symbol) {
        for (int i = 0; i < headList.size(); i++) {
            if (headList.get(i).equals(symbol)) {
                return 2;
            }
        }
        return 1;
    }
    //记录所有走过的符号
    private Set<String> noteFirstSet = new HashSet<>();
    private Set<String> noteFollowSet = new HashSet<>();

    //每取一次first集合或follow集合都要手动清空一次
    public void clearNoteArr() {
        noteFirstSet.clear();
        noteFollowSet.clear();
    }

    //得到一个符号的first集合,headList和bodyList均为完整文法规则的切割部分
    public Set<String> getFirstSet(ArrayList<String> headList,
            ArrayList<String> bodyList, String symbol) {
        Set<String> firstTmpSet = new HashSet<>();
        //检索相同的非终结符的文法规则，用一个新的ArrayList存储
        ArrayList<String> arrTemp = new ArrayList<>();
        Set<String> emptySet = new HashSet<>();
        emptySet.add("ε");
        //终结符直接加入
        if (judgeSymbol(headList, symbol) == 1) {
            firstTmpSet.add(symbol);
        } else {
            //非终结符递归
            //没记录过才递归
            if (!noteFirstSet.contains(symbol)) {
                noteFirstSet.add(symbol);
                for (int i = 0; i < headList.size(); i++) {
                    if (headList.get(i).equals(symbol)) {
                        arrTemp.add(bodyList.get(i));
                    }
                }
                //处理完后所有左侧与symbol相同的非终结符对应的文法规则都在arrTemp中
                for (String strTemp : arrTemp) {
                    String[] bodyX = strTemp.split("\\s+");
                    //停止标志
                    boolean stopFlag = false;
                    //对|分割的每一部分的每个字符进行递归，遇到非ε就停止
                    for (int j = 0; j < bodyX.length && stopFlag == false; j++) {
                        //递归求每一个字符的first集合
                        Set<String> bodyTmp = getFirstSet(headList, bodyList, bodyX[j]);
                        //不包含ε就终止
                        if (!bodyTmp.contains("ε")) {
                            firstTmpSet.addAll(bodyTmp);
                            stopFlag = true;
                            continue;
                        }
                        //包含ε的时候
                        bodyTmp.removeAll(emptySet);
                        firstTmpSet.addAll(bodyTmp);
                    }
                    if (stopFlag == false) {
                        firstTmpSet.add("ε");
                    }
                }
            }
        }
        return firstTmpSet;
    }

    //得到一串字符（一个产生式）的总first集合，headList和bodyList均为完整文法规则的切割部分
    public Set<String> getAllFirstSet(ArrayList<String> headList,
            ArrayList<String> bodyList, String inputStr) {
        Set<String> tmpSet = new HashSet<>();
        String[] spaceSplitStr = inputStr.split("\\s+");
        for (String tmpStr : spaceSplitStr) {
            Set<String> firstTmp = getFirstSet(headList, bodyList, tmpStr);
            tmpSet.addAll(firstTmp);
            //每求一次first集合要记得及时清空一次
            noteFirstSet.clear();
            //不含空就停止，否则继续
            if (!firstTmp.contains("ε")) {
                break;
            }
        }
        return tmpSet;
    }

    //得到一个非终结符的follow集合,headList和bodyList均为完整文法规则的切割部分
    public Set<String> getFollowSet(ArrayList<String> headList,
            ArrayList<String> bodyList, String symbol) {
        Set<String> followTmpSet = new HashSet<>();
        Set<String> emptySet = new HashSet<>();
        emptySet.add("ε");
        if (judgeSymbol(headList, symbol) == 1) {
            return followTmpSet;
        }
        if (headList.get(0).equals(symbol)) {
            followTmpSet.add("$");
        }
        if (!noteFollowSet.contains(symbol)) {
            noteFollowSet.add(symbol);
            for (int i = 0; i < bodyList.size(); i++) {
                String bodyTmp = bodyList.get(i);
                if (bodyTmp.contains(symbol)) {
                    String[] bodyX = bodyTmp.split("\\s+");
                    for (int j = 0; j < bodyX.length; j++) {
                        //bodyX[j]代表右侧拆分得到的需要求follow集合的非终结符
                        if (bodyX[j].equals(symbol)) {
                            //非终结符在最后一个位置
                            if (j == bodyX.length - 1) {
                                //若在此处跳出函数会导致noteFollowArr和noteFirstArr不为空，故使用此函数后需要全部清空一次
                                Set<String> lastTmp = getFollowSet(headList, bodyList, headList.get(i));
                                followTmpSet.addAll(lastTmp);
                            } else {//不在最后一个位置
                                Set<String> firstTmp = new HashSet<>();
                                //停止标志
                                boolean stopFlag = false;
                                for (int k = j + 1; k < bodyX.length && stopFlag == false; k++) {
                                    firstTmp.addAll(getFirstSet(headList, bodyList, bodyX[k]));
                                    //每一次取first集合时都要清空一次记录数组
                                    noteFirstSet.clear();
                                    //如果后一个的first集合不包含ε就不用往后递归了，否则一直进行下去
                                    if (!getFirstSet(headList, bodyList, bodyX[k]).contains("ε")) {
                                        //每一次取first集合时都要清空一次记录数组
                                        noteFirstSet.clear();
                                        stopFlag = true;
                                    }
                                }
                                firstTmp.removeAll(emptySet);
                                followTmpSet.addAll(firstTmp);
                                //递归到末尾还存在ε的时候
                                if (stopFlag == false) {
                                    Set<String> followSet = getFollowSet(headList, bodyList, headList.get(i));
                                    followTmpSet.addAll(followSet);
                                }
                            }
                        }
                    }
                }
            }
        }
        return followTmpSet;
    }

    //判断文法规则是否符合LL1文法,inputStr为完整文法规则
    public boolean judgeLL(String inputStr) {
        //处理完得到完整的分割部分，用于获取first集合
        PreProcess pre = new PreProcess(inputStr);
        //对文法规则进行行分割
        String[] lineSplitStr = inputStr.split("\\r?\\n");
        //对每一条文法规则进行操作
        for (String tmpStr : lineSplitStr) {
            //对每一条文法规则进行分割，用于判断LL文法
            PreProcess tmpPre = new PreProcess(tmpStr);
            //判断first(ai)和first(aj)交集是否为空，不为空则不是LL(1)
            for (int i = 0; i < tmpPre.getBodyList().size(); i++) {
                for (int j = i + 1; j < tmpPre.getBodyList().size(); j++) {
                    Set<String> tmpSet1 = getAllFirstSet(pre.getHeadList(),
                            pre.getBodyList(), tmpPre.getBodyList().get(i));
                    Set<String> tmpSet2 = getAllFirstSet(pre.getHeadList(),
                            pre.getBodyList(), tmpPre.getBodyList().get(j));
                    tmpSet1.retainAll(tmpSet2);
                    if (!tmpSet1.isEmpty()) {
                        return false;
                    }
                }
            }
            Set<String> tmpSet3 = getFirstSet(pre.getHeadList(), pre.getBodyList(),
                    tmpPre.getHeadList().get(0));
            //记得及时清空记录数组
            noteFirstSet.clear();
            if (tmpSet3.contains("ε")) {
                Set<String> tmpSet1 = getFirstSet(pre.getHeadList(),
                        pre.getBodyList(), tmpPre.getHeadList().get(0));
                //记得及时清空记录数组
                noteFirstSet.clear();
                Set<String> tmpSet2 = getFollowSet(pre.getHeadList(),
                        pre.getBodyList(), tmpPre.getHeadList().get(0));
                //记得及时清空记录数组
                clearNoteArr();
                tmpSet1.retainAll(tmpSet2);
                if (!tmpSet1.isEmpty()) {
                    return false;
                }
            }
        }
        //操作完了都不为false就代表为LL(1)文法
        return true;
    }

    //建立LL1分析表的对应位置的映射，输入为完整的文法规则
    //映射关系为(A->a)->rule
    public Map<Map<String, String>, String> buildTableMap(String inputStr) {
        PreProcess pre = new PreProcess(inputStr);
        //记录开始符号
        startSymbol = pre.getHeadList().get(0);
        //记录非终结符
        nonTerminal = getNonTerminal(pre.getHeadList());
        //记录终结符
        terminal = getTerminal(pre.getHeadList(), pre.getBodyList());
        Map<Map<String, String>, String> tableMap = new HashMap<>();
        //对每一个产生式
        for (int i = 0; i < pre.getBodyList().size(); i++) {
            Set<String> tmpSet = getAllFirstSet(pre.getHeadList(),
                    pre.getBodyList(), pre.getBodyList().get(i));
            //对该产生式A->α的每一个first(α)的元素添加映射(A->α)
            for (String strTmp : tmpSet) {
                Map<String, String> mapSet = new HashMap<>();
                mapSet.put(pre.getHeadList().get(i), strTmp);
                //拼装成完整的产生式A->α
                String ruleTmp = pre.getHeadList().get(i) + "->" + pre.getBodyList().get(i);
                if (!tableMap.containsKey(mapSet)) {
                    tableMap.put(mapSet, ruleTmp);
                } else {//存在就替换掉
                    tableMap.put(mapSet, tableMap.get(mapSet) + "; " + ruleTmp);
                }
            }
            //若ε在first(α)中，则对follow(A)中每个元素添加映射
            if (tmpSet.contains("ε")) {
                tmpSet = getFollowSet(pre.getHeadList(), pre.getBodyList(),
                        pre.getHeadList().get(i));
                //记得及时清空记录数组
                clearNoteArr();
                for (String strTmp : tmpSet) {
                    Map<String, String> mapSet = new HashMap<>();
                    mapSet.put(pre.getHeadList().get(i), strTmp);
                    //拼装成完整的产生式A->α
                    String ruleTmp = pre.getHeadList().get(i) + "->" + pre.getBodyList().get(i);
                    if (!tableMap.containsKey(mapSet)) {
                        tableMap.put(mapSet, ruleTmp);
                    } else {//存在就替换掉
                        tableMap.put(mapSet, tableMap.get(mapSet) + ";  " + ruleTmp);
                    }
                }
            }
        }
        return tableMap;
    }

    //输入LL表行，列对应的字符数据，输出表格对应的文法规则
    public String getTableRules(Map<Map<String, String>, String> tableMap,
            String row, String col) {
        String grammarRule = "";
        //建立临时映射用于找到对应表中的文法规则
        Map<String, String> tmpMap = new HashMap<>();
        tmpMap.put(row, col);
        //存在相应的映射才找对应映射
        if (tableMap.keySet().contains(tmpMap)) {
            //找对应的文法规则
            for (Map<String, String> findMap : tableMap.keySet()) {
                if (tmpMap.equals(findMap)) {
                    //拿到对应的文法规则，例如A->a b
                    grammarRule = tableMap.get(findMap);
                }
            }
        } else {//不存在这样的映射，直接终止
            grammarRule = "";
        }
        return grammarRule;
    }
    //记录开始符号
    private String startSymbol = "";
    //非终结符集合
    private Set<String> nonTerminal;
    //终结符集合
    private Set<String> terminal;
    //分析栈的变化情况
    private String anaStack = "";
    //需要分析句子的变化情况
    private String anaStr = "";
    //分析结果的变化情况
    private String anaResult = "";
    public String getAnaStack(){
        return anaStack;
    }
    public String getAnaStr(){
        return anaStr;
    }
    public String getAnaResult(){
        return anaResult;
    }
    
    //输出LL分析过程，输入为LL1表的映射和需要分析的句子
    //需要分析的句子中不同的字符串用空格分开
    public void analyzeLL(Map<Map<String, String>, String> tableMap, String inputStr) {
        Stack<String> analyzeStack = new Stack<>();
        //初始化栈
        analyzeStack.push("$");
        analyzeStack.push(startSymbol);
        anaStack += analyzeStack.toString() + "\n";
        inputStr += " $";
        anaStr += inputStr + "\n";
        //倒置压入输入栈
        String[] spaceSplitStr = inputStr.split("\\s+");
        //记录输入字符串的第几位字符串
        int i = 0;
        //开始循环操作
        while (analyzeStack.peek() != "$") {
            //是终结符对应终结符
            if (terminal.contains(analyzeStack.peek())
                    && spaceSplitStr[i].equals(analyzeStack.peek())) {
                //match掉
                analyzeStack.pop();
                anaStack += analyzeStack.toString() + "\n";
                inputStr = inputStr.substring(spaceSplitStr[i].length(),inputStr.length()).trim();
                anaStr += inputStr + "\n";
                anaResult += "匹配\n";
                i++;
            } //是非终结符对应终结符
            else if (nonTerminal.contains(analyzeStack.peek())
                    && terminal.contains(spaceSplitStr[i])) {
                //建立临时映射用于找到对应表中的文法规则
                Map<String, String> tmpMap = new HashMap<>();
                tmpMap.put(analyzeStack.peek(), spaceSplitStr[i]);
                //存在相应的映射才找对应映射
                if (tableMap.keySet().contains(tmpMap)) {
                    //找对应的文法规则
                    for (Map<String, String> findMap : tableMap.keySet()) {
                        if (tmpMap.equals(findMap)) {
                            //拿到对应的文法规则，例如A->a b，进行分割处理
                            //分割->,用右半部分a b
                            String[] arrowSplitStr = tableMap.get(findMap).split("->");
                            //分割空格，变成a,b
                            String[] spaceSplitStr1 = arrowSplitStr[1].split("\\s+");
                            //先弹出，然后将文法规则倒置压入分析栈
                            analyzeStack.pop();
                            for (int j = spaceSplitStr1.length - 1; j >= 0; j--) {
                                analyzeStack.push(spaceSplitStr1[j]);
                            }
                            anaStack += analyzeStack.toString() + "\n";
                            anaStr += inputStr + "\n";
                            anaResult += tableMap.get(findMap) + "\n";
                            //文法规则为A->ε时直接弹出即可
                            if (arrowSplitStr[1].equals("ε")) {
                                analyzeStack.pop();
                                anaStack += analyzeStack.toString() + "\n";
                                anaStr += inputStr + "\n";
                                anaResult += "\n";
                            }
                        }
                    }
                } else {//不存在这样的映射就报错，跳出循环
                    break;
                }
            } else {//既不是文法规则的终结符也不是非终结符就报错，跳出循环
                break;
            }
        }
        if (analyzeStack.peek().equals("$") && spaceSplitStr[i].equals("$")) {
            anaResult += "接受";
        } else {
            anaResult += "出错";
        }
    }
}
