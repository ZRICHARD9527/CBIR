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
@NoArgsConstructor
@AllArgsConstructor
public class Texture {
    Integer tId;
    Integer id;
    Double texture0;
    Double texture1;
    Double texture2;
    Double texture3;
    Double texture4;
    Double texture5;
    Double texture6;
    Double texture7;
    Integer flag;

    public Texture(double[] textureF) {
        this.texture0 = textureF[0];
        this.texture1 = textureF[1];
        this.texture2 = textureF[2];
        this.texture3 = textureF[3];
        this.texture4 = textureF[4];
        this.texture5 = textureF[5];
        this.texture6 = textureF[6];
        this.texture7 = textureF[7];
    }

    public double[] getArray() {
        double[] textures = new double[9];
        textures[0] = this.texture0;
        textures[1] = this.texture1;
        textures[2] = this.texture2;
        textures[3] = this.texture3;
        textures[4] = this.texture4;
        textures[5] = this.texture5;
        textures[6] = this.texture6;
        textures[7] = this.texture7;
        return textures;
    }
}
