package com.mycompany.app;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class CodeRefresh {
  private static final String clientId = "c0afd6c31ae94f2e8d60473569b4b57d";
  private static final String clientSecret = "3166369b93344a73a715e88e83e6715a";
  private static final String refreshToken = "AQC3EvqeUUHWzSgwmYjbsVLJ-wtg3itJ23sysoafJCB4HU0spdVZ0EmDuFEjSwAF0OSpJWwFY6J76KJwkawXGicLocDtFx_-lhNCMDCCH8FZXlQq4JxJItMXviclq8p4LiPJeQ";

  private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
          .setClientId(clientId)
          .setClientSecret(clientSecret)
          .setRefreshToken(refreshToken)
          .build();
  private static final AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh()
          .build();

  public static void authorizationCodeRefresh_Sync() {
    try {
      final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();

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

  public static void authorizationCodeRefresh_Async() {
    try {
      final Future<AuthorizationCodeCredentials> authorizationCodeCredentialsFuture = authorizationCodeRefreshRequest.executeAsync();

      // ...

      final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeCredentialsFuture.get();

      // Set access token for further "spotifyApi" object usage
      spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());

      System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
    } catch (InterruptedException | ExecutionException e) {
      System.out.println("Error: " + e.getCause().getMessage());
    }
  }
  public static void main (String[] args) {
    authorizationCodeRefresh_Sync();
  }
}
