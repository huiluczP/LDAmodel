package cluster;

import element.Document;

import java.util.ArrayList;
import java.util.Random;

/*
 * 使用文档对应的主题分布作为聚类特征进行聚类
 * kmean算法
 * 可使用与模型相同主题数也可自己设定
 */
public class KmeansAdapter {
    int k;//聚类数量
    double[][] topicDistribution;//主题分布
    double[][] distence;//距离矩阵
    double[][] clusterCenter;//聚类中心
    int topic[];//聚类类型

    int max=100;

    public KmeansAdapter(){}
    public KmeansAdapter(int k, double[][] distribution){
        this.k=k;
        this.topicDistribution=distribution;
        clusterCenter=new double[k][];
        topic=new int[topicDistribution.length];
    }

    public double calDis(double[] a,double[] b){
        //返回欧氏距离平方
        double sum=0;
        for(int i=0;i<a.length;i++){
            sum+=(a[i]-b[i])*(a[i]-b[i]);
        }
        return sum;
    }

    //初始化相似度矩阵
    public void initDistence(){
        int size=topicDistribution.length;
        distence=new double[size][size];
        for(int i=0;i<size;i++)
            for(int j=0;j<size;j++){
                distence[i][j]=calDis(topicDistribution[i],topicDistribution[j]);
            }
    }

    //初始化对应的聚类中心(完全随机)
    public void initCenter(){
        for(int i=0;i<k;i++) {
            double[] randomtemple=new double[topicDistribution[0].length];
            double sum=1;
            for (int j = 0; j < topicDistribution[0].length-1; j++) {
                randomtemple[j]=Math.random()*sum;
                sum-=randomtemple[j];
            }
            randomtemple[topicDistribution[0].length-1]=sum;
            clusterCenter[i]=randomtemple;
        }

        printClusterCenter();//
        System.out.println("初始化聚类中心完成");
    }

    boolean contain(int[] r,int i)
    {
        for(int j=0;j<i;j++)
        {
            if(r[j]==r[i])
                return true;
        }
        return false;
    }

    //随机设置中心点
    public int[] randomcenter()
    {
        int[] r=new int[k];
        Random rm=new Random();
        for(int i=0;i<k;i++)
        {
            do
                r[i]=rm.nextInt(topicDistribution.length);
            while(contain(r,i));
        }
        return r;
    }

    //初始化对应的聚类中心(从集合里面挑)
    public void initCenterInItems(){
        int []r=randomcenter();
        for(int i=0;i<k;i++){
            clusterCenter[i]=topicDistribution[r[i]];
        }
    }

    //一次聚类过程
    public void cluster(){
        int size=topicDistribution.length;
        //找到聚类中心最近的位置
        for(int i=0;i<size;i++){
            double min=calDis(topicDistribution[i],clusterCenter[0]);
            int cluster=0;
            for(int j=0;j<k;j++){
                double temple=calDis(topicDistribution[i],clusterCenter[j]);
                if(temple<min){
                    min=temple;
                    cluster=j;
                }
            }
            topic[i]=cluster;
        }

        //获取对应的新聚类中心
        int []num=new int[k];
        double[][] sum=new double[k][topicDistribution[0].length];
        for(int i=0;i<size;i++){
            for(int j=0;j<topicDistribution[0].length;j++){
                sum[topic[i]][j]+=topicDistribution[i][j];
            }
            num[topic[i]]++;
        }

        for(int i=0;i<k;i++) {
            for (int j = 0; j < topicDistribution[0].length; j++) {
                if (num[i] != 0)
                    clusterCenter[i][j] = sum[i][j] / num[i];
            }
        }

    }

    public void kmean(){
        initDistence();
        //initCenter();
        initCenterInItems();
        for(int i=0;i<max;i++){
            cluster();
        }
    }

    public void printCluster(){
        int size=topicDistribution.length;
        for(int i=0;i<size;i++){
            System.out.println(i+": "+topic[i]);
        }
    }

    public void printCluster(ArrayList<Document> docs){
        int size=topicDistribution.length;
        for(int i=0;i<size;i++){
            System.out.println(i+" "+docs.get(i).name+": "+topic[i]);
        }
    }

    public void printClusterCenter(){
        for(int i=0;i<k;i++) {
            for(int j=0;j<topicDistribution[0].length;j++){
                System.out.print(clusterCenter[i][j]+"|");
            }
            System.out.println();
        }
    }

}
