package com.xiattong.demo.action;

import com.xiattong.demo.service.IDemoService;
import com.xiattong.springframework.annotation.XTAutowired;
import com.xiattong.springframework.annotation.XTController;
import com.xiattong.springframework.annotation.XTRequestMapping;
import com.xiattong.springframework.annotation.XTRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ：xiattong
 * @description：业务控制类
 * @version: $
 * @date ：Created in 2021/6/30 17:53
 * @modified By：
 */
@XTController
@XTRequestMapping("/action")
public class DemoAction {

    @XTAutowired
    private IDemoService demoService;

    @XTRequestMapping("/query")
    public String query(HttpServletRequest request, HttpServletResponse response,
                      @XTRequestParam("name") String name,
                      @XTRequestParam("age") Integer age) {
        String result = demoService.get(name, age);
        try {
            response.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "haha!";
    }
}
