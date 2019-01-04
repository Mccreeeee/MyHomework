/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ll.pkg1;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author huqingyu18
 */
public class CommonFunction {

    //判断a是否在b所显示的文法规则中，且处于首字符位置
    public boolean judgeEqual(String a, String b) {
        String[] spaceSplitStr = b.split("\\s+");
        if (a.equals(spaceSplitStr[0])) {
            return true;
        }
        return false;
    }

    //输入一条文法规则，得到消除左递归后的文法规则
    public String eliminateLeft(String inputStr) {
        PreProcess pre = new PreProcess(inputStr);
        ArrayList<String> bodyTmp = pre.getBodyList();
        //存打头符号不为(->左边)的非终结符的文法规则
        String strTmp1 = "";
        //存打头符号为(->左边)的非终结符的文法规则
        String strTmp2 = "";
        //判断是否含有左递归，不含就不修改
        boolean modifyFlag = false;
        //存最终改写好的文法规则
        String completedStr = "";
        //bodyTmp为一条文法规则进行|分割后的产生式数组
        for (String bodyTmpStr : bodyTmp) {
            String[] spaceSplitStr = bodyTmpStr.split("\\s+");
            //分割后每一个打头的符号不为(->左边)的非终结符的单产生式A->X1的处理
            if (!spaceSplitStr[0].equals(pre.getHeadList().get(0))) {
                if (!spaceSplitStr[0].equals("ε")) {
                    bodyTmpStr = bodyTmpStr + " " + pre.getHeadList().get(0) + '\'';
                } else {
                    bodyTmpStr = pre.getHeadList().get(0) + '\'';
                }
                strTmp1 = strTmp1 + bodyTmpStr + "|";
            } else {
                modifyFlag = true;
                //去除第一个字符
                String strTmp3 = bodyTmpStr.replaceFirst(spaceSplitStr[0], "");
                //去除第一个字符后的空格
                strTmp3 = strTmp3.replaceFirst("\\s+", "");
                strTmp3 = strTmp3 + " " + pre.getHeadList().get(0) + '\'';
                strTmp2 = strTmp2 + strTmp3 + "|";
            }
        }
        //若该条文法规则不存在左递归则会跳到此处
        if (modifyFlag == false) {
            completedStr = inputStr;
        } else {
            //去除多余的|号（可能出现A->A a|A b的特殊情况，此时strTmp1会为空，要排除这种特殊情况）
            //重新拼好文法规则
            if(!strTmp1.equals("")){
                strTmp1=strTmp1.substring(0, strTmp1.length() - 1);
            }else{
                strTmp1=pre.getHeadList().get(0) + '\'';
            }
            completedStr = pre.getHeadList().get(0) + "->"
                    + strTmp1 + "\n"
                    + pre.getHeadList().get(0) + '\'' + "->"
                    + strTmp2 + "ε";
        }
        return completedStr;
    }

    //实现文法规则的替换的功能，把b中的a用a的文法规则替换，前提为先消除直接左递归
    public String replaceRules(String a, String b) {
        PreProcess preA = new PreProcess(a);
        PreProcess preB = new PreProcess(b);
        boolean modifyFlag = false;
        for (int i = 0; i < preB.getBodyList().size(); i++) {
            //B文法规则中某一条strTmp1有A非终结符打头就进行替换
            String strTmp1 = preB.getBodyList().get(i);
            if (judgeEqual(preA.getHeadList().get(0), strTmp1)) {
                modifyFlag = true;
                //添加替换后的规则
                for (String strTmp2 : preA.getBodyList()) {
                    preB.getBodyList().add(strTmp1.replace(preA.getHeadList().get(0), strTmp2));
                }
                preB.getBodyList().remove(i);
                //避免迭代时出错
                i--;
            }
        }
        //重组成文法规则
        String completedRules = "";
        if (modifyFlag == false) {
            completedRules = b;
        } else {
            String completedStr = "";
            for (String strTmp3 : preB.getBodyList()) {
                completedStr = completedStr + strTmp3 + "|";
            }
            completedRules = preB.getHeadList().get(0) + "->" + completedStr.substring(0, completedStr.length() - 1);
        }
        return completedRules;
    }

    //消除所有的普遍左递归，输出消除后的文法规则，输入完整的文法规则
    public String eliminateAllLeft(String inputStr) {
        //按行分割
        String completedRules1 = "";
        String completedRules2 = "";
        String[] lineSplitStr = inputStr.split("\\r?\\n");
        for (int i = 0; i < lineSplitStr.length; i++) {
            for (int j = 0; j <= i - 1; j++) {
                String[] completedStr = completedRules1.split("\\r?\\n");
                for (String strTmp : completedStr) {
                    //存在替换才替换
                    lineSplitStr[i] = replaceRules(strTmp, lineSplitStr[i]);
                }
            }
            //消除当前文法规则的左递归
            String[] strTmp1 = eliminateLeft(lineSplitStr[i]).split("\\r?\\n");
            completedRules1 += strTmp1[0] + "\n";
            //可能存在不用替换的情况，此时无A'所以不用剔除
            if (strTmp1.length == 2) {
                completedRules2 += strTmp1[1] + "\n";
            }
        }
        completedRules1 = completedRules1 + completedRules2;
        return completedRules1.substring(0, completedRules1.length() - 1);
    }

