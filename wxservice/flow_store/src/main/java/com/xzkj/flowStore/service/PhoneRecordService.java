package com.xzkj.flowStore.service;

import com.xzkj.flowStore.entity.PhoneRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 手机记录表 服务类
 * </p>
 *
 * @author dashan
 * @since 2020-09-27
 */
public interface PhoneRecordService extends IService<PhoneRecord> {


    PhoneRecord getByPhone(String phone);
}
