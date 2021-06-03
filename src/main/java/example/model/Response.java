package example.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 前后端接口需要的统一字段
 */

// 因为我们这里在File -> Settings -> Plugins -> 中安装了Lombok（没有安装的，在里面搜索Lombok然后点击install，安装完重启idea就行）
// 所以可以直接简写私有变量的get，set和toString方法

@Getter
@Setter
@ToString

public class Response {

    // 当前借口响应是否操作成功
    private boolean ok; // 默认为false

    // 操作失败是，前端要展示的错误信息
    private String reason;

    // 保存要返回给前端的业务数据
    private Object data;
}
