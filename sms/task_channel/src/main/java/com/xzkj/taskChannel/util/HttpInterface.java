package com.xzkj.taskChannel.util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class HttpInterface {

	//private static Logger logger =LoggerFactory.getLogger(HttpInterface.class);
	
	public static String httpClientGet(String url,int soTimeOut,String charset,Hashtable<String, String> formHeaders){
		return doClientGet(url,soTimeOut,charset,formHeaders);
	}
	public static String httpClientPostBody(String url, String output,int soTimeOut,String charset){
		return doClientPost(url, output,null,null,soTimeOut,charset,null,"Body");
	}
	public static String httpClientPostEntity(String url, String output,int soTimeOut,String charset){
		return doClientPost(url, output,null,null,soTimeOut,charset,null,"Entity");
	}
	public static String httpClientPost(String url, String output,int soTimeOut,String charset,Hashtable<String, String> formHeaders){
		return doClientPost(url, output,null,null,soTimeOut,charset,formHeaders,"Entity");
	}
	public static String httpClientPost(String url,Map<String, String> paramMap,int soTimeOut,String charset,Hashtable<String, String> formHeaders){
		return doClientPost(url, null,paramMap,null,soTimeOut,charset,formHeaders,"Entity");
	}
	public static String httpClientPostByBody(String url,Map<String, String> paramMap,int soTimeOut,String charset){
		Hashtable<String, String> formHeaders =new Hashtable<String, String>();
		formHeaders.put("Content-Type","application/x-www-form-urlencoded;");
		return doClientPost(url, null,paramMap,null,soTimeOut,charset,formHeaders,"Body");
	}
	public static String httpClientPostByBody(String url,Map<String, String> paramMap,int soTimeOut,String charset,Hashtable<String, String> formHeaders){
		return doClientPost(url, null,paramMap,null,soTimeOut,charset,formHeaders,"Body");
	}
	public static String httpClientPostByBodyString(String url,String paramString,int soTimeOut,String charset,Hashtable<String, String> formHeaders){
		return doClientPost(url, null,null,paramString,soTimeOut,charset,formHeaders,"Body");
	}
	private static String doClientPost(String url, String output,Map<String, String> paramMap,String paramString,int soTimeOut,String charset,Hashtable<String, String> formHeaders,String requestFormat){
		PostMethod post = null;
		String result = "";
		try {
			HttpClient httpClient = new HttpClient();
			// 字符集
			httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, charset);
			// 超时时间
			httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(soTimeOut);
			httpClient.getHttpConnectionManager().getParams().setSoTimeout(soTimeOut);
			post = new PostMethod(url);
			post.setRequestHeader("Connection", "close");
			if (formHeaders != null) {
				for (String key : formHeaders.keySet()) {
					post.setRequestHeader(key, formHeaders.get(key));
				}
			}else{
				post.setRequestHeader("Content-Type", "application/json");
				post.setRequestHeader("Authorization", "Basic lxh");
			}
			if(StringUtils.isNotBlank(output)){
				StringRequestEntity stringRequest = new StringRequestEntity(output, null, charset);
				post.setRequestEntity(stringRequest);
			}
			if(StringUtils.isNotBlank(paramString)){
				post.setRequestEntity(new StringRequestEntity(paramString, formHeaders.containsKey("Content-Type")?formHeaders.get("Content-Type"):"application/json", charset));
			}
			if(paramMap!=null){
				if(requestFormat.equals("Entity")){
					List<StringPart> partArr = new ArrayList<StringPart>();
					for (Map.Entry<String, String> entry : paramMap.entrySet()) {  
						partArr.add(new StringPart(entry.getKey(),entry.getValue()));
					}
					post.setRequestEntity(new MultipartRequestEntity(partArr.toArray(new Part[]{}), post.getParams()));
				}
				if(requestFormat.equals("Body")){
					List<NameValuePair> paramsArr = new ArrayList<NameValuePair>();
					for (Map.Entry<String, String> entry : paramMap.entrySet()) {  
						NameValuePair nameValue=new NameValuePair(entry.getKey(),entry.getValue());
						paramsArr.add(nameValue);
					}
					post.setRequestBody(paramsArr.toArray(new NameValuePair[]{}));
				}
			}
			
			
			// 发送请求
			int statusCode = httpClient.executeMethod(post);
			//
			if (statusCode == HttpStatus.SC_OK) {
				// 方法一
				InputStream inputStream = post.getResponseBodyAsStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
				StringBuffer sb = new StringBuffer();
				String ts = "";
				while ((ts = br.readLine()) != null) {
					sb.append(ts).append("\n");
				}
				result = sb.toString().trim();
			}else{
				//logger.info("请求出错: "+statusCode);
				result = "http请求异常: "+statusCode;
			}
			
		} catch (Exception e) {
			//e.printStackTrace();
			result = "http请求异常："+e.getMessage();
		}finally{
			if(post != null){
				post.releaseConnection();
			}
		}
		return result;
	}
	private static String doClientGet(String url,int soTimeOut,String charset,Hashtable<String, String> formHeaders){
		GetMethod method = null;
		String result = "";
		try {
			HttpClient httpClient = new HttpClient();

			// 字符集
			httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, charset);
			// 超时时间
			httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(soTimeOut);
			httpClient.getHttpConnectionManager().getParams().setSoTimeout(soTimeOut);
			method = new GetMethod(url);
			method.setRequestHeader("Connection", "close");
			if (formHeaders != null) {
				for (String key : formHeaders.keySet()) {
					method.setRequestHeader(key, formHeaders.get(key));
				}
			}else{
				method.setRequestHeader("Content-Type", "application/json");
				method.setRequestHeader("Authorization", "Basic lxh");
			}
			// 发送请求
			int statusCode = httpClient.executeMethod(method);
			//
			if (statusCode == HttpStatus.SC_OK) {
				// 方法一
				InputStream inputStream = method.getResponseBodyAsStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, charset));
				StringBuffer sb = new StringBuffer();
				String ts = "";
				while ((ts = br.readLine()) != null) {
					sb.append(ts).append("\n");
				}
				result = sb.toString().trim();
			}else{
				//logger.info("请求出错: "+statusCode);
				method.abort();
				result = "请求异常: "+statusCode;
			}
			
		} catch (Exception e) {
			//e.printStackTrace();
			result = "http请求异常："+e.getMessage();
		}finally{
			if(method != null){
				method.releaseConnection();
			}
		}
		return result;
	}
	public static String doPostToFile(String url, Map<String, String> paramMap,int soTimeOut,String charset,Hashtable<String, String> formHeaders,String filePath){
		PostMethod post = null;
		String result = "";
		FileOutputStream fos=null;
		BufferedInputStream bis = null;
		try {
			HttpClient httpClient = new HttpClient();

			// 字符集
			httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, charset);
			// 超时时间
			httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(soTimeOut);
			httpClient.getHttpConnectionManager().getParams().setSoTimeout(soTimeOut);
			post = new PostMethod(url);
			if (formHeaders != null) {
				for (String key : formHeaders.keySet()) {
					post.setRequestHeader(key, formHeaders.get(key));
				}
			}else{
				post.setRequestHeader("Content-Type", "application/json");
				post.setRequestHeader("Authorization", "Basic lxh");
			}
			if(paramMap!=null){
				List<NameValuePair> paramsArr = new ArrayList<NameValuePair>();
				for (Map.Entry<String, String> entry : paramMap.entrySet()) {  
					NameValuePair nameValue=new NameValuePair(entry.getKey(),entry.getValue());
					paramsArr.add(nameValue);
				}
				post.setRequestBody(paramsArr.toArray(new NameValuePair[]{}));
			}
			
			// 发送请求
			int statusCode = httpClient.executeMethod(post);
			//
			if (statusCode == HttpStatus.SC_OK) {
				// 方法一
				InputStream inputStream = post.getResponseBodyAsStream();
				int data=0;
				byte[] byteArray = new byte[1024];
				fos=new FileOutputStream(new File(filePath));
				bis = new BufferedInputStream(inputStream, 2048);
		        while ((data = bis.read(byteArray)) != -1) {
		            fos.write(byteArray, 0, data);
		        }
		        fos.flush();
				result = "请求成功";
			}else{
				//logger.info("请求出错: "+statusCode);
				result = "请求出错: "+statusCode;
			}
			
		} catch (Exception e) {
			result = "http请求异常："+e.getMessage();
		}finally{
			if(fos!=null){
				try {
					fos.close();  
				} catch (IOException e) {
				}  
			}
			if(bis!=null){
				try {
					bis.close();
				} catch (IOException e) {
				}
			}
			if(post != null){
				post.releaseConnection();
			}
		}
		return result;
	}

	/*public static void main(String[] args) {
		String pin="10000001";
		String batch = "15811047193"+UUID.randomUUID().toString();
		String taskno = Md5Tools.getMD5(batch);
		String url = "http://cmbc.bigdataoasis.com/handler/data.ashx?method=Build&pin="+pin+"&taskno="+taskno+"&file=15811047193";
		Hashtable<String, String> formHeaders = new Hashtable<String, String>();
		//formHeaders.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		formHeaders.put("Content-Type", "multipart/form-data ");
		Hashtable<String, String> formfiles = new Hashtable<String, String>();
		formfiles.put("file", "D://123.csv");
		Hashtable<String, String> formvars = new Hashtable<String, String>();
		formvars.put("method", "Build");
		formvars.put("pin", pin);
		formvars.put("taskno", taskno);
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("method", "Build");
		paramMap.put("pin", pin);
		paramMap.put("taskno", taskno);
		String output = "?method=Build&pin="+pin+"&taskno="+taskno;
		//String str = HttpInterface.httpPostFile(url,output ,paramMap, 30000, formHeaders,formvars,formfiles, "UTF-8");
		String str = HttpInterface.httpPostFile(url,null ,null, 30000, formHeaders,formvars,formfiles, "UTF-8");
		
	}*/
}
