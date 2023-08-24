package feature;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class GetTexture {
    //读取图片路径
    public ArrayList<String> getPath(String path_file) {
        ArrayList<String> paths = new ArrayList<String>();
        try {
            FileReader fr = new FileReader(path_file);
            BufferedReader bf = new BufferedReader(fr);
            String str;            // 按行读取字符串
            while ((str = bf.readLine()) != null) {
                paths.add(str);
            }
            bf.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return paths;
    }

    /**
     * 由图像路径获取图像的纹理特征
     * <p>
     * 1.将图像灰度化：图像灰度化即是将一幅彩色图像转换为灰度化图像的过程。彩色图像通常包括R、G、B三个分量，分别显示出红绿蓝等各种颜色，灰度化就是使彩色图像的R、G、B三个分量相等的过程。
     * 灰度图像中每个像素仅具有一种样本颜色，其灰度是位于黑色与白色之间的多级色彩深度
     * 2.使用4个灰度共生矩阵，分别记录4个不同方向的纹理信息（灰度矩阵用来记录相邻像素对值的分布情况）
     * 3.计算方差与期望
     *
     * @param path 图像路径
     * @return
     */
    public static double[] texture(String path) {
        Mat mat = Imgcodecs.imread(path);

        /**
         * CV_Type解释 可分为三个部分，各部分解释如下
         * bit_depth–>bit数，代表图片中每个像素点所占空间的大小，如CV_8UC3，则代表每个像素占8个bit，这里可以取到8/16/32/64。
         * S|U|F
         * S : signed int ,有符号整形型。
         * U : unsigned int ，无符号整型。
         * F : float，单精度浮点型。
         * C<number_of_channels>–> 图片的通道数
         * 1：单通道图像，即为灰度图像。
         * 2：双通道图像。
         * 3：RGB彩色图像，3通道图像。
         * 4：带Alpha通道的彩色图像，4通道图像。
         */
        Mat gray = new Mat(mat.rows(), mat.cols(), CvType.CV_8UC3);//用来存储灰度图象
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);//RGB->GRAY
        int height = gray.height();
        int width = gray.width();
        int[][] graylow = new int[height][width];//降低等级后的灰度图像,存储像素值

        /**
         * 由于纹理是由灰度分布在空间位置上反复出现而形成的，因而在图像空间中相隔某距离的两像素之间会存在一定的灰度关系，即图像中灰度的空间相关特性。
         * 灰度共生矩阵就是一种通过研究灰度的空间相关特性来描述纹理的常用方法。
         * 灰度共生矩阵是涉及像素距离和角度的矩阵函数，它通过计算图像中一定距离和一定方向的两点灰度之间的相关性，来反映图像在方向、间隔、变化幅度及快慢上的综合信息。
         * 灰度直方图是对图像上单个像素具有某个灰度进行统计的结果，而灰度共生矩阵是对图像上保持某距离的两像素分别具有某灰度的状况进行统计得到的。
         */
        double[][][] glcm = new double[4][8][8];//四个灰度共生矩阵(分别记录4个不同角度)
        int gi, gj;//像素对
        int sum0 = 0, sum45 = 0, sum90 = 0, sum135 = 0;//不同角度灰度共生矩阵中像素点个数

        //纹理一致性
        double[] asm = new double[4];
        /**纹理对比度
         * 度量矩阵的值是如何分布和图像中局部变化的多少，反应了图像的清晰度和纹理的沟纹深浅。
         * 纹理的沟纹越深，反差越大，效果越清晰；反之，对比值小，则沟纹浅，效果模糊。
         */
        double[] contrast = new double[4];
        /**纹理熵
         * 描述图像具有的信息量的度量，表明图像的复杂程度，当复杂程度高时，熵值较大，反之则较小
         */
        double[] entropy = new double[4];
        /**纹理相关性
         * 用来度量图像的灰度级在行或列方向上的相似程度，因此值得大小反应了局部灰度相关性，
         * 值越大，相关性也越大。
         */
        double[] correlation = new double[4];

        //相关性的μ
        double[] ux = new double[4];
        double[] uy = new double[4];
        //相关性的σ
        double[] ax = new double[4];
        double[] ay = new double[4];

        //降低灰度等级，分成8个区间
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                graylow[i][j] = (int) (gray.get(i, j)[0] / 32);
            }
        }
        //按四个方向遍历图片，记录灰度值对出现的次数
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                //0度
                if (i + 2 >= 0 && i + 2 < height) {
                    gi = graylow[i][j];
                    gj = graylow[i + 2][j];
                    glcm[0][gi][gj] += 1;
                    sum0++;
                }
                //45度
                if (i + 2 >= 0 && i + 2 < height && j + 2 >= 0 && j + 2 < width) {
                    gi = graylow[i][j];
                    gj = graylow[i + 2][j + 2];
                    glcm[1][gi][gj] += 1;
                    sum45++;
                }
                //90度
                if (j + 2 >= 0 && j + 2 < width) {
                    gi = graylow[i][j];
                    gj = graylow[i][j + 2];
                    glcm[2][gi][gj] += 1;
                    sum90++;
                }
                //135度
                if (i - 2 >= 0 && i - 2 < height && j + 2 >= 0 && j + 2 < width) {
                    gi = graylow[i][j];
                    gj = graylow[i - 2][j + 2];
                    glcm[3][gi][gj] += 1;
                    sum135++;
                }
            }
        }

        //求灰度共生矩阵，每个单元为像素对出现的概率
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                glcm[0][i][j] = glcm[0][i][j] / sum0;
                glcm[1][i][j] = glcm[1][i][j] / sum45;
                glcm[2][i][j] = glcm[2][i][j] / sum90;
                glcm[3][i][j] = glcm[3][i][j] / sum135;
            }
        }

        //计算纹理特征
        for (int index = 0; index < 4; index++) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    //纹理一致性
                    asm[index] += Math.pow(glcm[index][i][j], 2);
                    //纹理对比度
                    contrast[index] += Math.pow(i - j, 2) * glcm[index][i][j];
                    //纹理熵
                    if (glcm[index][i][j] != 0) {
                        entropy[index] -= glcm[index][i][j] * Math.log(glcm[index][i][j]);
                    }
                    //相关性的u
                    ux[index] += i * glcm[index][i][j];
                    uy[index] += j * glcm[index][i][j];
                }
            }
        }

        //相关性的σ
        for (int index = 0; index < 4; index++) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    ax[index] += Math.pow(i - ux[index], 2) * glcm[index][i][j];
                    ay[index] += Math.pow(j - uy[index], 2) * glcm[index][i][j];
                    correlation[index] += i * j * glcm[index][i][j];
                }
            }
            //纹理相关性
            if (ax[index] != 0 && ay[index] != 0) {
                correlation[index] = (correlation[index] - ux[index] * uy[index]) / ax[index] / ay[index];
            } else {
                correlation[index] = 8;
            }
        }
        double[] y = new double[8];
        //期望
        y[0] = (asm[0] + asm[1] + asm[2] + asm[3]) / 4;
        y[1] = (contrast[0] + contrast[1] + contrast[2] + contrast[3]) / 4;
        y[2] = (correlation[0] + correlation[1] + correlation[2] + correlation[3]) / 4;
        y[3] = (entropy[0] + entropy[1] + entropy[2] + entropy[3]) / 4;
        //标准差
        y[4] = Math.sqrt(Math.pow(asm[0] - y[0], 2) + Math.pow(asm[1] - y[0], 2) + Math.pow(asm[2] - y[0], 2) + Math.pow(asm[3] - y[0], 2));
        y[5] = Math.sqrt(Math.pow(contrast[0] - y[0], 2) + Math.pow(contrast[1] - y[0], 2) + Math.pow(contrast[2] - y[0], 2) + Math.pow(contrast[3] - y[0], 2));
        y[6] = Math.sqrt(Math.pow(correlation[0] - y[0], 2) + Math.pow(correlation[1] - y[0], 2) + Math.pow(correlation[2] - y[0], 2) + Math.pow(correlation[3] - y[0], 2));
        y[7] = Math.sqrt(Math.pow(entropy[0] - y[0], 2) + Math.pow(entropy[1] - y[0], 2) + Math.pow(entropy[2] - y[0], 2) + Math.pow(entropy[3] - y[0], 2));

        return y;
    }


}
