package com.easemob.dataexport.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.GenericXmlApplicationContext;


import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

public class EasemobCache {
	 private static EasemobCache easemobCache = null;
	 private static GenericXmlApplicationContext context;
	 
     public static final int DEFAULT_EXPIRE_SECONDS = 7 * 24 * 60 * 60;

	 public static EasemobCache getInstance(){
		if(easemobCache != null){
			return easemobCache;
		}
		synchronized (EasemobCache.class) {
			if(easemobCache == null){
				context = new GenericXmlApplicationContext();
				context.setValidating(false);
				context.load("classpath*:applicationContext*.xml");
				context.refresh();
				easemobCache = (EasemobCache) context.getBean("easemobCache");
			}
		}
		return easemobCache;
	 }

	 @Autowired
     @Qualifier("jedisPool")
     private ShardedJedisPool jedisPool;

     @Autowired
     @Qualifier("slaveJedisPool")
     private ShardedJedisPool slaveJedisPool;
     
     public void setUserId(String appUUID , String userUUID , String username){
    	 if(isEmpty(appUUID) || isEmpty(userUUID) || isEmpty(username) ){
    		 return;
    	 } 
    	 try{
    		 String appname = getAppnameByAppUUID(appUUID);
    		 if(isEmpty(appname)){
    			 return;
    		 }
    		 String value = appname + "_" + username;
    		 try(ShardedJedis jedis = jedisPool.getResource()){
    			 jedis.set(userUUID, value);
    		 }
    	 }catch(Exception e){
    		 e.printStackTrace();
    	 }
     }
     
     public String getAppnameByAppUUID(String uuid){
    	 if (isEmpty(uuid)) {
             return null;
         }
         try {
             try (ShardedJedis jedis = slaveJedisPool.getResource()) {
                 return jedis.get(uuid);
             }
         } catch (Exception e) {
        	 e.printStackTrace();
         }
         return null;
     }
     
     public void setApplicationOrOrganizationId(String name, String uuid) {
         if (isEmpty(name) || isEmpty(uuid)) {
             return;
         }
         try {
             try (ShardedJedis jedis = jedisPool.getResource()) {
            	 jedis.set(name.toLowerCase() , uuid);
            	 jedis.set(uuid , name.toLowerCase());
             }
         } catch (Exception e) {
        	 e.printStackTrace();
         }
     }
     
     /**
      * type: hash
      * key: token:{token_id}
      * <p>
      * 保存整个TokenInfo的信息
      */
     public void saveToken(final UUID tokenUUID, final String tokenType, final UUID applicationId, final String principalType, final UUID userId, final long created, final long expires) {
         if (isEmpty(tokenUUID) || isEmpty(tokenType) || isEmpty(applicationId) || isEmpty(principalType) || isEmpty(userId)) {
             return;
         }
         try {
             final String key = "token:" + tokenUUID;
             Map<String, String> map = new HashMap<String, String>(4);
             map.put("type", tokenType);
             map.put("applicationId", applicationId.toString());
             map.put("principalType", principalType);
             map.put("principalId", userId.toString());
             map.put("duration", String.valueOf(expires));
             map.put("created", String.valueOf(created));
             try (ShardedJedis jedis = jedisPool.getResource()) {
                 jedis.hmset(key, map);
             }
         } catch (Exception e) {
        	 e.printStackTrace();
         }
     }
     
     public void saveUserInfo(final String username , final String nickname, final Boolean activated, Long created, UUID userUUID, final String type) {
         if (isEmpty(username) || isEmpty(activated) || isEmpty(created) || isEmpty(userUUID) || isEmpty(type)) {
             return ;
         }
         try {
        	 try (ShardedJedis jedis = jedisPool.getResource()){
        		 String orgAppUser = jedis.get(userUUID.toString());
        		 if(isEmpty(orgAppUser)){
        			 return ;
        		 }
        		 String key = orgAppUser;
        		 Map<String, String> map = new HashMap<String, String>(4);
                 map.put("atd", String.valueOf(activated));
                 map.put("ctd", String.valueOf(created));
                 map.put("u", userUUID.toString());
                 map.put("auth", "au:" + type);
                 if(!isEmpty(nickname)){
                	 map.put("nickname", nickname);
                 }
                 
                 System.out.println(key + "|" + map);
                 
                 jedis.hmset(key, map);
        	 }
         }catch(Exception e){
        	 e.printStackTrace();
         }
     }
     
     public static boolean isEmpty(String s) {
         return StringUtils.isBlank(s);
     }
     
     public static boolean isEmpty(Object obj) {
         return obj == null;
     }
     
}
