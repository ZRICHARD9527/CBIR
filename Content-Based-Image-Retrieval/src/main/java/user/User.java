package user;

import feature.GetColor;
import feature.GetShape;
import feature.GetTexture;
import hash.AHash_256;
import hash.DHash_256;
import hash.PHash_256;
import httpLink.*;
import org.opencv.core.Core;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class User {
    private String picPath = null;//保存选择的待检索文件路径


    //存放hash值
    private String ahash = null;
    private String phash = null;
    private String dhash = null;


    JFrame frame = new JFrame("基于内容的图像检索平台");
    private JLabel imgLabel;//选择的图片显示标签
    private JTextField text;//图片路径
    JLabel[] img12 = new JLabel[12];//显示最匹配的12张图像
    JLabel[] imgPath = new JLabel[12];//显示最匹配的12张图像的位置
    JLabel[] type = new JLabel[4];//对比展示时显示在前面的类型提示
    JLabel[] img16 = new JLabel[16];

    double[] color = new double[9];//检索图像的颜色特征
    double[] texture = new double[9];//检索图像的纹理特征
    double[] shape = new double[8];//检索图像的形状特征


    /**
     * 窗体初始化
     */
    private void init() {

        frame.setBounds(100, 100, 1700, 1000);//设置一个左上角顶点在（100,200）长1700 宽1000的窗体
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//同步开关，关闭窗体的同时终止程序的运行
        frame.getContentPane().setLayout(null);//JFrame不能用于直接添加组件，否则会抛出异常 使用getContentPane方法获取内容面板，再对其加入组件


        //图片
        JLabel label = new JLabel("选择文件:");
        label.setFont(new Font("宋体", Font.PLAIN, 15));
        label.setBounds(15, 50, 70, 25);
        frame.add(label);

        //文件路径显示
        text = new JTextField();
        text.setBounds(90, 50, 170, 25);
        frame.add(text);
        text.setColumns(10);

        //点击选择要检索的文件
        JButton brow = new JButton("浏览");
        brow.setFont(new Font("宋体", Font.PLAIN, 15));
        brow.setBounds(260, 50, 70, 25);
        frame.add(brow);
        //弹窗按钮监听
        brow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    choosePic();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        //显示选择的图像
        JLabel label_1 = new JLabel("当前图像:");
        label_1.setFont(new Font("宋体", Font.PLAIN, 15));
        label_1.setBounds(15, 85, 80, 25);
        frame.add(label_1);
        //图片显示区
        imgLabel = new JLabel("");
        imgLabel.setBounds(30, 135, 300, 200);
        frame.add(imgLabel);

        //初始化12张显示的图像标签及其路径
        for (int i = 0; i < 12; i++) {
            img12[i] = new JLabel();
            imgPath[i] = new JLabel();

        }
        for (int j = 0; j < 16; j++) {
            img16[j] = new JLabel();
        }
        for (int i = 0; i < 4; i++) {
            type[i] = new JLabel();
        }


        JButton colorButton = new JButton("基于颜色检索");
        colorButton.setFont(new Font("宋体", Font.PLAIN, 15));
        colorButton.setBounds(30, 400, 130, 30);
        frame.add(colorButton);
        //颜色检索按钮监听
        colorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    color();//基于颜色检索
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        JButton textureButton = new JButton("基于纹理检索");
        textureButton.setFont(new Font("宋体", Font.PLAIN, 15));
        textureButton.setBounds(30, 450, 130, 30);
        frame.add(textureButton);
        //纹理检索按钮监听
        textureButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    texture();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }//基于纹理检索
            }
        });

        JButton shapeButton = new JButton("基于形状检索");
        shapeButton.setFont(new Font("宋体", Font.PLAIN, 15));
        shapeButton.setBounds(30, 500, 130, 30);
        frame.add(shapeButton);
        shapeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    shape();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        JButton mixButton = new JButton("混合特征检索");
        mixButton.setFont(new Font("宋体", Font.PLAIN, 15));
        mixButton.setBounds(30, 550, 130, 30);
        frame.add(mixButton);
        mixButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    mix();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        JButton fButton = new JButton("特征检索对比");
        fButton.setFont(new Font("宋体", Font.PLAIN, 15));
        fButton.setBounds(30, 600, 130, 30);
        frame.add(fButton);
        fButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    featureCon();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });


        JButton ahashButton = new JButton("均值哈希检索");
        ahashButton.setFont(new Font("宋体", Font.PLAIN, 15));
        ahashButton.setBounds(180, 400, 130, 30);
        frame.add(ahashButton);
        ahashButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    aHash(picPath);
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        JButton phashButton = new JButton("感知哈希检索");
        phashButton.setFont(new Font("宋体", Font.PLAIN, 15));
        phashButton.setBounds(180, 450, 130, 30);
        frame.add(phashButton);
        phashButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    pHash(picPath);
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        JButton dhashButton = new JButton("差异哈希检索");
        dhashButton.setFont(new Font("宋体", Font.PLAIN, 15));
        dhashButton.setBounds(180, 500, 130, 30);
        frame.add(dhashButton);
        dhashButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    dHash(picPath);
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        JButton lshButton = new JButton("混合哈希检索");
        lshButton.setFont(new Font("宋体", Font.PLAIN, 15));
        lshButton.setBounds(180, 550, 130, 30);
        frame.add(lshButton);
        lshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    mixHash();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        JButton hButton = new JButton("哈希检索对比");
        hButton.setFont(new Font("宋体", Font.PLAIN, 15));
        hButton.setBounds(180, 600, 130, 30);
        frame.add(hButton);
        hButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    hashCon();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        frame.setVisible(true);
    }

    /**
     * 选择图像文件（检索此图像的类似图像）
     *
     * @return
     * @throws Exception
     */
    public String choosePic() throws Exception {
        JFileChooser chooser = new JFileChooser("D:\\Desktop\\CBIR\\caltech-101\\101_ObjectCategories");
        //        JFileChooser chooser = new JFileChooser("D:\\Desktop\\CBIR\\256_ObjectCategories");
        chooser.setDialogTitle("请选择图片文件");
        //设置为只能选择图片文件
        FileNameExtensionFilter filter = new FileNameExtensionFilter("jpg", "jpg");
        chooser.setFileFilter(filter);
        //弹出选择框
        int returnVal = chooser.showOpenDialog(null);
        //如果选择了文件
        if (JFileChooser.APPROVE_OPTION == returnVal) {
            picPath = chooser.getSelectedFile().toString();//获取所选文件路径
            text.setText(picPath);//把路径值写到textField中
            //在当前面板上显示所选图像
            ImageIcon sourceimg = new ImageIcon(picPath);
            imgLabel.setIcon(sourceimg);

            /**
             * 选择文件后即计算
             */
            color = GetColor.color_HSV_msv(picPath);//获取图像颜色特征
            texture = GetTexture.texture(picPath);//获取图像纹理特征
            shape = new GetShape().shape(picPath);
            int[] a = AHash_256.fingerPrint(picPath);
            int[] d = DHash_256.fingerPrint(picPath);
            int[] p = PHash_256.fingerPrint(picPath);
            StringBuffer ah = new StringBuffer();
            StringBuffer dh = new StringBuffer();
            StringBuffer ph = new StringBuffer();

            for (int i = 0; i < a.length; i++) {
                ah.append(a[i]);
                dh.append(d[i]);
                ph.append(p[i]);
            }
            ahash = ah.toString();
            dhash = dh.toString();
            phash = ph.toString();

            return picPath;
        } else {
            System.out.println("还未选择文件");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JOptionPane.showMessageDialog(null, "未选中文件", "提示", JOptionPane.PLAIN_MESSAGE);
            return null;
        }
    }

    //基于颜色检索
    public void color() throws Exception {
        if (picPath == null) {//若未选择图像
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());//UI框架,设置弹窗的风格（与系统保持一致）
            JOptionPane.showMessageDialog(null, "还未选择文件", "提示", JOptionPane.PLAIN_MESSAGE);
            return;
        }

        //由服务器返回图像路径
        ArrayList<String> paths = ColorMatch.colorMatch(color, 12);
        if (paths == null || paths.size() == 0) {
            warn();
            return;
        }
        show(paths);
    }

    //基于纹理检索
    public void texture() throws Exception {
        if (picPath == null) {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JOptionPane.showMessageDialog(null, "还未选择文件", "提示", JOptionPane.PLAIN_MESSAGE);
            return;
        }

        ArrayList<String> paths = TextureMatch.textureMatch(texture, 12);
        if (paths == null || paths.size() == 0) {
            warn();
            return;
        }
        show(paths);
    }

    //基于形状检索
    public void shape() throws Exception {
        if (picPath == null) {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JOptionPane.showMessageDialog(null, "还未选择文件", "提示", JOptionPane.PLAIN_MESSAGE);
            return;
        }

        ArrayList<String> paths = ShapeMatch.shapeMatch(shape, 12);
        if (paths == null || paths.size() == 0) {
            warn();
            return;
        }
        show(paths);

    }

    //综合检索
    //检索时因为形状与纹理是cos距离，所以先用1减去，因为从小到大选出，所以权重应该也用1减去
    public void mix() throws Exception {
        if (picPath == null) {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JOptionPane.showMessageDialog(null, "还未选择文件", "提示", JOptionPane.PLAIN_MESSAGE);
            return;
        }


        double[] weight = new double[]{1, 1, 1};
        ArrayList<String> mixPath = MixMatch.getMatchPath(color, texture, shape, weight, 12);
        if (mixPath == null || mixPath.size() == 0) {
            warn();
            return;
        }
        show(mixPath);

    }

    public void featureCon() throws Exception {
        if (picPath == null) {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JOptionPane.showMessageDialog(null, "还未选择文件", "提示", JOptionPane.PLAIN_MESSAGE);
            return;
        }

        double[] weight = new double[]{1, 1, 1};

        ArrayList<String> mixPath = MixMatch.getMatchPath(color, texture, shape, weight, 4);
        ArrayList<String> cPath = ColorMatch.colorMatch(color, 4);
        ArrayList<String> tPath = TextureMatch.textureMatch(texture, 4);
        ArrayList<String> sPath = ShapeMatch.shapeMatch(shape, 4);

        if (mixPath == null || mixPath.size() == 0 || cPath == null || cPath.size() == 0 || tPath == null || tPath.size() == 0 || sPath == null || sPath.size() == 0) {
            warn();
            return;
        }

        //对比展示
        removeShow12();//清除已有地址和图像显示

        type[0].setText("混合特征检索:");
        type[0].setFont(new Font("宋体", Font.PLAIN, 15));
        type[0].setBounds(400, 50, 100, 25);
        frame.add(type[0]);

        int i = 0;//控制检索结果显示
        //x从400开始 y为70 每行4张
        for (int x = 550; x < 1750; x += 320) {//大于等于1360
            if (i < 4) {
                ImageIcon showImg = null;
                if (mixPath != null) {
                    showImg = new ImageIcon(mixPath.get(i));
                }
                img16[i].setBounds(x, 50, 300, 200);
                img16[i].setIcon(showImg);
                frame.add(img16[i]);
                i++;
            }
        }

        //上下间隔30
        type[1].setText("形状特征检索:");
        type[1].setFont(new Font("宋体", Font.PLAIN, 15));
        type[1].setBounds(400, 280, 100, 25);
        frame.add(type[1]);

        //x从400开始 y为300 每行4张
        for (int x = 550; x < 1750; x += 320) {//大于等于1360
            if (i < 8) {
                ImageIcon showImg = null;
                if (cPath != null) {
                    showImg = new ImageIcon(sPath.get(i - 4));
                }
                img16[i].setBounds(x, 280, 300, 200);
                img16[i].setIcon(showImg);
                frame.add(img16[i]);
                i++;
            }
        }

        type[2].setText("纹理特征检索:");
        type[2].setFont(new Font("宋体", Font.PLAIN, 15));
        type[2].setBounds(400, 510, 100, 25);
        frame.add(type[2]);

        //x从400开始 y为300 每行4张
        for (int x = 550; x < 1750; x += 320) {//大于等于1360
            if (i < 12) {
                ImageIcon showImg = null;
                if (tPath != null) {
                    showImg = new ImageIcon(tPath.get(i - 8));
                }
                img16[i].setBounds(x, 510, 300, 200);
                img16[i].setIcon(showImg);
                frame.add(img16[i]);
                i++;
            }
        }

        type[3].setText("颜色特征检索:");
        type[3].setFont(new Font("宋体", Font.PLAIN, 15));
        type[3].setBounds(400, 740, 100, 25);
        frame.add(type[3]);

        //x从400开始 y为300 每行4张
        for (int x = 550; x < 1750; x += 320) {//大于等于1360
            if (i < 16) {
                ImageIcon showImg = null;
                if (sPath != null) {
                    showImg = new ImageIcon(cPath.get(i - 12));
                }
                img16[i].setBounds(x, 740, 300, 200);
                img16[i].setIcon(showImg);
                frame.add(img16[i]);
                i++;
            }

        }
    }


    public void hashCon() throws Exception {
        if (picPath == null) {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JOptionPane.showMessageDialog(null, "还未选择文件", "提示", JOptionPane.PLAIN_MESSAGE);
            return;
        }

        double[] weight = new double[]{0.5, 0.5, 1};

        ArrayList<String> mixPath = MixHash.getMatchPath(ahash, dhash, phash, weight, 4);
        ArrayList<String> aPath = AHashMatch.aHashMatch(ahash, 4);
        ArrayList<String> dPath = DHashMatch.dHashMatch(dhash, 4);
        ArrayList<String> pPath = PHashMatch.pHashMatch(phash, 4);

        if (mixPath == null || mixPath.size() == 0 || aPath == null || aPath.size() == 0 || dPath == null || dPath.size() == 0 || pPath == null || pPath.size() == 0) {
            warn();
            return;
        }

        //对比展示
        removeShow12();//清除已有地址和图像显示

        type[0].setText("混合哈希检索:");
        type[0].setFont(new Font("宋体", Font.PLAIN, 15));
        type[0].setBounds(400, 50, 100, 25);
        frame.add(type[0]);

        int i = 0;//控制检索结果显示
        //x从400开始 y为70 每行4张
        for (int x = 550; x < 1750; x += 320) {//大于等于1360
            if (i < 4) {
                ImageIcon showImg = null;
                showImg = new ImageIcon(mixPath.get(i));
                img16[i].setBounds(x, 50, 300, 200);
                img16[i].setIcon(showImg);
                frame.add(img16[i]);
                i++;
            }
        }

        //上下间隔30
        type[1].setText("感知哈希检索:");
        type[1].setFont(new Font("宋体", Font.PLAIN, 15));
        type[1].setBounds(400, 280, 100, 25);
        frame.add(type[1]);

        //x从400开始 y为300 每行4张
        for (int x = 550; x < 1750; x += 320) {//大于等于1360
            if (i < 8) {
                ImageIcon showImg = null;
                showImg = new ImageIcon(pPath.get(i - 4));

                img16[i].setBounds(x, 280, 300, 200);
                img16[i].setIcon(showImg);
                frame.add(img16[i]);
                i++;
            }
        }

        type[2].setText("均值哈希检索:");
        type[2].setFont(new Font("宋体", Font.PLAIN, 15));
        type[2].setBounds(400, 510, 100, 25);
        frame.add(type[2]);

        //x从400开始 y为300 每行4张
        for (int x = 550; x < 1750; x += 320) {//大于等于1360
            if (i < 12) {
                ImageIcon showImg = null;
                showImg = new ImageIcon(aPath.get(i - 8));
                img16[i].setBounds(x, 510, 300, 200);
                img16[i].setIcon(showImg);
                frame.add(img16[i]);
                i++;
            }
        }

        type[3].setText("差异检索检索:");
        type[3].setFont(new Font("宋体", Font.PLAIN, 15));
        type[3].setBounds(400, 740, 100, 25);
        frame.add(type[3]);

        //x从400开始 y为300 每行4张
        for (int x = 550; x < 1750; x += 320) {//大于等于1360
            if (i < 16) {
                ImageIcon showImg = null;
                showImg = new ImageIcon(dPath.get(i - 12));
                img16[i].setBounds(x, 740, 300, 200);
                img16[i].setIcon(showImg);
                frame.add(img16[i]);
                i++;
            }

        }
    }

    //均值哈希检索
    public void aHash(String picPath) throws Exception {

        if (picPath == null) {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JOptionPane.showMessageDialog(null, "还未选择文件", "提示", JOptionPane.PLAIN_MESSAGE);
            return;
        }
        //获取检索路径
        ArrayList<String> paths = AHashMatch.aHashMatch(ahash, 12);
        show(paths);
    }

    //感知哈希检索
    public void pHash(String picPath) throws Exception {

        if (picPath == null) {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JOptionPane.showMessageDialog(null, "还未选择文件", "提示", JOptionPane.PLAIN_MESSAGE);
            return;
        }
        //获取检索路径
        ArrayList<String> paths = PHashMatch.pHashMatch(phash, 12);
        show(paths);

    }

    //差异哈希检索
    public void dHash(String picPath) throws Exception {

        if (picPath == null) {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JOptionPane.showMessageDialog(null, "还未选择文件", "提示", JOptionPane.PLAIN_MESSAGE);
            return;
        }
        //获取检索路径
        ArrayList<String> paths = DHashMatch.dHashMatch(dhash,12);
        show(paths);

    }

    //哈希综合检索
    public void mixHash() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        if (picPath == null) {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JOptionPane.showMessageDialog(null, "还未选择文件", "提示", JOptionPane.PLAIN_MESSAGE);
            return;
        }

        double[] weight = new double[]{0.5, 0.5, 1};
        ArrayList<String> paths = MixHash.getMatchPath(ahash, dhash, phash, weight, 12);
        show(paths);
    }

    public void show(ArrayList<String> paths) {

        removeShow16();
        //控制检索结果显示
        int i = 0;
        //x从400开始 y从70开始 每行4张 每列3张 显示12张图像（320,200）
        for (int y = 70; y < 600; y += 240) {//大于等于550
            for (int x = 400; x < 1600; x += 320) {//大于等于1360
                if (i < 12) {
                    ImageIcon sourceimg1 = new ImageIcon(paths.get(i));
                    img12[i].setBounds(x, y, 300, 200);
                    img12[i].setIcon(sourceimg1);
                    imgPath[i].setText(i + 1 + ":" + paths.get(i));
                    imgPath[i].setFont(new Font("宋体", Font.PLAIN, 12));
                    imgPath[i].setBounds(x, y + 205, 300, 20);

                    frame.add(img12[i]);
                    frame.add(imgPath[i]);
                    i++;
                }
            }
        }
        frame.setVisible(true);
    }

    //将已展示的内容清除
    public void removeShow12() {
        for (int k = 0; k < 12; k++) {
            frame.remove(imgPath[k]);//清除地址显示
            frame.remove(img12[k]);//清除图像显示
        }
    }

    public void removeShow16() {
        for (int k = 0; k < 16; k++) {
            frame.remove(img16[k]);//清除图像显示
        }
        for (int i = 0; i < 4; i++) {
            frame.remove(type[i]);    //综合对比显示后消除对比标签

        }
    }

    //无法从服务器获取信息报警
    void warn() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JOptionPane.showMessageDialog(null, "网络故障，无法从服务器获取信息", "提示", JOptionPane.PLAIN_MESSAGE);
        return;

    }

    public static void main(String[] args) throws Exception {
        //加载opencv动态链接库
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        new User().init();
    }

}
