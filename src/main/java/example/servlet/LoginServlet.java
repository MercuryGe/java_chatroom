package example.servlet;
import example.dao.UserDAO;
import example.exception.AppException;
import example.model.User;
import example.util.Util;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    // 第二步编写：检测登陆状态接口，主要是在页面初始化时执行
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");  // 请求编码格式
        resp.setCharacterEncoding("UTF-8"); // 响应编码格式
        resp.setContentType("application/json"); // 前后端是以json字符串的格式传递的

        // 返回给前端的还是user对象的用户信息
        User user = new User();

        // 获取当前请求的Session，并从中获取用户信息，如果获取不到，返回 ok 为false
        HttpSession session = req.getSession(false);// false的意思就是如果没有获取到session信息就不创建新的session
        if (session != null) {
            User get = (User) session.getAttribute("user"); //这里的"user"和登陆下面的setAttribute是对应的
            if (get != null) {
                // 说明已经登陆了
                // 设置返回个前端的参数
                user = get;
                user.setOk(true);
                resp.getWriter().println(Util.serialize(user)); // 返回响应数据
                return;
            }
        }
        // 没有获取到session或者用户信息
        user.setOk(false); // 其实默认就是false
        user.setReason("用户未登录");
        // 返回响应数据：从响应对象获取数据流，打印输出响应体body
        resp.getWriter().println(Util.serialize(user));
    }



    // 第一步编写：登陆接口
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8"); // 设置请求的编码格式
        resp.setCharacterEncoding("UTF-8"); // 设置响应的编码格式
        resp.setContentType("application/json"); // 前后端是以json字符串的格式传递的

        // 根据前端的请求（输入的账号密码）要去数据库中查询是否有与之匹配的信息
        User user = new User(); // 用于返回响应给前端
        try{
            // 1、解析请求数据，根据接口文档，需要使用反序列化操作
            //          将前端请求信息反序列化为用户对象
            User input = Util.deserialize(req.getInputStream(), User.class); // 反序列化为用户类对象

            // 2、业务处理：去数据库验证账号密码，如果验证通过，保存用户信息于Session中
            //      首先根据账号查询是否有此用户
            User query = UserDAO.queryByName(input.getName());
            if(query == null){
                // 没有在用户表中查询到
                throw  new AppException("用户不存在");
            }
            if(!query.getPassword().equals(input.getPassword())){
                // 从数据库中拿到该用户名的密码和前端输入的密码不一致
                throw new AppException("账号或密码错误");
            }
            // 能执行到这里，说明验证通过了
            // 在session中保存用户信息
            HttpSession session = req.getSession(); // 根据请求拿到session，如果没有，就创建一个session，默认是true
            session.setAttribute("user", query); // 以"user"作为名字，保存用户信息

            user = query; //将查询到的用户信息传给要返回给前端的user
            // 设置返回参数，成功返回了，就设置ok为true
            user.setOk(true);
        }catch (Exception e){
            e.printStackTrace();
            // 构造返回给前端响应失败了，就设置ok为false
            user.setOk(false);

            // 这里就体现出了自定义异常的好处，我们给前端返回我们想要返回的内容
            if(e instanceof AppException){
                user.setReason(e.getMessage());
            }else{
                // 如果出现了非自定义异常的情况，可以不报英文，报我们给定的内容
                user.setReason("未知错误，请联系管理员");
            }
        }
        // 3、返回相应数据：从响应对象中获取输出流（序列化为JSON字符串），打印输出到响应体body
         resp.getWriter().println(Util.serialize(user));

    }
}
