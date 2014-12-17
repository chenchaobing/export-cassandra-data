package com.easemob.dataexport.utils.dataexport;

import static com.easemob.dataexport.utils.JsonUtils.toObjectNode;
import static com.easemob.dataexport.utils.StringUtils.hexToByteBuffer;

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
import static com.easemob.dataexport.utils.CassandraDataParseUtils.decodeHexString;

public class ExportUserPasswordInfoUtils {

	public static void main(String[] args) throws Exception {
		String filePath = "entity_dictionaries.json";
		InputStream inputStream = ExportOrgIdUtils.class.getClassLoader().getResourceAsStream(filePath);
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
			UUID appUuid = (UUID)decodeHexString(key.substring(0 , 32), Serializers.ue);
			String value = (String)decodeHexString(key.substring(32) , Serializers.se);
			
			String[] ss = value.split(":");
			String userUuid = ss[0];
			String credentials = ss[1];
			if(credentials.equals("credentials")){
				System.out.println(appUuid + "|" + userUuid + "|" + credentials);
				ArrayNode arrayNode = (ArrayNode) objectNode.path("columns");
				for( JsonNode jsonNode : arrayNode){
					String propertyName = (String)decodeHexString(jsonNode.get(0).asText() , Serializers.se);
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
