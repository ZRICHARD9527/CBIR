package cn.hasakiii.cbir_server;

import cn.hasakiii.cbir_server.entity.*;
import cn.hasakiii.cbir_server.mapper.*;
import cn.hasakiii.cbir_server.util.Compare;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.*;

@SpringBootTest
class CbirServerApplicationTests {
    @Resource
    ColorMapper colorMapper;
    @Resource
    TextureMapper textureMapper;
    @Resource
    ShapeMapper shapeMapper;
    @Resource
    PathsMapper pathsMapper;
    @Resource
    AHashMapper aHashMapper;
    @Resource
    DHashMapper dHashMapper;
    @Resource
    PHashMapper pHashMapper;

    @Test
    void fun1() {
        String str = "hello" + 5;
        System.out.println(str);
    }

    @Test
    void contextLoads() {
        System.out.println("hello");
    }

    @Test
        //获取数据库中的特征数据
    void getFeature() {
        //颜色特征
        System.out.println("hello");
        List<Color> color = colorMapper.selectList(null);
        for (Color c : color
        ) {
            System.out.println(c.toString());
        }

    }

    static {
        //1为id 740的飞机 2为第一个摩托

        double[] color1 = new double[]{1.0000000000000000, 0.5453394650077568, 0.0000000000000000, 0.8509828834469647, 0.5524572260561230, 0.0931395951167608, 0.6093916528116804, 0.5726911743200044, 0.5294126628415381};
        double[] color2 = new double[]{1.0000000000000000, 0.7185392662271691, 0.0070425743046288, 0.9965957092250350, 0.7229281107703243, 0.0034621743089774, 0.9946231121578974, 0.7257662525550446, 0.0000000000000000};

        double[] texture1 = new double[]{0.6311543103035078, 0.4210559683321056, 0.6144116108161767, 1.1423087823143168, 0.0232563594279034, 0.4741368360360245, 0.0694945925656035, 1.0293501136429173};
        double[] texture2 = new double[]{0.5007910253298722, 1.7547072684572762, 0.1688338551165602, 1.7301261168163670, 0.0211865373974359, 2.6194030663781707, 0.6641344639338611, 2.4616743919775805};
        double[] shape1 = new double[]{0.6456608084079112, 1.6844614636812008, 1.8320047072583903, 0.2043048852486074, 0.1226810862494616, 0.0165935689646397, 0.0988051181285357, 0.0100939650208704};
        double[] shape2 = new double[]{0.2820700759255978, 0.5889176471186619, 0.0978286706272295, 0.0038296420003518, 0.0050586779658381, -0.0000221879699170, -0.0002670926208648, -0.0000018578139642};


    }


    @Test
    void fun() {
        String str = "1.0000000000000000 | 0.5453394650077568 | 0.0000000000000000 | 0.8509828834469647 | 0.5524572260561230 | 0.0931395951167608 | 0.6093916528116804 | 0.5726911743200044 | 0.5294126628415381";
        String arr = str.replace('|', ',');
        System.out.println(arr);

    }


    //查全率  ：判断查出的数目与数据集总数目之比


    //颜色特征

    /**
     * 颜色匹配
     *
     * @return 返回匹配结果的id
     */
    @Test
    public void colorMatch() {
        double[] color1 = new double[]{1.0000000000000000, 0.5453394650077568, 0.0000000000000000, 0.8509828834469647, 0.5524572260561230, 0.0931395951167608, 0.6093916528116804, 0.5726911743200044, 0.5294126628415381};
        double[] color2 = new double[]{1.0000000000000000, 0.7185392662271691, 0.0070425743046288, 0.9965957092250350, 0.7229281107703243, 0.0034621743089774, 0.9946231121578974, 0.7257662525550446, 0.0000000000000000};
        double[] feature = color2;
        //颜色特征
        QueryWrapper<Color> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("flag", 1);
        List<Color> color = colorMapper.selectList(queryWrapper);//获取所有颜色特征
        Map<Integer, Double> cMap = new HashMap<>();//用来存储对比的结果 <图片id，距离>
        for (Color c : color
        ) {
            cMap.put(c.getId(), Compare.euDist(feature, c.getArray()));
        }

        // 由于HashMap不属于list子类，所以无法使用Collections.sort方法来进行排序，所以将hashmap中的entryset取出放入一个ArrayList中
        ArrayList<Map.Entry<Integer, Double>> list = new ArrayList<>(cMap.entrySet());
        list.sort(Comparator.comparing(Map.Entry::getValue));

        //count(list, "airplane", 800);
        count(list, "Motorbikes", 798);

    }

