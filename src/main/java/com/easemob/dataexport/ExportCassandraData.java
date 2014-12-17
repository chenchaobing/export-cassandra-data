package com.easemob.dataexport;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easemob.dataexport.utils.dataexport.ExportAppIdUtils;
import com.easemob.dataexport.utils.dataexport.ExportOrgIdUtils;
import com.easemob.dataexport.utils.dataexport.ExportUserIdUtils;
import com.easemob.dataexport.utils.dataexport.ExportUserPropertiesUtils;

public class ExportCassandraData {
	private static Logger LOGGER = LoggerFactory.getLogger(ExportCassandraData.class);
	
	public static final int TYPE_EXPORT_ORG_UUID = 1;
	public static final int TYPE_EXPORT_APP_UUID = 2;
	public static final int TYPE_EXPORT_USER_UUID = 3;
	public static final int TYPE_EXPORT_USER_PROPERTIES = 4;
	
	public static void main(String[] args) throws Exception {
		String filePath = "entity_unique.json";
		int dataType = 3;
		InputStream inputStream = ExportCassandraData.class.getClassLoader().getResourceAsStream(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		String line = null;
		int count = 0;
		while((line = br.readLine()) != null){
			dealData(line, dataType);
			System.out.println("1111111111");
			if((count ++) % 1000 == 0){
				LOGGER.info("ExportCassandraData|" + filePath + "|" + dataType + "|" + count);
			}
		}
		br.close();
	}
	
	public static void dealData(String data , int dataType){
		if(StringUtils.isEmpty(data)){
			return;
		}
		if(dataType == TYPE_EXPORT_ORG_UUID){
			ExportOrgIdUtils.dealData(data);
		}else if(dataType == TYPE_EXPORT_APP_UUID){
			ExportAppIdUtils.dealData(data);
		}else if(dataType == TYPE_EXPORT_USER_UUID){
			ExportUserIdUtils.dealData(data);
		}else if(dataType == TYPE_EXPORT_USER_PROPERTIES){
			ExportUserPropertiesUtils.dealData(data);
		}
	}

}
