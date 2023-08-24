package cn.hasakiii.cbir_server.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Z.Richard
 * @CreateTime: 2022/4/17 12:46
 * @Description:
 **/

@Data//可以自动生成成员变量的get/set方法以及toStrung等
@AllArgsConstructor//全参构造
@NoArgsConstructor//无参构造
public class Admin {
    @TableId(value = "a_id", type = IdType.AUTO)
    Integer aId;
    String password;
    String account;
    String name;
    String title;
    String portrait;
    String authority;
}
