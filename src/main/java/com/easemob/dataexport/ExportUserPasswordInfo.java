package com.easemob.dataexport;

import static com.easemob.dataexport.utils.ConversionUtils.bytebuffer;
import static com.easemob.dataexport.utils.JsonUtils.toObjectNode;
import static com.easemob.dataexport.utils.StringUtils.hexToByteBuffer;
import static com.easemob.dataexport.utils.StringUtils.hexToBytes;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.easemob.dataexport.serializers.Serializers;
import com.easemob.dataexport.utils.Schema;

public class ExportUserPasswordInfo {

	public static void main(String[] args) throws Exception {
		String filePath = "entity_dictionaries.json";
		InputStream inputStream = ExportOrgInfo.class.getClassLoader().getResourceAsStream(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		String line = null;
		while((line = br.readLine()) != null){
			dealData(line);
		}
		br.close();
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
			String key = objectNode.path("key").asText();
			String appUkey = key.substring(0 , 32);
			byte[] bbs = hexToBytes(appUkey);
			ByteBuffer bf = bytebuffer(bbs);
			
			UUID appUuid = Serializers.ue.fromByteBuffer(bf);
			System.out.println(appUuid);
			
			String rowKey = key.substring(32);
			byte[] bytes = hexToBytes(rowKey);
			ByteBuffer byteBuffer = bytebuffer(bytes);
			String value = Serializers.se.fromByteBuffer(byteBuffer);
			String[] ss = value.split(":");
			
			
			if(ss[1].equals("credentials")){
				System.out.println(value);
				ArrayNode arrayNode = (ArrayNode) objectNode.path("columns");
				for( JsonNode jsonNode : arrayNode){
					String propertyName = decodeHexString(jsonNode.get(0).asText());
					ByteBuffer propertyValueByteBuffer = hexToByteBuffer(jsonNode.get(1).asText());
					@SuppressWarnings("unchecked")
					Map<String , Object> valueMap = (Map<String , Object>)Schema.deserializeEntityProperty(propertyName, propertyValueByteBuffer);
					System.out.println(propertyName + "|" + valueMap);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
