package cn.hasakiii.cbir_server.service;

import cn.hasakiii.cbir_server.entity.Ahash;
import cn.hasakiii.cbir_server.entity.Dhash;
import cn.hasakiii.cbir_server.entity.Phash;
import cn.hasakiii.cbir_server.entity.Paths;
import cn.hasakiii.cbir_server.mapper.AHashMapper;
import cn.hasakiii.cbir_server.mapper.DHashMapper;
import cn.hasakiii.cbir_server.mapper.PHashMapper;
import cn.hasakiii.cbir_server.mapper.PathsMapper;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: Z.Richard
 * @CreateTime: 2022/5/14 21:16
 * @Description:
 **/

@Service
public class HashService {

    @Resource
    AHashMapper aHashMapper;
    @Resource
    DHashMapper dhashMapper;
    @Resource
    PHashMapper pHashMapper;
    @Resource
    PathsMapper pathsMapper;


    /**
     * 读取所有路径计算路径下图像的特征值
     */
    public void setAllHash() {
        //1.从数据库中读取所有的数据
        resetHash();
        List<Paths> paths = pathsMapper.selectList(null);
        for (Paths p : paths) {
            int id = p.getId();
            String str = p.getPath();
            System.out.println(id + " : " + str);
            asave(id, getAhash(str));
            dsave(id, getDhash(str));
            psave(id, getPhash(str));
        }

    }

    //重置，清空hash表项
    public void resetHash() {
        aHashMapper.delete();
        dhashMapper.delete();
        pHashMapper.delete();
    }

    /****************************************获取均值哈希
     *
     */

    public void asave(int id, int[] h) {
        Ahash aHash = new Ahash(h);
        aHash.setId(id);
        aHashMapper.insert(aHash);
    }


    //均值Hash算法
    public static int[] getAhash(String imgPath) {
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


    /***********************************************************************差异哈希
     */

    public void dsave(int id, int[] h) {
        Dhash dHash = new Dhash(h);
        dHash.setId(id);
        dhashMapper.insert(dHash);
    }

    public static int[] getDhash(String imagePath) {

        Mat oMat = Imgcodecs.imread(imagePath, Imgcodecs.IMREAD_COLOR);//读取图像

        //缩小图像
        Mat resizeMat = new Mat();
        Imgproc.resize(oMat, resizeMat, new Size(17, 16));

        //转换为灰度图
        Mat grayMat = new Mat();
        Imgproc.cvtColor(resizeMat, grayMat, Imgproc.COLOR_BGR2GRAY);

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


    /******************************************************************感知哈希
     *
     */

    public void psave(int id, int[] h) {
        Phash pHash = new Phash(h);
        pHash.setId(id);
        pHashMapper.insert(pHash);
    }

    //pHash算法
    public static int[] getPhash(String imgPath) {


        Mat oMat = Imgcodecs.imread(imgPath, Imgcodecs.IMREAD_COLOR);//读取图像

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

        // 第三步，离散余弦变换，DCT系数求取
        Mat dctMat = new Mat();
        //先将灰度图转换为32位浮点型，因为DCT函数需要使用的图像类型为type == CV_32FC1 || type == CV_64FC1
        grayMat.convertTo(grayMat, CvType.CV_32F);
        //调用DCT函数完成离散余弦变换
        Core.dct(grayMat, dctMat);

        //图像输出需要先转换为CvType.CV_8U
        dctMat.convertTo(dctMat, CvType.CV_8U);


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


}