    //最长串匹配，输入为2个字符串，输出为最长串，针对文法规则而言
    public String longestMatch(String a, String b) {
        String matchedStr = "";
        int len = 0;
        if (a.length() >= b.length()) {
            len = b.length();
        } else {
            len = a.length();
        }
        for (int i = 0; i < len; i++) {
            if (a.charAt(i) == b.charAt(i)) {
                matchedStr = matchedStr + a.charAt(i);
            } else {//尽可能快的结束
                break;
            }
        }
        //去除最后的空格
        if (matchedStr.length() != len) {
            matchedStr = matchedStr.trim();
        }
        return matchedStr;
    }

    //提取一条文法规则的左公因子，并改写，返回改写后的文法规则
    public String extractLeft(String inputStr) {
        PreProcess pre = new PreProcess(inputStr);
        //存产生式的数组
        ArrayList<String> bodyTmp = pre.getBodyList();
        //存将要一起处理的产生式
        ArrayList<String> production = new ArrayList<>();
        //存处理后得到的产生式
        ArrayList<String> endProduction = new ArrayList<>();
        //处理产生式大于1个时不存在左公因子的情况
        boolean firstFlag = true;
        //无需提取，及时停止即可
        if (bodyTmp.size() <= 1) {
            return inputStr;
        }
        boolean stopFlag = false;
        String extend = pre.getHeadList().get(0);
        //存最终结果
        String endStr = "";
        do {
            //记录最终最长串
            String longestStr = "";
            //暂存目前最长串
            String tempLongestStr = "";
            //存改好的文法规则的第一部分
            String completedRules1 = "";
            //存改好的文法规则的第二部分
            String completedRules2 = "";
            //按字母升序排序，方便后续处理
            Collections.sort(bodyTmp);
            //两两比较找最长串
            for (int i = 0; i < bodyTmp.size() - 1; i++) {
                tempLongestStr = longestMatch(bodyTmp.get(i), bodyTmp.get(i + 1));
                if (longestStr.length() <= tempLongestStr.length()) {
                    longestStr = tempLongestStr;
                }
            }
            //无左公因子时
            if (longestStr.equals("")) {
                //第一次进来时就无左公因子就直接不继续后续操作
                if (firstFlag == true) {
                    return inputStr;
                } else {
                    //终止本次循环同时直接跳出去
                    stopFlag = true;
                    continue;
                }
            }
            //第一次进来时有左公因子就继续后续操作
            //同时避免firstFlag=true的if语句中，置为false
            firstFlag = false;
            for (String strTmp : bodyTmp) {
                //记录相同的字符位数
                int num = 0;
                //最长匹配串大于当前字符串时意味着肯定不会对这个字符串进行提取
                //直接保留并跳过此次循环即可
                if(strTmp.length()<longestStr.length()){
                    completedRules1 = completedRules1 + strTmp + "|";
                    continue;
                }
                for (int i = 0; i < longestStr.length(); i++) {
                    if (longestStr.charAt(i) != strTmp.charAt(i)) {
                        num = i - 1;
                        break;
                    } else {
                        num = i;
                    }
                }
                //存在最长匹配串，把符合条件的存到一起
                if (num == longestStr.length() - 1) {
                    production.add(strTmp);
                } else {//其余的保留
                    completedRules1 = completedRules1 + strTmp + "|";
                }
            }
            //重写文法规则
            completedRules1 = pre.getHeadList().get(0) + "->"
                    + completedRules1 + longestStr + " " + extend + '\'';
            completedRules2 = extend + '\'' + "->";
            //对符合条件的产生式提出左公因子后剩下的字符进行操作
            for (String strTmp : production) {
                String replace = strTmp.replace(longestStr, "");
                //提取后为空的情况
                if (replace.length() == 0) {
                    completedRules2 += "ε";
                } else {
                    //把首尾两端空格除去
                    completedRules2 += replace.trim();
                }
                completedRules2 += "|";
            }
            //添加换行符
            completedRules1 += "\n";
            //去除多余的|号
            completedRules2 = completedRules2.substring(0, completedRules2.length() - 1);
            //用另一个数组暂存不会再有左公因子的新文法规则
            endProduction.add(completedRules2);
            //累加'号，产生S,S',S''等
            extend = extend + '\'';
            //重置一下对新的提取后的文法规则进行尝试性的再提取
            PreProcess pre2 = new PreProcess(completedRules1);
            pre = pre2;
            bodyTmp = pre.getBodyList();
            production.clear();
            //存放最终提取完了的文法规则
            endStr = completedRules1;
        } while (stopFlag == false);
        for (String endStr1 : endProduction) {
            endStr += endStr1 + "\n";
        }
        //首尾空格格式化
        return endStr.trim();
    }

    //提取所有文法规则的左公因子，全部提取完，返回最终的文法规则
    public String extractAllLeft(String inputStr) {
        //按行分割
        String[] lineSplitStr = inputStr.split("\\r?\\n");
        //存最终的结果
        String completedRules = "";
        for (String strTmp : lineSplitStr) {
            completedRules += extractLeft(strTmp) + "\n";
        }
        //首尾空格格式化
        return completedRules.trim();
    }
}
