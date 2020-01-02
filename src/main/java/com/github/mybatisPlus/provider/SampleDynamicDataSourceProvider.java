package com.github.mybatisPlus.provider;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.mybatisPlus.entity.TDatasources;
import com.github.mybatisPlus.mapper.TDatasourcesMapper;
import com.github.mybatisPlus.config.DynamicDataSourcePlusCreator;
import com.github.mybatisPlus.config.DynamicDataSourcePlusProvider;

@Component
public class SampleDynamicDataSourceProvider implements DynamicDataSourcePlusProvider {

	@Autowired
	private TDatasourcesMapper tDatasourcesMapper;

	@Autowired
	 private DynamicDataSourcePlusCreator dynamicDataSourcePlusCreator;

	@Override
	public DataSource createDataSource(String dsKey) {
		TDatasources tDatasources = tDatasourcesMapper.selectOne(new QueryWrapper<TDatasources>().eq("ds_key",dsKey));
		DataSourceProperty dataSourceProperty = new DataSourceProperty();
		dataSourceProperty.setUrl(tDatasources.getDsUrl());
		dataSourceProperty.setUsername(tDatasources.getDsUsername());
		dataSourceProperty.setPassword(tDatasources.getDsPassword());
		dataSourceProperty.setDriverClassName(tDatasources.getDriverClassName());
		dataSourceProperty.setPollName(dsKey);

		return dynamicDataSourcePlusCreator.createDruidDataSource(dataSourceProperty);
	}
}
