package it.chusen.hyg.tools;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.zip.GZIPInputStream;

/**
 * @Auther: 楚森
 * @Description:
 * @Company: 枣庄学院
 * @Date: 2018/7/31 23:34
 * @Version: 1.0
 */
public class ProvinceTools implements Serializable {

    /**
     *解析城市
     * @param cityName
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String processProvince(String cityName ) throws UnsupportedEncodingException {
        String city= URLEncoder.encode(cityName,"UTF-8");

        String appid="1f90abf37a4f648119df05bb8b4d1793";

        String httpUrl = "https://api.shenjian.io/";

        String httpArg = "appid="+appid+"&city="+city;

        String jsonResult = request(httpUrl, httpArg);

//        System.out.println(jsonResult);
        return jsonResult;
    }


    public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println(processProvince("菏泽市"));//荷泽市

    }

    /**


     * :请求接口

     * @param httpArg

     * :参数

     * @return 返回结果

     */

    public static String request(String httpUrl, String httpArg) {

        BufferedReader reader = null;

        String result = null;

        StringBuffer sbf = new StringBuffer();

        httpUrl = httpUrl + "?" + httpArg;

        try {

            URL url = new URL(httpUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            connection.setRequestProperty("charset", "utf-8");

            connection.setRequestProperty("Accept-Encoding", "gzip");

            connection.connect();

            InputStream is = new GZIPInputStream(connection.getInputStream());

            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            String strRead = null;

            while ((strRead = reader.readLine()) != null) {

                sbf.append(strRead);

                sbf.append("\r\n");

            }

            reader.close();

            result = sbf.toString();

        } catch (Exception e) {

            e.printStackTrace();

        }

        return result;

    }


}
