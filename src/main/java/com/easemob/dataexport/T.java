package com.easemob.dataexport;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.UUID;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.smile.SmileFactory;

import com.easemob.dataexport.serializers.Serializers;
import com.easemob.dataexport.utils.ConversionUtils;
import com.easemob.dataexport.utils.JsonUtils;

public class T {
    private static SmileFactory smile = new SmileFactory();

    private static ObjectMapper objectMapper = new ObjectMapper(smile);
    
    public static void main(String[] args) {
    	String s1 = "3a290a01447573657231";
    	byte[] bytes = hexToBytes(s1);
    	ByteBuffer byteBuffer = ConversionUtils.bytebuffer(bytes);
    	System.out.println(JsonUtils.normalizeJsonTree(JsonUtils.fromByteBuffer(byteBuffer)));

    	String s2 = "30626461353965302d383135352d313165342d616638662d3862633732316338396132383a6170706c69636174696f6e733a6e616d653a6f72672f74657374";
    	byte[] bytes2 = hexToBytes(s2);
    	ByteBuffer byteBuffer2 = ConversionUtils.bytebuffer(bytes2);
    	System.out.println(Serializers.se.fromByteBuffer(byteBuffer2));
    	
    	String s3 = "0bda59e0815511e4af8f8bc721c89a28";
    	byte[] byte3 = hexToBytes(s3);
    	ByteBuffer byteBuffer3 = ConversionUtils.bytebuffer(byte3);
    	System.out.println(Serializers.ue.fromByteBuffer(byteBuffer3));
    	
//    	ByteBuffer bf = Serializers.se.toByteBuffer("user1");
//    	byte[] bbs = bf.array();
//		System.out.println(bytesToHexString(bbs));
    }
    
    /*
	public static void main(String[] args) throws Exception {
		String filePath = "entity_properties.json";
		InputStream inputStream = ExportData.class.getClassLoader().getResourceAsStream(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		String line = null;
		while((line = br.readLine()) != null){
			ObjectNode objectNode = JsonUtil.parseStrToJsonNode(line);
			if(objectNode == null){
				continue;
			}
			
			String result = Serializers.se.fromByteBuffer(ConversionUtils.bytebuffer(objectNode.path("key").asText()));
			System.out.println(result);
			//			String value = "user1";
//			byte[] bytes = objectMapper.writeValueAsBytes(value);
//			System.out.println(bytesToHexString(bytes));

			
//			byte[] bb2 = ByteBuffer.wrap( bytes ).array();
//			System.out.println(bytesToHexString(bytes));
//			System.out.println(bytesToHexString(bb2));

//			System.out.println(objectNode.path("key").getBinaryValue());
			
			
			
			System.out.println();
		}
		br.close();
	}
	
   /** public static void main(String[] args) {
		String hexString = "0000000000000000000000000000000130303030303030302d303030302d303030302d303030302d3030303030303030303030313a75736572733a757365726e616d653a7765626d6173746572";
//		System.out.println(parseValue(hexString));
		
//		String str = "00000000-0000-0000-0000-000000000001:applications:name:usergrid/management";
		String str = "user1";
		System.out.println(bytebuffer(str));
    }**/
    
	public static String parseValue(String hexString){
		try{
			String result = objectMapper.readValue(hexToBytes(hexString) , String.class);
			return result;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static byte[] hexToBytes(String hexString) {
	    char[] hex = hexString.toCharArray();
	    int length = hex.length / 2;
	    byte[] rawData = new byte[length];
	    for (int i = 0; i < length; i++) {
	        int high = Character.digit(hex[i * 2], 16);
	        int low = Character.digit(hex[i * 2 + 1], 16);
	        int value = (high << 4) | low;
	        if (value > 127) {
	            value -= 256;
	        }
	        rawData[i] = (byte) value;
	    }
	    return rawData;
	}
	
    public static final String bytesToHexString(byte[] buf) {
        StringBuilder sb = new StringBuilder(buf.length * 2);
        String tmp = "";
        for (int i = 0; i < buf.length; i++) {
            tmp = Integer.toHexString(0xff & buf[i]);
            tmp = tmp.length() == 1 ? "0" + tmp : tmp;
            sb.append(tmp);
        }
        return sb.toString();
    }
	
}
