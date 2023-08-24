package httpLink;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @Author: Z.Richard
 * @CreateTime: 2022/5/15 2:48
 * @Description:
 **/

public class PHashMatch {
    static String baseUrl = "http://localhost:8181/user/phashMatch";

    public static ArrayList<String> pHashMatch(String hash, int catchNum) {
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        // 创建Post请求
        HttpPost httpPost = new HttpPost(baseUrl);
        // 响应模型
        CloseableHttpResponse response = null;
        //json参数
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("hash", hash);
        jsonObject.put("catchNum", catchNum);
        ArrayList<String> path = new ArrayList<>();//保存返回数据

        try {
            StringEntity jsonParam = new StringEntity(jsonObject.toString());
            jsonParam.setContentEncoding("UTF-8");//发送数据编码为utf-8
            jsonParam.setContentType("application/json");//发送json数据需要设置contentType

            // 由客户端执行(发送)Post请求
            httpPost.setEntity(jsonParam);
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();

            if (response.getStatusLine().getStatusCode() != 200) {
                //请求失败则返回
                return null;
            }
            if (responseEntity != null) {

                JSONObject result = JSONObject.parseObject(EntityUtils.toString(responseEntity));//获取响应的JSON数据
                System.out.println("服务器返回结果---" + result);
                JSONArray jsonArray = result.getJSONArray("data");
                for (int i = 0; i < catchNum; i++) {
                    path.add(jsonArray.getString(i));
                    System.out.println(path.get(i));
                }

                return path;//返回路径
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } finally {
            /**
             * finally语句在return语句执行之后，返回之前。
             * 若在finally中有return会覆盖try中的return
             */

            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    public static void main(String[] args) {
        String hash="1110010000001000110100000000000111000010010100011100000100000010000111101110101000000011110010010001111001001100001011110110010100011110000001000101110100000000010000000000011000000000101010100000100000011100000001001000010100000010010000000000000001010111";
        ArrayList<String> test = pHashMatch(hash, 12);
        //String[] test = getMatchPath(color, texture, shape, d, 14);
        for (String st : test
        ) {
            System.out.println(st);
        }
    }
}
