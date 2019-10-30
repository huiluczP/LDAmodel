import cluster.KmeansAdapter;
import lda.LDAAdapter;

public class LDA_KmeansTest {
    public static void main(String[] args){
        int k=6;
        //训练模型
        LDAAdapter adapter=new LDAAdapter(k);
        System.out.println("初始化数据......");
        adapter.initData();
        System.out.println("开始采样......");
        adapter.gibbs();
        System.out.println("计算对应矩阵......");
        adapter.calDocTopic();
        adapter.calTopicWord();

        KmeansAdapter kmean=new KmeansAdapter(6,adapter.getDoc_topic());
        kmean.kmean();
        kmean.printCluster(adapter.getUtil().getDocuments());
    }
}
