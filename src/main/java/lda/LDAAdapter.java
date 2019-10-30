package lda;

import data.DataUtil;
import element.Document;
import element.SortWord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/*
 * LDA核心处理类
 * 维护语料库和词汇表
 * gibbs采样
 * 矩阵计算生成
 * 新文档主题分布获取
 */
public class LDAAdapter {

    int k=3;//主题数
    double alpha;//先验参数
    double beta;//先验参数

    DataUtil util;//语料库

    int max=10000;//max迭代次数

    double[][] doc_topic;//文档主题概率矩阵 [documentsize][k]
    double[][] top_word;//主题文档概率矩阵  [k][vocabularysize]

    public LDAAdapter(int k){
        this.k=k;
        alpha=50/k;
        beta=0.01;
    }

    public LDAAdapter(int k,double alpha,double beta){
        this.k=k;
        this.alpha=alpha;
        this.beta=beta;
    }

    public DataUtil getUtil(){
        return util;
    }
    public double[][] getDoc_topic(){
        return doc_topic;
    }

    //初始化数据，随机主题,设置结果矩阵大小
    public void initData(){
        util=new DataUtil(k);
        util.initData();

        int v=util.getVocabulary().getWords().size();
        doc_topic=new double[util.getDocuments().size()][k];
        top_word=new double[k][v];
    }

    //利用条件概率公式进行计算,dindex的文档的第windex个词
    //关键
    int gibbs_P(int dindex,int windex){
        Document dtemple=util.getDocuments().get(dindex);
        int wordNumber=dtemple.wordNumber[windex];//词编号
        int wordTopic=dtemple.wordTopic[windex];//词主题

        //除了该词以外的条件
        util.word_topic_sum[wordNumber][wordTopic]--;//该词语归入主题次数-1
        dtemple.topic_word_num[wordTopic]--;//文档中该主题的词总数-1
        int wordNumAfter=dtemple.getWords().size()-1;//文档中词语总数-1
        int topicWordSum=0;//当前对应主题被归入词次数总数-1
        for(int i=0;i<util.getVocabulary().getWords().size();i++){
            topicWordSum+=util.word_topic_sum[i][wordTopic];
        }

        //调用条件概率分布公式
        double[] p = new double[k];
        for(int i=0;i<k;i++){
            p[i]=(dtemple.topic_word_num[i]+alpha)/(wordNumAfter+k*alpha)*(util.word_topic_sum[wordNumber][i]+beta)/(topicWordSum+util.getVocabulary().getWords().size()*beta);
        }
        //为了方便取舍计算将分布相加
        for(int i=1;i<k;i++){
            p[i]+=p[i-1];
        }
        double u=Math.random()*p[k-1];//总概率不一定为1，使用累加后最大的概率
        for(wordTopic=0;wordTopic<k;wordTopic++){
            if(u<p[wordTopic])//当u在对应的概率段中，将该段对应topic作为该词新topic
                break;
        }

        //将新主题对应数据更新
        util.word_topic_sum[wordNumber][wordTopic]++;
        dtemple.topic_word_num[wordTopic]++;

        return wordTopic;
    }

    //模型建立主采样过程
    public void gibbs(){
        //初始化马氏链
        initData();
        //迭代
        for(int t=0;t<max;t++){
            //对每个文档的每个词进行处理,gibba采样设置topic
            for(int i=0;i<util.getDocuments().size();i++) {
                for (int j = 0; j < util.getDocuments().get(i).getWords().size(); j++) {
                    util.getDocuments().get(i).wordTopic[j] = gibbs_P(i, j);
                }
            }
        }
    }

    //计算文档——主题矩阵
    public void calDocTopic(){
        for(int i=0;i<util.getDocuments().size();i++){
            Document dtemple=util.getDocuments().get(i);
            for(int j=0;j<k;j++){
                doc_topic[i][j]=(dtemple.topic_word_num[j]+alpha)/(dtemple.getWords().size()+k*alpha);
            }
        }
    }

    //计算主题——词汇矩阵
    public void calTopicWord(){
        for(int i=0;i<k;i++){
            int topicWordSum=0;//当前对应主题被归入词次数总数
            for(int p=0;p<util.getVocabulary().getWords().size();p++){
                topicWordSum+=util.word_topic_sum[p][i];
            }
            for(int j=0;j<util.getVocabulary().getWords().size();j++){
                top_word[i][j]=(util.word_topic_sum[j][i]+beta)/(topicWordSum+beta*util.getVocabulary().getWords().size());
            }
        }
    }

