package util;

/**
 * @Author: Z.Richard
 * @CreateTime: 2022/4/17 11:12
 * @Description:
 **/

public class Compare {

    //比较特征值的欧氏距离 即两点之间距离
    public static double euDist(double[] a, double[] b) {
        double D, sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum = sum + Math.pow((a[i] - b[i]), 2);
        }
        D = Math.pow(sum, 0.5);
        return D;
    }


    /**
     * 用余弦定理求匹配图片与数据库中图片的相似度,余弦值越接近1，也就是两个向量越相似，这就叫"余弦相似性"，余弦值越接近0，也就是两个向量越不相似，也就是这两个字符串越不相似。
     *
     * @param daicha 选择的图片特征值
     * @param kuzhi  图库中的图片特征值
     * @return
     */
    public static double cosDist(double[] daicha, double[] kuzhi) {
        double cosvalue = 1, fenzi = 0, fenmu1 = 0, fenmu2 = 0;
        for (int i = 0; i < kuzhi.length; i++) {
            fenzi += daicha[i] * kuzhi[i];
            fenmu1 += daicha[i] * daicha[i];
            fenmu2 += kuzhi[i] * kuzhi[i];
        }
        fenmu1 = Math.sqrt(fenmu1);
        fenmu2 = Math.sqrt(fenmu2);
        cosvalue = fenzi / (fenmu1 * fenmu2);
        return cosvalue;
    }

    public static int hammingDist(String hash1, String hash2) {
        int dist = 0;
        for (int i = 0; i < hash1.length(); i++) {
            if (hash1.charAt(i) != hash2.charAt(i)) {
                dist++;
            }
        }
        return dist;
    }

    public static int hammingDist(int[] hash1, String hash2) {
        int dist = 0;
        for (int i = 0; i < hash1.length; i++) {
            if (hash1[i] != (hash2.charAt(i) - '0')) {
                dist++;
            }
        }
        return dist;
    }

    public static int hammingDist(int[] hash1, int[] hash2) {
        int dist = 0;
        for (int i = 0; i < hash1.length; i++) {
            if (hash1[i] != hash2[i]) {
                dist++;
            }
        }
        return dist;
    }

}