    @Test
    public void textureMatch() {
        double[] texture1 = new double[]{0.6311543103035078, 0.4210559683321056, 0.6144116108161767, 1.1423087823143168, 0.0232563594279034, 0.4741368360360245, 0.0694945925656035, 1.0293501136429173};
        double[] texture2 = new double[]{0.5007910253298722, 1.7547072684572762, 0.1688338551165602, 1.7301261168163670, 0.0211865373974359, 2.6194030663781707, 0.6641344639338611, 2.4616743919775805};


        double[] feature = texture2;
        //纹理特征
        QueryWrapper<Texture> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("flag", 1);
        List<Texture> texture = textureMapper.selectList(queryWrapper);
        Map<Integer, Double> tMap = new HashMap<>();
        for (Texture t : texture
        ) {
            tMap.put(t.getId(), Compare.cosDist(feature, t.getArray()));
        }
        ArrayList<Map.Entry<Integer, Double>> entryArrayList = new ArrayList<>(tMap.entrySet());
        entryArrayList.sort((o1, o2) -> {
            return o2.getValue().compareTo(o1.getValue());//按value从大到小排序
        });

        //countb(entryArrayList, "airplane", 800);
        countb(entryArrayList, "Motorbikes", 798);
    }

    @Test
    public void shapeMatch() {
        double[] shape1 = new double[]{0.6456608084079112, 1.6844614636812008, 1.8320047072583903, 0.2043048852486074, 0.1226810862494616, 0.0165935689646397, 0.0988051181285357, 0.0100939650208704};
        double[] shape2 = new double[]{0.2820700759255978, 0.5889176471186619, 0.0978286706272295, 0.0038296420003518, 0.0050586779658381, -0.0000221879699170, -0.0002670926208648, -0.0000018578139642};

        double[] feature = shape2;
        QueryWrapper<Shape> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("flag", 1);
        List<Shape> shape = shapeMapper.selectList(queryWrapper);//获取数据库中所有形状特征
        Map<Integer, Double> sMap = new HashMap<>();
        for (Shape s : shape
        ) {
            sMap.put(s.getId(), Compare.cosDist(feature, s.getArray()));
        }
        ArrayList<Map.Entry<Integer, Double>> entryArrayList = new ArrayList<>(sMap.entrySet());
        entryArrayList.sort((o1, o2) -> {
            return o2.getValue().compareTo(o1.getValue());//按value从大到小排序
        });

        //countb(entryArrayList, "airplane", 800);
        countb(entryArrayList, "Motorbikes", 798);

    }


