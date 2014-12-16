package com.easemob.dataexport;

import static com.easemob.dataexport.utils.ConversionUtils.bytebuffer;
import static com.easemob.dataexport.utils.ConversionUtils.getLong;
import static com.easemob.dataexport.utils.ConversionUtils.string;
import static com.easemob.dataexport.utils.ConversionUtils.uuid;
import static com.easemob.dataexport.utils.JsonUtils.toObjectNode;
import static com.easemob.dataexport.utils.StringUtils.hexToByteBuffer;
import static com.easemob.dataexport.utils.StringUtils.hexToBytes;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.easemob.dataexport.security.AuthPrincipalType;
import com.easemob.dataexport.serializers.Serializers;

public class ExportTokenInfo {

	public static void main(String[] args) throws Exception {
		String filePath = "token.json";
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
	
    private static final String TOKEN_UUID = "uuid";
    private static final String TOKEN_TYPE = "type";
    private static final String TOKEN_CREATED = "created";
    private static final String TOKEN_ACCESSED = "accessed";
    private static final String TOKEN_INACTIVE = "inactive";
    private static final String TOKEN_DURATION = "duration";
    private static final String TOKEN_PRINCIPAL_TYPE = "principal";
    private static final String TOKEN_ENTITY = "entity";
    private static final String TOKEN_APPLICATION = "application";
    private static final String TOKEN_STATE = "state";

    private static final String TOKEN_TYPE_ACCESS = "access";
	
	public static void dealData(String data){
		try{
			ObjectNode objectNode = toObjectNode(data);
			if(objectNode == null){
				return ;
			}
			String key = objectNode.path("key").asText();
			byte[] bytes = hexToBytes(key);
			ByteBuffer bf = bytebuffer(bytes);
			
			UUID tokenUuid = Serializers.ue.fromByteBuffer(bf);
			System.out.println(tokenUuid);
		
			Set<String> propertyNameSet = new HashSet<String>();
			propertyNameSet.add(TOKEN_CREATED);
			propertyNameSet.add(TOKEN_ACCESSED);
			propertyNameSet.add(TOKEN_INACTIVE);
			propertyNameSet.add(TOKEN_DURATION);
			ArrayNode arrayNode = (ArrayNode) objectNode.path("columns");
			Map<String , ByteBuffer> columns = new HashMap<String , ByteBuffer>();
			for( JsonNode jsonNode : arrayNode){
				String propertyName = decodeHexString(jsonNode.get(0).asText());
				ByteBuffer propertyValueByteBuffer = hexToByteBuffer(jsonNode.get(1).asText());
				columns.put(propertyName, propertyValueByteBuffer);
				
				System.out.println(propertyName);
				
			}
	        String type = string(columns.get(TOKEN_TYPE));
	        long created = getLong(columns.get(TOKEN_CREATED));
	        long accessed = getLong(columns.get(TOKEN_ACCESSED));
	        long inactive = getLong(columns.get(TOKEN_INACTIVE));
	        long duration = getLong(columns.get(TOKEN_DURATION));
	        String principalTypeStr = string(columns.get(TOKEN_PRINCIPAL_TYPE));
	        
	        AuthPrincipalType principalType = null;
	        if (principalTypeStr != null) {
	            try {
	                principalType = AuthPrincipalType.getFromString(principalTypeStr);
	            } catch (IllegalArgumentException e) {
	            }
	            if (principalType == null) {
	                try {
	                    principalType = AuthPrincipalType.valueOf(principalTypeStr.toUpperCase());
	                } catch (Exception e) {
	                }
	            }
	        }
	        String appUuid = null;
	        String userUuid = null;
	        if (principalType != null) {
	            UUID entityId = uuid(columns.get(TOKEN_ENTITY));
	            UUID appId = uuid(columns.get(TOKEN_APPLICATION));
	            appUuid = appId.toString();
	            userUuid = entityId.toString();
	        }
			System.out.println(type + "|" + created + "|" + accessed + "|" + inactive + "|" + duration + "|" + appUuid + "|" + userUuid);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
