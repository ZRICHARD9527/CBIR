package hash;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import static imgUtil.ImgUtil.showImg;

/**
 * @Author: Z.Richard
 * @CreateTime: 2022/5/6 15:58
 * @Description:
 **/

public class PHash_256 {

    //pHash算法
    public static int[] fingerPrint(String imgPath) {


        Mat oMat = Imgcodecs.imread(imgPath, Imgcodecs.IMREAD_COLOR);//读取图像
//        //原始图像输出
//        DHash.showImg(oMat, "原始");
//        //输出原始图像的DCT变化图(必须先转为单通道色彩空间)
//        System.out.println(oMat.type());
//
//        Mat gMat = new Mat();
//        Imgproc.cvtColor(oMat,gMat,Imgproc.COLOR_BGR2GRAY);
//        gMat.convertTo(gMat, CvType.CV_32FC1);
//
//        Core.dct(gMat, gMat);
//        gMat.convertTo(gMat, CvType.CV_8U);
//        DHash.showImg(gMat, "DCT");

        /**
         * 第一步，缩小尺寸
         * 将图片缩小到32x32的尺寸
         */

        Mat resizeMat = new Mat();
        Imgproc.resize(oMat, resizeMat, new Size(32, 32));


        /**
         *  第二步，简化色彩(Color Reduce)
         *  将缩小后的图片，转为256级灰度
         */
        Mat grayMat = new Mat();
        Imgproc.cvtColor(resizeMat, grayMat, Imgproc.COLOR_BGR2GRAY);
        //showImg(grayMat, "GRAY");

        // 第三步，离散余弦变换，DCT系数求取
        Mat dctMat = new Mat();
        //先将灰度图转换为32位浮点型，因为DCT函数需要使用的图像类型为type == CV_32FC1 || type == CV_64FC1
        grayMat.convertTo(grayMat, CvType.CV_32F);
        //调用DCT函数完成离散余弦变换
        Core.dct(grayMat, dctMat);

        //图像输出需要先转换为CvType.CV_8U
        dctMat.convertTo(dctMat, CvType.CV_8U);
        //showImg(dctMat, "DCT");


        /**
         * 第四步，求取DCT系数均值（左上角16*16区块的DCT系数）
         * 经过DCT变换后数值主要集中在左上角，而右下角的像素基本为0，被称为高频区域。变换后数据量会变得很小，这也是DCT的优点所在
         */

        double sum = 0;//总值
        //计算左上角16*16区域均值
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                sum += dctMat.get(i, j)[0];
            }
        }

        // 均值
        double average = sum / 256.0;

        /**
         *  第五步，计算哈希值
         */
        int[] finger = new int[256];
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                finger[i * 16 + j] = dctMat.get(i, j)[0] > average ? 1 : 0;
            }
        }
        return finger;

    }

    public static void main(String[] args) {
        String imgPath = "img\\raw\\image_0008.jpg";
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        int[] f = PHash_256.fingerPrint(imgPath);
        System.out.println(f.length);
        for (int i : f) {
            System.out.print(i);
        }

    }
}
