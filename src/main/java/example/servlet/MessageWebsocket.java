package example.servlet;

import example.dao.MessageDAO;
import example.dao.UserDAO;
import example.model.Message;
import example.model.MessageCenter;
import example.util.Util;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;

@ServerEndpoint("/message/{userId}")
public class MessageWebsocket {

    // 建立连接
    @OnOpen
    public void onOpen(@PathParam("userId") Integer userId,
                       Session session) throws IOException {
        // 1、把每个客户端的session都保存起来，之后将消息转发到所有的客户端时要使用
        MessageCenter.addOnLinUser(userId, session);
        // 2、查询本客户端（用户）在上次登录之后，别人发送到服务器的消息（在数据库查）
        List<Message> list = MessageDAO.queryByLastLogout(userId);
        // 3、将这些查询到的消息发送给当前用户
        for (Message m: list) {
            // 将消息序列化为JSON字符串后发送
            session.getBasicRemote().sendText(Util.serialize(m));
        }
        System.out.println("建立连接" + userId);

    }

    // 服务器转发消息，并将消息存储在数据库中
    @OnMessage
    public void onMessage(Session session, String message){

        // 1、遍历所保存的所有session信息，对每个都发送消息
        MessageCenter.sendMessage(message);
        // 2、将消息保存在数据库中
        // （1） 反序列化json字符串为message对象
        Message msg = Util.deserialize(message, Message.class);
        // （2）插入数据库
        int n = MessageDAO.insert(msg);

        // 服务器显示一下接收到的消息
        System.out.printf("接收到消息：%s\n", message);


    }

    // 断开连接
    @OnClose
    public void onClose(@PathParam("userId") Integer userId){
        // 1、该客户端断开连接，要将在MessageCenter中保存的该客户端的session信息删除
        MessageCenter.delOnlinUser(userId);

        // 2、下次该用户如果建立连接时，需要收到在该用户下线的这段时间有其他客户端发送给该客户端的消息，
        // 所以要更新该用户最后下线时刻的时间
        int n = UserDAO.updateLastLogout(userId);
        System.out.println("关闭连接");
    }

    @OnError
    public void onError(@PathParam("userId") Integer userId, Throwable t){
        System.out.println("出错了");
        MessageCenter.delOnlinUser(userId);
        t.printStackTrace();
        //和关闭连接的操作一样
    }



}