    //通过输入新文档来判断对应的主题分布(不影响模型)
    //topic_word已知
    public double[] getSingleDocTopic(Document d){
        int wordNumber[]=new int[d.getWords().size()];
        int sum=0;//词汇总数

        //先初始化主题状态,将词汇表里存在的词语进行处理
        for(int i=0;i<d.getWords().size();i++){
            String word=d.getWords().get(i);
            if(util.getVocabulary().getWords().containsKey(word)){
                wordNumber[sum]=util.getVocabulary().getWords().get(word);
                sum++;
            }
        }
        d.wordNumber=new int[sum];//词汇编号
        d.wordTopic=new int[sum];//词汇对应主题
        for(int i=0;i<sum;i++){
            d.wordNumber[i]=wordNumber[i];
            int randomTopic=(int) (Math.random() * k);
            d.wordTopic[i] = randomTopic;
            d.topic_word_num[randomTopic]++;
        }

        //采样
        for(int t=0;t<max;t++){
            for(int j=0;j<d.wordNumber.length;j++){//对词汇表中存在的词语进行处理
                int topic=d.wordTopic[j];
                d.topic_word_num[topic]--;//文档中该主题的词总数-1
                int topicWordSum=0;//当前对应主题被归入词次数总数
                for(int i=0;i<util.getVocabulary().getWords().size();i++){
                    topicWordSum+=util.word_topic_sum[i][topic];
                }
                int wordsum=d.wordNumber.length-1;//文档词总数-1

                //调用分布公式
                double[] p = new double[k];
                for(int i=0;i<k;i++){
                    p[i]=(d.topic_word_num[i]+alpha)/(wordsum+k*alpha)*(util.word_topic_sum[d.wordNumber[j]][i]+beta)/(topicWordSum+util.getVocabulary().getWords().size()*beta);
                }
                //为了方便取舍计算将分布相加
                for(int i=1;i<k;i++){
                    p[i]+=p[i-1];
                }
                double u=Math.random()*p[k-1];//总概率不一定为1，使用累加后最大的概率
                for(topic=0;topic<k;topic++) {
                    if (u < p[topic])//当u在对应的概率段中，将该段对应topic作为该词新topic
                        break;
                }
                //更新主题
                d.topic_word_num[topic]++;
                d.wordTopic[j]=topic;
            }
        }

        //获取主题分布矩阵
        double []topicDistribution=new double[k];
        for(int i=0;i<k;i++)
            topicDistribution[i]=(d.topic_word_num[i]+alpha)/(d.wordNumber[i]+k*alpha);

        return topicDistribution;
    }

    public void printDocTopicMatrix(){
        System.out.println("Doc_topic_matrix:");
        for(int i=0;i<util.getDocuments().size();i++){
            for(int j=0;j<k;j++){
                System.out.print(doc_topic[i][j]+"|");
            }
            System.out.println();
        }
    }

    public void printTopicWordMatrix(){
        System.out.println("topic_word_matrix:");
        for(int i=0;i<k;i++){
            for(int j=0;j<util.getVocabulary().getWords().size();j++){
                System.out.print(top_word[i][j]+"|");
            }
            System.out.println();
        }
    }

    //更直观方式表示主题——词,参数为最多显示词数量
    public void printTopicWordArray(int maxsize){
        for(int i=0;i<k;i++){
            System.out.println("topic "+i+":");
            ArrayList<SortWord> words=new ArrayList<SortWord>();
            for(int j=0;j<util.getVocabulary().getWords().size();j++){
                words.add(new SortWord(j,top_word[i][j]));
            }
            Collections.sort(words, new Comparator<SortWord>() {
                public int compare(SortWord o1, SortWord o2) {
                    if(o1.rate<o2.rate)
                        return 1;
                    if(o1.rate==o2.rate)
                        return 0;
                    if(o1.rate>o2.rate)
                        return -1;
                    return 0;
                }
            });
            for(int t=0;t<util.getVocabulary().getWords().size()&&t<maxsize;t++){
                System.out.println("    "+words.get(t).number+"  "+util.getVocabulary().getNumber_word()[words.get(t).number]+"  "+words.get(t).rate);
            }
        }
    }

}
