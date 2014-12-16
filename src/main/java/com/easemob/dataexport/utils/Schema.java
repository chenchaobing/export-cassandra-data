package com.easemob.dataexport.utils;

import static com.easemob.dataexport.utils.ConversionUtils.string;
import static com.easemob.dataexport.utils.ConversionUtils.uuid;

import java.nio.ByteBuffer;

import com.easemob.dataexport.utils.JsonUtils;


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
