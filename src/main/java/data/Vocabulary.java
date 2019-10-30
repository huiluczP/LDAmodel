package data;

import element.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/*
 * 词汇表
 * 获取所有文档中词汇并放入map
 * 支持单个文档和文档集合添加
 */
public class Vocabulary {
    HashMap<String,Integer> words;
    String[] number_word;

    public Vocabulary(){
        words=new HashMap<String, Integer>();
    }

    public HashMap<String,Integer> getWords(){
        return words;
    }

    public String[] getNumber_word(){
        return number_word;
    }

    //为每个词做上对应的处理(为已经处理好word的文档对象)
    public void uniqueVoc(ArrayList<Document> docs){
        for(Document d:docs){
            putWord(d);
        }

        //将number映射到word上
        number_word=new String[words.size()];
        Iterator iterator=words.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            number_word[(Integer)val]=(String)key;
        }
    }

    //单个文档的词汇添加
    public void putWord(Document doc){
        ArrayList<String> dWords=doc.getWords();
        for(String w:dWords){
            if(!words.containsKey(w)){
                words.put(w,words.size());
            }
        }
    }

    public void printVoc(){
        HashMap<String,Integer> wordMap=words;
        Iterator iterator=wordMap.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            System.out.println(key.toString()+" "+val.toString());
        }
    }

}