    @Test
    public void mixMatch() {

        double[] color1 = new double[]{1.0000000000000000, 0.5453394650077568, 0.0000000000000000, 0.8509828834469647, 0.5524572260561230, 0.0931395951167608, 0.6093916528116804, 0.5726911743200044, 0.5294126628415381};
        double[] color2 = new double[]{1.0000000000000000, 0.7185392662271691, 0.0070425743046288, 0.9965957092250350, 0.7229281107703243, 0.0034621743089774, 0.9946231121578974, 0.7257662525550446, 0.0000000000000000};
        double[] texture1 = new double[]{0.6311543103035078, 0.4210559683321056, 0.6144116108161767, 1.1423087823143168, 0.0232563594279034, 0.4741368360360245, 0.0694945925656035, 1.0293501136429173};
        double[] texture2 = new double[]{0.5007910253298722, 1.7547072684572762, 0.1688338551165602, 1.7301261168163670, 0.0211865373974359, 2.6194030663781707, 0.6641344639338611, 2.4616743919775805};
        double[] shape1 = new double[]{0.6456608084079112, 1.6844614636812008, 1.8320047072583903, 0.2043048852486074, 0.1226810862494616, 0.0165935689646397, 0.0988051181285357, 0.0100939650208704};
        double[] shape2 = new double[]{0.2820700759255978, 0.5889176471186619, 0.0978286706272295, 0.0038296420003518, 0.0050586779658381, -0.0000221879699170, -0.0002670926208648, -0.0000018578139642};

        double[] colorF = color2;
        double[] textureF = texture2;
        double[] shapeF = shape2;
        double[] weight = new double[]{1, 1, 1};

        QueryWrapper<Color> colorWrapper = new QueryWrapper<>();
        QueryWrapper<Shape> shapeWrapper = new QueryWrapper<>();
        QueryWrapper<Texture> textureWrapper = new QueryWrapper<>();
        colorWrapper.eq("flag", 1);
        shapeWrapper.eq("flag", 1);
        textureWrapper.eq("flag", 1);

        //颜色特征
        List<Color> color = colorMapper.selectList(colorWrapper);
        //纹理特征
        List<Texture> texture = textureMapper.selectList(textureWrapper);
        //形状特征
        List<Shape> shape = shapeMapper.selectList(shapeWrapper);

        Map<Integer, Double> map = new HashMap<>();

        Map<Integer, Double> cMap = new HashMap<>();
        for (Color c : color
        ) {
            cMap.put(c.getId(), weight[0] * Compare.euDist(colorF, c.getArray()));
        }

        Map<Integer, Double> tMap = new HashMap<>();
        for (Texture t : texture
        ) {
            tMap.put(t.getId(), weight[1] * (1 - Compare.cosDist(textureF, t.getArray())));
        }

        Map<Integer, Double> sMap = new HashMap<>();
        for (Shape s : shape
        ) {
            sMap.put(s.getId(), weight[2] * (1 - Compare.cosDist(shapeF, s.getArray())));
        }

        for (Integer key : sMap.keySet()
        ) {
            map.put(key, cMap.get(key) + tMap.get(key) + sMap.get(key));
        }

        ArrayList<Map.Entry<Integer, Double>> entryArrayList = new ArrayList<>(map.entrySet());
        entryArrayList.sort(Comparator.comparing(Map.Entry::getValue));//按value从小到大排序

        //count(entryArrayList, "airplane", 800);
        count(entryArrayList, "Motorbikes", 798);

    }


    @Test
    public void aHashMatch() {

        String ahash1 = "1000000000000000100000000000000010000000000000001010000000000000100100000000000010010100000000001011100000000000100110000000000010001100111011001000111000110000100000000000000010000000000000001000000000000000100000000000000010000000000000001000000000000000";
        String ahash2 = "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111101111111111111110001111111100110000111111100000000001111100000011000011110000000010001111000000000000111111000000100111111101111111011111111111111111111111111111111111";
        String local = ahash2;

        Map<Integer, Integer> map = new HashMap<>();//存储海明距离
        QueryWrapper<Ahash> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("flag", 1);
        List<Ahash> ahashList = aHashMapper.selectList(queryWrapper);

        for (Ahash h : ahashList
        ) {
            map.put(h.getId(), Compare.hammingDist(local, h.getHash()));
        }

        ArrayList<Map.Entry<Integer, Integer>> entryArrayList = new ArrayList<>(map.entrySet());
        entryArrayList.sort(Comparator.comparing(Map.Entry::getValue));

        //counth(entryArrayList, "airplane", 800);
        counth(entryArrayList, "Motorbikes", 798);
    }


    @Test
    //差异哈希匹配
    public void dHashMatch() {

        String dhash1 = "1000000001010111100010010010111110000001010101101001001000011101100110000110101110011000100111111000110001110111101010100110111110101110001101111010011101111101100011100000100110000100100110111000011011010111100001000101101110000010010111111000001000100111";
        String dhash2 = "0000000000000000011000000000000000000000010000000000000001000000000000001110000000000000110000000001000111010000001100011110000001111100100010000110001001001000011010101011000001110100010010000010101100010100000110110011000000100000000001000000000000000000";
        String local = dhash2;

        Map<Integer, Integer> map = new HashMap<>();//存储海明距离

        QueryWrapper<Dhash> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("flag", 1);
        List<Dhash> dhashList = dHashMapper.selectList(queryWrapper);

        for (Dhash h : dhashList
        ) {
            map.put(h.getId(), Compare.hammingDist(local, h.getHash()));
        }

        ArrayList<Map.Entry<Integer, Integer>> entryArrayList = new ArrayList<>(map.entrySet());
        entryArrayList.sort(Comparator.comparing(Map.Entry::getValue));

        //counth(entryArrayList, "airplane", 800);
        counth(entryArrayList, "Motorbikes", 798);

    }

