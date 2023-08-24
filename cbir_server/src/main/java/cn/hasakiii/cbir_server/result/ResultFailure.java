package cn.hasakiii.cbir_server.result;

/**
 * 返回结果出现错误 错误码为1
 */
public class ResultFailure extends ResultModel {
    public ResultFailure(String msg) {
        super(1, msg, null);
    }
}
