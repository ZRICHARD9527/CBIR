package cn.hasakiii.cbir_server.service;

import cn.hasakiii.cbir_server.entity.*;
import cn.hasakiii.cbir_server.mapper.*;
import cn.hasakiii.cbir_server.util.Compare;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author: Z.Richard
 * @CreateTime: 2022/4/17 19:42
 * @Description:
 **/

@Service
public class UserService {

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

    /**
     * 颜色匹配
     *
     * @param catchNum     匹配的数目
     * @param localFeature 用户上传的图像颜色特征
     * @return 返回匹配结果的id
     */
    public ArrayList<String> colorMatch(int catchNum, Double[] localFeature) {
        double[] feature = trans(localFeature);
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
        List<Map.Entry<Integer, Double>> list = new ArrayList<>(cMap.entrySet());
        list.sort(Comparator.comparing(Map.Entry::getValue));

        int[] colorIds = new int[catchNum];
        for (int i = 0; i < catchNum; i++) {
            colorIds[i] = list.get(i).getKey();
        }

        return getPath(colorIds);
    }

    /**
     * 纹理匹配
     *
     * @param catchNum     匹配的数目
     * @param localFeature 用户上传的图像纹理特征
     * @return 返回匹配结果的id
     */
    public ArrayList<String> textureMatch(int catchNum, Double[] localFeature) {
        double[] feature = trans(localFeature);
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

        int[] textureIds = new int[catchNum];
        for (int i = 0; i < catchNum; i++) {
            textureIds[i] = entryArrayList.get(i).getKey();
        }

        return getPath(textureIds);
    }


    /**
     * 形状匹配
     *
     * @param catchNum     匹配的数目
     * @param localFeature 用户上传的图像形状特征
     * @return 返回匹配结果的id
     */
    public ArrayList<String> shapeMatch(int catchNum, Double[] localFeature) {
        double[] feature = trans(localFeature);
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

        int[] shapeIds = new int[catchNum];
        for (int i = 0; i < catchNum; i++) {
            shapeIds[i] = entryArrayList.get(i).getKey();
        }

        return getPath(shapeIds);
    }

    //将Double数组转换为double数组
    public double[] trans(Double[] arr) {
        double[] doubles = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            doubles[i] = arr[i];
        }
        return doubles;
    }


    /**
     * 综合检索
     *
     * @param colorF
     * @param textureF
     * @param shapeF
     * @param weight
     * @param catchNum
     * @return
     */
    public ArrayList<String> mixMatch(Double[] colorF, Double[] textureF, Double[] shapeF, Double[] weight, int catchNum) {

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
            cMap.put(c.getId(), weight[0] * Compare.euDist(trans(colorF), c.getArray()));
        }

        Map<Integer, Double> tMap = new HashMap<>();
        for (Texture t : texture
        ) {
            tMap.put(t.getId(), weight[1] * (1 - Compare.cosDist(trans(textureF), t.getArray())));
        }

        Map<Integer, Double> sMap = new HashMap<>();
        for (Shape s : shape
        ) {
            sMap.put(s.getId(), weight[2] * (1 - Compare.cosDist(trans(shapeF), s.getArray())));
        }

        for (Integer key : sMap.keySet()
        ) {
            map.put(key, cMap.get(key) + tMap.get(key) + sMap.get(key));
        }

        ArrayList<Map.Entry<Integer, Double>> entryArrayList = new ArrayList<>(map.entrySet());
        entryArrayList.sort(Comparator.comparing(Map.Entry::getValue));//按value从小到大排序

        //选取前catchNum张图像
        int[] ids = new int[catchNum];
        for (int i = 0; i < catchNum; i++) {
            ids[i] = entryArrayList.get(i).getKey();
        }

        return getPath(ids);

    }


    /*******************************************************************************hash匹配
     *
     */

    //均值哈希匹配
    public ArrayList<String> aHashMatch(String local, int catchNum) {

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

        int[] ids = new int[catchNum];
        for (int i = 0; i < catchNum; i++) {
            ids[i] = entryArrayList.get(i).getKey();
        }

        return getPath(ids);
    }

    //差异哈希匹配
    public ArrayList<String> dHashMatch(String local, int catchNum) {

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

        int[] ids = new int[catchNum];
        for (int i = 0; i < catchNum; i++) {
            ids[i] = entryArrayList.get(i).getKey();
        }
        return getPath(ids);

    }

    //感知哈希匹配
    public ArrayList<String> pHashMatch(String local, int catchNum) {

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

        int[] ids = new int[catchNum];
        for (int i = 0; i < catchNum; i++) {
            ids[i] = entryArrayList.get(i).getKey();
        }
        return getPath(ids);

    }

    public ArrayList<String> mixHash(String ahash, String dhash, String phash, Double[] weight, int catchNum) {
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

        int[] ids = new int[catchNum];
        for (int i = 0; i < catchNum; i++) {
            ids[i] = entryArrayList.get(i).getKey();
        }
        return getPath(ids);
    }

}

