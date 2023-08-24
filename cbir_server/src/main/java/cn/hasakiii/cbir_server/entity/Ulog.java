package cn.hasakiii.cbir_server.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: Z.Richard
 * @CreateTime: 2022/4/17 13:07
 * @Description:
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ulog {
    Integer id;
    String uIp;
    Date time;
    String operation;
    String object;
}
