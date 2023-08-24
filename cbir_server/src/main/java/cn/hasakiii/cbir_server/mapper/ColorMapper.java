package cn.hasakiii.cbir_server.mapper;

import cn.hasakiii.cbir_server.entity.Color;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

public interface ColorMapper extends BaseMapper<Color> {
    @Update("truncate table color")
    void delete();
    @Update("update `color` set `flag`=0 where id=#{id}")
    void del(Integer id);
}
