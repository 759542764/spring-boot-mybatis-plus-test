package com.github.mybatisPlus.config;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.spel.DynamicDataSourceSpelParser;
import com.baomidou.dynamic.datasource.spel.DynamicDataSourceSpelResolver;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.github.mybatisPlus.annotation.SAAS;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

/**
 * 动态数据源AOP核心拦截器
 */
@Slf4j
@Setter
public class DynamicDataSourcePlusAnnotationInterceptor implements MethodInterceptor {

    private DynamicDataSourcePlusProvider dynamicDataSourceProvider;

    private DynamicRoutingDataSource dynamicRoutingDataSource;

    private DynamicDataSourceSpelResolver dynamicDataSourceSpelResolver;

    private DynamicDataSourceSpelParser dynamicDataSourceSpelParser;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
        	String dsKeyName = determineDatasource(invocation);
            determineDatasource(invocation);
        	HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        	String dsKey=StringUtils.isNotBlank(request.getHeader(dsKeyName))?request.getHeader(dsKeyName):(String)request.getSession().getAttribute(dsKeyName);

        	if(StringUtils.isNotBlank(dsKey)){
        		switchDatasource(dsKey);
                DynamicDataSourceContextHolder.setDataSourceLookupKey(dsKey);
        	}
            return invocation.proceed();
        } catch(Exception e){
        	e.printStackTrace();
        	return invocation.proceed();
        }finally {
            DynamicDataSourceContextHolder.clearDataSourceLookupKey();
        }
    }

    /**
     * 获取数据源key
     */
    private String determineDatasource(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        Object target = invocation.getThis();
        SAAS annotation = AnnotationUtils.findAnnotation(target.getClass(), SAAS.class);
        String key;
        try {
            SAAS ds = method.isAnnotationPresent(SAAS.class) ? method.getAnnotation(SAAS.class) : annotation;
            key = ds.value();
        } catch (Exception e) {
            key = annotation.value();
        }
        return key;
    }

    /**
     * 安装数据源
     */
    private void switchDatasource(String dsKey) {
    	Map<String,DataSource> dsMap=dynamicRoutingDataSource.getCurrentDataSources();
    	if(dsMap != null && dsMap.containsKey(dsKey)){
    		return;
    	}
    	//生成数据源
    	DataSource ds = dynamicDataSourceProvider.createDataSource(dsKey);
    	dynamicRoutingDataSource.addDataSource(dsKey, ds);
    }
}