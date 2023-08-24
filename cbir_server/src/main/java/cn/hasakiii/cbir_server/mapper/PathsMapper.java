package cn.hasakiii.cbir_server.mapper;

import cn.hasakiii.cbir_server.entity.Paths;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.ArrayList;

public interface PathsMapper extends BaseMapper<Paths> {
    @Select("select * from `paths` where `useflag`=1 limit #{start},#{end}")
    ArrayList<Paths> getPics(Integer start, Integer end);

    @Select("select count(*) from `paths` where `useflag`=1")
    Integer getTotal();

    @Update("update `paths` set `useflag`=0 where id=#{id}")
    void del(Integer id);

    @Select("select * from paths where (useflag=1 and path rlike #{content}) ")
    ArrayList<Paths> search(String content);

}
