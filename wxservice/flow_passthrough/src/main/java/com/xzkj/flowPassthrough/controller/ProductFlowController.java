package com.xzkj.flowPassthrough.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author LinMengHao
 * @since 2023-02-24
 */
@Slf4j
@RestController
@RequestMapping("/flowPassthrough/product-flow")
public class ProductFlowController {
    @RequestMapping("product")
    public String product(){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("errorMsg","正常");
        jsonObject.put("errorCode","100000");

        JSONObject data1=new JSONObject();
        data1.put("operator_name", "移动");
        data1.put("pkgcode", "10015");
        data1.put("pkgsize", 100);
        data1.put("pkgstprice", 10);
        data1.put("province_name", "全国");
        data1.put("areatypename", "全国包");

        JSONObject data2=new JSONObject();
        data2.put("operator_name", "移动");
        data2.put("pkgcode", "10015");
        data2.put("pkgsize", 150);
        data2.put("pkgstprice", 20);
        data2.put("province_name", "全国");
        data2.put("areatypename", "全国包");

        JSONObject data3=new JSONObject();
        data3.put("operator_name", "移动");
        data3.put("pkgcode", "10015");
        data3.put("pkgsize", 150);
        data3.put("pkgstprice", 20);
        data3.put("province_name", "全国");
        data3.put("areatypename", "全国包");

        JSONObject data4=new JSONObject();
        data4.put("operator_name", "移动");
        data4.put("pkgcode", "10019");
        data4.put("pkgsize", 300);
        data4.put("pkgstprice", 30);
        data4.put("province_name", "全国");
        data4.put("areatypename", "全国包");

        JSONArray jsonArray =new JSONArray();

        jsonArray.add(data1);
        jsonArray.add(data2);
        jsonArray.add(data3);
        jsonArray.add(data4);
        jsonObject.put("listProduct",jsonArray);
        log.info("查询结果：{}",jsonObject);

        return jsonObject.toJSONString();
    }
}

