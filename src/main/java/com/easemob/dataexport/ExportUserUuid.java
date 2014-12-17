package com.easemob.dataexport;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.easemob.dataexport.cache.EasemobCache;
import com.easemob.dataexport.serializers.Serializers;

import static com.easemob.dataexport.utils.CassandraDataParseUtils.decodeHexString;
import static com.easemob.dataexport.utils.JsonUtils.toObjectNode;

public class ExportUserUuid {

	public static void main(String[] args) throws Exception {
		String filePath = "entity_unique.json";
		InputStream inputStream = ExportUserUuid.class.getClassLoader().getResourceAsStream(filePath);
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
			String value = (String)decodeHexString(key.substring(32) , Serializers.se);
			String[] ss = value.split(":");
			
			String appUUID = ss[0];
			if(ss[1].equals("users") && ss[2].equals("username")){
				String username = ss[3];
				ArrayNode arrayNode = (ArrayNode) objectNode.path("columns");
				String userUUID = arrayNode.get(0).get(0).asText();
				String timestamp = arrayNode.get(0).get(2).asText();
				
				System.out.println(appUUID + "|" + userUUID + "|" + timestamp);
				EasemobCache.getInstance().setUserId(appUUID, userUUID, username);
//				String orgAppName = RedisApI.get(appUuid);
//				String orgAppUserName = orgAppName + "_" + username; 
				
//				RedisApI.set(orgAppUserName , userUUID);
//				RedisApI.set(userUUID, orgAppUserName);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
