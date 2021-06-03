package example.dao;

import example.exception.AppException;
import example.model.Channel;
import example.model.Response;
import example.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ChannelDAO {
    public static List<Channel> query() {
        // 定义查询数据库要用的对象
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        // 定义存放最终放回的数据的列表
        List<Channel> list = new ArrayList<>(); // 返回的对象都是频道对象
        try {
            // 1、获取数据库连接Connection
            c = Util.getConnection();

            // 2、通过Connection + sql 操作命令对象Statement
            String sql = "select * from channel";
            ps = c.prepareStatement(sql);

            // 3、执行sql：执行前 替换占位符
            rs = ps.executeQuery();

            // 4、如果是查询操作，需要处理结果集
            while(rs.next()){ // 移动到下一行，有数据返回true
                Channel channel = new Channel(); // 在返回的结果List中是一个个的Channel对象
                // 设置属性
                channel.setChannelId(rs.getInt("channelId")); // 要与数据表中的属性对应
                channel.setChannelName(rs.getString("channelName"));
                list.add(channel); // 将频道信息添加进list中
            }
            return list;

        }catch (Exception e){
            throw new AppException("查询频道出错", e); //我们自定义的异常输出方法
        }finally {
            // 5、释放资源
            Util.close(c,ps,rs);
        }

    }
}
