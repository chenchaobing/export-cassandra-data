package com.easemob.dataexport.utils.dataexport;

import static com.easemob.dataexport.utils.JsonUtils.toObjectNode;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.easemob.dataexport.serializers.Serializers;

import static com.easemob.dataexport.utils.CassandraDataParseUtils.decodeHexString;
public class ExportPrincipalTokenInfoUtils {

	public static void main(String[] args) throws Exception {
		String filePath = "principal_tokens.json";
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
			UUID userUuid = (UUID)decodeHexString(key.substring(32 , 64), Serializers.ue);
			String auType = (String)decodeHexString(key.substring(64), Serializers.se);
			if(auType.trim().equals("au")){
				ArrayNode arrayNode = (ArrayNode) objectNode.path("columns");
				for( JsonNode jsonNode : arrayNode){
					String tokenId = jsonNode.get(0).asText();
					System.out.println(appUuid + "|" + userUuid + "|" + auType.trim() + "|" + tokenId);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
