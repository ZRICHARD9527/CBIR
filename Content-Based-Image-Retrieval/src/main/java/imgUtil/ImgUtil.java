package imgUtil;

import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @Author: Z.Richard
 * @CreateTime: 2022/5/5 0:23
 * @Description:
 **/

public class ImgUtil {

    private static int DELAY = 500;

    //显示图像
    public static void showImg(Mat imread, String dec) {
        HighGui.namedWindow(dec, HighGui.WINDOW_AUTOSIZE);
        HighGui.imshow(dec, imread);
        HighGui.waitKey(DELAY);
    }

    public static void showImg(Mat imread, int delay, String dec) {
        HighGui.namedWindow(dec, HighGui.WINDOW_AUTOSIZE);
        HighGui.imshow(dec, imread);
        HighGui.waitKey(delay);
    }


    //输出图像信息
    public static void imgInfo(Mat mat, String dec) {
        System.out.println(dec + " : ");
        System.out.println("类型   ：" + mat.type());
        System.out.println("通道数 : " + mat.channels());
        System.out.println("大小   ：" + mat.size());
        System.out.println("列数   ：" + mat.cols());
        System.out.println("行数   ：" + mat.rows());
    }

    /**
     * 读取文件中所有路径
     *
     * @param path_file
     * @return
     */
    public static ArrayList<String> getPath(String path_file) {
        ArrayList<String> paths = new ArrayList<String>();
        try {
            FileReader fr = new FileReader(path_file);
            BufferedReader bf = new BufferedReader(fr);
            String str;            //按行读取字符串
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


    static void fun(String str) {
        String arr = str.replace('|', ',');
        System.out.println(arr);

    }

    public static void main(String[] args) {
        String str = "0.2820700759255978 | 0.5889176471186619 | 0.0978286706272295 | 0.0038296420003518 | 0.0050586779658381 | -0.0000221879699170 | -0.0002670926208648 | -0.0000018578139642";
        fun(str);
        String arr1 = "xxxxxx.airplane/xxxxxxx";
        String arr2="airplane";
        boolean f = arr1.contains(arr2);
    }

}
