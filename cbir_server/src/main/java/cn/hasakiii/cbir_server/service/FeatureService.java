package cn.hasakiii.cbir_server.service;

import cn.hasakiii.cbir_server.entity.Color;
import cn.hasakiii.cbir_server.entity.Paths;
import cn.hasakiii.cbir_server.entity.Shape;
import cn.hasakiii.cbir_server.entity.Texture;
import cn.hasakiii.cbir_server.mapper.ColorMapper;
import cn.hasakiii.cbir_server.mapper.PathsMapper;
import cn.hasakiii.cbir_server.mapper.ShapeMapper;
import cn.hasakiii.cbir_server.mapper.TextureMapper;
import cn.hasakiii.cbir_server.util.Iteration_Threshold;
import cn.hasakiii.cbir_server.util.Sharpen;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: Z.Richard
 * @CreateTime: 2022/5/14 11:22
 * @Description:
 **/
@Service
public class FeatureService {

    @Resource
    ColorMapper colorMapper;
    @Resource
    PathsMapper pathsMapper;
    @Resource
    ShapeMapper shapeMapper;
    @Resource
    TextureMapper textureMapper;

    /**
     * 读取所有路径计算路径下图像的特征值
     */
    public void setAllFeature() {

        resetFeature();
        //1.从数据库中读取所有的数据
        List<Paths> paths = pathsMapper.selectList(null);
        for (Paths p : paths) {
            int id = p.getId();
            String str = p.getPath();
            System.out.println(id + " : " + str);
            colorSave(id, getColor(str));//存颜色特征
            textureSave(id, getTexture(str));//纹理特征
            try {
                shapeSave(id, getShape(str));//存形状特征
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public void resetFeature() {
        colorMapper.delete();
        textureMapper.delete();
        shapeMapper.delete();
    }

    /***************************************************************颜色特征
     *  * 1.循环读取所有的图像路径
     *  * 2.每次读入即调用算法计算出图像的各项特征值
     *  * 3.将图像特征值存入数据库中
     */




    /**
     * 将颜色特征值存入数据库
     *
     * @param id     图像的id
     * @param colorF 特征值数组
     */
    public void colorSave(int id, double[] colorF) {
        Color c = new Color(colorF);
        c.setId(id);
        colorMapper.insert(c);
    }

    private static double mean(double[] data) {                //一阶矩 均值
        double sum = 0;
        for (double datum : data) {
            sum += datum;
        }
        return sum / data.length;
    }

    private static double std(double[] data, double mean) {    //二阶矩 标准方差
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
    public double[] getColor(String path) {

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


    /***********************************************************************************形状特征
     *
     */

    //将形状特征存储到数据库中
    public void shapeSave(int id, double[] shapeF) {
        Shape s = new Shape(shapeF);
        s.setId(id);
        shapeMapper.insert(s);
    }


    /**
     * 获取图像形状特征值 形状不变矩
     **/
    public double[] getShape(String src_path) throws IOException {
        BufferedImage bufImg = null;
        File file = new File(src_path);
        if (!file.exists()) {
            System.out.println("file doesn't find.");
        }
        bufImg = ImageIO.read(file);
        int Height = bufImg.getHeight();
        int Width = bufImg.getWidth();
        //图像处理
        //图像灰度化 使用自带的图像缓冲流
        BufferedImage bufImg_gray = new BufferedImage(Width, Height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < Width; i++) {        /**注意行列顺序**/
            for (int j = 0; j < Height; j++) {
                int ARGB = bufImg.getRGB(i, j);
                int A = (ARGB >> 24) & 0Xff;
                int R = 30 * (((ARGB >> 16) & 0xff));
                /**灰度化公式y=0.30*R+0.59*G+0.11*B**/
                int G = 59 * (((ARGB >> 8) & 0xff));
                int B = 11 * ((ARGB & 0xff));
                int gray = (R + G + B) / 100;
                int g = (A << 24) | (gray << 16) | (gray << 8) | gray;
                bufImg_gray.setRGB(i, j, g);
            }
        }
        bufImg = bufImg_gray;

        //中值滤波，使图像平滑
        BufferedImage filter = new BufferedImage(Width, Height, BufferedImage.TYPE_INT_RGB);
        int[] pixel = new int[9];
        int t;
        int alpha = (bufImg.getRGB(0, 0) >> 24) & 0xff;
        for (int i = 1; i < Width - 1; i++)
            for (int j = 1; j < Height - 1; j++) {
                t = 0;
                //找中心及周围的八个点
                int p0 = bufImg.getRGB(i - 1, j - 1) & 0xff;
                pixel[t++] = p0;
                int p1 = bufImg.getRGB(i - 1, j) & 0xff;
                pixel[t++] = p1;
                int p2 = bufImg.getRGB(i - 1, j + 1) & 0xff;
                pixel[t++] = p2;
                int p3 = bufImg.getRGB(i, j - 1) & 0xff;
                pixel[t++] = p3;
                int p4 = bufImg.getRGB(i, j) & 0xff;
                pixel[t++] = p4;
                int p5 = bufImg.getRGB(i, j + 1) & 0xff;
                pixel[t++] = p5;
                int p6 = bufImg.getRGB(i + 1, j - 1) & 0xff;
                pixel[t++] = p6;
                int p7 = bufImg.getRGB(i + 1, j) & 0xff;
                pixel[t++] = p7;
                int p8 = bufImg.getRGB(i + 1, j + 1) & 0xff;
                pixel[t++] = p8;
                Arrays.sort(pixel);
                int mid = pixel[pixel.length / 2];//排序后取中间值
                int rgb = (alpha << 24) | (mid << 16) | (mid << 8) | mid;
                filter.setRGB(i, j, rgb);
            }
        bufImg = filter;
        //图像锐化_Sobel
        Sharpen sharpen = new Sharpen();
        BufferedImage bufImg_sharpen = sharpen.get_Sharpen_Sobel(bufImg, Width, Height);
        //二值化：迭代阈值法(iteration_threshold)
        Iteration_Threshold thres = new Iteration_Threshold();
        bufImg = thres.get_Iteration_Threshold(bufImg_sharpen, Width, Height);


        //计算矩心  计算中心矩  中心矩归一化  计算不变矩和离心率
        double[] e = get_Momet_Invariants(bufImg);//存储形状特征值
        return e;
    }


    //计算矩心
    public double[] get_Centroid(BufferedImage bufImg) {
        double[] x = new double[2];
        double m10 = 0.0;
        double m01 = 0.0;
        double m00 = 0.0;
        for (int i = 0; i < bufImg.getWidth(); i++)
            for (int j = 0; j < bufImg.getHeight(); j++) {
                int pixel = (bufImg.getRGB(i, j) & 0xff) & 1;    /**二值图0和255转化为0和1**/
                if (pixel == 1)                            /**目标物体**/ {
                    m10 += i;
                    m01 += j;
                    m00 += 1;
                }
            }
        x[0] = m10 / m00;
        x[1] = m01 / m00;
        return x;
    }

    //计算中心矩
    public double[][] get_Central_Moment(BufferedImage bufImg) {
        double[] x = get_Centroid(bufImg);//获得图像的矩心
        double[][] M = new double[4][4];
        for (int p = 0; p < 4; p++)
            for (int k = 0; k < 4; k++) {
                M[p][k] = 0.0;
                for (int i = 0; i < bufImg.getWidth(); i++)
                    for (int j = 0; j < bufImg.getHeight(); j++) {
                        int pixel = (bufImg.getRGB(i, j) & 0xff) & 1;    /**二值图0和255转化为0和1**/
                        if (pixel == 1)
                            M[p][k] += Math.pow(i - x[0], p * 1.0) * Math.pow(j - x[1], k * 1.0);
                    }
            }
        return M;
    }

    //中心矩归一化
    public double[][] get_Normalization(double[][] M) {
        double[][] u = new double[4][4];
        for (int p = 0; p < 4; p++) {
            for (int k = 0; k < 4; k++) {
                u[p][k] = M[p][k] / (Math.pow(M[0][0], (((double) (p + k)) / 2.0 + 1)));
            }
        }
        return u;
    }

    //计算不变矩和离心率
    public double[] get_Momet_Invariants(BufferedImage bufImg) {

        double[][] u = get_Normalization(get_Central_Moment(bufImg));//获取归一化后的中心矩
        double[] e = new double[8];//存储形状特征值
        //离心率
        e[0] = (Math.pow(u[2][0] - u[0][2], 2.0) + 4 * Math.pow(u[1][1], 2.0)) / Math.pow(u[2][0] + u[0][2], 2.0);

        //不变矩
        e[1] = u[2][0] + u[0][2];
        e[2] = Math.pow(u[2][0] - u[0][2], 2.0) + 4 * Math.pow(u[1][1], 2.0);
        e[3] = Math.pow(u[3][0] - 3 * u[1][2], 2.0) + Math.pow(u[0][3] - 3 * u[2][1], 2.0);
        e[4] = Math.pow(u[3][0] + u[1][2], 2.0) + Math.pow(u[0][3] + u[2][1], 2.0);
        e[5] = (u[3][0] - 3 * u[1][2]) * (u[3][0] + u[1][2]) * (Math.pow(u[3][0] + u[1][2], 2.0)
                - 3 * Math.pow(u[2][1] + u[0][3], 2.0)) + (u[0][3] - 3 * u[2][1]) * (u[0][3] + u[2][1])
                * (Math.pow(u[0][3] + u[2][1], 2.0) - 3 * Math.pow(u[1][2] + u[3][0], 2.0));
        e[6] = (u[2][0] - u[0][2]) * (Math.pow(u[3][0] + u[1][2], 2.0) - Math.pow(u[2][1] + u[0][3], 2.0))
                + 4 * u[1][1] * (u[3][0] + u[1][2]) * (u[0][3] + u[2][1]);
        e[7] = (3 * u[2][1] - u[0][3]) * (u[3][0] + u[1][2]) * (Math.pow(u[3][0] + u[1][2], 2.0) -
                3 * Math.pow(u[0][3] + u[2][1], 2.0)) + (3 * u[1][2] - u[3][0]) * (u[2][1] + u[0][3])
                * (3 * Math.pow(u[3][0] + u[1][2], 2.0) - Math.pow(u[0][3] + u[2][1], 2.0));
        return e;
    }

    /***********************************************************************************纹理特征
     *
     */


    public void textureSave(int id, double[] texureF) {
        Texture t = new Texture(texureF);
        t.setId(id);
        textureMapper.insert(t);
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
    public static double[] getTexture(String path) {
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
                    //纹理一致性（ASM能量，每个矩阵元素的平方和）
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
