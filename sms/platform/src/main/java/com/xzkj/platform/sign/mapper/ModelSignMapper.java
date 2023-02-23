package com.xzkj.platform.sign.mapper;

import com.xzkj.platform.sign.domain.ModelSign;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelSignMapper {
    List<ModelSign> selectModelSignList(ModelSign modelSign);
}
