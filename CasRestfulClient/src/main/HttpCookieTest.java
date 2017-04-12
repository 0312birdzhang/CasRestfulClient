package main;

import java.io.IOException;
import java.util.List;

import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * @author debo.zhang
 *
 */
public class HttpCookieTest {

	private static final String CAS_URL = "https:/cas-server.com/v1/tickets";
	private static final String REST_BASE_URL = "http://example.service.com"; 
	
	public static void main(String args[]){
		HttpClient http = null;
		CookieStore httpCookieStore = new BasicCookieStore();
		HttpClientBuilder builder = HttpClientBuilder.create().setDefaultCookieStore(httpCookieStore);
		http = builder.build();
		
		String username ="user";
        String password ="name";
        try {
			String ticket = new CasLogin(username, password,CAS_URL).getServiceTicket(REST_BASE_URL);
			
			HttpGet httpRequest = new HttpGet(REST_BASE_URL+"/index.php?ticket="+ticket);
			
			HttpResponse httpResponse = http.execute(httpRequest);
			List<Cookie> cookies = httpCookieStore.getCookies();
			httpCookieStore.addCookie(cookies.get(0));
	        String chartURL = REST_BASE_URL + "/test.php";
	        CloseableHttpClient client = HttpClients.custom()
	                .setDefaultCookieStore(httpCookieStore).build();  
	        HttpGet httpGet = new HttpGet(chartURL);  
	        System.out.println("request line:" + httpGet.getRequestLine());  
	        try {  
	            // 执行get请求  
	            HttpResponse response = client.execute(httpGet);  
	            printResponse(response);  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        } finally {  
	            try {  
	                // 关闭流并释放资源  
	                client.close();  
	            } catch (IOException e) {  
	                e.printStackTrace();  
	            }  
	        }  
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	public static void printResponse(HttpResponse httpResponse)  
            throws ParseException, IOException {
        // 获取响应消息实体  
        HttpEntity entity = httpResponse.getEntity();  
        // 响应状态  
        System.out.println("status:" + httpResponse.getStatusLine());  
        System.out.println("headers:");  
        HeaderIterator iterator = httpResponse.headerIterator();  
        while (iterator.hasNext()) {  
            System.out.println("\t" + iterator.next());  
        }  
        // 判断响应实体是否为空  
        if (entity != null) {  
            String responseString = EntityUtils.toString(entity);  
            System.out.println("response length:" + responseString.length());  
            System.out.println("response content:"  
                    + responseString.replace("\r\n", ""));  
        }  
    }
}
