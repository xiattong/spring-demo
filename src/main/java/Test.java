import com.xiattong.springframework.utils.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ：xiattong
 * @description：
 * @version: $
 * @date ：Created in 2021/8/2 10:46
 * @modified By：
 */
public class Test {

    public void fun(String a, Integer b) {
        return;
    }

    public static void main(String[] args) {
        /*Test test = new Test();
        Method[] methods = test.getClass().getMethods();
        for (Method method : methods) {
            Parameter[] parameters = method.getParameters();
            System.out.println(parameters);
        }*/

        Pattern pattern = Pattern.compile("\\$\\{[^\\}]+\\}", Pattern.CASE_INSENSITIVE);
        String line = "    <h1>你好，${msg}</h1>";
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            String paramName = matcher.group();
            System.out.println(paramName);
            line = matcher.replaceFirst(StringUtils.makeStringForRegExp("xiatt"));
            System.out.println(line);
            matcher = pattern.matcher(line);
        }
    }
}
