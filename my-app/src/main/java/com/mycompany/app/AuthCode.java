package com.mycompany.app;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AuthCode {
  private static final String clientId = "c0afd6c31ae94f2e8d60473569b4b57d";
  private static final String clientSecret = "3166369b93344a73a715e88e83e6715a";
  private static final URI redirectUri = SpotifyHttpManager.makeUri("http://localhost:8888/callback/");
  private static final String code = "AQDxuxQWReu0z56pkRIuPwIg_UvjqhpDYg5n79fb5iolInPuKVQsG7mhykeGLyVJfjNtsrEE3x-IXTt6GpxNi8B1QQjB3bKliQ82NRapVVuUvI7ZGe0GVwj_JDNjn_aohK4v7jPMM7PFuNyGTwESlXbR-8DUOx6BRz-Hxd5t3DfTkd2-elpWs6P-l7uQkGtJyIlLiaflrsr1mzNM7xJseCJHF7qpuI_fnau1LY4n7AiEFkbuvz8lcwMKuS3TRFvuJMtOKRsZ2XeD0-cBimU";

  private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
          .setClientId(clientId)
          .setClientSecret(clientSecret)
          .setRedirectUri(redirectUri)
          .build();
  private static final AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code)
          .build();

  public static void authorizationCode_Sync() {
    try {
      final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

      // Set access and refresh token for further "spotifyApi" object usage
      spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
      spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
      System.out.println("Access: " + authorizationCodeCredentials.getAccessToken());
      System.out.println("Refresh: " + authorizationCodeCredentials.getRefreshToken());
      System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
    } catch (IOException | SpotifyWebApiException e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  public static void authorizationCode_Async() {
    try {
      final Future<AuthorizationCodeCredentials> authorizationCodeCredentialsFuture = authorizationCodeRequest.executeAsync();

      // ...

      final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeCredentialsFuture.get();

      // Set access and refresh token for further "spotifyApi" object usage
      spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
      spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

      System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
    } catch (InterruptedException | ExecutionException e) {
      System.out.println("Error: " + e.getCause().getMessage());
    }
  }
  public static void main (String[] args) {
    authorizationCode_Sync();
  }
}
