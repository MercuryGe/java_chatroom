package example.dao;

import example.exception.AppException;
import example.model.User;
import example.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;

public class UserDAO {


    /**
     * 根据用户名查询数据表中的用户
     * */
    public static User queryByName(String name) {
        // 先定义我们肯定要用的对象
        Connection connection = null; // 用于连接数据库
        PreparedStatement preparedStatement = null; // 用户sql注入，就是在写sql语句是可以使用占位符
        ResultSet resultSet = null; // 查询的结果集

        // 先定义返回数据，根据技术文档要返回给前端的是一个用户类型的对象
        User user = null;

        try{
            // 1、获取数据库连接
            connection = Util.getConnection(); // 调用先前在工具类中写的连接数据库地方法

            // 2、通过Connection + sql 创建操作命令对象Statement
            String sql = "select * from user where name=?";
            preparedStatement = connection.prepareStatement(sql);

            // 3、执行sql：执行前替换占位符
            preparedStatement.setString(1, name);
            resultSet = preparedStatement.executeQuery(); // 存放查询后的结果集

            // 4、如果是查询操作，处理结果集
            while(resultSet.next()){
                user = new User();
                // 设置结果集字段到用户对象的属性中
                user.setUserId(resultSet.getInt("userId")); // 注意这个“userId”要和表中的属性名相同
                user.setName(name);
                user.setPassword(resultSet.getString("password"));
                user.setNickName(resultSet.getString("nickName"));
                user.setIconPath(resultSet.getString("iconPath"));
                user.setSignature(resultSet.getString("signature"));
                java.sql.Timestamp lastLogout = resultSet.getTimestamp("lastLogout"); // 得到从1970年到今的毫秒数
                user.setLastLogout(new Date(lastLogout.getTime())); // 根据毫秒数拿到年月日时分秒
            }
            return user;
        }catch (Exception e){
            throw new AppException("查询用户账号出错", e);
        }finally {
            // 5、无论如何都要释放资源
            Util.close(connection, preparedStatement, resultSet);
        }
    }

    public static int updateLastLogout(Integer userId) {
        Connection c = null;
        PreparedStatement ps = null;
        try{
            c = Util.getConnection();
            String sql = "update user set lastLogout=? where userId=?";
            ps = c.prepareStatement(sql);
            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.setInt(2, userId);
            return ps.executeUpdate();
        }catch (Exception e){
            throw new AppException("修改用户上次登录时间出错", e);
        }finally {
            Util.close(c, ps);
        }
    }
}