    @Test
    //感知哈希匹配
    public void pHashMatch() {

        String phash1 = "1111011111111111010000001100000000101100010000000000011000000011100100111000000101100000110000000110100001100000100000100011000010010000000010000110100011000000011000000110000010000010000000000001100110000000000010000100000001100000010000001000000100000000";
        String phash2 = "1011110000000101110001011110001011000010010100100011101000000101100000001111001011000100011000010000011110001001000110100001001000110000101001101000010000100000010010100000010100010001000010000011100000100000011001001000000100001000010100101000000100000101";
        String local = phash2;
        Map<Integer, Integer> map = new HashMap<>();//存储海明距离
        QueryWrapper<Phash> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("flag", 1);
        List<Phash> phashList = pHashMapper.selectList(queryWrapper);
        for (Phash h : phashList
        ) {
            map.put(h.getId(), Compare.hammingDist(local, h.getHash()));
        }

        ArrayList<Map.Entry<Integer, Integer>> entryArrayList = new ArrayList<>(map.entrySet());
        entryArrayList.sort(Comparator.comparing(Map.Entry::getValue));

        counth(entryArrayList, "airplane", 800);
        //counth(entryArrayList, "Motorbikes", 798);

    }

    @Test
    public void mixHash() {

        String ahash1 = "1000000000000000100000000000000010000000000000001010000000000000100100000000000010010100000000001011100000000000100110000000000010001100111011001000111000110000100000000000000010000000000000001000000000000000100000000000000010000000000000001000000000000000";
        String ahash2 = "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111101111111111111110001111111100110000111111100000000001111100000011000011110000000010001111000000000000111111000000100111111101111111011111111111111111111111111111111111";


        String dhash1 = "1000000001010111100010010010111110000001010101101001001000011101100110000110101110011000100111111000110001110111101010100110111110101110001101111010011101111101100011100000100110000100100110111000011011010111100001000101101110000010010111111000001000100111";
        String dhash2 = "0000000000000000011000000000000000000000010000000000000001000000000000001110000000000000110000000001000111010000001100011110000001111100100010000110001001001000011010101011000001110100010010000010101100010100000110110011000000100000000001000000000000000000";

        String phash1 = "1111011111111111010000001100000000101100010000000000011000000011100100111000000101100000110000000110100001100000100000100011000010010000000010000110100011000000011000000110000010000010000000000001100110000000000010000100000001100000010000001000000100000000";
        String phash2 = "1011110000000101110001011110001011000010010100100011101000000101100000001111001011000100011000010000011110001001000110100001001000110000101001101000010000100000010010100000010100010001000010000011100000100000011001001000000100001000010100101000000100000101";

        String ahash = ahash2;
        String dhash = dhash2;
        String phash = phash2;
        double[] weight = new double[]{0.1, 0.1, 0.8};


        Map<Integer, Double> map = new HashMap<>();//存储海明距离

        QueryWrapper<Ahash> ahashQueryWrapper = new QueryWrapper<>();
        ahashQueryWrapper.eq("flag", 1);
        QueryWrapper<Dhash> dhashqueryWrapper = new QueryWrapper<>();
        dhashqueryWrapper.eq("flag", 1);
        QueryWrapper<Phash> phashQueryWrapper = new QueryWrapper<>();
        phashQueryWrapper.eq("flag", 1);

        List<Ahash> ahashList = aHashMapper.selectList(ahashQueryWrapper);
        List<Dhash> dhashList = dHashMapper.selectList(dhashqueryWrapper);
        List<Phash> phashList = pHashMapper.selectList(phashQueryWrapper);


        System.out.println("********");
        System.out.println(ahashList.size());
        System.out.println(dhashList.size());
        System.out.println(phashList.size());
        int asize = ahashList.size();
        int dsize = dhashList.size();
        int psize = phashList.size();
        int mix = asize > dsize ? (dsize > psize ? psize : dsize) : (asize > psize ? psize : asize);


        for (int i = 0; i < mix; i++) {
            int id = ahashList.get(i).getId();
            double value = weight[0] * Compare.hammingDist(ahash, ahashList.get(i).getHash()) + weight[1] * Compare.hammingDist(dhash, dhashList.get(i).getHash()) + weight[2] * Compare.hammingDist(phash, phashList.get(i).getHash());
            map.put(id, value);
        }

        ArrayList<Map.Entry<Integer, Double>> entryArrayList = new ArrayList<>(map.entrySet());
        entryArrayList.sort(Comparator.comparing(Map.Entry::getValue));
        //counth1(entryArrayList, "airplane", 800);
        counth1(entryArrayList, "Motorbikes", 798);

    }


