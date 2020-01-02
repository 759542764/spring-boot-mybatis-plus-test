package com.github.mybatisPlus.config;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.spel.DynamicDataSourceSpelParser;
import com.baomidou.dynamic.datasource.spel.DynamicDataSourceSpelResolver;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.druid.DruidDynamicDataSourceConfiguration;
import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.extension.parsers.DynamicTableNameParser;
import com.baomidou.mybatisplus.extension.parsers.ITableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantSqlParser;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Slf4j
@Configuration
@EnableConfigurationProperties(DynamicDataSourceProperties.class)
@AutoConfigureAfter(DynamicDataSourceAutoConfiguration.class)
@Import(DruidDynamicDataSourceConfiguration.class)
public class DynamicDataSourcePlusAutoConfiguration {

    private static final String SYSTEM_TENANT_ID = "provider_id";

	@Autowired
    private DataSource dynamicRoutingDataSource;

	@Autowired
	private DynamicDataSourcePlusProvider dynamicDataSourceProvider;

	@Bean
    @ConditionalOnMissingBean
    public DynamicDataSourcePlusAnnotationAdvisor dynamicDataSourcePlusAnnotationAdvisor(DynamicDataSourceSpelParser dynamicDataSourceSpelParser, DynamicDataSourceSpelResolver dynamicDataSourceSpelResolver,DynamicDataSourceProperties properties) {
        DynamicDataSourcePlusAnnotationInterceptor interceptor = new DynamicDataSourcePlusAnnotationInterceptor();
        DynamicDataSourcePlusAnnotationAdvisor advisor = new DynamicDataSourcePlusAnnotationAdvisor(interceptor);
        interceptor.setDynamicDataSourceSpelParser(dynamicDataSourceSpelParser);
        interceptor.setDynamicDataSourceSpelResolver(dynamicDataSourceSpelResolver);
        interceptor.setDynamicDataSourceProvider(dynamicDataSourceProvider);
        interceptor.setDynamicRoutingDataSource((DynamicRoutingDataSource)dynamicRoutingDataSource);
        advisor.setOrder(properties.getOrder());
        return advisor;
    }

	@Bean
	@ConditionalOnMissingBean
	public DynamicDataSourcePlusCreator dynamicDataSourcePlusCreator(DynamicDataSourceProperties properties){
		return new DynamicDataSourcePlusCreator(properties);
	}


    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        DynamicTableNameParser dynamicTableNameParser = new DynamicTableNameParser();
        List<ISqlParser> sqlParserList = new ArrayList<>();

        // 动态修改表名
        dynamicTableNameParser.setTableNameHandlerMap(new HashMap<String, ITableNameHandler>(2) {{
            put("user", (metaObject, sql, tableName) -> {
                // metaObject 可以获取传入参数，这里实现你自己的动态规则
                String year = "_2018";
                int random = new Random().nextInt(10);
                if (random % 2 == 1) {
                    year = "_2019";
                }
                return tableName + year;
            });
        }});

        sqlParserList.add(dynamicTableNameParser);

        // SQL解析处理拦截：增加租户处理回调。
        TenantSqlParser tenantSqlParser = new TenantSqlParser()
                .setTenantHandler(new TenantHandler() {

                    @Override
                    public Expression getTenantId(boolean where) {
                        String dsKeyName = "x-datasource-key";
                        // 从当前系统上下文中取出当前请求的服务商ID，通过解析器注入到SQL中。
                        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                        String dsKey= StringUtils.isNotBlank(request.getHeader(dsKeyName))?request.getHeader(dsKeyName):(String)request.getSession().getAttribute(dsKeyName);
                        return new LongValue("\"" + dsKey + "\"");
                    }

                    @Override
                    public String getTenantIdColumn() {
                        return SYSTEM_TENANT_ID;
                    }

                    @Override
                    public boolean doTableFilter(String tableName) {
                        // 忽略掉一些表：如租户表（provider）本身不需要执行这样的处理。
                        return tablePass().contains(tableName);
                    }
                });
        sqlParserList.add(tenantSqlParser);
        paginationInterceptor.setSqlParserList(sqlParserList);
        return paginationInterceptor;
    }

    private static ArrayList<String> tablePass() {
        ArrayList<String> list = new ArrayList<>();
        list.add("t_datasources");
        return list;
    }
}
