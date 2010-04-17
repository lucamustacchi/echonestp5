//
// ENTrack.java
// echonestp5
// 
// The Echo Nest API Processing Wrapper
// http://the.echonest.com/
// Copyright (C) 2010 melka - Kamel Makhloufi
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

package com.melka.echonest;

import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import processing.core.PApplet;
import processing.xml.*;

/**
 * Contains global estimated information, applicable to the whole track. Note that because local
 * data may vary in the course of the track, this global estimation may not apply well to certain sections
 * of the music.
 * 
 * @example EchoNestBasics 
 * @author melka
 * 
 */

public class ENTrack implements Serializable {
	
	/**
	 * 
	 */
	transient private static final long serialVersionUID = -8528838453602347204L;
	/**
	 * Echo Nest Return Code : -1
	 * An unknown error has occurred.
	 */	
	transient public static final int UNKNOWN_ERROR = -1;
	/**
	 * Echo Nest Return Code : 0
	 * Success.
	 */	
	transient public static final int SUCCESS = 0;
	/**
	 * Echo Nest Return Code : 1
	 * The API Key you entered is invalid.
	 */	
	transient public static final int INVALID_KEY = 1;
	/**
	 * Echo Nest Return Code : 2
	 * Your API Key do not allow you to call this method.
	 */	
	transient public static final int NOT_ALLOWED = 2;
	/**
	 * Echo Nest Return Code : 3
	 * Too many requests made.
	 */	
	transient public static final int LIMIT_EXCEEDED = 3;
	/**
	 * Echo Nest Return Code : 4
	 * A parameter is missing.
	 */	
	transient public static final int MISSING_PARAM = 4;
	/**
	 * Echo Nest Return Code : 5
	 * An invalid parameter has been used.
	 */	
	transient public static final int INVALID_PARAM = 5;
	
	transient PApplet parent;
	
	transient private String baseUrl;
	transient private String apiKey;
	private String artistId;
	private String trackMD5;
	transient private String filePath;
	private int	   analysis_version=3;
	
	transient public final String VERSION = "0.1.2";
	
	public ENBar[] 			bars;
	public ENBeat[] 		beats;
	public ENDuration 		duration;
	public ENEndOfFadeIn 	endOfFadeIn;
	public ENStartOfFadeOut startOfFadeOut;
	public ENKey 			key;
	public ENLoudness 		loudness;
	public ENMetadata 		metadata;
	public ENMode 			mode;
	public ENSection[] 		sections;
	public ENSegment[]		segments;
	public ENTatum[]		tatums;
	public ENTempo			tempo;
	public ENTimeSignature	timeSignature;

	/**
	 * Initialize a new class for retrieving track analysis.
	 * 
	 * @param parent The main PApplet.
	 * @param ApiKey Your Echo Nest API Key.
	 * @param TrackMD5 MD5 hash of the audio file.
	 */	
	public ENTrack(PApplet parent, String ApiKey, String TrackMD5) {
		this.parent = parent;
		setBaseUrl("http://developer.echonest.com/api/");
		setApiKey(ApiKey);
		setTrackMD5(TrackMD5);
		parent.registerDispose(this);
	}
	
