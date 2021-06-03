package example.model;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

public class MessageCenter {

    /**
     *  ConcurrentHashMap：支持线程安全的map结构，并且满足高并发（读写，读读并发，写写互斥）
     * **/
    private static final ConcurrentHashMap<Integer, Session> clients = new ConcurrentHashMap<>();

    /**
     *  阻塞队列，用来存放消息，接受客户端的消息放进队列；
     *
     *  再启动一个线程，不停的拉去队列中的消息，发送
     * **/
    private static BlockingDeque<String> queue = new LinkedBlockingDeque<>();

    // 定义类
    private static MessageCenter center;

    // 构造方法
    private MessageCenter(){}

    // 启动一个线程，以单例模式，在该线程中，不停的从阻塞队列拿取数据
//    public static  MessageCenter getInstance(){
//        if(center == null){ // 单例模式
//
//            center = new MessageCenter();
//            new Thread(()->{  //
//                try {
//                    String message = queue.take() ; // 获取数据，如果队列为空，阻塞等待
//                    sendMessage(message);
//                }catch (InterruptedException e){
//                    e.printStackTrace();
//                }
//            }).start();
//        }
//        return center;
//    }

    /**
     * 不直接发送消息，先将消息存放在队列中，由另一个线程去发送消息
     * **/
    public void addMessage(String message){
        queue.add(message);
    }

    /**
     *  WebSocket建立连接时，添加用户id和客户端session，并保存起来
     * **/
    public static void  addOnLinUser(Integer userId, Session session){
        clients.put(userId, session);
    }

    /**
     *  关闭websocket连接、或出错时，删除客户端的session
     * **/
    public static void delOnlinUser(Integer userId){
        clients.remove(userId);
    }

    /**
     * 接收到某用户的消息时，转发到所有客户端：
     * **/
    public static void sendMessage(String message){
        try{
            Enumeration<Session> e = clients.elements();
            while(e.hasMoreElements()){ //  遍历每个用户的session
                Session session = e.nextElement();
                session.getBasicRemote().sendText(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
