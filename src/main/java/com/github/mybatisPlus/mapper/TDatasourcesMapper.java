package com.github.mybatisPlus.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.mybatisPlus.entity.TDatasources;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface TDatasourcesMapper extends BaseMapper<TDatasources> {

}
