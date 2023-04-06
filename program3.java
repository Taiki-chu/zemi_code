package zemi_code;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

public class program3 {
    static int average = 0;
    static double distans_sampling = 0.1; //Δds サンプリング間隔
    static double[] distans_soukan = {10,600}; //dc 相関距離 本来は{10,50}
    static double sigma = 6; //σs 標準偏差
    static int nmax = (int)(1000 / distans_sampling) ;//最大距離を1000mとして、カウントするnのmaxを設定
    static double[] ro_P = new double[distans_soukan.length];

    public static void main(String[] args) throws IOException {
        while(true) {
            for (int i = 0; i < ro_P.length; i++) {
                ro_P[i] = Math.pow(0.5, distans_sampling / distans_soukan[i]);
            }
            double[] z = new double[nmax + 1];
            double a;
            double[] a_ = new double[nmax + 1];
            double dn;
            double N_SUM = 0;
            double N_average[] = new double[2];
            double s_N[] = new double[2];
            double z_SUM = 0;
            double z_average[] = new double[2];
            double s_z[] = new double[2];
            for (int ro_count = 0; ro_count < ro_P.length; ro_count++) {
                PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(" グルスタ_課題3_" + distans_soukan[ro_count] + ".csv")));
                writer.println("i,dn,z[i],,行番号,番号n,受信電力値,番号n/N");
                for (int i = 0; i < nmax; i++) {
                    if (i == 0) {
                        z[0] = N_random_number(average, sigma)[0];
                        System.out.println("相関距離dc : " + distans_soukan[ro_count]);
                        z_SUM += z[i];
                        N_SUM += z[i];
                        continue;
                    }
                    a = N_random_number(average, sigma)[0];
                    a_[i] = a;
                    z[i] = ro_P[ro_count] * z[i - 1] + Math.sqrt(1 - Math.pow(ro_P[ro_count], 2)) * a;
                    dn = ((double) Math.round(10 * 0.8 * i)) / 10;
                    System.out.println("i : " + i + " , " + "Δdn : " + dn + " , z[" + i + "] : " + z[i]
                            + " , n/N : " + (double) (i + 1) / nmax);//+ " , z[" + i + "] : " +z[i]
                    writer.println(i + "," + dn + "," + z[i] + ",," + i + "," + (i + 1) + "," + z[i] + "," + (double) (i + 1) / nmax);
                    z_SUM += z[i];//z[i]の合計を計算
                    N_SUM += a;//a_[i]の合計を計算
                }
                z_average[ro_count] = z_SUM / nmax;
                N_average[ro_count] = N_SUM / nmax;
                for (double Z : z) {
                    s_z[ro_count] += Math.pow(Z - z_average[ro_count], 2);
                }
                for (double N : a_) {
                    s_N[ro_count] += Math.pow(N - N_average[ro_count], 2);
                }

                s_z[ro_count] = Math.sqrt(s_z[ro_count] / z.length);
                s_N[ro_count] = Math.sqrt(s_N[ro_count] / a_.length);


                z_SUM = 0;
                N_SUM = 0;

                writer.close();
                program2.jikosoukan(z, nmax, distans_sampling, (int) distans_soukan[ro_count]);//jikosoukan
                System.out.println();
            }
            System.out.println("平均： " + N_average[0] + "\n標準偏差： " + s_N[0]);
            System.out.println("平均： " + z_average[0] + "\n標準偏差： " + s_z[0]);
            System.out.println();
            System.out.println("平均： " + N_average[1] + "\n標準偏差： " + s_N[1]);
            System.out.println("平均： " + z_average[1] + "\n標準偏差： " + s_z[1]);

            if(( -0.5 <= z_average[1] && z_average[1] <= 0.5 && 5.5 <= s_z[1] && s_z[1] <= 6.5 )){//この誤差内の場合、ループを止める//dc=50の時
                break;
            }
        }
    }
    //生成した正規乱数2つを平均値のdBと標準偏差σから求める
    public static double[] N_random_number(double m_dB , double sigma){
        Random random = new Random();
        double[] x_y = new double[2];
        double[] z   = new double[2];
        for(int i = 0; i < 2; i++ ){//生成した一様乱数x,yを生成
            x_y[i] = random.nextDouble();
        }
        z[0] = sigma * Math.sqrt( -2 * Math.log(x_y[0]) ) * Math.cos( 2 * Math.PI*x_y[1] ) + m_dB ;//z1を生成
        z[1] = sigma * Math.sqrt( -2 * Math.log(x_y[0]) ) * Math.sin( 2 * Math.PI*x_y[1] ) + m_dB ;//z1を生成
        return z;
    }
    //平均と標準偏差を確認する
    public static void check(){
        ArrayList<String> array = new ArrayList<String>();
        int count = 500;//何ループするか//1ループで2つ生成される

        double sum = 0;
        double[] two = new double[2];
        for(int i = 0; i < count ; i++ ){
            two = N_random_number(0,6);
            array.add("" + two[0] );
            array.add("" + two[1] );
        }
        double[] N_number = new double[array.size()];
        for(int i = 0 ; i < N_number.length ; i++ ){
            N_number[i] = Double.parseDouble (array.get(i));
            sum += N_number[i];
        }
        double average_check = sum / N_number.length;
        double s ; // s 標準偏差
        int n = 0;
        for(sum = 0; n < N_number.length ; n++ ){
            System.out.println("生成した正規乱数 " + n + " : " + N_number[n]);
            sum += program2.Fpow(N_number[n] - average_check,2);//glst_2に作成した累乗関数を使う
        }
        s = Math.sqrt(sum / n);
        System.out.println("生成した正規乱数の平均は : " + average_check);
        System.out.println("生成した正規乱数の標準偏差は : " + s);
    }
}