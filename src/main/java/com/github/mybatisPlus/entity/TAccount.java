package com.github.mybatisPlus.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.experimental.Accessors;

import java.io.Serializable;

@lombok.Data
@Accessors(chain = true)
@TableName(autoResultMap = true)
public class TAccount implements Serializable{

	/**姓名**/
	private String name;
	/**年龄**/
	private String age;

	//声明字段为非必要，排除映射
	/**
	 * 组id
	 */
	private static String providerId;


	@TableField(typeHandler = FastjsonTypeHandler.class)
	private JSONObject imageUrl;

}
