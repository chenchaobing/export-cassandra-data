package com.easemob.dataexport;

import static com.easemob.dataexport.util.ConversionUtils.bytebuffer;
import static com.easemob.dataexport.util.JsonUtils.toObjectNode;
import static com.easemob.dataexport.util.StringUtils.hexToByteBuffer;
import static com.easemob.dataexport.util.StringUtils.hexToBytes;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.easemob.dataexport.cassandra.Serializers;
import com.easemob.dataexport.util.Schema;

public class ExportPrincipalTokenInfo {

	public static void main(String[] args) throws Exception {
		String filePath = "principal_tokens.json";
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
//			System.out.println(appUuid);
			
			String userUkey = key.substring(32, 64);
			byte[] bbs2 = hexToBytes(userUkey);
			ByteBuffer bb = bytebuffer(bbs2);
			
			UUID userUuid = Serializers.ue.fromByteBuffer(bb);
//			System.out.println(userUuid);

			String rowKey = key.substring(64);
			byte[] bytes = hexToBytes(rowKey);
			ByteBuffer byteBuffer = bytebuffer(bytes);
			String value = Serializers.se.fromByteBuffer(byteBuffer);
			
//			System.out.println(value);
			
//			String[] ss = value.split(":");
			
//			System.out.println(value);
			if(value.trim().equals("au")){
				ArrayNode arrayNode = (ArrayNode) objectNode.path("columns");
//				System.out.println(arrayNode);
				for( JsonNode jsonNode : arrayNode){
					String tokenId = jsonNode.get(0).asText();
					System.out.println(tokenId);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
