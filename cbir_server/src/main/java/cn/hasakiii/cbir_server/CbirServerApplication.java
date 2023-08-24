package cn.hasakiii.cbir_server;

import org.mybatis.spring.annotation.MapperScan;
import org.opencv.core.Core;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(value = {"cn.hasakiii.cbir_server.mapper"})
public class CbirServerApplication {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        SpringApplication.run(CbirServerApplication.class, args);
    }

}
