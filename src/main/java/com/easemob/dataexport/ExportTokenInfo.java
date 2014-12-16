package com.easemob.dataexport;

import static com.easemob.dataexport.utils.ConversionUtils.getLong;
import static com.easemob.dataexport.utils.ConversionUtils.string;
import static com.easemob.dataexport.utils.ConversionUtils.uuid;
import static com.easemob.dataexport.utils.JsonUtils.toObjectNode;
import static com.easemob.dataexport.utils.StringUtils.hexToByteBuffer;
import static com.easemob.dataexport.utils.CassandraDataParseUtils.decodeHexString;

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
	
    private static final String TOKEN_TYPE = "type";
    private static final String TOKEN_CREATED = "created";
    private static final String TOKEN_ACCESSED = "accessed";
    private static final String TOKEN_INACTIVE = "inactive";
    private static final String TOKEN_DURATION = "duration";
    private static final String TOKEN_PRINCIPAL_TYPE = "principal";
    private static final String TOKEN_ENTITY = "entity";
    private static final String TOKEN_APPLICATION = "application";
//    private static final String TOKEN_UUID = "uuid";
//    private static final String TOKEN_STATE = "state";
//    private static final String TOKEN_TYPE_ACCESS = "access";
	
	public static void dealData(String data){
		try{
			ObjectNode objectNode = toObjectNode(data);
			if(objectNode == null){
				return ;
			}
			String key = objectNode.path("key").asText();
			UUID tokenUuid = (UUID)decodeHexString(key , Serializers.ue);
			ArrayNode arrayNode = (ArrayNode) objectNode.path("columns");
			Map<String , ByteBuffer> columns = new HashMap<String , ByteBuffer>();
			for( JsonNode jsonNode : arrayNode){
				String propertyName = (String)decodeHexString(jsonNode.get(0).asText() , Serializers.se);
				ByteBuffer propertyValueByteBuffer = hexToByteBuffer(jsonNode.get(1).asText());
				columns.put(propertyName, propertyValueByteBuffer);
			}
	        String type = string(columns.get(TOKEN_TYPE));
	        long created = getLong(columns.get(TOKEN_CREATED));
	        long accessed = getLong(columns.get(TOKEN_ACCESSED));
	        long inactive = getLong(columns.get(TOKEN_INACTIVE));
	        long duration = getLong(columns.get(TOKEN_DURATION));
	        UUID appUuid = null;
	        UUID entityUuid = null;
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
	        if (principalType != null) {
	        	appUuid = uuid(columns.get(TOKEN_APPLICATION));
	            entityUuid = uuid(columns.get(TOKEN_ENTITY));
	        }
			System.out.println(tokenUuid + "|" + type + "|" + created + "|" + accessed + "|" + inactive + "|" + duration + "|" + appUuid + "|" + entityUuid);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
