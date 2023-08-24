package cn.hasakiii.cbir_server.mapper;

import cn.hasakiii.cbir_server.entity.Shape;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

public interface ShapeMapper extends BaseMapper<Shape> {
    @Update("truncate table shape")
    void delete();
    @Update("update `shape` set `flag`=0 where id=#{id}")
    void del(Integer id);
}
