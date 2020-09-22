package ben.study.mvcframework.v2.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.List;

public class DispatcherServlet extends HttpServlet {
    private Properties contextConfig = new Properties();
    private List<String> classNames = new ArrayList<>();

    // Ioc (Inversion of Control) Container
    private Map<String, Object> ioc = new HashMap<>();  //

    private Map<String, Method> handlerMapping = new HashMap<>();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        // 1. Load config file web.xml
        doLoadConfig(config.getInitParameter("contextConfigLocation"));


        // 2. parse 
        doScanner(contextConfig.getProperty("scanPackage"));

        // 3. Initialize objects and save them to IOC container
        doInstance();

        // 4. Ioc
        doAutowired();

        // 5. Handler url -> method mapping
        doInitHandlerMapping();


        System.out.println("Spring Framework is ready...");
    }

    private void doInitHandlerMapping() {
        if (ioc.isEmpty()) return;

        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if (clazz.isAnnotationPresent(MyController.class)) {
                for (Method method : clazz.getMethods()) {
                    if (method.isAnnotationPresent(MyRequestMapping.class)) {
                        continue;
                    }

                    MyRequestMapping requestMapping = method.getAnnotation(MyRequestMapping.class);
                    String url = requestMapping.value();
                    handlerMapping.put(url, method);

                    System.out.println("Mapped: " + url + ", " + method);
                }
            }
        }
    }

    private void doAutowired() {
        if (ioc.isEmpty()) return;

        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Field[] fields = entry.getValue().getClass().getDeclaredFields();

            for (Field field : fields) {
                if (field.isAnnotationPresent(MyAutowired.class)) {
                    MyAutowired autowired = field.getAnnotation(MyAutowired.class);
                    String beanName = autowired.value().trim();
                    if ("".equals(beanName)) {
                        beanName = field.getType().getName();
                    }
                    // private
                    field.setAccessible(true);
                    try {
                        field.set(entry.getValue(), ioc.get(beanName));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    private void doInstance() {
        if (this.classNames.isEmpty())
            return;

        try {
            for (String className : classNames) {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(MyController.class)) {
                    String benName = toLowerFirstCase(clazz.getSimpleName());
                    Object instance = clazz.newInstance();
                    ioc.put(benName, instance);
                } else if (clazz.isAnnotationPresent(MyService.class)) {
                    String benName = toLowerFirstCase(clazz.getSimpleName());

                    // 自定义命名
                    MyService service = clazz.getAnnotation(MyService.class);
                    if (!"".equals(service.value())) {
                        benName = service.value();
                    }
                    Object instance = clazz.newInstance();
                    ioc.put(benName, instance);

                    // is interface
                    for (Class<?> i : clazz.getInterfaces()) {
                        if (ioc.containsKey(i.getName())) {
                            throw new Exception("The beanName is existed");
                        }
                        ioc.put(i.getName(), instance);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String ClassName : classNames) {

        }
    }

    /*

     */
    private void doScanner(String scanPackage) {
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (file.getName().endsWith(".class")) {
                    String className = file.getName();
                    classNames.add(className);
                }
            }
        }

    }

    private void doLoadConfig(String contextConfigLocation) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
        try {
            contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32; //to lowercase;
        return String.valueOf(chars);
    }
}
