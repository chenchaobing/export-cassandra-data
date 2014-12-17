package com.easemob.dataexport;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.easemob.dataexport.cache.EasemobCache;
import com.easemob.dataexport.serializers.Serializers;
import com.easemob.dataexport.utils.Schema;

import static com.easemob.dataexport.utils.JsonUtils.toObjectNode;
import static com.easemob.dataexport.utils.StringUtils.hexToByteBuffer;
import static com.easemob.dataexport.utils.CassandraDataParseUtils.decodeHexString;

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
				if(decodeHexString(jsonNode.get(0).asText(), Serializers.se).equals("type") ){
					if(decodeHexString(jsonNode.get(1).asText() , Serializers.se).equals("user")){
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
	
	private static final String USER_ACTIVATED = "activated";
    private static final String USER_CREATED = "created";
    private static final String USER_MODIFIED = "modified";
    private static final String USER_NICKNAME = "nickname";
    private static final String USER_TYPE = "type";
    private static final String USER_NAME = "username";
    private static final String USER_UUID = "uuid";
	
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
//			UUID uid = (UUID)decodeHexString(key.substring(32), Serializers.ue);
			
			ArrayNode arrayNode = (ArrayNode) objectNode.path("columns");
			Map<String , Object> userInfo = new HashMap<String , Object>();
			for(JsonNode jsonNode : arrayNode){
				String propertyName = (String)decodeHexString(jsonNode.get(0).asText(), Serializers.se);
				ByteBuffer propertyValueByteBuffer = hexToByteBuffer(jsonNode.get(1).asText());
				Object propertyValue = Schema.deserializeEntityProperty(propertyName, propertyValueByteBuffer);
				userInfo.put(propertyName, propertyValue);
			}
			
			String username = (String)userInfo.get(USER_NAME);
			String nickname = (String)userInfo.get(USER_NICKNAME);
			boolean activated = (Boolean)userInfo.get(USER_ACTIVATED);
			long created = (Long)userInfo.get(USER_CREATED);
			UUID userUUID = (UUID)userInfo.get(USER_UUID);
			String type = (String)userInfo.get(USER_TYPE);
			
			if(type.equals("user")){
				System.out.println(username + "|" + nickname + "|" + activated + "|" + created + "|" + userUUID +"|"+ type);
				EasemobCache.getInstance().saveUserInfo(username, nickname, activated, created, userUUID, type);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
