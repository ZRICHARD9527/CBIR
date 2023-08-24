package cn.hasakiii.cbir_server.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 封装返回结果
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)//用于去除json中的空值
public class ResultModel implements Serializable {
    private int errorCode;
    private String msg;
    private Object data;


    public ResultModel(int errorCode, String msg, Object data) {
        this.errorCode = errorCode;
        this.msg = msg;
        this.data = data;
    }

    public ResultModel(int errorCode, String msg) {
        this.errorCode = errorCode;
        this.msg = msg;
        this.data = null;
    }

    public ResultModel(boolean errorCode, String msg) {
        this.errorCode = errorCode ? 1 : 0;
        this.msg = msg;
        this.data = null;
    }

}
