package com.easemob.dataexport.utils;

import static com.easemob.dataexport.utils.ConversionUtils.bytebuffer;
import static com.easemob.dataexport.utils.StringUtils.hexToBytes;

import java.nio.ByteBuffer;

import me.prettyprint.hector.api.Serializer;

public class CassandraDataParseUtils {
	
	public static Object decodeHexString(String hexString , Serializer<?> serializers){
		byte[] bytes = hexToBytes(hexString);
		ByteBuffer byteBuffer = bytebuffer(bytes);
		Object value = serializers.fromByteBuffer(byteBuffer);
		return value;
	}

}
