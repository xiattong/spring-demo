package com.xiattong.springframework.web.servlet;

import com.xiattong.springframework.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ：xiattong
 * @description：模板解析引擎
 * @version: $
 * @date ：Created in 2021/8/2 17:08
 * @modified By：
 */
public class XTView {

    private static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";

    private File viewFile;

    public XTView(File viewFile) {
        this.viewFile = viewFile;
    }

    public String getContentType() {
        return DEFAULT_CONTENT_TYPE;
    }

    /**
     * 渲染模板，返回可被浏览器识别的字符串
     * @param model
     * @param request
     * @param response
     * @throws Exception
     */
    public void render(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception{
        StringBuffer sb = new StringBuffer();
        RandomAccessFile ra = new RandomAccessFile(this.viewFile, "r");
        try{
            String line = null;
            while (null != (line = ra.readLine())) {
                line = new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                // [^…] 匹配除括号中的任意单个字符
                Pattern pattern = Pattern.compile("$\\{[^\\}]+\\}", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    String paramName = matcher.group();
                    paramName = paramName.replaceAll("$\\{|\\}","");
                    Object paramValue = model.get(paramName);
                    if (paramValue == null) {
                        continue;
                    }
                    line = matcher.replaceFirst(StringUtils.makeStringForRegExp(paramValue.toString()));
                    matcher = pattern.matcher(line);
                }
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ra.close();
        }
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(sb.toString());
    }
}
