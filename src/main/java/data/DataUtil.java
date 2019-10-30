package data;

import element.Document;

import java.util.ArrayList;

/*
 * 语料库
 * 实现documents内存存放
 * 初始化文档和初始化马氏链状态
 * 初始化词汇表
 */
public class DataUtil {
    int k=3;
    ArrayList<Document> documents;
    DataDao dao;
    Vocabulary vocabulary;

    public int word_topic_sum[][];//每个词归入主题的次数

    public DataUtil(int k){
        this.k=k;
        documents=new ArrayList<Document>();
        dao=new DataDao();
        dao.initCon();
        vocabulary=new Vocabulary();
    }

    public ArrayList<Document> getDocuments(){
        return documents;
    }

    public Vocabulary getVocabulary(){
        return vocabulary;
    }

    //读取文档并修改为对应文档对象
    public void documentHandle(){
        ArrayList<String> docStrings=dao.getDoc();
        ArrayList<String> docName=dao.getDocName();
        if(docStrings==null){
            System.out.println("无文档数据");
            return;
        }
        for(int i=0;i<docStrings.size();i++){
            String content=docStrings.get(i);
            String name=docName.get(i);
            addDocument(content,name);
        }
    }

    //处理空格字符串
    public void addDocument(String d,String n){
        Document docitem=new Document(k);
        ArrayList<String> docword=docitem.getWords();
        String [] arr =d.split("\\s+");
        for(String s:arr){
            docword.add(s);
        }
        docitem.name=n;
        documents.add(docitem);
    }

    //初始化documents的wordnum
    public void initWordNum(){
        for(Document d:documents){
            d.initWordNumber(vocabulary.getWords());
        }
    }

    //随机每个词汇主题
    public void initRandomTopic(){
        word_topic_sum=new int[vocabulary.getWords().size()][k];
        for(Document d:documents){
            d.wordTopic=new int[d.getWords().size()];
            for(int i=0;i<d.getWords().size();i++) {
                //随机主题
                int randomTopic=(int) (Math.random() * k);
                d.wordTopic[i] = randomTopic;
                //该文档中该主题词数量+1
                d.topic_word_num[randomTopic]++;
                //每个词归入主题的次数+1
                word_topic_sum[d.wordNumber[i]][randomTopic]++;
            }
        }
    }

    //总data初始化（读取文档，生成词汇表，处理文档对应文字编号，随机进行主题分配，计算文档当前各主题数量（利用wordtopic））
    public void initData(){
        documentHandle();
        vocabulary.uniqueVoc(documents);
        initWordNum();
        initRandomTopic();
    }

    //输出所有文档的词与对应的词——主题分布
    public void printWordAndTopic(){
        int t=0;
        for(Document d:documents){
            System.out.println(t+":");
            for(int i=0;i<d.wordNumber.length;i++){
                System.out.print(d.wordNumber[i]+"|");
            }
            System.out.println();

            for(int i=0;i<d.wordTopic.length;i++){
                System.out.print(d.wordTopic[i]+"|");
            }
            System.out.println();
            t++;
        }
    }

    //将string转换为文档对象
    public static Document stringToDocument(String d,int k){
        Document docitem=new Document(k);
        ArrayList<String> docword=docitem.getWords();
        String [] arr =d.split("\\s+");
        for(String s:arr){
            docword.add(s);
        }
        return docitem;
    }

    public void printword_topic_sum(){
        for(int i=0;i<vocabulary.getWords().size();i++) {
            for (int j = 0; j < k; j++){
                System.out.print(word_topic_sum[i][j]+"|");
            }
            System.out.println();
        }
    }
}
