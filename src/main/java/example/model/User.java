package example.model;

// 因为我们这里在File -> Settings -> Plugins -> 中安装了Lombok（没有安装的，在里面搜索Lombok然后点击install，安装完重启idea就行）
// 所以可以直接简写私有变量的get，set和toString方法

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString

public class User extends Response implements Serializable {
    // 因为数据库使用、前后端ajax、Session保存是基于对象和二进制的数据转换，所以要实现串行化接口

    private  static  final  Long serialVersionUID = 1L;
    // serialVersionUID 的作用主要是验证传来的字节流中seriaVersionUID与本地响应实体类的serialVersionUID进行比较
    // 如果相同说明是一致，就可以进行反序列化（这个概念我也不是很理解，大概就是个安全验证的意思吧）
    // 1L就是默认生成  serialVersionUID 的方式


    // 这里要根据数据库中来定义
    // 数据库中 用户表 有多少个 属性
    // 在 用户的模板类中 就有多少个 成员变量
    // 要一一对应起来
    private Integer userId;   // 用户Id
    private String  name;     // 用户名(账号)
    private String  password; // 用户密码
    private String  nickName; // 昵称
    private String  iconPath; // 头像路径（这个属性本项目不用）
    private String signature; // 个性签名
    private java.util.Date lastLogout; // 用户最后一次登陆的时间（记录的是用户下线的时间点）
}
