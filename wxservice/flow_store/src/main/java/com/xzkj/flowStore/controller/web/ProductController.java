package com.xzkj.flowStore.controller.web;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.xzkj.flowStore.entity.Product;
import com.xzkj.flowStore.service.ProductService;
import com.xzkj.flowStore.utils.MsgBean;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 商品表 前端控制器
 * </p>
 *
 * @author dashan
 * @since 2020-10-02
 */
@Api(tags = "003.商品")
@CrossOrigin
@RestController
@RequestMapping("web/product")
public class ProductController {


    @Autowired
    private ProductService productService;

    @GetMapping("/homeList")
    @ApiOperation("首页商品列表")
    @ResponseBody
    @ApiResponses(value = {@ApiResponse(code = 200, message = "首页商品列表", response = Product.class)})
    public MsgBean homeList(@ApiParam("token") @RequestHeader(value = "token", required = false) String token) {

        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.eq("state", 1);
        List<Product> list = productService.list(wrapper);


        List<Product> personalList = Lists.newArrayList();
        List<Product> companyList = Lists.newArrayList();
        for (Product product : list) {
            if (product.getType() == 0) {
                personalList.add(product);
            } else {
                companyList.add(product);
            }
        }

        MsgBean msg = new MsgBean();
        msg.putData("personalList", personalList);
        msg.putData("companyList", companyList);
        return msg;
    }


    @GetMapping("/detail")
    @ApiOperation("商品详情")
    @ResponseBody
    @ApiResponses(value = {@ApiResponse(code = 200, message = "首页商品列表", response = Product.class)})
    public MsgBean detail(@ApiParam("token") @RequestHeader(value = "token", required = false) String token,
                          @ApiParam("商品id") @RequestParam(value = "id",required = false)Long id
                          ) {

        if (id == null || id <=0 ){
            return MsgBean.error("商品id不正确！");
        }

        Product product = productService.getById(id);

        if (product == null || product.getState() ==-1 || product.getState() ==0 ){
            return MsgBean.error("商品不存在！");
        }
        return MsgBean.ok().putData(product);
    }

}

