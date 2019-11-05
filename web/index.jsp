<%--
  Created by IntelliJ IDEA.
  User: ldy
  Date: 2019/11/5
  Time: 13:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>$Title$</title>
    <script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
  </head>
  <body>
  <div id="login">
    <input type="text" name="name" id="name">

    <button onclick="login()">登录</button>
  </div>


  <div id="list">
    <ul id="listUl">

    </ul>
  </div>

  <div id="chat">
    <input type="text" id="member">
    <input type="checkbox" id="all"> 群发
    <textarea id="text"></textarea>
    <button onclick="echo()">send</button>
  </div>

  </body>

  <script>
    var ws;//ws对象
    var name ;
    var target = "ws://localhost/websocketTest_war_exploded//websocket/chat/";//Java服务端，这里直接用name模拟id



    function login() {
      name = $("#name").val();


      $.ajax({
        type:'post',
        url:'login',
        data:'name='+name,
        success:function (result) {
          $("#login").css("display","none");
          var id = result;
          target = target+id;
          connect();
        }
      })


    }


  /*
  * 连接客户端，并且注册onmessage：接收服务端的消息
  *                 close：关闭(服务端关闭)
  * */
    function connect() {
      if ('WebSocket' in window) {
        ws = new WebSocket(target);
      } else if ('MozWebSocket' in window) {
        ws = new MozWebSocket(target);
      } else {
        alert('WebSocket is not supported by this browser.');
        return;
      }
      ws.onmessage = function (event) {
        alert(event.data)
      }

      ws.close = function () {

      }



    }


    function echo() {
      var text = $("#text").val();
      var datas = {
        text:text,
        isAll:$("#all").prop("checked")?0:1,
        user:$("#member").val()
      };
      data = JSON.stringify(datas)
      ws.send(data);//send方法向服务器发送消息
    }




  </script>
</html>
