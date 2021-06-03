package example.exception;

// 自定义我们的异常格式，这样做的目的是为了将系统后端出现的异常可以返回到前段
// 类似于对它进行一个标准化的格式吧，我是这么理解的
//      大概就是，这个异常类捕获系统异常后，输出自己想要输出的异常信息
// 这个算是个模板吧，感觉挺通用
public class AppException extends RuntimeException{
    public  AppException(String message){ // 只输出异常信息
        super(message);
    }

    public AppException(String message, Throwable cause){
        super(message, cause);  // 输出异常种类
    }
}
