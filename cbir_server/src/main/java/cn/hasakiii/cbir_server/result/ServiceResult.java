package cn.hasakiii.cbir_server.result;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServiceResult<T> {
    boolean isSuccess;
    T msg;
}
