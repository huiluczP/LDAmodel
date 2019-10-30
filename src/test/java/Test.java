import data.DataUtil;
import element.Document;
import lda.LDAAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Test {
    public static void main(String[] args){

        //测试数据库读取
        /*int t=0;
        DataUtil util=new DataUtil(3);
        util.documentHandle();
        ArrayList<Document> documents=util.getDocuments();
        for(Document d:documents){
            System.out.println(t+":");
            ArrayList<String> words=d.getWords();
            StringBuffer buffer=new StringBuffer();
            for(String w:words){
                buffer.append(w+"|");
            }
            System.out.println(buffer.toString());
            t++;
        }*/

        int k=3;

       /* //测试词汇表生成
        DataUtil util=new DataUtil(k);
        util.initData();
        System.out.println("vocabulary:");
        util.getVocabulary().printVoc();

        //测试将文档词汇和词汇表进行映射
        ArrayList<Document> docs=util.getDocuments();
        for(Document d:docs){
            for(int i=0;i<d.wordNumber.length;i++){
                System.out.print(d.wordNumber[i]+"|");
            }
            System.out.println();
        }

        //测试将随机文档主题输出
        for(Document d:util.getDocuments()){
            System.out.print("topic:");
            for(int i=0;i<d.wordTopic.length;i++){
                System.out.print(d.wordTopic[i]+"|");
            }
            System.out.println();
            System.out.print("topicwordsum for doc:");
            for(int i=0;i<k;i++){
                System.out.print(d.topic_word_num[i]+"|");
            }
            System.out.println();
        }

        //测试输出词汇被归入主题的次数(初始化)
        for(int i=0;i<util.word_topic_sum.length;i++) {
            for (int j = 0; j < k; j++) {
                System.out.print(util.word_topic_sum[i][j]);
            }
            System.out.println();
        }
        */

       //测试lda模型取样
        LDAAdapter adapter=new LDAAdapter(k);
        adapter.initData();
        //adapter.getUtil().getVocabulary().printVoc();

        adapter.gibbs();
        adapter.getUtil().printWordAndTopic();
        System.out.println();
        adapter.getUtil().printword_topic_sum();
        System.out.println();

        //测试输出文档——主题概率矩阵
        adapter.calDocTopic();
        adapter.printDocTopicMatrix();

        //测试输出主题——文档概率矩阵
        adapter.calTopicWord();
        adapter.printTopicWordMatrix();
        System.out.println();

        //测试输出主题对应词
        adapter.printTopicWordArray(5);
    }
}
