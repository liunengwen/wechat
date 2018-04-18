package com.newland.wechat.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.newland.wechat.common.Constants;
import com.newland.wechat.common.PropertyPlaceholder;
import com.newland.wechat.emun.ExceptionCodeEnum;
import com.newland.wechat.model.response.ResponseModel;
import com.newland.wechat.service.WeixinAuthorizeService;
@Component
 public class WeChatApiUtil {
    private static  Logger logger = LoggerFactory.getLogger(WeChatApiUtil.class);
   @Autowired
   private WeixinAuthorizeService weixinAuthorizeService;
    /**
     * 微信服务器素材上传
     * @param file  表单名称media
     * @param token access_token
     * @param type  type只支持四种类型素材(video/image/voice/thumb)
     * @throws IOException 
     * @throws IllegalStateException 
     */
    public  String uploadMedia(String fileUploadPath, String accessToken) throws IllegalStateException, IOException {
    	//获取文件的原名字
	/*	String fileUploadPath = getFilePath(fileUpload.getOriginalFilename());
		fileUploadPath =  StringUtils.replace(fileUploadPath,"\\", "/");
		File fileUploadFile = new File(fileUploadPath);
		//文件写入磁盘
		fileUpload.transferTo(fileUploadFile);*/
    	//localhost
    	//fileUploadPath =  StringUtils.replace(fileUploadPath,"/", "\\");
    	String imgUrl = "";
    	
    	File file = new File(fileUploadPath);
    	ResponseModel resp = new ResponseModel();
        if(file==null||accessToken==null){
            return null;
        }

        if(!file.exists()){
            logger.info("上传文件不存在,请检查!");
            return null;
        }
        //appId = "wx199eb1c6cfbd735a";
        /*String access_token = weixinAuthorizeService.getAccessTokenByAppIdAndMecId(appId);
        if(StringUtils.isBlank(access_token)){
			return new ResponseModel(ExceptionCodeEnum.INVALID_AUTHORIZE.getCode(),ExceptionCodeEnum.INVALID_AUTHORIZE.getLabel());
		}*/
        String url = PropertyUtils.getUploadMediaUrl(accessToken);
        JSONObject jsonObject = null;
        PostMethod post = new PostMethod(url);
        post.setRequestHeader("Connection", "Keep-Alive");
        post.setRequestHeader("Cache-Control", "no-cache");
        FilePart media = null;
        HttpClient httpClient = new HttpClient();
       // String mediaId = "";
        String getUrl = "";
        //信任任何类型的证书
        Protocol myhttps = new Protocol("https", new MySSLProtocolSocketFactory(), 443); 
        Protocol.registerProtocol("https", myhttps);
        try {
            media = new FilePart("media", file);
            Part[] parts = new Part[] { new StringPart("access_token", accessToken),
                    new StringPart("type", "image"), media };
            MultipartRequestEntity entity = new MultipartRequestEntity(parts,
                    post.getParams());
            post.setRequestEntity(entity);
            int status = httpClient.executeMethod(post);
           
            if (status == HttpStatus.SC_OK) {
                String text = post.getResponseBodyAsString();
                logger.info("------上传图片到微信服务器返回值   text:{}------" ,text);
                jsonObject = JSONObject.parseObject(text);
                getUrl = jsonObject.getString("url");
                //getUrl = PropertyUtils.getDownloadMediaUrl(accessToken, mediaId);
                //Map<String, Object> data = new HashMap<String, Object>();
                imgUrl= getUrl;
            } else {
                logger.info("upload Media failure status is:" + status);
            }
        } catch (FileNotFoundException execption) {
            logger.error(execption.toString());
        } catch (HttpException execption) {
            logger.error(execption.toString());
        } catch (IOException execption) {
            logger.error(execption.toString());
        }
        return imgUrl;
    }

    /**
     * 多媒体下载接口
     * @comment 不支持视频文件的下载
     * @param fileName  素材存储文件路径
     * @param token     认证token
     * @param mediaId   素材ID（对应上传后获取到的ID）
     * @return 素材文件
     */
    public  File downloadMedia(String fileName, String token,
            String mediaId) {
        String url = PropertyUtils.getDownloadMediaUrl(token, mediaId);
        return httpRequestToFile(fileName, url, "GET", null);
    }

    /**
     * 以http方式发送请求,并将请求响应内容输出到文件
     * @param path    请求路径
     * @param method  请求方法
     * @param body    请求数据
     * @return 返回响应的存储到文件
     */
    public  File httpRequestToFile(String fileName,String path, String method, String body) {
        if(fileName==null||path==null||method==null){
            return null;
        }

        File file = null;
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        FileOutputStream fileOut = null;
        try {
            URL url = new URL(path);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod(method);
            if (null != body) {
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(body.getBytes("UTF-8"));
                outputStream.close();
            }

            inputStream = conn.getInputStream();
            if(inputStream!=null){
                file = new File(fileName);
            }else{
                return file;
            }

            //写入到文件
            fileOut = new FileOutputStream(file);
            if(fileOut!=null){
                int c = inputStream.read();
                while(c!=-1){
                    fileOut.write(c);
                    c = inputStream.read();
                }
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }finally{
            if(conn!=null){
                conn.disconnect();
            }

            /*
             * 必须关闭文件流
             * 否则JDK运行时，文件被占用其他进程无法访问
             */
            try {
                inputStream.close();
                fileOut.close();
            } catch (IOException execption) {
                logger.error(execption.toString());
            }
        }
        return file;
    }
    /**
	 * 
	 * 生成新的文件名
	 * */
	private String getFilePath(String sourceFileName) {
		String baseFolder =  /*"f:"+*/File.separator + "downloads" + File.separator + "wechat"+ File.separator + "logo";
		Date nowDate = new Date();
		// yyyy/MM/
		String fileFolder = baseFolder + File.separator + new SimpleDateFormat("yyyy").format(nowDate) + File.separator + new SimpleDateFormat("MM").format(nowDate);
		File file = new File(fileFolder);
		if (!file.isDirectory()) {
			// 如果目录不存在，则创建目录
			file.mkdirs();
		}
		// 生成新的文件名
		String fileName = new SimpleDateFormat("yyyyMMddhhmmssSSSS").format(nowDate) + RandomUtils.nextInt(100, 9999) + "." + StringUtils.substringAfterLast(sourceFileName, ".");
		return fileFolder + File.separator + fileName;
	}
	  public  ResponseModel uploadMediaToService(MultipartFile fileUpload, String appId) throws IllegalStateException, IOException {
	//获取文件的原名字
			String fileUploadPath = getFilePath(fileUpload.getOriginalFilename());
			//fileUploadPath =  StringUtils.replace(fileUploadPath,"\\", "/");
			File fileUploadFile = new File(fileUploadPath);
			//文件写入磁盘
			fileUpload.transferTo(fileUploadFile);
			Map<String, Object> data = new HashMap<String, Object>();
			StringUtils.replace(fileUploadPath,"/", "\\");
			data.put("imgUrl", fileUploadPath);
			return new ResponseModel(Constants.SUCCESS_CODE,Constants.SUCCESS_MSG,data);
	  }
	
 }

