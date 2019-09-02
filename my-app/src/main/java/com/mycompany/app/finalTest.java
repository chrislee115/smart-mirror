package com.mycompany.app;
//google cal api
import org.apache.http.config.ConnectionConfig;
import org.apache.commons.lang3.text.WordUtils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
//spotify api
import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import com.wrapper.spotify.requests.data.player.GetUsersCurrentlyPlayingTrackRequest;

import java.io.File;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import org.json.JSONException;
import java.security.GeneralSecurityException;

import java.time.LocalDateTime;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.lang.Object;

//TODO: ADD THE WEATHER API STUFF, SIMPLIFY CALENDAR STUFF POSSIBLY
public class finalTest {
  //SPOTIFY MEMBER VARIABLES
  private static final String clientId = "c0afd6c31ae94f2e8d60473569b4b57d";
  private static final String clientSecret = "3166369b93344a73a715e88e83e6715a";
  private static final String refreshToken ="AQCqX0jKI3M7zOOZ49PikuvZCyaxMyutcNmm6ZOTtexqMWNgbtLctYG6QE4-HqXKE_XhJf87e648vc60axgjICl3uf0yZlXRd7O4NT5wm_nd4e0yWXsbk41omvZ7MIQ61yjoxQ";
  private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
          .setClientId(clientId)
          .setClientSecret(clientSecret)
          .setRefreshToken(refreshToken)
          .build();
  private static final AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh()
          .build();       
  private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  private static final String TOKENS_DIRECTORY_PATH = "tokens";
  private static final OwmClient client = new OwmClient();
	private static final float lat = 42.605591f;
	private static final float lon = -83.149933f;
	private static final int cityId = 5012639;
  // WEATHER MEMBER VARIABLES
  
    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
     
