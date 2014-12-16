package com.easemob.dataexport;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.easemob.dataexport.cache.RedisApI;
import com.easemob.dataexport.serializers.Serializers;

import static com.easemob.dataexport.utils.ConversionUtils.bytebuffer;
import static com.easemob.dataexport.utils.JsonUtils.toObjectNode;
import static com.easemob.dataexport.utils.StringUtils.hexToBytes;

public class ExportAppInfo {

	public static void main(String[] args) throws Exception {
		String filePath = "entity_unique.json";
		InputStream inputStream = ExportAppInfo.class.getClassLoader().getResourceAsStream(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		String line = null;
		while((line = br.readLine()) != null){
			dealData(line);
		}
		br.close();
	}
	
	public static void dealData(String data){
		try{
			ObjectNode objectNode = toObjectNode(data);
			if(objectNode == null){
				return ;
			}
			String key = objectNode.path("key").asText();
			String rowKey = key.substring(32);
			byte[] bytes = hexToBytes(rowKey);
			ByteBuffer byteBuffer = bytebuffer(bytes);
			String value = Serializers.se.fromByteBuffer(byteBuffer);
			String[] ss = value.split(":");
			
			if(ss[1].equals("applications") && ss[2].equals("name")){
				String appname = ss[3];
				ArrayNode arrayNode = (ArrayNode) objectNode.path("columns");
				String uuid = arrayNode.get(0).get(0).asText();
				String timestamp = arrayNode.get(0).get(2).asText();
				System.out.println(appname + "|" + uuid +"|"+ timestamp);
				
				RedisApI.set(appname , uuid);
				RedisApI.set(uuid, appname);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
