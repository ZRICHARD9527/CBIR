package cn.hasakiii.cbir_server.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Z.Richard
 * @CreateTime: 2022/5/14 21:22
 * @Description:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ahash {
    Integer aId;
    Integer id;
    String hash;
    Integer flag;

    public Ahash(int[] print) {
        StringBuffer str = new StringBuffer();
        for (int i : print) {
            str.append(i);
        }
        this.hash = str.toString();
    }
}
