package zemi_code;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class program2 {
    static final double fr = 2*Fpow(10,9);//周波数　f 10^9[Hz] GHz
    static final double ramuda = 3*Fpow(10,8)/fr;//波長 λ : 0.3[Hz]
    static final double sampling_interval = (ramuda/20);//サンプリング間隔Δd//static final double sampling_interval = (ramuda/20);
    static final double max_distance = ramuda*1000;//最大距離dmax
    static int nmax = (int)(max_distance / sampling_interval);//nmax
    static int N = 6;//波の数N  ...たぶんランダム？
    static double ang_f1 = Create_ang_f();//Φ1 丸める
    public static void main(String[] args) throws IOException {
        double S[] = new double[N];
        double[] fai = new double[N];
        double E[][] = new double[nmax][2];//[0]real,[1]image//imageは足し引き算のみ
        double P[] = new double[nmax + 1];
        double P_log[] = new double[nmax + 1];

        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(N+" グルスタ_課題2_" + sampling_interval + ".csv")));//旧グルスタ_課題2.csv


        for(int i = 0 ;i < N ; i++ ){//θ[i]を計算
            S[i] = Create_shita(i,N);
            fai[i] = Create_ang_f();
        }
        writer.println("行番号,Δdn,P[n],10logP[n],,行番号,受信電力値,番号n,番号n/N");
        for(int n = 0 ;n < nmax ;n++ ){//E[n],P[n]を計算
            E[n][0] = calE_N(n,sampling_interval,ramuda,S,fai,N,0);
            E[n][1] = calE_N(n,sampling_interval,ramuda,S,fai,N,1);
            P[n] = calP(E[n][0],E[n][1]);
            P_log[n] = 10*Math.log10(P[n]);//dB表示
            showgpaphN(E,P,n);
            writer.println(n+","+sampling_interval * n+","+P[n]+","+10*Math.log10(P[n])+",,"+n+","+10*Math.log10(P[n])+","+(n+1)+","+((double)n+1)/nmax);
        }
        writer.close();
        jikosoukan(P_log , nmax , sampling_interval , 0);//課題2ではこのnum = 0　の意味はない
    }

    public static void showgpaphN(double[][] E,double[] P,int n){
        System.out.print("nmax : " + nmax);
        System.out.print(" n : " + n);
        System.out.print(" Re{E[" + n + "]} : " + E[n][0] );
        System.out.print(" Im{E[" + n + "]} : " + E[n][1] );
        System.out.print(" Δdn : " + sampling_interval * n );
        System.out.print(" P[" + n + "] : " + P[n] );
        System.out.println(" 10log10P[" + n + "] : " + 10*Math.log10(P[n]) );
    }
    public static double Create_shita(int i , int N){//θ[i]を計算
        return 2*Math.PI*(i-1)/N;
    }
    public static double Create_ang_f(){//Φを生成
        Random random = new Random();
        return random.nextDouble()*Math.PI*2;
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

    public static double calE_N(int n,double d_d,double ramuda,
                              double[] ang_s,double[] fai,int N,int judge){
        //judge == 0 の時cosで返す（実数）
        //judge == 1 の時sinで返す（虚数）
        //        Math.exp(2*Math.PI*n*d_d*Math.cos(ang_s)/ramuda+ang_f);
        double ans = 0 ;
        if(judge == 0){//実数を返す
            for(int i = 0 ;i < N ;i++){
                ans += Math.cos(2*Math.PI*n*d_d*Math.cos(ang_s[i])/ramuda+fai[i]);
            }
            return ans/Math.sqrt(N);
        }else{//虚数を返す
            for(int i = 0 ;i < N ;i++){
                ans += Math.sin(2*Math.PI*n*d_d*Math.cos(ang_s[i])/ramuda+fai[i]);
            }
            return ans/Math.sqrt(N);
        }
    }

    public static double calP(double E_real,double E_image){//Pを計算　実部の2乗+虚部の2乗
        return Fpow(E_real,2)+Fpow(E_image,2);
    }
    public static double[] jikosoukan(double[] P,int nmax , double sampling_interval , int num) throws IOException {
//        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(N + " グルスタ_課題2plus_" + sampling_interval + ".csv")));//旧グルスタ_課題2.csv//課題2で使用
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(" グルスタ_課題3plus_" + num +  ".csv")));//課題3で使用
        double m = 0; double sigma_2 = 0; double[] ro_ = new double[nmax + 1];//
        for( int i = 0 ; i < nmax ; i++){
            m += P[i];
        } m /= (nmax+1) ;//mを作成
        for( int i = 0 ; i < nmax ; i++){sigma_2 += Fpow(P[i] - m,2);} sigma_2 /= nmax ;//σ²を作成
        int j;
        writer.println("k,Δdk,ρ(Δdk)");
        System.out.println(m + "," + sigma_2 );
        for(int k = 0 ; k < nmax ; k++){
            for(int i = 0 ; i < nmax ; i++ ){
                j = ( i + k ) % ( nmax + 1 );
                ro_[k] += ( P[i] - m ) * ( P[j] - m );
            }
            ro_[k] = (ro_[k] / nmax) / sigma_2;
            System.out.println("k : " + k + "," + ro_[k]);//確認のため表示
            writer.println( k + "," + sampling_interval * k + "," + ro_[k]);
        }
        writer.close();
        return ro_;
    }
}
