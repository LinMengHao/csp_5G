package com.xzkj.flowStore.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xzkj.flowStore.controller.web.bo.MyOrderBo;
import com.xzkj.flowStore.entity.FlowProduct;
import com.xzkj.flowStore.entity.Product;
import com.xzkj.flowStore.service.FlowProductService;
import com.xzkj.flowStore.utils.MsgBean;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author LinMengHao
 * @since 2023-02-28
 */
@CrossOrigin
@RestController
@RequestMapping("web/flowproduct")
public class FlowProductController {
    @Autowired
    private FlowProductService flowProductService;

    @PostMapping("homelist")
    @ResponseBody
    public MsgBean flowProductList(@RequestHeader(value = "token", required = false) String token,
                                   @RequestBody FlowProduct product){

        if(product==null||product.getOperatorType()==null){
            return MsgBean.error("请提供商品分类！");
        }
        Integer operatorType = product.getOperatorType();
        List<FlowProduct> list=new ArrayList<>();
        if(operatorType==0){
            //全部
            QueryWrapper<FlowProduct> wrapper = new QueryWrapper<>();
            wrapper.eq("status",1);
            list = flowProductService.list(wrapper);
        }else {
            // 1 移动
            // 2 电信
            // 3 联通
            // 4 国际
            QueryWrapper<FlowProduct> wrapper = new QueryWrapper<>();
            wrapper.eq("status",1);
            wrapper.eq("operator_type",operatorType);
            list = flowProductService.list(wrapper);
        }
        MsgBean msg = new MsgBean();
        msg.putData("productList",list);
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

        FlowProduct product = flowProductService.getById(id);

        if (product == null || product.getStatus() == 2 ){
            return MsgBean.error("商品不存在！");
        }
        return MsgBean.ok().putData(product);
    }
}

