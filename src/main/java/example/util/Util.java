package example.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import example.exception.AppException;


import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Util {

    private static final ObjectMapper M = new ObjectMapper(); // 一种数据模型转换框架
                                                              // 方便将模型对象转换为JSON
    private static final MysqlDataSource DS = new MysqlDataSource(); // 用来数据库连接的对象

    // 设置静态变量
    static {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 设置时间的标准格式
        M.setDateFormat(df);
        DS.setURL("jdbc:mysql://localhost:3306/java_chatroom"); // 数据库名字设置成自己的数据库（我提供的数据库名字叫做java_chatroom）
        DS.setUser("root"); // 设置mysql的用户名
        DS.setPassword("123456"); // 设置mysql的密码（用户名和密码设置自己本机的）
        DS.setUseSSL(false); // 当JDBC 比 mysql 版本不兼容(JDBC版本高于mysql兼容版本)设置为true
        DS.setCharacterEncoding("UTF-8"); // 防止中文乱码
    }

    /***
     * json序列化：java对象转化为json字符串
     *
     * json字符串就理解为前后端沟通常用的一种字符串格式
     */
    public static String serialize(Object o){
        try{
            return M.writeValueAsString(o);
        }catch (JsonProcessingException e){ // 注意异常的种类不要写错了
            throw new AppException("json序列化失败" + o, e);
        }
    }


    /***
     * json反序列化：json字符串转换为java对象
     */
    public static <T> T deserialize(String s, Class<T> c){
        // 这里我们用到了泛型，因为我们要转换成为的java对象并不固定
        // 比如我们要把json中的信息转换成用户对象；把另一个json中的信息转换成发送的消息对象
        // 所以这里用泛型来定义反序列化
        try{
            return M.readValue(s, c); // 注意这里不是readValues，我当时就没注意被这个s折磨了老久
        }catch (JsonProcessingException e){
            throw new AppException("json反序列化失败", e);
        }
    }

    // 为了满足输入是InputStream对象，我们重载（同一个类下，方法名一样，参数和返回值不一样）反序列方法
    public static <T> T deserialize(InputStream is, Class<T> c){
        try {
            return M.readValue(is, c);
        }catch (IOException e){
            throw new AppException("json反序列化失败", e);
        }
    }

    /**
     * 获取数据库链接
     * */
    public static Connection getConnection(){
        try{
            return DS.getConnection();
        }catch (SQLException e){
            throw new AppException("获取数据库连接失败", e);
        }
    }

    /**
     * 释放jdbc资源
     */
    public static void close(Connection c, Statement s, ResultSet r){
        try{
            if(r != null) r.close();
            if(s != null) s.close();
            if(c != null) c.close();
        }catch (SQLException e){
            throw new AppException("释放数据资源出错", e);
        }
    }
    public static void close(Connection c, Statement s){
        close(c, s, null);
    }

    // 以上我们就把一些常用工具写完了，这里可以写一个主函数测试一下
//    public static void main(String[] args){
//        // 测试一下json序列化
//        Map<String, Object> map = new HashMap<>();
//        map.put("ok", true);
//        map.put("d", new Date());
//
//        System.out.println(serialize(map));
//        // 运行后就可以看到，这里将使用map存放的键和值转化成了一个JSON字符串（用map的原因应该是有键值对儿的原因吧）
//
//        // 测试数据库链接，执行这步前，先把我提供的初始化数据库代码在cmd的mysql里面运行一下，保证自己本机有这个数据库
//        System.out.println(getConnection());
//    }
}
