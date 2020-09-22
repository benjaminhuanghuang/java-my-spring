package ben.study.mvcframework.v2.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DispatcherServlet extends HttpServlet {
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
        // 1. Load config file
        doLoadConifg();


        // 2. parse 
        doScanner();

        // 3. Initialize objects and save them to IOC container
        doInstance();

        // 4. Ioc
        doAutowired();

        // 5. Handler url -> method mapping
        doInitHandlerMapping();



        System.out.println("Spring Framework is ready...");
    }

    private void doInitHandlerMapping() {
    }

    private void doAutowired() {
    }

    private void doInstance() {
    }

    private void doScanner() {
    }

    private void doLoadConifg() {

    }
}
