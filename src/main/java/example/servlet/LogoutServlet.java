package example.servlet;

import example.model.Response;
import example.model.User;
import example.util.Util;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 惯例开头先设置格式
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");

        // 从浏览器中获取Session信息
        HttpSession session = req.getSession(false); // false 表示，如果没有获取到Session 则不创建新的
                                                    // 默认为true 表示，没有获取到就创建新的Session
        if(session != null) {
            // 如果session不为空
            // 根据session中的信息，user是我们在登陆的时候设置到session中的字段，根据它中的信息，以User为模板创建user对象
            User user = (User) session.getAttribute("user");
            if (user != null) {
                // 用户已登陆，要实现注销，就要删除session中保存的用户信息
                session.removeAttribute("user");
                // 注销成功，根据开发手册，返回OK为true
                Response r = new Response();
                r.setOk(true);
                // 返回相应数据：从响应对象中获取输出流（序列化为JSON字符串），打印输出到响应体body
                resp.getWriter().println(Util.serialize(r));
                return;
            }
        }

        // 用户未登录
        Response r = new Response();
        r.setReason("用户未登录，不允许访问");
        resp.getWriter().println(Util.serialize(r));
    }
}
