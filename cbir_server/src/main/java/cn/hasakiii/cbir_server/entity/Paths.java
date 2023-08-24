package cn.hasakiii.cbir_server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Z.Richard
 * @CreateTime: 2022/4/17 13:06
 * @Description:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paths {
    @TableId(value = "id", type = IdType.AUTO)
    Integer id;
    String path;
    Integer useflag;

    public Paths(String path) {
        this.path = path;
    }
}
