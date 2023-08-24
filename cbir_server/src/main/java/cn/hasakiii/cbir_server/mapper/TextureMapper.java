package cn.hasakiii.cbir_server.mapper;

import cn.hasakiii.cbir_server.entity.Texture;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

public interface TextureMapper extends BaseMapper<Texture> {
    @Update("truncate table texture")
    void delete();
    @Update("update `texture` set `flag`=0 where id=#{id}")
    void del(Integer id);
}
