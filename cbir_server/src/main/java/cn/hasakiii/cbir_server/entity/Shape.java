package cn.hasakiii.cbir_server.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Z.Richard
 * @CreateTime: 2022/4/17 13:07
 * @Description:
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Shape {

    Integer sId;
    Integer id;

    Double shape0;
    Double shape1;
    Double shape2;
    Double shape3;
    Double shape4;
    Double shape5;
    Double shape6;
    Double shape7;
    Integer flag;

    public Shape(double[] shapeF) {
        this.shape0 = shapeF[0];
        this.shape1 = shapeF[1];
        this.shape2 = shapeF[2];
        this.shape3 = shapeF[3];
        this.shape4 = shapeF[4];
        this.shape5 = shapeF[5];
        this.shape6 = shapeF[6];
        this.shape7 = shapeF[7];
    }
    public double[] getArray() {
        double[] shapes = new double[9];
        shapes[0] = this.shape0;
        shapes[1] = this.shape1;
        shapes[2] = this.shape2;
        shapes[3] = this.shape3;
        shapes[4] = this.shape4;
        shapes[5] = this.shape5;
        shapes[6] = this.shape6;
        shapes[7] = this.shape7;
        return shapes;
    }
}
