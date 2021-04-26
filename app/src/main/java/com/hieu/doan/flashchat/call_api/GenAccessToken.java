package com.hieu.doan.flashchat.call_api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Stringee
 */
public class GenAccessToken {
    public static String genAccessToken(String userId) {
        try {
            String keySid = "SKJxOhXK5Savv8Zux2xtPvIkCnVBoAi4E3";
            String keySecret = "eUJ6d3dSQlJBdUNBNm5mQmI3eEV0ZU4zQjdsUFJLOWQ=";
            Algorithm algorithmHS = Algorithm.HMAC256(keySecret);

            Map<String, Object> headerClaims = new HashMap<String, Object>();
            headerClaims.put("typ", "JWT");
            headerClaims.put("alg", "HS256");
            headerClaims.put("cty", "stringee-api;v=1");

            long exp = (long) (System.currentTimeMillis()) + 3600 * 1000*24;

            String token = JWT.create().withHeader(headerClaims)
                    .withClaim("jti", keySid + "-" + System.currentTimeMillis())
                    .withClaim("iss", keySid)
                    .withClaim("userId", userId)
                    .withExpiresAt(new Date(exp))
                    .sign(algorithmHS);

            return token;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

}
