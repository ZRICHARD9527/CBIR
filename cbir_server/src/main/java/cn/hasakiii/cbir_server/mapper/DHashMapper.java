package cn.hasakiii.cbir_server.mapper;

import cn.hasakiii.cbir_server.entity.Dhash;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

public interface DHashMapper extends BaseMapper<Dhash> {
    @Update("truncate table dhash")
    void delete();
    @Update("update `dhash` set `flag`=0 where id=#{id}")
    void del(Integer id);
}
