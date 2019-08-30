package com.mycompany.app;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class CodeUri {
  private static final String clientId = "c0afd6c31ae94f2e8d60473569b4b57d";
  private static final String clientSecret = "3166369b93344a73a715e88e83e6715a";
  private static final URI redirectUri = SpotifyHttpManager.makeUri("http://localhost:8888/callback/");

  private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
          .setClientId(clientId)
          .setClientSecret(clientSecret)
          .setRedirectUri(redirectUri)
          .build();
  private static final AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
          //.state("x4xkmn9pu3j6ukrs8n")
          .scope("user-read-currently-playing,user-read-playback-state")
          .show_dialog(true)
          .build();

  public static void authorizationCodeUri_Sync() {
    final URI uri = authorizationCodeUriRequest.execute();

    System.out.println("URI: " + uri.toString());
  }

  public static void authorizationCodeUri_Async() {
    try {
      final Future<URI> uriFuture = authorizationCodeUriRequest.executeAsync();

      // ...

      final URI uri = uriFuture.get();

      System.out.println("URI: " + uri.toString());
    } catch (InterruptedException | ExecutionException e) {
      System.out.println("Error: " + e.getCause().getMessage());
    }
  }
  public static void main (String[] args) {
    authorizationCodeUri_Sync();
  }
}
