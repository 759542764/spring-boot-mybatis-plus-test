package com.github.mybatisPlus.entity;

import java.io.Serializable;

@lombok.Data
public class TAccount implements Serializable{

	/**姓名**/
	private String name;
	/**年龄**/
	private String age;
	private String providerId;
}
