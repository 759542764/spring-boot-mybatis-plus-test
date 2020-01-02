package com.github.mybatisPlus.config;

import javax.sql.DataSource;

public interface DynamicDataSourcePlusProvider{

    /**
     * 创建数据源
     * @return 所有数据源，key为数据源名称
     */
	DataSource createDataSource(String dbKey);
}
