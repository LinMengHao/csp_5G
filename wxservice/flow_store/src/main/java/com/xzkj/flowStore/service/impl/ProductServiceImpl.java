package com.xzkj.flowStore.service.impl;

import com.xzkj.flowStore.entity.Product;
import com.xzkj.flowStore.mapper.ProductMapper;
import com.xzkj.flowStore.service.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author dashan
 * @since 2020-10-02
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

}
