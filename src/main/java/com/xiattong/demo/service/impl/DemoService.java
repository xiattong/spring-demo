package com.xiattong.demo.service.impl;


import com.xiattong.demo.service.IDemoService;
import com.xiattong.springframework.annotation.XTService;

/**
 * @author ：xiattong
 * @description：业务服务接口实现
 *
 * @version: $
 * @date ：Created in 2021/6/30 17:12
 * @modified By：
 */
@XTService
public class DemoService implements IDemoService {
	public String get(String name, Integer age) {
		return "My name is " + name +","+ age + "from service.";
	}
}