    //获取排序好后的list
    void count(ArrayList<Map.Entry<Integer, Double>> list, String str, int n) {
        int[] catchNum = {12, 30, 50, 100, 300, 500, 1000, 1500};
        for (int num : catchNum
        ) {
            int[] Ids = new int[num];
            for (int i = 0; i < num; i++) {
                Ids[i] = list.get(i).getKey();
            }
            int count = 0;
            int j = 0;
            ArrayList<String> path = getPath(Ids);
            int k = path.size();
            for (int i = 0; i < k; i++) {
                if (path.get(i).contains(str)) {
                    count++;
                    j = i;
                }
            }
            double d1 = (double) count / n;//查获比总数 查全率
            double d2 = (double) count / num;//查获比查找总数 查准率

            System.out.print(num + " : " + count + "  " + "查全率：" + d1 + "  " + "查准率：" + d2);
            System.out.println("  " + list.get(j).getValue() + "  " + path.get(j));
        }

    }


    void counth(ArrayList<Map.Entry<Integer, Integer>> list, String str, int n) {
        int[] catchNum = {12, 30, 50, 100, 300, 500, 1000, 1500};
        for (int num : catchNum
        ) {
            int[] Ids = new int[num];
            for (int i = 0; i < num; i++) {
                Ids[i] = list.get(i).getKey();
            }
            int count = 0;
            int j = 0;
            ArrayList<String> path = getPath(Ids);
            int k = path.size();
            for (int i = 0; i < k; i++) {
                if (path.get(i).contains(str)) {
                    count++;
                    j = i;
                }
            }
            double d1 = (double) count / n;//查获比总数 查全率
            double d2 = (double) count / num;//查获比查找总数 查准率

            System.out.print(num + " : " + count + "  " + "查全率：" + d1 + "  " + "查准率：" + d2);
            System.out.println("  " + list.get(j).getValue() / 256.0 + "  " + path.get(j));
        }

    }

    void counth1(ArrayList<Map.Entry<Integer, Double>> list, String str, int n) {
        int[] catchNum = {12, 30, 50, 100, 300, 500, 1000, 1500};
        for (int num : catchNum
        ) {
            int[] Ids = new int[num];
            for (int i = 0; i < num; i++) {
                Ids[i] = list.get(i).getKey();
            }
            int count = 0;
            int j = 0;
            ArrayList<String> path = getPath(Ids);
            int k = path.size();
            for (int i = 0; i < k; i++) {
                if (path.get(i).contains(str)) {
                    count++;
                    j = i;
                }
            }
            double d1 = (double) count / n;//查获比总数 查全率
            double d2 = (double) count / num;//查获比查找总数 查准率

            System.out.print(num + " : " + count + "  " + "查全率：" + d1 + "  " + "查准率：" + d2);
            System.out.println("  " + list.get(j).getValue() / 256.0 + "  " + path.get(j));
        }

    }

    void countb(ArrayList<Map.Entry<Integer, Double>> list, String str, int n) {
        int[] catchNum = {12, 30, 50, 100, 300, 500, 1000, 1500};
        for (int num : catchNum
        ) {
            int[] Ids = new int[num];
            for (int i = 0; i < num; i++) {
                Ids[i] = list.get(i).getKey();
            }
            int count = 0;
            int j = 0;
            ArrayList<String> path = getPath(Ids);
            int k = path.size();
            for (int i = 0; i < k; i++) {
                if (path.get(i).contains(str)) {
                    count++;
                    j = i;
                }
            }
            double d1 = (double) count / n;//查获比总数 查全率
            double d2 = (double) count / num;//查获比查找总数 查准率

            System.out.print(num + " : " + count + "  " + "查全率：" + d1 + "  " + "查准率：" + d2);
            System.out.println("  " + (1 - list.get(j).getValue()) + "  " + path.get(j));
        }

    }


    /**
     * 根据id获取图像路径
     *
     * @param ids
     * @return
     */
    public ArrayList<String> getPath(int[] ids) {
        ArrayList<String> paths = new ArrayList<>();
        for (int id : ids
        ) {
            paths.add(pathsMapper.selectById(id).getPath());
        }
        return paths;
    }

    //查准率  ：查询出的数目与查出的总数之比


}
