package hash;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

/**
 * @Author: Z.Richard
 * @CreateTime: 2022/5/6 15:49
 * @Description:
 **/

public class AHash_256 {

    //均值Hash算法
    public static int[] fingerPrint(String imgPath) {


        //设置读入模式为三通道彩色图像（否则在转化为灰度图时会因为通道数报错）
        Mat oMat = Imgcodecs.imread(imgPath, Imgcodecs.IMREAD_COLOR);//读取图像

        /*第一步，缩小尺寸。
         将图片缩小到8x8的尺寸，总共64个像素,去除图片的细节*/
        Mat resizeMat = new Mat();
        Imgproc.resize(oMat, resizeMat, new Size(16, 16));

        //System.out.println(oMat.channels());

 	    /* 第二步，简化色彩(Color Reduce)。
 	       将缩小后的图片，转为256级灰度。*/
        Mat grayMat = new Mat();
        Imgproc.cvtColor(resizeMat, grayMat, Imgproc.COLOR_BGR2GRAY);

        /* 第三步，计算平均值。
       	   计算所有256个像素的灰度平均值。*/
        double average = 0;
        double sum = 0;
        for (int i = 0; i < grayMat.rows(); i++) {
            for (int j = 0; j < grayMat.cols(); j++) {
                sum += grayMat.get(i, j)[0];
            }
        }
        average = sum / 256;

        /* 第四步，比较像素的灰度。
 	       将每个像素的灰度，与平均值进行比较。大于或等于平均值记为1,小于平均值记为0*/

        int[] figure = new int[256];
        for (int i = 0; i < grayMat.rows(); i++) {
            for (int j = 0; j < grayMat.cols(); j++) {
                figure[i * 16 + j] = grayMat.get(i, j)[0] < average ? 0 : 1;
            }
        }
        return figure;
    }


    public static void main(String[] args) throws IOException {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);


        String imgPath = "D:\\Desktop\\CBIR\\caltech-101\\101_ObjectCategories\\accordion\\image_0007.jpg";
        int[] f = fingerPrint(imgPath);
        for (int i : f
        ) {
            System.out.print(i);
        }


    }

}
