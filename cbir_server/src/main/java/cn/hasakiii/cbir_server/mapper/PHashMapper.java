package cn.hasakiii.cbir_server.mapper;

import cn.hasakiii.cbir_server.entity.Phash;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

public interface PHashMapper extends BaseMapper<Phash> {
    @Update("truncate table phash")
    void delete();
    @Update("update `phash` set `flag`=0 where id=#{id}")
    void del(Integer id);
}
