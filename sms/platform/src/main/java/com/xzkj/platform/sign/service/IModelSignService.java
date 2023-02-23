package com.xzkj.platform.sign.service;

import com.xzkj.platform.sign.domain.ModelSign;

import java.util.List;

public interface IModelSignService {
    List<ModelSign> selectModelSignList(ModelSign modelSign);
}
