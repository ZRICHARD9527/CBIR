package cn.hasakiii.cbir_server.service;

import cn.hasakiii.cbir_server.entity.*;
import cn.hasakiii.cbir_server.mapper.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @Author: Z.Richard
 * @CreateTime: 2022/4/17 16:38
 * @Description:
 **/

@Service
public class AdminService {

    @Resource
    AdminMapper adminMapper;
    @Resource
    PathsMapper pathsMapper;
    @Resource
    AHashMapper aHashMapper;
    @Resource
    DHashMapper dHashMapper;
    @Resource
    PHashMapper pHashMapper;
    @Resource
    ColorMapper colorMapper;
    @Resource
    TextureMapper textureMapper;
    @Resource
    ShapeMapper shapeMapper;


    /**
     * 通过名称和密码查询管理员id
     *
     * @param account  账户
     * @param password 密码
     * @return 查询结果
     */
    public Admin login(String account, String password) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account)
                .eq("password", password);
        Admin admin = adminMapper.selectOne(queryWrapper);

        if (admin != null) {
            return admin;
        } else {
            return null;
        }
    }


    /**
     * 将文件夹下的所有图片载入数据库中
     *
     * @param folder 文件夹路径
     */
    public void loadGallery(String folder) {
        Pattern pat = Pattern.compile("\\S+\\.jpg");//正则表达式对象
        File folderFile = new File(folder);//文件夹对象
        File[] arr = folderFile.listFiles();//获取文件夹下的所有文件对象

        if (arr != null) {
            for (File file : arr) {
                //判断是否是文件夹，文件夹需递归调用find方法找出所有的图像文件
                if (file.isDirectory()) {
                    loadGallery(file.getAbsolutePath());
                }
                //调用模式的matcher方法，生成一个匹配器
                String str = file.getAbsolutePath();//这个getAbsolutePath()方法返回一个String的文件绝对路径
                Matcher mat = pat.matcher(str);

                if (mat.matches()) {//根据正则表达式，如果符合匹配规则，则将文件写入
                    System.out.println(str);
                    Paths p = new Paths(str);
                    pathsMapper.insert(p);//将图像文件存入数据库中
                }
            }
        }
    }

    //获取图像分页
    public Map<String, Object> getPicList(int page, int size) {
        Map<String, Object> map = new HashMap<>();
        List<Paths> pics = pathsMapper.getPics(page * size, size);
        map.put("pics", pics);
        map.put("totalElements", pathsMapper.getTotal());
        return map;
    }

    //删除图片
    public void del(int id) {
        pathsMapper.del(id);//图像flag置否
        //特征flag置否
        aHashMapper.del(id);
        dHashMapper.del(id);
        pHashMapper.del(id);
        colorMapper.del(id);
        textureMapper.del(id);
        shapeMapper.del(id);
    }

    //查找
    public Map<String, Object> search(Integer id, String content) {
        Map<String, Object> map = new HashMap<>();
        int num = 0;
        List<Paths> list = new ArrayList<>();
        if (id != null) {//有id时只按照id
            list.add(pathsMapper.selectById(id));
            map.put("pics", list);
        } else {
            list = pathsMapper.search(content);//模糊搜索，限定100
            num = list.size();
            map.put("pics", list);
        }
        map.put("totalElements", num);
        return map;
    }

}
