package com.example.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;
import org.slim3.repackaged.org.json.JSONException;
import org.slim3.repackaged.org.json.JSONObject;

import com.google.appengine.api.datastore.Key;

@Model(schemaVersion = 1)
public class AccessToken implements Serializable {

    private static final long serialVersionUID = 1L;

    @Attribute(primaryKey = true)
    private Key key;

    @Attribute(version = true)
    private Long version;

	private String clientId;
	private String clientSecret;
	private String redirectUri;
	private String code;
	private String token;
	private String tokenType;
	private long expiresIn;

	@Attribute(lob = true)
	private String refreshToken;


	private Date timestamp;

    /**
     * Returns the key.
     *
     * @return the key
     */
    public Key getKey() {
        return key;
    }

    /**
     * Sets the key.
     *
     * @param key
     *            the key
     */
    public void setKey(Key key) {
        this.key = key;
    }

    /**
     * Returns the version.
     *
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Sets the version.
     *
     * @param version
     *            the version
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public long getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(long expiresIn) {
		this.expiresIn = expiresIn;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isExpired(){
		if(this.timestamp.getTime() + (this.expiresIn * 1000) - new Date().getTime() < (3600 * 1000)){
			return true;
		}
		return false;
	}

	public void refresh() throws IOException, JSONException{
		Map<String, String> params = new LinkedHashMap<String, String>();

		params.put("client_id", this.clientId);
		params.put("client_secret", this.clientSecret);
		params.put("grant_type", "refresh_token");
		params.put("refresh_token", this.refreshToken);

		this.getJSON(params);
	}

	public void init() throws IOException, JSONException{
		Map<String, String> params = new LinkedHashMap<String, String>();

		params.put("client_id", this.clientId);
		params.put("client_secret", this.clientSecret);
		params.put("redirect_uri", this.redirectUri);
		params.put("grant_type", "authorization_code");
		params.put("code", this.code);

		this.getJSON(params);
	}

	private void getJSON(Map<String, String> params) throws IOException, JSONException{
		try{
			URL url = new URL("https://accounts.google.com/o/oauth2/token");	//MalformedURLException
			URLConnection uc = url.openConnection();	//IOException
			uc.setDoOutput(true);

			// setting headers.
//			uc.setRequestProperty("Accept-Language", "ja");

			PrintStream ps = new PrintStream(uc.getOutputStream());	//IOException
			StringBuilder sb = new StringBuilder();
			int count = 0;
			for(String key : params.keySet()){
				System.out.println(key);

				sb.append(key + "=" + URLEncoder.encode(params.get(key),"utf-8"));
//				sb.append(key + "=" + params.get(key));

				if(params.size() - 1 > count) sb.append("&");
				count++;
			}

			System.out.println(sb.toString());
			ps.print(sb.toString());
			ps.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(uc.getInputStream()));	//IOException

			// get response.
			StringBuilder readString = new StringBuilder();
			String s = null;
			while(null != (s = reader.readLine())){	//IOException
				readString.append(s);
			}
			reader.close();	//IOException

			System.out.println("JSON:" + readString.toString());

			JSONObject json = new JSONObject(readString.toString());
			this.token = json.getString("access_token");
			this.tokenType = json.getString("token_type");
			this.expiresIn = json.getLong("expires_in");
			this.refreshToken = json.getString("id_token");
			this.timestamp = new Date();

		}catch(MalformedURLException ex){
			ex.printStackTrace();
			throw ex;
		}catch(IOException ex){
			ex.printStackTrace();
			throw ex;
		}catch(JSONException ex){
			ex.printStackTrace();
			throw ex;
		}catch(Exception ex){
			ex.printStackTrace();
			throw ex;
		}
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AccessToken other = (AccessToken) obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        return true;
    }
}
