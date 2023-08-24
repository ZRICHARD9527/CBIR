package feature;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//读取txt获取图片路径，计算每个图片的颜色特征保存在color.txt
public class GetColor {

    private static double mean(double[] data) {                //一阶矩 均值
        double sum = 0;
        for (double datum : data) {
            sum += datum;
        }
        return sum / data.length;
    }

    private static double std(double[] data, double mean) {    //二阶矩 方差
        double sum = 0;
        for (double datum : data) {
            sum += Math.pow((datum - mean), 2);
        }
        return Math.pow((sum / data.length), 0.5);
    }

    private static double skew(double[] data, double mean) {   //三阶矩 偏斜度
        double sum = 0;
        for (double datum : data) {
            sum += Math.pow((datum - mean), 3);
        }
        return Math.cbrt(sum / data.length);
    }

    //先提高图像的对比度再计算各通道色彩值
    //计算9个特征值的double数组
    public static double[] color_HSV_msv(String path) {
        Mat mat = Imgcodecs.imread(path);//通过路径将图像文件传入mat矩阵中存储

        /**
         * 将图像从BGR色彩空间转为YCrCb色彩空间
         * Y指亮度 Cr Cb分别是蓝色和红色的浓度偏移量成分 ，只有Y成分的图基本等同于彩色图像的灰度图
         * 将BGR三色图转换为YCrCb色彩空间后第一通道（Y成分）就是亮度值
         */
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2YCrCb);

        List<Mat> list1 = new ArrayList<Mat>();//将图像的各通道图像存储至list1中
        Core.split(mat, list1);//使用split函数分割图像，获取各通道成分
        /**
         * 直方图是图像中像素强度分布的图形表达方式，它统计了每一个强度值所具有的像素个数.
         * 所谓强度即像素单通道图像像素的强度（值的大小）
         * 而直方图均衡化就是拉伸像素的强度范围来调整图像的对比度（使图像的物体和形状更加突出），使分布曲线平坦化
         */
        Imgproc.equalizeHist(list1.get(0), list1.get(0));//直方图均衡化，提升对比度（依然存进原图像中）
        /**
         * 归一化，（x-min）/(max-min) 使数据位于0-1之间
         * 首先归一化是为了后面数据处理的方便，其次是保证程序运行时收敛加快
         * 消除量纲的影响、防止数值差距过大抹掉边缘值
         */
        Core.normalize(list1.get(0), list1.get(0), 0, 255, Core.NORM_MINMAX);

        Core.merge(list1, mat);//将list1中的单通道图像合并为多通道图像存进mat矩阵中
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_YCrCb2BGR);//将图像转换回RGB色彩空间

        int r = mat.rows();
        int c = mat.cols();
        int pixelNum = r * c;//像素数

        //创建数组，分别用来存储特征值以及像素各通道值
        double[] y = new double[9];//存储特征值（每个通道包括一二三阶矩特征值）
        double[] B = new double[pixelNum];//存储像素B通道值
        double[] G = new double[pixelNum];//存储像素G通道值
        double[] R = new double[pixelNum];//存储像素R通道值

        //提取像素各通道值
        for (int j = 0; j < r; j++) {
            for (int k = 0; k < c; k++) {
                double[] data = mat.get(j, k);//获取第j行k列的像素值（包括三个通道的数据依次为BGR，使用数组存储）
                B[j * c + k] = data[0];//每行有c个元素，第j行k列有 j*c+k 个元素 （从0开始）
                G[j * c + k] = data[1];
                R[j * c + k] = data[2];
            }
        }

        //分别计算三通道的特征值
        y[0] = mean(B);
        y[1] = std(B, y[0]);
        y[2] = skew(B, y[0]);
        y[3] = mean(G);
        y[4] = std(G, y[3]);
        y[5] = skew(G, y[3]);
        y[6] = mean(R);
        y[7] = std(R, y[6]);
        y[8] = skew(R, y[6]);
        //特征值归一化处理
        double[] temp = new double[9];
        System.arraycopy(y, 0, temp, 0, 9);//将y的值赋给temp
        Arrays.sort(temp);//从小到大排序
        double mm = temp[8] - temp[0];//最大相差
        double min = temp[0];//最小值
        for (int i = 0; i < 9; i++) {
            y[i] = (y[i] - min) / mm;
        }
        return y;
    }


}