	public void saveTrackAnalysisToDisk() {
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream("/Library/Application Support/Processing/EchoNest/"+trackMD5+".enp5");
			out = new ObjectOutputStream(fos);
			out.writeObject(this);
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Retrieve all the bars of the audio file.
	 * 
	 * @return An array of {@link ENBar} objects.
	 */
	public ENBar[] getBars() {
		if (bars == null) {
			XMLElement data = new XMLElement(parent,getBaseUrl()+"get_bars?api_key="+getApiKey()+"&md5="+getTrackMD5()+"&version=3&analysis_version="+analysis_version);
			if (Integer.parseInt(data.getChild("status").getChild("code").getContent()) == SUCCESS) {
				int nbBars = data.getChild("analysis").getChildCount();
				bars = new ENBar[nbBars];
				for (int i=0;i<nbBars;i++){
					float confidence = data.getChild("analysis").getChild(i).getFloatAttribute("confidence");
					float time = Float.parseFloat(data.getChild("analysis").getChild(i).getContent());
					bars[i] = new ENBar(confidence, time);
				}
				System.out.println(">> LOADED "+nbBars+" BARS");
				return bars;
			} else {
				System.err.println(">> ERROR. CODE "+Integer.parseInt(data.getChild("status").getChild("code").getContent()));
			}
		} else {
			System.err.println(">> BARS ALREADY LOADED");
			return bars;
		}
		return null;
	}
	
	/**
	 * Retrieve all the beats of the audio file.
	 * 
	 * @return An array of {@link ENBeat} objects.
	 */
	public ENBeat[] getBeats() {
		if (beats == null) {
			XMLElement data = new XMLElement(parent,getBaseUrl()+"get_beats?api_key="+getApiKey()+"&md5="+getTrackMD5()+"&version=3&analysis_version="+analysis_version);
			if (Integer.parseInt(data.getChild("status").getChild("code").getContent()) == SUCCESS) {
				int nbBeats = data.getChild("analysis").getChildCount();
				beats = new ENBeat[nbBeats];
				for (int i=0;i<nbBeats;i++){
					float confidence = data.getChild("analysis").getChild(i).getFloatAttribute("confidence");
					float time = Float.parseFloat(data.getChild("analysis").getChild(i).getContent());
					beats[i] = new ENBeat(confidence, time);
				}
				System.out.println(">> LOADED "+nbBeats+" BEATS");
				return beats;
			} else {
				System.err.println(">> ERROR. CODE "+Integer.parseInt(data.getChild("status").getChild("code").getContent()));
			}
		} else {
			System.err.println(">> BEATS ALREADY LOADED");
			return beats;
		}
		return null;
	}
	
	/**
	 * Retrieve the duration of the audio file.
	 * 
	 * @return An {@link ENDuration} object.
	 */
	public ENDuration getDuration() {
		if (duration == null) {
			XMLElement data = new XMLElement(parent,getBaseUrl()+"get_duration?api_key="+getApiKey()+"&md5="+getTrackMD5()+"&version=3&analysis_version="+analysis_version);
			if (Integer.parseInt(data.getChild("status").getChild("code").getContent()) == SUCCESS) {
				float time = Float.parseFloat(data.getChild("analysis").getChild("duration").getContent());
				duration = new ENDuration(time);
				System.out.println(">> DURATION : "+duration.duration);
				return duration;
			} else {
				System.err.println(">> ERROR. CODE "+Integer.parseInt(data.getChild("status").getChild("code").getContent()));
			}
		} else {
			System.err.println(">> DURATION ALREADY LOADED");
			return duration;
		}
		return null;
	}
	
	/**
	 * Retrieve the end of the fade in of the audio file.
	 * 
	 * @return An {@link ENEndOfFadeIn} object.
	 */
	public ENEndOfFadeIn getEndOfFadeIn() {
		if (endOfFadeIn == null) {
			XMLElement data = new XMLElement(parent,getBaseUrl()+"get_end_of_fade_in?api_key="+getApiKey()+"&md5="+getTrackMD5()+"&version=3&analysis_version="+analysis_version);
			if (Integer.parseInt(data.getChild("status").getChild("code").getContent()) == SUCCESS) {
				float time = Float.parseFloat(data.getChild("analysis").getChild("end_of_fade_in").getContent());
				endOfFadeIn = new ENEndOfFadeIn(time);
				System.out.println(">> END OF FADE IN : "+endOfFadeIn.time);
				return endOfFadeIn;
			} else {
				System.err.println(">> ERROR. CODE "+Integer.parseInt(data.getChild("status").getChild("code").getContent()));
			}
		} else {
			System.err.println(">> END OF FADE IN ALREADY LOADED");
			return endOfFadeIn;
		}
		return null;
	}
	
	/**
	 * Retrieve the key of the audio file.
	 * 
	 * @return An {@link ENKey} object.
	 */
	public ENKey getKey() {
		if (key == null) {
			XMLElement data = new XMLElement(parent,getBaseUrl()+"get_key?api_key="+getApiKey()+"&md5="+getTrackMD5()+"&version=3&analysis_version="+analysis_version);
			if (Integer.parseInt(data.getChild("status").getChild("code").getContent()) == SUCCESS) {
				float confidence = data.getChild("analysis").getChild("key").getFloatAttribute("confidence");
				int keyValue = Integer.parseInt(data.getChild("analysis").getChild("key").getContent());
				key = new ENKey(confidence, keyValue);
				System.out.println(">> KEY : "+key.confidence+"/"+key.key);
				return key;
			} else {
				System.err.println(">> ERROR. CODE "+Integer.parseInt(data.getChild("status").getChild("code").getContent()));
			}
		} else {
			System.err.println(">> KEY ALREADY LOADED");
			return key;
		}
		return null;
	}
	
	/**
	 * Retrieve the loudness of the audio file.
	 * 
	 * @return An {@link ENLoudness} object.
	 */
	public ENLoudness getLoudness() {
		if (loudness == null) {
			XMLElement data = new XMLElement(parent,getBaseUrl()+"get_loudness?api_key="+getApiKey()+"&md5="+getTrackMD5()+"&version=3&analysis_version="+analysis_version);
			if (Integer.parseInt(data.getChild("status").getChild("code").getContent()) == SUCCESS) {
				float value = Float.parseFloat(data.getChild("analysis").getChild("loudness").getContent());
				loudness = new ENLoudness(value);
				System.out.println(">> LOUDNESS : "+loudness.loudness);
				return loudness;
			} else {
				System.err.println(">> ERROR. CODE "+Integer.parseInt(data.getChild("status").getChild("code").getContent()));
			}
		} else {
			System.err.println(">> LOUDNESS ALREADY LOADED");
			return loudness;
		}
		return null;
	}
	
	/**
	 * Retrieve the metadatas written in the mp3 file.
	 * 
	 * @return An {@link ENMetadata} object.
	 */
	public ENMetadata getMetadata() {
		if (metadata == null) {
			XMLElement data = new XMLElement(parent,getBaseUrl()+"get_metadata?api_key="+getApiKey()+"&md5="+getTrackMD5()+"&version=3&analysis_version="+analysis_version);
			if (Integer.parseInt(data.getChild("status").getChild("code").getContent()) == SUCCESS) {
				String status = data.getChild("analysis").getChild("status").getContent();
				String id = data.getChild("analysis").getChild("id").getContent();
				String md5 = data.getChild("analysis").getChild("md5").getContent();
				String artist = null;
				String release = null;
				String title = null;
				String genre = null;
				float duration = 0;
				int samplerate = 0;
				int bitrate = 0;
				try {
					artist = data.getChild("analysis").getChild("artist").getContent();
					release = data.getChild("analysis").getChild("release").getContent();
					title = data.getChild("analysis").getChild("title").getContent();
					genre = data.getChild("analysis").getChild("genre").getContent();
					duration = Float.parseFloat(data.getChild("analysis").getChild("duration").getContent());
					samplerate = Integer.parseInt(data.getChild("analysis").getChild("samplerate").getContent());
					bitrate = Integer.parseInt(data.getChild("analysis").getChild("bitrate").getContent());
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				metadata = new ENMetadata(status, id, md5, artist, release, title, genre, duration, samplerate, bitrate);
				System.out.println(">> METADATA LOADED : Artist = "+metadata.artist);
				return metadata;
			} else {
				System.err.println(">> ERROR. CODE "+Integer.parseInt(data.getChild("status").getChild("code").getContent()));
			}
		} else {
			System.err.println(">> METADATA ALREADY LOADED");
			return metadata;
		}
		return null;
	}
	
	/**
	 * Retrieve the mode of the audio file.
	 * 
	 * @return An {@link ENMode} object.
	 */
	public ENMode getMode() {
		if (mode == null) {
			XMLElement data = new XMLElement(parent,getBaseUrl()+"get_mode?api_key="+getApiKey()+"&md5="+getTrackMD5()+"&version=3&analysis_version="+analysis_version);
			if (Integer.parseInt(data.getChild("status").getChild("code").getContent()) == SUCCESS) {
				float confidence = data.getChild("analysis").getChild("mode").getFloatAttribute("confidence");
				int modeValue = Integer.parseInt(data.getChild("analysis").getChild("mode").getContent());
				mode = new ENMode(confidence, modeValue);
				System.out.println(">> MODE : "+mode.confidence+"/"+mode.mode);
				return mode;
			} else {
				System.err.println(">> ERROR. CODE "+Integer.parseInt(data.getChild("status").getChild("code").getContent()));
			}
		} else {
			System.err.println(">> MODE ALREADY LOADED");
			return mode;
		}
		return null;
	}
	
	/**
	 * Retrieve all the sections of the audio file.
	 * 
	 * @return An array of {@link ENSection} objects.
	 */
	public ENSection[] getSections() {
		if (sections == null) {
			XMLElement data = new XMLElement(parent,getBaseUrl()+"get_sections?api_key="+getApiKey()+"&md5="+getTrackMD5()+"&version=3&analysis_version="+analysis_version);
			if (Integer.parseInt(data.getChild("status").getChild("code").getContent()) == SUCCESS) {
				int nbSections = data.getChild("analysis").getChildCount();
				sections = new ENSection[nbSections];
				for (int i=0;i<nbSections;i++){
					float startTime = data.getChild("analysis").getChild(i).getFloatAttribute("start");
					float duration = data.getChild("analysis").getChild(i).getFloatAttribute("duration");
					sections[i] = new ENSection(startTime, duration);
				}
				System.out.println(">> LOADED "+nbSections+" SECTIONS");
				return sections;
			} else {
				System.err.println(">> ERROR. CODE "+Integer.parseInt(data.getChild("status").getChild("code").getContent()));
			}
		} else {
			System.err.println(">> SECTIONS ALREADY LOADED");
			return sections;
		}
		return null;
	}
	
	/**
	 * Retrieve all the segments of the audio file.
	 * 
	 * @return An array of {@link ENSegment} objects.
	 */
	public ENSegment[] getSegments() {
		if (segments == null) {
			XMLElement data = new XMLElement(parent,getBaseUrl()+"get_segments?api_key="+getApiKey()+"&md5="+getTrackMD5()+"&version=3&analysis_version="+analysis_version);
			if (Integer.parseInt(data.getChild("status").getChild("code").getContent()) == SUCCESS) {
				int nbSegments = data.getChild("analysis").getChildCount();
				segments = new ENSegment[nbSegments];
				for (int i=0;i<nbSegments;i++){
					XMLElement s = data.getChild("analysis").getChild(i);
					float start = s.getFloatAttribute("start");
					float duration = s.getFloatAttribute("duration");
					
					float avLoudness = Float.parseFloat(s.getChild("loudness").getChild(0).getContent());
					float avTime = s.getChild("loudness").getChild(0).getFloatAttribute("time");
					ENSegmentLoudness average = new ENSegmentLoudness(avLoudness, avTime, "average");
					
					float maxLoudness = Float.parseFloat(s.getChild("loudness").getChild(1).getContent());
					float maxTime = s.getChild("loudness").getChild(1).getFloatAttribute("time");
					ENSegmentLoudness max = new ENSegmentLoudness(maxLoudness, maxTime, "maximum");
					
					float[] pitches = new float[12];
					float[] timbres = new float[12];
					
					for (int j=0;j<12;j++) {
						pitches[j] = Float.parseFloat(s.getChild("pitches").getChild(j).getContent());
						timbres[j] = Float.parseFloat(s.getChild("timbre").getChild(j).getContent());
					}
					
					segments[i] = new ENSegment(start, duration, average, max, pitches, timbres);
				}
				System.out.println(">> LOADED "+nbSegments+" SEGMENTS");
				return segments;
			} else {
				System.err.println(">> ERROR. CODE "+Integer.parseInt(data.getChild("status").getChild("code").getContent()));
			}
		} else {
			System.err.println(">> SEGMENTS ALREADY LOADED");
			return segments;
		}
		return null;
	}
	
	/**
	 * Retrieve the start of the fade out of the audio file.
	 * 
	 * @return An {@link ENStartOfFadeOut} object.
	 */
	public ENStartOfFadeOut getStartOfFadeOut() {
		if (startOfFadeOut == null) {
			XMLElement data = new XMLElement(parent,getBaseUrl()+"get_start_of_fade_out?api_key="+getApiKey()+"&md5="+getTrackMD5()+"&version=3&analysis_version="+analysis_version);
			if (Integer.parseInt(data.getChild("status").getChild("code").getContent()) == SUCCESS) {
				float time = Float.parseFloat(data.getChild("analysis").getChild("start_of_fade_out").getContent());
				startOfFadeOut = new ENStartOfFadeOut(time);
				System.out.println(">> START OF FADE OUT : "+startOfFadeOut.time);
				return startOfFadeOut;
			} else {
				System.err.println(">> ERROR. CODE "+Integer.parseInt(data.getChild("status").getChild("code").getContent()));
			}
		} else {
			System.err.println(">> START OF FADE OUT ALREADY LOADED");
			return startOfFadeOut;
		}
		return null;
	}
	
	/**
	 * Retrieve all the tatums of the audio file.
	 * 
	 * @return An array of {@link ENTatum} objects.
	 */
	public ENTatum[] getTatums() {
		if (tatums == null) {
			XMLElement data = new XMLElement(parent,getBaseUrl()+"get_tatums?api_key="+getApiKey()+"&md5="+getTrackMD5()+"&version=3&analysis_version="+analysis_version);
			if (Integer.parseInt(data.getChild("status").getChild("code").getContent()) == SUCCESS) {
				int nbTatums = data.getChild("analysis").getChildCount();
				tatums = new ENTatum[nbTatums];
				for (int i=0;i<nbTatums;i++){
					float confidence = data.getChild("analysis").getChild(i).getFloatAttribute("confidence");
					float time = Float.parseFloat(data.getChild("analysis").getChild(i).getContent());
					tatums[i] = new ENTatum(confidence, time);
				}
				System.out.println(">> LOADED "+nbTatums+" TATUMS");
				return tatums;
			} else {
				System.err.println(">> ERROR. CODE "+Integer.parseInt(data.getChild("status").getChild("code").getContent()));
			}
		} else {
			System.err.println(">> TATUMS ALREADY LOADED");
			return tatums;
		}
		return null;
	}
	
	/**
	 * Retrieve the tempo of the audio file.
	 * 
	 * @return An {@link ENTempo} object.
	 */
	public ENTempo getTempo() {
		if (tempo == null) {
			XMLElement data = new XMLElement(parent,getBaseUrl()+"get_tempo?api_key="+getApiKey()+"&md5="+getTrackMD5()+"&version=3&analysis_version="+analysis_version);
			if (Integer.parseInt(data.getChild("status").getChild("code").getContent()) == SUCCESS) {
				float confidence = data.getChild("analysis").getChild("tempo").getFloatAttribute("confidence");
				float tempoValue = Float.parseFloat(data.getChild("analysis").getChild("tempo").getContent());
				tempo = new ENTempo(confidence, tempoValue);
				System.out.println(">> TEMPO : "+tempo.confidence+"/"+tempo.tempo);
				return tempo;
			} else {
				System.err.println(">> ERROR. CODE "+Integer.parseInt(data.getChild("status").getChild("code").getContent()));
			}
		} else {
			System.err.println(">> TEMPO ALREADY LOADED");
			return tempo;
		}
		return null;
	}
	
	/**
	 * Retrieve the time signature of the audio file.
	 * 
	 * @return An {@link ENTimeSignature} object.
	 */
	public ENTimeSignature getTimeSignature() {
		if (timeSignature == null) {
			XMLElement data = new XMLElement(parent,getBaseUrl()+"get_time_signature?api_key="+getApiKey()+"&md5="+getTrackMD5()+"&version=3&analysis_version="+analysis_version);
			if (Integer.parseInt(data.getChild("status").getChild("code").getContent()) == SUCCESS) {
				float confidence = data.getChild("analysis").getChild("time_signature").getFloatAttribute("confidence");
				int signature = Integer.parseInt(data.getChild("analysis").getChild("time_signature").getContent());
				timeSignature = new ENTimeSignature(confidence, signature);
				System.out.println(">> TIME SIGNATURE : "+timeSignature.confidence+"/"+timeSignature.signature);
				return timeSignature;
			} else {
				System.err.println(">> ERROR. CODE "+Integer.parseInt(data.getChild("status").getChild("code").getContent()));
			}
		} else {
			System.err.println(">> TIME SIGNATURE ALREADY LOADED");
			return timeSignature;
		}
		return null;
	}
	
	/**
	 * Returns the version of the library.
	 * 
	 * @return Version of the library.
	 */
	public String version() {
		return VERSION;
	}
	
	public void dispose() {
		// anything in here will be called automatically when 
		// the parent applet shuts down. for instance, this might
		// shut down a thread used by this library.
		// note that this currently has issues, see bug #183
		// http://dev.processing.org/bugs/show_bug.cgi?id=183
	}
	
	public int getAnalysisVersion() {
		return analysis_version;
	}
	
	public void setAnalysisVersion(int version) {
		this.analysis_version = version;
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getArtistId() {
		return artistId;
	}

	public void setArtistId(String artistId) {
		this.artistId = artistId;
	}

	public String getTrackMD5() {
		return trackMD5;
	}

	public void setTrackMD5(String trackMD5) {
		this.trackMD5 = trackMD5;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	/**
	 * Nested classes to describes some useful data structures.
	 * 
	 * @author melka
	 */
	
	/**
	 * A bar, or measure, is a unit of time in Western music representing a regular grouping of beats.
	 */
	public class ENBar implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7520970068489066097L;
		/**
		 * Computational error margin. Higher is better. Varies between 0 and 1.
		 */
		public float confidence;
		/**
		 * Time (in seconds) at which the bar begins.
		 */
		public float time;
		
		public ENBar(float confidence, float time) {
			this.confidence = confidence;
			this.time = time;
		}
	}
	
	/**
	 * The basic time unit of a piece of music.
	 */	
	public class ENBeat implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2652190481126589487L;
		/**
		 * Computational error margin. Higher is better. Varies between 0 and 1.
		 */
		public float confidence;
		/**
		 * Time (in seconds) at which the bar begins.
		 */
		public float time;
		public ENBeat(float confidence, float time) {
			this.confidence = confidence;
			this.time = time;
		}
	}
	
	/**
	 * Duration of the track.
	 */
	public class ENDuration implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6160062620506682217L;
		/**
		 * Duration (in seconds)
		 */
		public float duration;
		public ENDuration(float duration) {
			this.duration = duration;
		}
	}
	
	/**
	 * A fade-in is a gradual increase in the level of the audio signal.
	 */
	public class ENEndOfFadeIn implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7567859021567548180L;
		/**
		 * Time (in seconds) giving the end of a possible initial fade-in section.
		 * Equals 0 when insignificant.
		 */
		public float time;
		public ENEndOfFadeIn(float time) {
			this.time = time;
		}
	}
	
