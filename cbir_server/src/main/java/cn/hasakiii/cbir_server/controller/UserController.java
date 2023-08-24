package cn.hasakiii.cbir_server.controller;

import cn.hasakiii.cbir_server.result.ResultFailure;
import cn.hasakiii.cbir_server.result.ResultModel;
import cn.hasakiii.cbir_server.result.ResultSuccess;
import cn.hasakiii.cbir_server.service.UserService;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * @Author: Z.Richard
 * @CreateTime: 2022/4/17 16:07
 * @Description: 响应请求返回对应图像的地址
 **/
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    UserService userService;

    /**
     * 从用户处接收请求
     *
     * @param jsonObject 应该收到图像的特征值（后续可加密特征值）
     * @return 返回匹配的图片
     */

    @PostMapping("/mixMatch")
    ResultModel mixMatch(@RequestBody JSONObject jsonObject) {
        System.out.println(jsonObject);

        Double[] weight = ((ArrayList<Double>) jsonObject.get("weight")).toArray(new Double[3]);
        Double[] color = ((ArrayList<Double>) jsonObject.get("color")).toArray(new Double[9]);
        Double[] texture = ((ArrayList<Double>) jsonObject.get("texture")).toArray(new Double[8]);
        Double[] shape = ((ArrayList<Double>) jsonObject.get("shape")).toArray(new Double[8]);
        int catchNum = jsonObject.getInteger("catchNum");

        ArrayList<String> path = userService.mixMatch(color, texture, shape, weight, catchNum);

        if (path.size() != 0) {
            return new ResultSuccess("匹配成功", path);
        } else {
            return new ResultFailure("没有数据");
        }
    }

    @PostMapping("/colorMatch")
    ResultModel colorMatch(@RequestBody JSONObject jsonObject) {
        System.out.println(jsonObject);

        Double[] color = ((ArrayList<Double>) jsonObject.get("color")).toArray(new Double[9]);
        int catchNum = jsonObject.getInteger("catchNum");

        return new ResultModel(0, "匹配结果", userService.colorMatch(catchNum, color));
    }

    @PostMapping("/textureMatch")
    ResultModel textureMatch(@RequestBody JSONObject jsonObject) {
        System.out.println(jsonObject);

        Double[] texture = ((ArrayList<Double>) jsonObject.get("texture")).toArray(new Double[8]);
        int catchNum = jsonObject.getInteger("catchNum");

        return new ResultModel(0, "匹配结果", userService.textureMatch(catchNum, texture));
    }

    @PostMapping("/shapeMatch")
    ResultModel shapeMatch(@RequestBody JSONObject jsonObject) {
        System.out.println(jsonObject);

        Double[] shape = ((ArrayList<Double>) jsonObject.get("shape")).toArray(new Double[8]);
        int catchNum = jsonObject.getInteger("catchNum");

        return new ResultModel(0, "匹配结果", userService.shapeMatch(catchNum, shape));
    }

    @PostMapping("/ahashMatch")
    ResultModel aHashMatch(@RequestBody JSONObject jsonObject) {
        System.out.println(jsonObject);

        String hash = jsonObject.getString("hash");
        int num = jsonObject.getInteger("catchNum");

        return new ResultModel(0, "均值哈希匹配结果", userService.aHashMatch(hash, num));
    }

    @PostMapping("/dhashMatch")
    ResultModel dHashMatch(@RequestBody JSONObject jsonObject) {
        System.out.println(jsonObject);

        String hash = jsonObject.getString("hash");
        int num = jsonObject.getInteger("catchNum");

        return new ResultModel(0, "均值哈希匹配结果", userService.dHashMatch(hash, num));
    }

    @PostMapping("/phashMatch")
    ResultModel pHashMatch(@RequestBody JSONObject jsonObject) {
        System.out.println(jsonObject);

        String hash = jsonObject.getString("hash");
        int num = jsonObject.getInteger("catchNum");

        return new ResultModel(0, "均值哈希匹配结果", userService.pHashMatch(hash, num));
    }

    @PostMapping("/mixHash")
    ResultModel mixHash(@RequestBody JSONObject jsonObject) {
        System.out.println(jsonObject);

        Double[] weight = ((ArrayList<Double>) jsonObject.get("weight")).toArray(new Double[3]);
        String ahash = jsonObject.getString("ahash");
        String dhash = jsonObject.getString("dhash");
        String phash = jsonObject.getString("phash");
        int catchNum = jsonObject.getInteger("catchNum");

        ArrayList<String> path = userService.mixHash(ahash, dhash, phash, weight, catchNum);

        if (path.size() != 0) {
            return new ResultSuccess("匹配成功", path);
        } else {
            return new ResultFailure("没有数据");
        }
    }
}