  // CALENDAR MEMBER VARIABLES 
  private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);
  private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
  public static void authorizationCodeRefresh_Sync() {
    try {
      final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();

      // Set access and refresh token for further "spotifyApi" object usage
      spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
      spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
      //System.out.println("Access: " + authorizationCodeCredentials.getAccessToken());
      //System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
    } catch (IOException | SpotifyWebApiException e) {
      System.out.println("Error: " + e.getMessage());
    }
  }
  public static void getUsersCurrentlyPlayingTrack_Sync() throws IOException {
    GetUsersCurrentlyPlayingTrackRequest getUsersCurrentlyPlayingTrackRequest = spotifyApi
          .getUsersCurrentlyPlayingTrack()
//          .market(CountryCode.SE)
          .build();
    List<String>  artists = new ArrayList<>();
    String song, album;
    try {
      final CurrentlyPlaying currentlyPlaying = getUsersCurrentlyPlayingTrackRequest.execute();
      if ((currentlyPlaying != null) && currentlyPlaying.getIs_playing()) {
        song = currentlyPlaying.getItem().getName();
        for (int i = 0; i < currentlyPlaying.getItem().getArtists().length; ++i) {
          artists.add(currentlyPlaying.getItem().getArtists()[i].getName());
        }
        album = currentlyPlaying.getItem().getAlbum().getName();
        FileWriter fileWriter = new FileWriter("/home/pi/Desktop/my-app/now-playing.txt");
        PrintWriter printWriter = new PrintWriter(fileWriter);
        System.out.println("Open");
        printWriter.println("Song: " + song);
        printWriter.print("Artist: ");
        for (int i = 0; i < artists.size(); ++i) {
          if (i != artists.size() -1) {
            printWriter.print(artists.get(i) + ", ");
          }
          else {
            printWriter.println(artists.get(i));
          }
        }
        printWriter.println("Album: " + album);
        System.out.println("close");
        printWriter.close();
      }
      else {
        FileWriter fileWriter = new FileWriter("/home/pi/Desktop/my-app/now-playing.txt");
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.close();
      }
    } catch (IOException | SpotifyWebApiException e) {
    }
  }
  
  // CALENDAR METHODS
    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
  private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
      InputStream in = CalendarQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
      if (in == null) {
          throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
      }
      GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
      GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                                                                                   HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
        .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
        .setAccessType("offline")
        .build();
      LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
      return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
  }
  
  //WEATHER LOOP
  private static class weatherLoop implements Runnable {
    public void run() {
      try {
        while (true) {
          //get(0) to grab the first condition, the client returns an array of them
          //though, we only really need one
          String condition = client.currentWeatherAtCity(cityId).getWeatherConditions()
              .get(0)
							.getDescription();
          float temp = client.currentWeatherAtCity(cityId).getTemp();
          float tempMax = client.currentWeatherAtCity(cityId).getTempMax();
          float tempMin = client.currentWeatherAtCity(cityId).getTempMin();
          float humidity = client.currentWeatherAtCity(cityId).getHumidity();
          float windSpd = 0;
          float rain = 0;
          float snow = 0;
          if (client.currentWeatherAtCity(cityId).hasWind()) {
            windSpd = client.currentWeatherAtCity(cityId).getWindSpeed();
          }
          if (client.currentWeatherAtCity(cityId).hasRain()) {
            rain = client.currentWeatherAtCity(cityId).getRain();
          }  
          if (client.currentWeatherAtCity(cityId).hasSnow()) {
            snow = client.currentWeatherAtCity(cityId).getSnow(); 
          }
          FileWriter fileWriter = new FileWriter("/home/pi/Desktop/my-app/weather.txt");
          PrintWriter printWriter = new PrintWriter(fileWriter);
          printWriter.println("Condition: " + WordUtils.capitalizeFully(condition));
          printWriter.println("Temp: " + (int)temp + (char)176 + "F");
          printWriter.println("Hi/Lo\n" + (int)tempMax	+ (char)176 + "/" + (int)tempMin + (char)176);
          printWriter.println("Humidity\n" + (int)humidity + "%");
          printWriter.println("Wind Speed\n" + (int)windSpd + "mph");
          printWriter.println("Rain\n" + (int)rain + "mm");
          printWriter.println("Snow\n" + (int)snow + "mm");
          printWriter.close();
          Thread.sleep(20000);
        }
      } catch (InterruptedException e) {
        System.out.println("weatherinterrupedCrap");
      } catch (JSONException e) {
        System.out.println("weatherjsoncrap");
      } catch (IOException e) {
        System.out.println("weatherIOcrap");
      }
    }
  }
  private static List<Event> getEvents(String listURL) throws IOException, GeneralSecurityException{
    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
      .setApplicationName(APPLICATION_NAME)
      .build();
    DateTime now = new DateTime(System.currentTimeMillis());
    DateTime weekAway = new DateTime(System.currentTimeMillis() + 
                          1000 * 60 * 60 * 24 * 7);
    return service.events().list(listURL)
      .setMaxResults(10)
      .setTimeMin(now)
      .setTimeMax(weekAway)
      .setOrderBy("startTime")
      .setSingleEvents(true)
      .execute()
      .getItems();
  }
  //class made to retrieve both name and url during iterations
  public static class eventObj {
    public eventObj(String name_in, String url_in) {
      name = name_in;
      url = url_in;
    }  
    public eventObj(String name_in) {
      name = name_in;
    }
    public String getName() {
      return name;
    }
    public String getURL() {
      return url;
    }
    public List<Event> getItems() {
      return items;
    }
    public void addItems(List<Event> items_in) {
      items.addAll(items_in);
    }
    public void addItems(Event obj) {
      items.add(obj);
    }
    public void clearItems() {
      items.removeAll(items);
    }
    public boolean hasItems() {
      return items.size() != 0;
    }
    private List<Event> items = new ArrayList<>();
    private String name;
    private String url;
  }
  
  public static List<eventObj> eventList = Arrays.asList(new eventObj("General", 
        "primary"), new eventObj("FunKtion", 
        "umich.edu_anpmdd59qq6gopmdt3l9lp8kdg@group.calendar.google.com"),new eventObj("KTP", 
        "umich.edu_slf0shn0ott48uiegpr8uth3h4@group.calendar.google.com"),new eventObj("Birthdays",
        "addressbook#contacts@group.v.calendar.google.com"), new eventObj("Holidays",    
        "en.usa#holiday@group.v.calendar.google.com"));
        
  //CALENDAR LOOP
  private static class calLoop implements Runnable {
    public void run() {
      Thread t2 = new Thread(new weatherLoop());
      t2.start();
      try {
        SimpleDateFormat timedEvent = new SimpleDateFormat("EEEE, MM/dd 'at' hh:mma");
        SimpleDateFormat allDay = new SimpleDateFormat("EEEE, MM/dd");
        while (t2.isAlive()) {
          Date weekAway = new Date(System.currentTimeMillis());
          GregorianCalendar calendar = new GregorianCalendar();
          calendar.setTime(weekAway);
          calendar.add(java.util.Calendar.DATE, 7);
          weekAway.setTime(calendar.getTime().getTime());
          String time;
          Date temp;
        // List the next 10 events from the primary calendar.
          //general, funk, ktp, holiday, bday in that order
          for (int i = 0; i < eventList.size(); ++i) {
            eventList.get(i).clearItems();
            eventList.get(i).addItems(getEvents(eventList.get(i).getURL()));
          }
          FileWriter fileWriter = new FileWriter("/home/pi/Desktop/my-app/events.txt");
          PrintWriter printWriter = new PrintWriter(fileWriter);
          List<eventObj> datedItems = Arrays.asList(new eventObj("TODAY"), 
                    new eventObj("TOMORROW"), new eventObj("UPCOMING"));
          for (eventObj eObj : eventList) {
            if (eObj.hasItems()) {
                for (Event event : eObj.getItems()) { 
                  DateTime start = event.getStart().getDateTime();
                  DateTime end = event.getEnd().getDateTime();
                  if (start == null) {
                      start = event.getStart().getDate();
                      //the all day events are for some reason off by a day
                                          temp = new Date(event.getStart().getDate().getValue() + 86400000);
                      time = allDay.format(temp);
                  }
                  else {
                      temp = new Date(event.getStart().getDateTime().getValue());
                      time = timedEvent.format(temp);
                  }
                  DateTime td = new DateTime(System.currentTimeMillis());
                  DateTime tmrw = new DateTime(System.currentTimeMillis() + 86400000);
                  if ((start.getValue() / 86400000) - 
                      (td.getValue() / 86400000) < 1) {
                    datedItems.get(0).addItems(event);
                    //printWriter.print("TD:");
                  }
                  else if ((start.getValue() / 86400000) - 
                      (tmrw.getValue() / 86400000) < 1) {
                    datedItems.get(1).addItems(event);
                    //printWriter.print("TM:");
                  }
                  else { 
                    datedItems.get(2).addItems(event);
                    //printWriter.print("UP:");
                  }
                  //printWriter.printf("%s EventName: %s \n", 
                    //                 time, event.getSummary());
                }
            }
          }
          for (int i = 0; i < datedItems.size(); ++i) {
            List<Event> tempList = datedItems.get(i).getItems();
            printWriter.println(datedItems.get(i).getName());
            for (Event event : tempList)  {
              DateTime start = event.getStart().getDateTime();
              DateTime end = event.getEnd().getDateTime();
              if (start == null) {
                  start = event.getStart().getDate();
                      //the all day events are for some reason off by a day
                  temp = new Date(event.getStart().getDate().getValue() + 86400000);
                  time = allDay.format(temp);
              }
              else {
                  temp = new Date(event.getStart().getDateTime().getValue());
                  time = timedEvent.format(temp);
              }
              String name = event.getOrganizer().getDisplayName();
              if (name == null) { 
                name = "General";
              }
              printWriter.printf("%sDateTime:%sEventName:%s \n", 
                                     name, time, event.getSummary());
            }
          }
          printWriter.close();
          
          t2.join(10000);
        }
      } catch (InterruptedException e) {
        System.out.println("calIEcrap");
      } catch (IOException e) {
        System.out.println("calIOcrap");
      } catch (GeneralSecurityException e) {
        System.out.println("calGSUcrap");
      }
    }
    
  }
  //main will act as spotify loop, 
  //as it has the shortest refresh time
  public static void main (String[] args) throws IOException, InterruptedException {
    long authRefresh = 1000 * 60 * 10;
    long startTime = System.currentTimeMillis();
    Thread t = new Thread(new calLoop());
    t.start();
    authorizationCodeRefresh_Sync();
    while (t.isAlive()) {
      if ((System.currentTimeMillis() - startTime) > authRefresh) {
        authorizationCodeRefresh_Sync();
        startTime = System.currentTimeMillis();
      }
      getUsersCurrentlyPlayingTrack_Sync();
      t.join(1000);
    }
  }
}
