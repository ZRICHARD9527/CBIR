package cn.hasakiii.cbir_server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: Z.Richard
 * @CreateTime: 2020/8/8 12:13
 * @Description: 资源映射路径
 **/

@Configuration
public class WebPathConfig implements WebMvcConfigurer {
    /**
     * 上传地址
     */
    @Value("${file.upload.path}")
    private String filePath;
    /**
     * 显示相对地址
     */
    @Value("${file.upload.path.relative}")
    private String fileRelativePath;

    @Override
    //文件磁盘图片url 映射
    //配置server虚拟路径，handler为前台访问的目录，locations为files相对应的本地路径
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(fileRelativePath).
                addResourceLocations("file:/" + filePath);
    }
}
