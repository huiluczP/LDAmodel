package element;

import java.util.ArrayList;
import java.util.HashMap;

public class Document {

    public String name;
    ArrayList<String> words=new ArrayList<String>();//词语列表
    int k;//主题数

    public int topic_word_num[];//每个主题对应的词语数量
    public int wordNumber[];//词语转化为对应序号方便计算
    public int wordTopic[];//每个词语对应的主题

    public Document(int k){
        this.k=k;
        topic_word_num=new int[k];
        for(int i=0;i<k;i++){
            topic_word_num[i]=0;
        }
    }

    public ArrayList<String> getWords(){
        return words;
    }

    //映射词语为对应编号
    public void initWordNumber(HashMap<String,Integer> map){
        wordNumber=new int[words.size()];
        int t=0;
        for(String s:words){
            wordNumber[t]=(int)map.get(s);
            t++;
        }
    }
}
