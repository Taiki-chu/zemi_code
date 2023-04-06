package zemi_code;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class program1 {
    static final double fr = 2*Fpow(10,9);//周波数　f 10^9[Hz] GHz
    static final double ramuda = 3*Fpow(10,8)/fr;//波長 λ : 10^8[Hz]
    static final double sampling_interval = ramuda/20;//サンプリング間隔Δd
    static final double max_distance = ramuda*1000;//最大距離dmax
    static int nmax = (int)(max_distance / sampling_interval);//nmax
    static double ang_f1 = Create_ang_f();//Φ1
    static final double ang_s1 = 0;//θ1
//    static Random random ;
    public static void main(String[] args) throws IOException {
        //C:\Users\nyasu\IdeaProjects\netpro2022_java17\src\glst_work1
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("グルスタ_課題1.csv")));
//        double d_d_n = sampling_interval * 0;
        System.out.println( "fr : " + fr );
        System.out.println( "nmax : " + nmax );
        double E[][] = new double[nmax][2];//[0]real,[1]image//imageは足し引き算のみ
        double P[] = new double[nmax];
        double P10log[] = new double[nmax];
        //N=1のE[0]
//        E[0][0] = calE(0,sampling_interval,ramuda,ang_s1,ang_f1,0);//実部
//        E[0][1] = calE(0,sampling_interval,ramuda,ang_s1,ang_f1,1);//虚部

        writer.println("n,Δdn,Re{E[n},Im{E[n]},10log10P[n]");
        for(int n = 0 ; n < nmax; n++){//10の場所にnmaxを入れる
            E[n][0] = calE(n,sampling_interval,ramuda,ang_s1,ang_f1,0);//実部
            E[n][1] = calE(n,sampling_interval,ramuda,ang_s1,ang_f1,1);//虚部
            P[n] = Math.round(calP(E[n][0],E[n][1]));P10log[n] = 10*Math.log10(P[n]);
            System.out.print(" Δdn : " + sampling_interval * n );
            System.out.print(" Re{E[" + n + "]} : " + E[n][0] );
            System.out.print(" Im{E[" + n + "]} : " + E[n][1] );
            System.out.println(" 10log10P[" + n + "] : " + P10log[n] );
            writer.println(n+","+sampling_interval * n+","+E[n][0]+","+E[n][1]+","+P10log[n]);
        }
        writer.close();
    }
    public static double Create_ang_f(){//Φを生成
        Random random = new Random();
        return random.nextDouble()*Math.PI;
    }
    /**Fpow(double n,int r) n*10^r
     * n:累乗したい数 , r:10につく指数**/
    public static double Fpow(double n,int r){//累乗の関数//1乗以上限定
        double n0 = n;
        for(int i = 1 ; i < r ; i++){
            n *= n0;
        }
        return n;
    }
    public static long Fpow(long n,int r){//累乗の関数
        long n0 = n ;
        for(int i = 1 ; i < r ; i++){
            n *= n0;
        }
        return n;
    }

    public static double calE(int n,double d_d,double ramuda,
                              double ang_s,double ang_f,int judge){//Eを計算
        //judge == 0 の時cosで返す（実数）
        //judge == 1 の時sinで返す（虚数）
        //        Math.exp(2*Math.PI*n*d_d*Math.cos(ang_s)/ramuda+ang_f);
        if(judge == 0){//実数を返す
            return Math.cos(2*Math.PI*n*d_d*Math.cos(ang_s)/ramuda+ang_f);
        }else{//虚数を返す
            return Math.sin(2*Math.PI*n*d_d*Math.cos(ang_s)/ramuda+ang_f);
        }
    }
    public static double calP(double E_real,double E_image){//Pを計算　実部の2乗+虚部の2乗
        return Fpow(E_real,2)+Fpow(E_image,2);
    }
}
