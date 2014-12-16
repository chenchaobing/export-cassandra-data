package com.easemob.dataexport;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.UUID;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.easemob.dataexport.cassandra.Serializers;
import com.easemob.dataexport.util.Schema;

import static com.easemob.dataexport.util.StringUtils.hexToBytes;
import static com.easemob.dataexport.util.JsonUtils.toObjectNode;
import static com.easemob.dataexport.util.ConversionUtils.bytebuffer;
import static com.easemob.dataexport.util.StringUtils.hexToByteBuffer;

public class ExportUserProperties {

	public static void main(String[] args) throws Exception {
		String filePath = "entity_properties.json";
		InputStream inputStream = ExportUserProperties.class.getClassLoader().getResourceAsStream(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		String line = null;
		while((line = br.readLine()) != null){
			dealData(line);
		}
		br.close();
	}
	
	public static boolean isUserData(ObjectNode jsonData){
		try{
			ArrayNode arrayNode = (ArrayNode) jsonData.path("columns");
			for(JsonNode jsonNode : arrayNode){
				if(decodeHexString(jsonNode.get(0).asText()).equals("type")){
					if(decodeHexString(jsonNode.get(1).asText()).equals("user")){
						return true;
					}
				}
			}
			return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static String decodeHexString(String hexString){
		byte[] bytes = hexToBytes(hexString);
		ByteBuffer byteBuffer = bytebuffer(bytes);
		String value = Serializers.se.fromByteBuffer(byteBuffer);
		return value;
	}
	
	public static void dealData(String data){
		try{
			ObjectNode objectNode = toObjectNode(data);
			if(objectNode == null){
				return ;
			}
			if(!isUserData(objectNode)){
				return ;
			}
			
			String key = objectNode.path("key").asText();
			String rowKey = key.substring(32);
			byte[] bytes = hexToBytes(rowKey);
			ByteBuffer byteBuffer = bytebuffer(bytes);
			UUID uuid = Serializers.ue.fromByteBuffer(byteBuffer);
			String username = "";
			String nickname = "";
			ArrayNode arrayNode = (ArrayNode) objectNode.path("columns");
			for(JsonNode jsonNode : arrayNode){
				String propertyName = decodeHexString(jsonNode.get(0).asText());
				ByteBuffer propertyValueByteBuffer = hexToByteBuffer(jsonNode.get(1).asText());
				
				if(propertyName.equals("username")){
					username = (String)Schema.deserializeEntityProperty("username", propertyValueByteBuffer);
				}
				if(propertyName.equals("nickname")){
					nickname = (String)Schema.deserializeEntityProperty("nickname", propertyValueByteBuffer);
				}
			}
			System.out.println(uuid + "|" + username + "|" + nickname);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
