import data.DataUtil;
import element.Document;
import lda.LDAAdapter;

public class GetDocTopicTest {
    public static void main(String[] args){
        int k=3;
        //训练模型
        LDAAdapter adapter=new LDAAdapter(k);
        System.out.println("初始化数据......");
        adapter.initData();
        System.out.println("开始采样......");
        adapter.gibbs();
        System.out.println("计算对应矩阵......");
        adapter.calDocTopic();
        adapter.calTopicWord();

        //测试获取单一文档的主题
        String docString="tank ship car";
        Document d=DataUtil.stringToDocument(docString,k);//此时将所有词转为word
        double []topicDistribution=adapter.getSingleDocTopic(d);//采样计算
        for(int i=0;i<topicDistribution.length;i++){
            System.out.println("topic "+i+": "+topicDistribution[i]);
        }
    }
}
