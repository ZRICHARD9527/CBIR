package cn.hasakiii.cbir_server.controller;

import cn.hasakiii.cbir_server.entity.Admin;
import cn.hasakiii.cbir_server.result.ResultFailure;
import cn.hasakiii.cbir_server.result.ResultModel;
import cn.hasakiii.cbir_server.result.ResultSuccess;
import cn.hasakiii.cbir_server.service.AdminService;
import cn.hasakiii.cbir_server.service.FeatureService;
import cn.hasakiii.cbir_server.service.HashService;
import cn.hasakiii.cbir_server.util.JwtUtils;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Z.Richard
 * @CreateTime: 2022/4/17 16:07
 * @Description: 管理员控制器，返回json格式数据
 **/

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Resource
    AdminService adminService;
    @Resource
    FeatureService featureService;
    @Resource
    HashService hashService;

    //登录
    @PostMapping("/login")
    public ResultModel login(@RequestBody JSONObject jsonObject) {
        String account = jsonObject.getString("account");
        String password = jsonObject.getString("password");
        Admin admin = adminService.login(account, password);

        if (admin == null) {
            return new ResultFailure("登录失败");
        } else {
            Map<String, String> payload = new HashMap<>();
            payload.put("a_id", String.valueOf(admin.getAId()));
            payload.put("account", account);
            String token = JwtUtils.getToken(payload);
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("adminName", admin.getName());
            data.put("portrait", admin.getPortrait());
            data.put("authority", admin.getAuthority());
            data.put("title", admin.getTitle());
            return new ResultSuccess("登录成功", data);
        }

    }

    //加载图库
    @PostMapping("/loadGallery")
    public ResultModel loadGallery(@RequestBody JSONObject jsonObject) {
        String folder = jsonObject.getString("folder");
        System.out.println(folder);
        adminService.loadGallery(folder);

        return new ResultModel(0, "", null);
    }

    //解析所有图像的特征值
    @GetMapping("/setFeature")
    public ResultModel setFeature() {
        featureService.setAllFeature();
        return new ResultSuccess("", null);
    }

    //解析所有图像hash值
    @GetMapping("/setHash")
    public ResultModel setHash() {
        hashService.setAllHash();
        return new ResultSuccess("", null);
    }

    //获取在库图像分页
    @PostMapping("/getPicList")
    public ResultModel getProPage(@RequestBody JSONObject jsonObject) {
        int page = jsonObject.getInteger("page") - 1;
        int size = jsonObject.getInteger("size");

        Map<String, Object> map = adminService.getPicList(page, size);
        System.out.println(map.toString());

        return new ResultModel(0, "", map);
    }

    @PostMapping("/del")
    public ResultModel del(@RequestBody JSONObject jsonObject) {
        int id = jsonObject.getInteger("id");
        adminService.del(id);
        return new ResultModel(0, "", null);
    }

    @PostMapping("/search")
    public ResultModel search(@RequestBody JSONObject jsonObject) {
        Integer id = jsonObject.getInteger("id");
        String content = jsonObject.getString("content");
        Map<String, Object> map = adminService.search(id, content);
        System.out.println(map.toString());
        return new ResultModel(0, "", map);
    }


}
