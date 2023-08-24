package cn.hasakiii.cbir_server.result;

/**
 * 正确返回 错误码为0
 */
public class ResultSuccess extends ResultModel{
    public ResultSuccess(String msg, Object data) {
        super(0, msg, data);
    }
    public ResultSuccess(String msg) {
        super(0, msg, null);
    }
}
