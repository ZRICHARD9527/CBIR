package cn.hasakiii.cbir_server.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: Z.Richard
 * @CreateTime: 2022/4/17 13:06
 * @Description:
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mlog {
    Integer id;
    Integer aId;
    Date time;
    String operation;
    String object;
}
