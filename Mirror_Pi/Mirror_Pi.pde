import processing.serial.*;
//TODO add a no events text
//TODO add all the weather pictures
//MIRROR SPECS
//dont forget to change in setup as well
int mWidth = 1920;
int mHeight = 1080;
//FONT SPECS
PFont lightFont;
PFont regFont;
PFont boldFont;
int bigSize = 200;
int regSize = 50;
int smSize = 24;

//GREETING SPECS
float greetingX = 30;
float greetingY = 80;
float nameX = 30;
float nameY = 160;
String name = "Chris";

//TIME SPECS
int hour, minute;
float timeY = 175;
float timeX = mWidth - 100;
float ampmX = mWidth - 30;

//DATE SPECS
String months[] = {"January", "February", "March", "April", "May", "June", 
                  "July", "August", "September", "October", "November", "December"};
String daysWeek[] = {"Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};  
int daysInMonth[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
int daysInLYMonth[] = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
float dateX = mWidth - 30;
float dateY = 250;
//for the day of the week calculation
float dow; // day of week
int monthKeys[] = {1, 4, 4, 0, 2, 5, 0, 3, 6, 1, 4, 6};
int centuryKeys[] = {4, 2, 0, 6};

//WEATHER SPECS
String wdata[];
String condit; 
PImage clear, cloudy, hail, mist, rain, snow, sunny, thunderstorm, windy;
//TODO: find more images
String weatherTypes[] = {"thunder", "wind", "rain", "cloud", "hail", 
                          "mist",  "snow", "sun", "clear"};
String weatherText;
float weatherPX = mWidth - 100;
float weatherPY = 390;
float weatherTX = mWidth - 65;
float weatherTY = 480;

//NOW PLAYING SPECS
String songData[];

//CALENDAR SPECS
String events[];
String timeStart, timeEnd, timeStamp;
String prevHeader;
int dateStart, dateEnd;
int eventYear, eventMonth, eventDay;
float eventX = 30;
float defEventY = 250;
float eventY;
boolean isLYear = (year() % 4 == 0);
boolean hasEvents;
int eventPage = 0;
int updateCal = 0;
int updateSpot = 0;
int prevTime = 0;
PImage bg;
static public void main(String args[]) {
  PApplet.main("Mirror_Pi");
}
void setup(){
  //fullScreen();
  size(1920, 1080);
  lightFont = createFont("Open Sans Light", 72);
  regFont = createFont("Open Sans Regular", 72);
  boldFont = createFont("Open Sans SemiBold", 72);
  textFont(lightFont, regSize);
  launch("/home/pi/Desktop/widget-start.desktop");
  
  clear = loadImage("/home/pi/Desktop/Mirror_Pi/Sunny.jpg");
  cloudy = loadImage("/home/pi/Desktop/Mirror_Pi/Cloudy.jpg");
  hail = loadImage("/home/pi/Desktop/Mirror_Pi/Hail.jpg");
  mist = loadImage("/home/pi/Desktop/Mirror_Pi/Mist.jpg");
  rain = loadImage("/home/pi/Desktop/Mirror_Pi/Rain.jpg"); 
  snow = loadImage("/home/pi/Desktop/Mirror_Pi/Snow.jpg");
  sunny = loadImage("/home/pi/Desktop/Mirror_Pi/Sunny.jpg");
  thunderstorm = loadImage("/home/pi/Desktop/Mirror_Pi/Thunderstorm.jpg");
  windy = loadImage("/home/pi/Desktop/Mirror_Pi/Windy.jpg");
  
  bg = loadImage("/home/pi/Desktop/northern-lights.jpeg");
}

void draw(){
  background(bg);
  int texcol = 255;
  int fillcol = 0;
  weather();
  calendar();
  nowPlaying();
  fill(fillcol);
  noStroke();
  rectMode(CORNER);
   
  //EVENTS ANIMATION
  fill(255);
    
    //greeting + name
  textSize(regSize);
  textAlign(LEFT);
  if (hour() < 6 || hour() > 20) {
    text("Good Evening,", greetingX, greetingY);
  } else if (hour() < 12) {
    text("Good Morning,", greetingX, greetingY);
  } else if (hour() < 17) {
    text("Good Afternoon,", greetingX, greetingY);
  } else { 
    text("Good Evening,", greetingX, greetingY);
  }
  textFont(boldFont);
  text(name, nameX, nameY);
 
  textAlign(RIGHT);
     //time stuff
  textFont(lightFont, bigSize);
  fill(texcol);
  hour = standardizeHour(hour());
  minute = minute();
  if (minute > 9) {
    text(hour + ":" + minute, timeX, timeY);
  } 
  else {
    text(hour + ":" + "0"  + minute, timeX, timeY);
  }
    
  textFont(boldFont, regSize);
  if (hour() > 12) { 
    text("PM", ampmX, timeY);
  }
  else {
    text("AM", ampmX, timeY);
  }
   
  textFont(lightFont, regSize);
    //month day date display
  text(months[month() - 1] + " " + day(), dateX, dateY);
     //DAY
  text(getDOW(year(), month(), day()) + ", " + months[month() - 1]+ " " + day(), dateX, dateY);
  //////////////////////////////////////////////////
  //EVENTS
}  
//fail safe, click  top left to close program
void mousePressed() {
  if (mouseX >= 0 && mouseX < 300) {
    if (mouseY >= 0 && mouseY < 300) {
      exit();
    }
  }
}
void nowPlaying() {
  songData = loadStrings("/home/pi/Desktop/my-app/now-playing.txt");
  //0 is name, 1 is artist, 2 is album
  if (songData.length > 0) {
    if (songData[0] == "Nothing is playing.") { 
      return;
    }
    text(songData[0], 500, 500);
    text(songData[1], 600, 600);
    text(songData[2], 700, 700);
  }
}
String getDOW(int y, int m, int d) {
  // day of the week formula, key value method
    dow = ((y % 100) / 4) + d + monthKeys[m - 1];
    if (y / 4 == 0) {
      dow--;
    }
    dow += centuryKeys[(y - 1700) / 100];
    dow += y % 100;    
    return daysWeek[(int)dow % 7];
}
int standardizeHour(int hour_in){
  hour_in %= 12;
  if (hour_in == 0) {
      return 12;
  }
  return hour_in;
}
int defWeatherY = 620;
void weather(){
  PImage imageTypes[] = {clear, cloudy, hail, mist, rain, snow, sunny, thunderstorm,
                       windy};
  //THIS IS WHERE YOU WILL NEED TO FIND A WEATHER SOURCE FOR YOUR CITY
  wdata = loadStrings("/home/pi/Desktop/my-app/weather.txt");
  //weather listed in this order: conditions (0), temp (1), hi/lo (2), humidity (3)
  //windspeed(4), rain (5), snow (6)
  if (wdata.length == 0) { 
    return;
  }
  condit = wdata[0];
  fill(255);
  textSize(smSize);
   //ICONS FOR WEATHER CONDITION
  //weather image 
  //weather type
  for (int i = 0; i < weatherTypes.length; i++) {
    if(condit.toLowerCase().contains(weatherTypes[i])) {
      imageMode(CENTER);
      image(imageTypes[i], weatherPX, weatherPY);
      textAlign(RIGHT);
      //"CONDITIONS: "is 11 characters
      text(condit.substring(11), weatherTX + 10, weatherTY);
      break;
    }
  }
  textAlign(RIGHT);
  //temperature stuff
  fill(255);
  textFont(boldFont, regSize);
  float tempX = 50;
  float tempY = defWeatherY;
  //temp: has 6 characters
  text(wdata[1].substring(6), tempX, tempY);
  tempY += 45;
  textFont(regFont, smSize);
  for (int i = 2; i < wdata.length; ++i) {
    if (i % 2 == 0) {
      textSize(smSize);
      text(wdata[i], tempX, tempY);
      tempY += 20; 
    }
    else { 
      textSize(smSize - 4);
      text(wdata[i], tempX, tempY);
      tempY += 15;
    }
  }
}
void printEvents(int start, int end) {
  eventY = defEventY;
  for (int i = start; i < end; i++) {
    String event = events[i];
    if (i == start && !event.contains("HEADER:")) {
      textFont(regFont, smSize + 10);
      text(prevHeader + " Continued...", eventX, eventY);
      eventY += 35;
      end -= 2;
    }
    //if the current line is a header and contains no upcoming events
    if (event.contains("HEADER:")) {
      textFont(regFont, smSize + 10);
      text(event.substring(event.indexOf(":") + 2), eventX, eventY);
      prevHeader = event.substring(event.indexOf(":") + 2);
      eventY += 35;
      hasEvents = false;
    }
    else {
      int splitIndex = event.indexOf("EventName:");
      textFont(lightFont, smSize);
      text(event.substring(0, splitIndex), eventX + 30, eventY);
      eventY += 30;
      textFont(regFont, smSize);
      text(event.substring(splitIndex + 11, event.length()), eventX + 50, eventY);
      eventY += 50;
    }
  }
}
void calendar(){
   //google calendar api implementation, pull from events.txt  
   events = loadStrings("/home/pi/Desktop/my-app/events.txt");
   //13 is the maximum number of events that can come up without the
   //text flying off the screen
     
   int cutOff = 10;
   //fix this later
   textAlign(LEFT);
   if (events.length == 0) { 
     textFont(regFont, smSize + 5);
     text("No upcoming events.", eventX, eventY);
     return;
   }
   else {
     if (eventPage >= ((events.length - 1) / cutOff)) {
       printEvents(eventPage * cutOff, events.length);
     }
     else {
       printEvents(eventPage * cutOff, (eventPage * cutOff) + cutOff);
     }
     if (millis() - prevTime >= 7000) {
       prevTime = millis();
       eventPage = (eventPage + 1) % (((events.length - 1) / cutOff) + 1);
     }
     textFont(lightFont, smSize);
     text(events.length, 500, 500);
     text(eventPage + 1 + "/" + ceil((events.length) / (float)cutOff), 20, height - 20);
   }
}
