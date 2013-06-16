package com.example.controller;

import javax.servlet.http.HttpServletResponse;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.datastore.Datastore;

import com.example.model.AccessToken;
import com.google.appengine.api.datastore.Key;

public class AuthController extends Controller {

	@Override
	protected Navigation run() throws Exception {
		// TODO 自動生成されたメソッド・スタブ
		Key key = Datastore.createKey(AccessToken.class, 1);
		AccessToken token = null;

		System.out.println("request now");
		if(null == (token = Datastore.getOrNull(AccessToken.class, key))){

			String clientId = request.getParameter("client_id");
			String clientSecret = request.getParameter("client_secret");
			String redirectUri = request.getParameter("redirect_uri");
			String code = request.getParameter("code");
/*
			if(clientId == null || "".equals(clientId)){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return null;
			}

			if(clientSecret == null || "".equals(clientSecret)){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return null;
			}

			if(redirectUri == null || "".equals(redirectUri)){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return null;
			}
*/
			if(code == null || "".equals(code)){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return null;
			}

			token = new AccessToken();
			token.setClientId("927097438608-4j0js4ltnpi1f0vdbujol6dba435emij.apps.googleusercontent.com");
			token.setClientSecret("S7peURU5kh9J9jat75BSuKL2");
			token.setRedirectUri("http://localhost:8888/Auth");
			token.setCode(code);
			try{
				token.init();
			}catch(Exception ex){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return null;
			}

			token.setKey(key);
			Datastore.put(token);
			System.out.println("save token!!");
		}

		if(token.isExpired()){
			System.out.println("refresh!!");
			try{
				token.refresh();
			}catch(Exception ex){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return null;
			}
		}

		System.out.println(token.getToken());

		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().write(token.getToken());
		return null;
	}

}
