package com.mycompany.app;

import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import com.wrapper.spotify.requests.data.player.GetUsersCurrentlyPlayingTrackRequest;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class NowPlaying {
  private static final String clientId = "c0afd6c31ae94f2e8d60473569b4b57d";
  private static final String clientSecret = "3166369b93344a73a715e88e83e6715a";
  private static final String accessToken = "BQApRcvuNTEkGKcbVSquwExGWt2CclivwjTGF-v8OGNvkulTTUfc4M-cgtfYywOpcCLmS3aKJ8kMo0hbEO0i4Iev1ZCIWoQPUgbon5U_EeIH5POwkkP-Gsy5TNYfW7gDS1HKfXJIdC9q7XpMZDJUUfTqtPYwRrgzVVQlTie8";

  private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
          .setClientId(clientId)
          .setClientSecret(clientSecret)
          .setAccessToken(accessToken)
          .build();
  private static final GetUsersCurrentlyPlayingTrackRequest getUsersCurrentlyPlayingTrackRequest = spotifyApi
          .getUsersCurrentlyPlayingTrack()
//          .market(CountryCode.SE)
          .build();

  public static void getUsersCurrentlyPlayingTrack_Sync() {
    try {
      final CurrentlyPlaying currentlyPlaying = getUsersCurrentlyPlayingTrackRequest.execute();
      if (currentlyPlaying.getIs_playing()) {
        System.out.println("Song: " + currentlyPlaying.getItem().getName());
        System.out.print("Artist: ");
        for (int i = 0; i < currentlyPlaying.getItem().getArtists().length; ++i) {
          if (i == currentlyPlaying.getItem().getArtists().length - 1) {
            System.out.println(currentlyPlaying.getItem().getArtists()[i].getName());
          }
          else {
            System.out.print(currentlyPlaying.getItem().getArtists()[i].getName() + ", ");
          }
        }
        System.out.println("Album: " + currentlyPlaying.getItem().getAlbum().getName());
      }
      else {
        System.out.println("Nothing is playing");
      }
    } catch (IOException | SpotifyWebApiException e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  public static void getUsersCurrentlyPlayingTrack_Async() {
    try {
      final Future<CurrentlyPlaying> currentlyPlayingFuture = getUsersCurrentlyPlayingTrackRequest.executeAsync();

      // ...

      final CurrentlyPlaying currentlyPlaying = currentlyPlayingFuture.get();

      System.out.println("Song: " + currentlyPlaying.getTimestamp());
    } catch (InterruptedException | ExecutionException e) {
      System.out.println("Error: " + e.getCause().getMessage());
    }
  }
  public static void main (String[] args) {
    getUsersCurrentlyPlayingTrack_Sync();
  }
}
