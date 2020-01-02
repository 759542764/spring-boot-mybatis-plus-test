package com.github.mybatisPlus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.mybatisPlus.entity.TAccount;
import com.github.mybatisPlus.annotation.SAAS;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@SAAS()
@Mapper
@Repository
public interface TAccountMapper extends BaseMapper<TAccount> {

}