	/**
	 * A fade-out is a gradual decrease in the level of the audio signal, typically at the end of the song.
	 */
	public class ENStartOfFadeOut implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4925849563064077480L;
		/**
		 * Time (in seconds) giving giving the beginning of a possible final fade-out section.
		 * Equals 0 when insignificant.
		 */
		public float time;
		public ENStartOfFadeOut(float time) {
			this.time = time;
		}
	}
	
	/**
	 * Tracks's harmonic center or tonic.
	 */
	public class ENKey implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2975867804981100262L;
		/**
		 * Computational error margin. Higher is better. Varies between 0 and 1.
		 */
		public float confidence;
		/**
		 * Key of the track. Ranges from 0 (C) and 11 (B). (ie : 1 = C#)
		 */
		public int key;
		public ENKey(float confidence, int key) {
			this.confidence = confidence;
			this.key = key;
		}
	}

	/**
	 * Loudness is the quality of a sound that is the primary psychological correlate of physical strength (amplitude).
	 */
	public class ENLoudness implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5458624608681096542L;
		/**
		 * Overall track loudness estimation (in dB).
		 */
		public float loudness;
		public ENLoudness(float loudness) {
			this.loudness = loudness;
		}
	}
	
	/**
	 * Loudness of a particular segment.
	 */
	public class ENSegmentLoudness extends ENLoudness {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1694486969225299184L;
		/**
		 * Position of the peak.
		 * For the average loudness, always 0.
		 * Varies for the max loudness.
		 */
		public float startTime;
		/**
		 * Type of loudness : average or maximum loudness for the segment
		 */
		public String type;
		public ENSegmentLoudness(float loudness, float startTime, String type) {
			super(loudness);
			this.startTime = startTime;
			this.type = type;
		}
	}
	
	/**
	 * Metadatas are informations embedded in an MP3 file.
	 * All those informations are not always available, it depends on the uploaded file.
	 */
	public class ENMetadata implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3742473372624779856L;
		/**
		 * Status of the analysis
		 */
		public String status;
		/**
		 * Track ID
		 */
		public String id;
		/**
		 * MP3 File MD5 Hash
		 */
		public String md5;
		/**
		 * Artist Name
		 */
		public String artist;
		/**
		 * Album Name
		 */
		public String release;
		/**
		 * Track Title
		 */
		public String title;
		/**
		 * Track Genre (Hip-Hop, Punk, etc...)
		 */
		public String genre;
		/**
		 * Track Duration
		 */
		public float duration;
		/**
		 * Sample rate of the file
		 * Number of samples per second (or per other unit) taken from a continuous signal to make a discrete signal.
		 */
		public int samplerate;
		/**
		 * Bit rate of the file
		 * Number of bits processed per second
		 */
		public int bitrate;
		public ENMetadata(String status, String id, String md5, String artist,
				String release,	String title, String genre, float duration, int samplerate, int bitrate) {
			this.status = status;
			this.id = id;
			this.md5 = md5;
			this.artist = artist;
			this.release = release;
			this.title = title;
			this.genre = genre;
			this.duration = duration;
			this.samplerate = samplerate;
			this.bitrate = bitrate;
		}
	}
	
	/**
	 * Mode of the track (Major or Minor)
	 */
	public class ENMode implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2037314745792590657L;
		/**
		 * Computational error margin. Higher is better. Varies between 0 and 1.
		 */
		public float confidence;
		/**
		 * 0 = minor, 1 = major.
		 */
		public int mode;
		public ENMode(float confidence, int mode) {
			this.confidence = confidence;
			this.mode = mode;
		}
	}
	
	/**
	 * Sections are the largest chunks in the track, corresponding to major changes in the music, 
	 * e.g. chorus, verse, bridge, solo, etc.
	 */
	public class ENSection implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5912086165148028006L;
		/**
		 * Start of this section (in seconds)
		 */
		public float start;
		/**
		 * Duration of this section (in seconds)
		 */
		public float duration;
		public ENSection(float start, float duration) {
			this.start = start;
			this.duration = duration;
		}
	}

	/**
	 * Short sound entity (e.g. 80-400 ms) somewhat timbrally and harmonically uniform, including solo 
	 * or mixtures of sounds (e.g. a piano note, a guitar chord, a mix of bass, cymbal and voice phoneme,
	 * a snare with sax, etc.). A segment is typically defined by the inter onset duration and has the
	 * time envelope of an attack and decay.
	 */
	public class ENSegment implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4019040248438782058L;
		/**
		 * Start of this segment (in seconds)
		 */
		public float start;
		/**
		 * duration of this segment (in seconds)
		 */
		public float duration;
		/**
		 * a 12-number chroma array representing the harmonic content of the whole 
		 * segment, as folded into the 12 pitches of the chromatic scale (from C to B).
		 * All numbers range from 0 to 1, with 1 always describing the highest value.
		 * As a result, noisy sounds tend to give 12 high values, whereas pitched sounds
		 * emphasize the strength of one or few pitch bins.
		 */
		public float[] pitches;
		/**
		 * a 12-number array describing the timbre of the segment (i.e. the color of the
		 * sound) in an eigen space. Because timbre is ill-defined, it is difficult to describe
		 * what each of these dimensions precisely represent. However, the 12 dimensions
		 * aren't independently normalized, that is their relevance is directly comparable
		 * on a unique scale. Dimensions are ordered by importance of the dimensionality.
		 * You can think of each dimension describing a particular aspect of the spectral
		 * surface of a sound, i.e. 1st dimension is the average loudness of the segment,
		 * 2nd dimension is a rough representation of the weight of low frequencies, 3rd
		 * dimension emphasizes the middle frequencies, 4th dimension is more comparing
		 * the attacks, etc. Combined, those 12 dimensions represent a fairly smooth, yet
		 * accurate description of the spectral surface of the sound segment, capturing
		 * at once both time and frequency evolutions of an auditory (perceptual) spectrogram.
		 */
		public float[] timbres;
		/**
		 * Average loudness of the segment.
		 * {@link ENSegmentLoudness}
		 */
		public ENSegmentLoudness averageLoudness;
		/**
		 * Maximum loudness of the segment.
		 * {@link ENSegmentLoudness}
		 */
		public ENSegmentLoudness maxLoudness;
		public ENSegment(float start, float duration, ENSegmentLoudness averageLoudness,
				ENSegmentLoudness maxLoudness, float[] pitches, float[] timbres) {
			this.start = start;
			this.duration = duration;
			this.averageLoudness = averageLoudness;
			this.maxLoudness = maxLoudness;
			this.pitches = pitches;
			this.timbres = timbres;
		}
	}
	
	/**
	 * Tatums are typically sub-divisions of beats, describing the smallest
	 * perceptual metrical unit of the music. (in seconds)
	 */
	public class ENTatum implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7705218004699997207L;
		/**
		 * Computational error margin. Higher is better. Varies between 0 and 1.
		 */
		public float confidence;
		/**
		 * Time (in seconds) at which the tatum begins.
		 */
		public float time;
		public ENTatum(float confidence, float time) {
			this.confidence = confidence;
			this.time = time;
		}
	}
	
	/**
	 * Speed or pace of the audio track.
	 */
	public class ENTempo implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2854928503317301762L;
		/**
		 * Computational error margin. Higher is better. Varies between 0 and 1.
		 */
		public float confidence;
		/**
		 * Overall track tempo estimation (in beat per minute or BPM). Estimation errors
		 * may include doubling or halfing the perceive value. Note however that humans
		 * may also disagree on an actual correct answer.
		 */
		public float tempo;
		public ENTempo(float confidence, float tempo) {
			this.confidence = confidence;
			this.tempo = tempo;
		}
	}
	
	/**
	 * Estimated overall time signature (number of beats per measure).
	 */
	public class ENTimeSignature implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3973067814075359793L;
		/**
		 * Computational error margin. Higher is better. Varies between 0 and 1.
		 */
		public float confidence;
		/**
		 * Estimated overall time signature (number of beats per measure). Note this is
		 * perceptual measures, as opposed to what the composer might have written on
		 * the score. The description goes as follows
		 * 0=NONE, 1=UNKNOWN (perhaps too many variations), 2=2/4, 3=3/4 (ie waltz),
		 * 4=4/4 (typical in most pop music), 5=5/4, 6=6/4. 7=7/4 etc.
		 */
		public float signature;
		public ENTimeSignature(float confidence, float signature) {
			this.confidence = confidence;
			this.signature = signature;
		}
	}
}