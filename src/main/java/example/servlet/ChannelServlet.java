package example.servlet;

import example.dao.ChannelDAO;
import example.model.Channel;
import example.model.Response;
import example.util.Util;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/channel")
public class ChannelServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1、设置请求响应格式
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");

        // 2、实现业务逻辑
        // 目标是从数据库中查询到频道信息，将频道信息返回给前端
        Response response = new Response(); // 将内容以我们定义的响应格式传输
        try {
            // 查询所有频道，以列表的形式返回
            List<Channel> List = ChannelDAO.query();
            response.setOk(true); // 查询成功，设置输出给前端的ok为true
            response.setData(List); // 将查询到的频道信息列表返回给前端
        }catch (Exception e){
            e.printStackTrace();
            response.setReason(e.getMessage()); // 查询失败，返回错误信息
        }
        // 3、返回相应数据：从响应对象中获取输出流（序列化为JSON字符串），打印输出到响应体body
        resp.getWriter().println(Util.serialize(response));


    }
}
