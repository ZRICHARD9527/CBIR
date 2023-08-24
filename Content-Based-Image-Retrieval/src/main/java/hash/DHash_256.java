package hash;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

import static imgUtil.ImgUtil.imgInfo;
import static imgUtil.ImgUtil.showImg;

/**
 * @Author: Z.Richard
 * @CreateTime: 2022/5/6 15:49
 * @Description:
 **/

public class DHash_256 {

    /**
     * 图片指纹
     *
     * @param imagePath
     * @return
     * @throws IOException
     */
    public static int[] fingerPrint(String imagePath) {


        Mat oMat = Imgcodecs.imread(imagePath, Imgcodecs.IMREAD_COLOR);//读取图像
        //显示原始图像
//        showImg(oMat, 0,"origin");
//        imgInfo(oMat, "");


        //缩小图像
        Mat resizeMat = new Mat();
        Imgproc.resize(oMat, resizeMat, new Size(17, 16));
//        showImg(resizeMat, 0, "resize");
//        imgInfo(resizeMat, "");

//        Imgproc.resize(oMat, resizeMat, new Size(17, 16));
//        showImg(resizeMat, 0,"resize");
//        imgInfo(resizeMat, "");


        //转换为灰度图
        Mat grayMat = new Mat();
        Imgproc.cvtColor(resizeMat, grayMat, Imgproc.COLOR_BGR2GRAY);
//        showImg(grayMat, 0, "gray");
//        imgInfo(grayMat, "");


        /*
         * 计算差异值：dHash算法工作在相邻像素之间，这样每行9个像素之间产生了8个不同的差异，一共8行，则产生了64个差异值.
         * 获取指纹.如果左边的像素比右边的更亮，则记录为1，否则为0.
         */

        int eleNum = (grayMat.cols() - 1) * grayMat.rows();
        int[] figure = new int[eleNum];
        for (int i = 0; i < grayMat.rows(); i++) {
            for (int j = 0; j < grayMat.rows(); j++) {
                int b = grayMat.get(i, j)[0] > grayMat.get(i, j + 1)[0] ? 1 : 0;
                figure[i * (grayMat.cols() - 1) + j] = b;
            }
        }

        return figure;
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String imgPath = "img/raw/image_0008.jpg";
        System.out.println();
        int[] hashcode = DHash_256.fingerPrint(imgPath);
        System.out.println(hashcode.length);
        for (int i : hashcode
        ) {
            System.out.print(i);
        }

    }
}
