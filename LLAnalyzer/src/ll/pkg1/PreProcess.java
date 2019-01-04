/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ll.pkg1;

import java.util.ArrayList;

/**
 *
 * @author huqingyu18
 */
public class PreProcess {
    //文法规则head的ArrayList
    private ArrayList<String> headArrList;
    //文法规则body的ArrayList
    private ArrayList<String> bodyArrList;
    //输入文法规则
    public PreProcess(String inputStr){
        headArrList=new ArrayList<>();
        bodyArrList=new ArrayList<>();
        //先按行分割总的字符串
        String[] lineSplitStr=inputStr.split("\\r?\\n");
        for(String strTemp:lineSplitStr){
            //分为head和body两部分
            String[] arrowSplitStr=strTemp.split("->");
            //去除首尾空格
            arrowSplitStr[1]=arrowSplitStr[1].trim();
            //把中间多余的空格转变成一个空格
            String[] spaceSplitStr=arrowSplitStr[1].split("\\s+");
            arrowSplitStr[1]="";
            for(int i=0;i<spaceSplitStr.length;i++){
                arrowSplitStr[1]=arrowSplitStr[1]+spaceSplitStr[i]+" ";
            }
            arrowSplitStr[1]=arrowSplitStr[1].substring(0,arrowSplitStr[1].length()-1);
            //把文法规则的左右两部分拆开放入对应的ArrayList中，同步索引
            if(arrowSplitStr[1].contains("|")){
                String orSplitStr[]=arrowSplitStr[1].split("\\|");
                for(String orStrTemp:orSplitStr){
                    headArrList.add(arrowSplitStr[0]);
                    bodyArrList.add(orStrTemp);
                }
            }else{
                headArrList.add(arrowSplitStr[0]);
                bodyArrList.add(arrowSplitStr[1]);
            }
        }
    }
    public ArrayList<String> getHeadList(){
        return this.headArrList;
    }
    public ArrayList<String> getBodyList(){
        return this.bodyArrList;
    }
    public void clsList(){
        this.headArrList.clear();
        this.bodyArrList.clear();
    }
}
