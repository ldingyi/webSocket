package webSocket;

import com.google.gson.Gson;
import util.Data;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ServerEndpoint("/websocket/chat/{id}")//注解，作为客户端。多例
public class Websocket {
    private static int onlineCount = 0;//连入的人数
    private static Map<String, Session> map = new HashMap<>();//用于将用户id和socket id关联
    private String id;//当前用户socket id：

    @OnOpen
    public void connect(Session session, @PathParam("id") String id) {
        /**
         * @author: ldy
         * @Description: 有用户连接的时候执行此方法
         * @Data: 2019/11/5 20:20
         * @param: [session, id]
         * @return: void
         */
        this.id = id;
        map.put(id, session);
        addOnlineCount();
        broadcast(id + "上线了" + "共" + getOnlineCount());//有人连接时 全局广播了一下
    }

    @OnMessage
    public void echoMessage(String data) {
        /**
         * @author: ldy
         * @Description: 客户端发送消息时执行，接收客户端发送的消息,然后再发给客户端。这里可以添加一些业务，比如过滤，加入数据库。
         * @Data: 2019/11/5 20:27
         * @param: [data]客户端发来的消息   这个例子中的data是一个json对象，model为util中的data
         * @return: void
         */
        Gson gson = new Gson();//使用了gson转换数据
        Data data1 = gson.fromJson(data, Data.class);
        String text = data1.getText();

        /*
         * 进行判断,若是单发，找到需要发的session，发送
         * 若是群发，调用broadcast.
         * */

        if (data1.getIsAll() == 0) {
            broadcast(id + "说" + text);
        } else {
            for (String s : map.keySet()) {
                if (s.equals(data1.getUser())) {
                    try {
                        map.get(s).getBasicRemote().sendText(id + "对你说" + data1.getText());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @OnClose
    public void close() {
        /**
         * @author: ldy
         * @Description: 关闭连接的时执行的方法
         * @Data: 2019/11/5 20:30
         * @param: []
         * @return: void
         */
        map.remove(id);//从关联中去掉
        subOnlineCount();
        broadcast(id + "离线了" + "共" + getOnlineCount());//这里通知了一下
    }

    @OnError
    public void onerror(Session session, Throwable throwable) {
        /**
         * @author: ldy
         * @Description: 有的浏览器直接关闭会报错
         * @Data: 2019/11/5 20:29
         * @param: [session, throwable]
         * @return: void
         */
        System.out.println("有个人强制下线了");
    }

    public void broadcast(String text) {
        /**
         * @author: ldy
         * @Description: 遍历map 得到所有session，并发送消息
         * @Data: 2019/11/5 20:28
         * @param: [text]
         * @return: void
         */

        for (String s : map.keySet()) {
            try {
                map.get(s).getBasicRemote().sendText(text);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        Websocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        Websocket.onlineCount--;
    }


}
