package com.example.util.oauth2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slim3.repackaged.org.json.JSONException;
import org.slim3.repackaged.org.json.JSONObject;

public class AccessToken {

	private String clientId;
	private String clientSecret;
	private String redirectUri;
	private String code;

	private String token;
	private String tokenType;
	private long expiresIn;
	private String refreshToken;

	private Date timestamp;

	public String getToken(){
		// 期限が切れたときは再取得する
		if(this.timestamp.getTime() + this.expiresIn - new Date().getTime() < 60){
			this.refresh();
		}
		return this.token;
	}


	public String getClientId() {
		return clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public String getCode() {
		return code;
	}

	public String getTokenType() {
		return tokenType;
	}

	public long getExpiresIn() {
		return expiresIn;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public Date getTimestamp() {
		return timestamp;
	}


	public void refresh(){
		Map<String, String> params = new LinkedHashMap<String, String>();

		params.put("client_id", this.clientId);
		params.put("client_secret", this.clientSecret);
		params.put("grant_type", "refresh_token");
		params.put("refres_token", this.refreshToken);

		this.getJSON(params);
	}

	public void init(String clientId, String clientSecret, String redirectUri, String code){

		Map<String, String> params = new LinkedHashMap<String, String>();

		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.redirectUri = redirectUri;
		this.code = code;

		params.put("client_id", this.clientId);
		params.put("client_secret", this.clientSecret);
		params.put("redirect_uri", this.redirectUri);
		params.put("grant_type", "code");
		params.put("code", this.code);

		this.getJSON(params);

	}
	private void getJSON(Map<String, String>params){

		try{
			URL url = new URL("https://accounts.google.com/o/oauth2/token");	//MalformedURLException
			URLConnection uc = url.openConnection();	//IOException
			uc.setDoOutput(true);

			// setting headers.
			uc.setRequestProperty("Accept-Language", "ja");

			PrintStream ps = new PrintStream(uc.getOutputStream());	//IOException
			StringBuilder sb = new StringBuilder();
			int count = 0;
			for(String key : params.keySet()){
				sb.append(key + "=" + params.get(key));
				if(count > 0) sb.append("&");
				count++;
			}


			ps.print(sb.toString());
			ps.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(uc.getInputStream()));	//IOException

			// get response.
			String s = null;
			while(null != (s = reader.readLine())){	//IOException

			}
			reader.close();	//IOException

			JSONObject json = new JSONObject(s);
			this.token = json.getString("access_token");
			this.tokenType = json.getString("token_type");
			this.expiresIn = json.getLong("expires_in");
			this.refreshToken = json.getString("refresh_token");
			this.timestamp = new Date();

		}catch(MalformedURLException ex){

		}catch(IOException ex){

		}catch(JSONException ex){

		}catch(Exception ex){

		}
	}
}
