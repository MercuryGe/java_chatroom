package example.dao;
import example.model.Message;
import example.exception.AppException;
import example.util.Util;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    // 根据用户id查询该用户最后一次下线之后，服务器数据库中接收到的消息（当前用户应该接受到的消息）
    public static List<Message> queryByLastLogout(Integer userId) {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        // 定义返回数据
        List<Message> list = new ArrayList<>(); // 返回的是一个消息列表，包含了用户下线这个时间段内所有的消息
        try{
            // 1、获取数据库链接
            c = Util.getConnection();

            // 2、通过Connection + sql 创建操作命令对象 Statement
            String sql = "select m.*,u.nickName from message m join user u on u.userId=m.userId where m.sendTime>(select lastLogout from user where userId=?)";
            // 该sql语句的意思为：以message表中发送时间大于use表中该用户下线时间为条件，联合用户表和消息表查询message表的所有信息与user表中的用户昵称
            ps = c.prepareStatement(sql);

            // 3、执行sql，执行前替换占位符
            ps.setInt(1,userId);
            rs = ps.executeQuery();

            // 4、对于查询操作，需要处理结果及
            while(rs.next()){ // 看下一行是否有数据，有数据则为true，进入循环
                // 获取结果集字段，设置所需要的对象属性
               Message m = new Message();
               m.setUserId(userId);
               m.setNickName(rs.getString("nickName"));
               m.setContent(rs.getString("content"));
               m.setChannelId(rs.getInt("channelId"));
               list.add(m);
            }
            return list;


        } catch (Exception e) {
            throw new AppException("查询用户[" + userId + "]的消息出错");
        } finally {
            Util.close(c, ps, rs);
        }
    }

    public static int insert(Message msg) {
        Connection c = null;
        PreparedStatement ps = null;
        try{
            c = Util.getConnection();
            String sql = "insert into message values(null, ?, ?, ?, ?)";
            ps = c.prepareStatement(sql);
            ps.setInt(1,msg.getUserId());
            ps.setInt(2,msg.getChannelId());
            ps.setString(3, msg.getContent());
            ps.setTimestamp(4,new Timestamp(System.currentTimeMillis()));
            return ps.executeUpdate();
        }catch (Exception e){
            throw new AppException("保存消息出错", e);
        }finally {
            Util.close(c, ps);
        }
    }
}
