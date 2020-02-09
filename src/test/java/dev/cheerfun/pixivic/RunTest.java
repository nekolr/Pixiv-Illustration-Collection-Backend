package dev.cheerfun.pixivic;


/**
 * @author echo huang
 * @version 1.0
 * @date 2019-06-28 23:05
 * @description
 */
public class RunTest {
    public static void main(String[] args) {
        String body="callback( {\"client_id\":\"101728546\",\"openid\":\"598B1979A33F36A43EC83B1CF3EC86C6\"} );";
        int i = body.indexOf("\"openid\":\"");
        System.out.println(body.substring(i + 10, i + 42));
    }

}
