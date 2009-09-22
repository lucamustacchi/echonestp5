//
// EchoNestBasics.pde
// echonestp5
// 
// The Echo Nest API Processing Wrapper
// http://the.echonest.com/
// Copyright (C) 2009 melka - Kamel Makhloufi
// http://melka.one.free.fr/blog/

// Includes code by Vlad Patryshev
// http://www.myjavatools.com/

// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.


// This is just a project to show how to use the library.
// For more infos about the different classes, check the JavaDoc
// in <sketchbook>/libraries/processingp5/reference/index.html

import melka.echonest.*;
import melka.echonest.ENTrack.*;

// Enter here your Echo Nest API Key
String APIKey = "<apikey>";
// This is an mp3 file to upload.
// Put the file in your sketch's data folder
String fileToUpload = "<audio>.mp3";

EchoNest          nest;
ENTrack           track;

ENBar[]           bars;
ENBeat[]          beats;
ENDuration        duration;
ENEndOfFadeIn 	  endOfFadeIn;
ENKey             trackKey;
ENLoudness        loudness;
ENMetadata        metadata;
ENMode            mode;
ENSection[]       sections;
ENSegment[]       segments;
ENStartOfFadeOut  startOfFadeOut;
ENTatum[]         tatums;
ENTempo           tempo;
ENTimeSignature   timeSignature;


// VARIABLES INIT 
int nbLines = 6;
float canvasWidth = 800;
float canvasHeight = 600;

float imgWidth = canvasWidth;
float imgHeight = canvasHeight;

float lineHeight = canvasHeight/nbLines;
float lineDuration, secondLength;
float startTime, endTime;


void setup() {
  size((int)imgWidth,(int)imgHeight);
  background(255);
  nest = new EchoNest();
  nest.init(this,APIKey);
  retrieveData(fileToUpload);
}

void retrieveData(String file) {
  if (nest.uploadFile(file)) {
    track = nest.initTrack();
    /*bars = track.getBars();
    beats = track.getBeats();*/
    duration = track.getDuration();
    /*endOfFadeIn = track.getEndOfFadeIn();
    trackKey = track.getKey();
    loudness = track.getLoudness();
    metadata = track.getMetadata();
    mode = track.getMode();
    sections = track.getSections();*/
    segments = track.getSegments();
    /*startOfFadeOut = track.getStartOfFadeOut();
    tatums = track.getTatums();
    tempo = track.getTempo();
    timeSignature = track.getTimeSignature();*/
  }  
  drawSoundscape();
}

void drawSoundscape() {
  println("--- START DRAWING ---");
  
  lineDuration = duration.duration/nbLines;
  secondLength = canvasWidth/lineDuration;
  
  strokeCap(SQUARE);
  smooth();
  noStroke();
  fill(255);
  for (int i=0;i<segments.length;i++) {
    drawSegment(i);
  }
}

void drawSegment(int i) {
  float start = segments[i].start*secondLength;
  float end = start+segments[i].duration*secondLength;
  float ln = 1;
  while (start>canvasWidth*ln) {
    ln+=1;
  }

  if (i%2 != 0) {
    fill(245);
  } else {
    fill(255);
  }

  start = start-((ln-1)*canvasWidth);
  end = end-((ln-1)*canvasWidth);
  noStroke();
  rect(start,(ln-1)*lineHeight,end-start,lineHeight);
  
  for (int j=0; j<12;j++) {
    float val = segments[i].pitches[j];
    colorMode(HSB,12);
    stroke(j,8,12,6);
    colorMode(RGB,255);
    strokeWeight(0.5); 
    float h = (ln*lineHeight-val*lineHeight);
    line(start,h,end,h);
  }

  noStroke();
  float dX = 0;
  if (end > canvasWidth) {
    dX = end - canvasWidth;
    end = canvasWidth;
    rect(0,ln*lineHeight,dX,lineHeight);
    for (int j=0; j<12;j++) {
      float val = segments[i].pitches[j];
      colorMode(HSB,12);
      stroke(j,8,12,6);
      colorMode(RGB,255);
      strokeWeight(0.5); 
      float h = (ln+1)*lineHeight-val*lineHeight;
      line(0,h,dX,h);
    }
  }
}

