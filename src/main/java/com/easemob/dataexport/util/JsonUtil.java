//package com.easemob.dataexport.util;
//
//import org.apache.commons.lang3.StringUtils;
//import org.codehaus.jackson.map.ObjectMapper;
//import org.codehaus.jackson.node.ObjectNode;
//
//public class JsonUtil {
//	
//	public static ObjectMapper mapper = new ObjectMapper();
//	public static ObjectNode parseStrToJsonNode(String str){
//		if(StringUtils.isEmpty(str)){
//			return null;
//		}
//		try{
//			if(str.length() <= 1){
//				return null;
//			}
//			if(str.charAt(str.length() -1 ) == ','){
//				str = str.substring(0, str.length() - 1);
//			}
//			System.out.println(str);
//			return mapper.readValue(str , ObjectNode.class);
//		}catch(Exception e){
////			e.printStackTrace();
//			return null;
//		}
//	}
//
//}
