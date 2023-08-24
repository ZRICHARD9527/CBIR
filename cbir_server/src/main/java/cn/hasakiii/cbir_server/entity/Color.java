package cn.hasakiii.cbir_server.entity;

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
public class Color {
    Integer cId;
    Integer id;
    Double color0;
    Double color1;
    Double color2;
    Double color3;
    Double color4;
    Double color5;
    Double color6;
    Double color7;
    Double color8;
    Integer flag;

    public Color(double[] colors) {
        if (colors.length == 9) {
            this.color0 = colors[0];
            this.color1 = colors[1];
            this.color2 = colors[2];
            this.color3 = colors[3];
            this.color4 = colors[4];
            this.color5 = colors[5];
            this.color6 = colors[6];
            this.color7 = colors[7];
            this.color8 = colors[8];
        } else {
            System.out.println("颜色特征数不符");
        }

    }

    //获取特征数组
    public double[] getArray() {
        double[] colors = new double[9];
        colors[0] = this.color0;
        colors[1] = this.color1;
        colors[2] = this.color2;
        colors[3] = this.color3;
        colors[4] = this.color4;
        colors[5] = this.color5;
        colors[6] = this.color6;
        colors[7] = this.color7;
        colors[8] = this.color8;
        return colors;
    }
}
