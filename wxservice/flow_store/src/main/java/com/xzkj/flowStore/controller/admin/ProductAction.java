package com.xzkj.flowStore.controller.admin;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.xzkj.flowStore.controller.admin.bo.ProductSearchBo;
import com.xzkj.flowStore.entity.Product;
import com.xzkj.flowStore.service.ProductService;
import com.xzkj.flowStore.utils.MsgBean;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "005.商品")
@RestController
@RequestMapping("/admin/product")
public class ProductAction {


    @Autowired
    private ProductService productService;


    @PostMapping("/list")
    @ApiOperation("商品列表")
    @ResponseBody
    @ApiResponses(value = {@ApiResponse(code = 200, message = "商品列表", response = Product.class)})
    public MsgBean save(
            @ApiParam("token") @RequestHeader(value = "token", required = false) String token,
            @RequestBody ProductSearchBo bo) {
        if (bo == null) {
            bo = new ProductSearchBo();
        }

        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
        IPage<Product> page = new Page<>(bo.getPageNo(), bo.getPageSize());
        productService.page(page, wrapper);
        return MsgBean.ok().putData(page);
    }

    @PostMapping("/save")
    @ApiOperation("商品新增或修改")
    @ResponseBody
    @ApiResponses(value = {@ApiResponse(code = 200, message = "商品新增或修改", response = MsgBean.class)})
    public MsgBean save(
            @ApiParam("token") @RequestHeader(value = "token", required = false) String token,
            @RequestBody Product product) {

        if (product == null) {
            return MsgBean.error("请求参数为空！");
        }

        if (StrUtil.isBlank(product.getTitle())) {
            return MsgBean.error("商品名称不能为空！");
        } else if (StrUtil.isBlank(product.getSubTitle())) {
            return MsgBean.error("副标题不能为空！");
        } else if (product.getAmount() == null || product.getAmount() <= 0) {
            return MsgBean.error("价格不正确！");
        } else if (StrUtil.isBlank(product.getContent())) {
            return MsgBean.error("内容不能为空！");
        } else if (StrUtil.isBlank(product.getService())) {
            return MsgBean.error("服务内容不能空！");
        }

        boolean result = productService.saveOrUpdate(product);

        if (result) {
            return MsgBean.ok("保存成功！");
        }
        return MsgBean.error("操作失败，请重试！");
    }



}
