package cn.hasakiii.cbir_server.mapper;

import cn.hasakiii.cbir_server.entity.Ahash;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

public interface AHashMapper extends BaseMapper<Ahash> {
    @Update("truncate table ahash")
    void delete();
    @Update("update `ahash` set `flag`=0 where id=#{id}")
    void del(Integer id);
}
