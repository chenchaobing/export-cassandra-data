package com.easemob.dataexport.util;

import static com.easemob.dataexport.util.ConversionUtils.string;
import static com.easemob.dataexport.util.ConversionUtils.uuid;

import java.nio.ByteBuffer;

import com.easemob.dataexport.util.JsonUtils;


public class Schema {
	public static final String PROPERTY_UUID = "uuid";
	public static final String PROPERTY_TYPE = "type";
	
    public static Object deserializeEntityProperty(String propertyName, ByteBuffer bytes ) {
        Object propertyValue = null;
        if ( PROPERTY_UUID.equals( propertyName ) ) {
            propertyValue = uuid( bytes );
        }
        else if ( PROPERTY_TYPE.equals( propertyName ) ) {
            propertyValue = string( bytes );
        }
        else {
            propertyValue = Schema.deserializePropertyValueFromJsonBinary( bytes );
        }
        return propertyValue;
    }
    
    public static Object deserializePropertyValueFromJsonBinary( ByteBuffer bytes ) {
        return JsonUtils.normalizeJsonTree( JsonUtils.fromByteBuffer( bytes ) );
    }

}
