package cn.hasakiii.cbir_server.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Z.Richard
 * @CreateTime: 2022/4/19 22:45
 * @Description:
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Folder {
    Integer id;
    String path;
    Boolean flag;
}
