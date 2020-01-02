package com.github.mybatisPlus.config;

import javax.sql.DataSource;

import com.baomidou.dynamic.datasource.DynamicDataSourceCreator;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;

public class DynamicDataSourcePlusCreator{

	private DynamicDataSourceProperties properties;

	public DynamicDataSourcePlusCreator(DynamicDataSourceProperties properties){
		this.properties=properties;
	}

	/**
     * 创建DRUID数据源
     *
     * @param dataSourceProperty 数据源参数
     * @return 数据源
     */
    public DataSource createDruidDataSource(DataSourceProperty dataSourceProperty) {
    	DynamicDataSourceCreator dynamicDataSourceCreator=new DynamicDataSourceCreator();
    	dynamicDataSourceCreator.setDruidGlobalConfig(properties.getDruid());
    	dataSourceProperty.setDruid(properties.getDruid());
        return dynamicDataSourceCreator.createDruidDataSource(dataSourceProperty);
    }
}
