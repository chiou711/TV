package com.cw.tv.data;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;

import com.cw.tv.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class VideoProvider {

	private static final String TAG = VideoProvider.class.getSimpleName();

	// original
//		public  static final String VIDEO_LIST_URL = "https://raw.githubusercontent.com/corochann/AndroidTVappTutorial/master/app/src/main/assets/video_lists.json";
//		public  static final String PREFIX_URL = "http://corochann.com/wp-content/uploads/2015/11/";

	// test
	public  static final String VIDEO_LIST_URL = "https://raw.githubusercontent.com/chiou711/TV/master/app/src/main/assets/video_lists.json";
	public  static final String PREFIX_URL = "https://storage.googleapis.com/android-tv/Sample%20videos/Google%2B/";

	private static String TAG_ID = "id";
	private static String TAG_MEDIA = "videos";
	private static String TAG_VIDEO_LISTS = "videolists";
	private static String TAG_CATEGORY = "category";
	private static String TAG_STUDIO = "studio";
	private static String TAG_SOURCES = "sources";
	private static String TAG_DESCRIPTION = "description";
	private static String TAG_CARD_THUMB = "card";
	private static String TAG_BACKGROUND = "background";
	private static String TAG_TITLE = "title";

	private static LinkedHashMap<String, List<Movie>> sMovieList;

	private static Resources sResources;
	private static Uri sPrefixUrl;

	public static void setContext(Context context) {
		if (null == sResources) {
			sResources = context.getResources();
		}
	}

	/**
	 * It may return null when data is not prepared yet by {@link #buildMedia}.
	 * Ensure that data is already prepared before call this function.
	 * @return
	 */
	public static LinkedHashMap<String, List<Movie>> getMedia() {
		return sMovieList;
	}

	/**
	 *  ArrayList of movies within specified "category".
	 *  If argument is null, then returns all movie list.
	 * @param category
	 * @return
	 */
	public static ArrayList<Movie> getMovieItems(String category) {
		if(sMovieList == null) {
			Log.e(TAG, "sMovieList is not prepared yet!");
			return null;
		} else {
			ArrayList<Movie> movieItems = new ArrayList<>();
			for (Map.Entry<String, List<Movie>> entry : sMovieList.entrySet()) {
				String categoryName = entry.getKey();
				if(category !=null && !category.equals(categoryName)) {
					continue;
				}
				List<Movie> list = entry.getValue();
				for (int j = 0; j < list.size(); j++) {
					movieItems.add(list.get(j));
				}
			}
			if(movieItems == null) {
				Log.w(TAG, "No data foud with category: " + category);
			}
			return movieItems;
		}
	}

	public static LinkedHashMap<String, List<Movie>> buildMedia(Context ctx) throws JSONException{
		return buildMedia(ctx, VIDEO_LIST_URL);
	}

	public static LinkedHashMap<String, List<Movie>> buildMedia(Context ctx, String url)
			throws JSONException {
		if (null != sMovieList) {
			return sMovieList;
		}
		sMovieList = new LinkedHashMap<>();
		//sMovieListById = new HashMap<>();

		JSONObject jsonObj = parseUrl(url);

		if (null == jsonObj) {
			Log.e(TAG, "An error occurred fetching videos.");
			return sMovieList;
		}

		JSONArray categories = jsonObj.getJSONArray(TAG_VIDEO_LISTS);

		if (null != categories) {
			final int categoryLength = categories.length();
			Log.d(TAG, "category #: " + categoryLength);
			long id;
			String title;
			String videoUrl;
			String bgImageUrl;
			String cardImageUrl;
			String studio;
			for (int catIdx = 0; catIdx < categoryLength; catIdx++) {
				JSONObject category = categories.getJSONObject(catIdx);
				String categoryName = category.getString(TAG_CATEGORY);
				JSONArray videos = category.getJSONArray(TAG_MEDIA);
				Log.d(TAG,
						"category: " + catIdx + " Name:" + categoryName + " video length: "
								+ (null != videos ? videos.length() : 0));
				List<Movie> categoryList = new ArrayList<Movie>();
				Movie movie;
				if (null != videos) {
					for (int vidIdx = 0, vidSize = videos.length(); vidIdx < vidSize; vidIdx++) {
						JSONObject video = videos.getJSONObject(vidIdx);
						String description = video.getString(TAG_DESCRIPTION);
						JSONArray videoUrls = video.getJSONArray(TAG_SOURCES);
						if (null == videoUrls || videoUrls.length() == 0) {
							continue;
						}
						id = video.getLong(TAG_ID);
						title = video.getString(TAG_TITLE);

						//todo formal
						videoUrl = PREFIX_URL + getVideoSourceUrl(videoUrls);

						//todo test only
//						videoUrl = "https://pixabay.com/videos/download/video-22589_small.mp4?attachment";
//						videoUrl = "https://player.vimeo.com/external/302482838.hd.mp4?s=b6958a1ec55375feff547828393b0807d8cfe18e&profile_id=174&oauth2_token_id=57447761&download=1";
//						videoUrl = "https://pixabay.com/videos/download/video-91_medium.mp4?attachment";

						System.out.println("videoUrl = " + videoUrl);
						bgImageUrl = PREFIX_URL + video.getString(TAG_BACKGROUND);
						cardImageUrl = PREFIX_URL + video.getString(TAG_CARD_THUMB);
						studio = video.getString(TAG_STUDIO);
						System.out.println("studio = " + studio);

						movie = buildMovieInfo(id, categoryName, title, description, studio,
												videoUrl, cardImageUrl, bgImageUrl);
						categoryList.add(movie);
					}
					sMovieList.put(categoryName, categoryList);
				}
			}
		}
		return sMovieList;
	}

	private static Movie buildMovieInfo(long id,
	                                    String category,
	                                    String title,
	                                    String description,
	                                    String studio,
	                                    String videoUrl,
	                                    String cardImageUrl,
	                                    String bgImageUrl) {
		Movie movie = new Movie();
		movie.setId(id);
		//movie.setId(Movie.getCount());
		//Movie.incrementCount();
		movie.setTitle(title);
		movie.setDescription(description);
		movie.setStudio(studio);
		movie.setCategory(category);
		movie.setCardImageUrl(cardImageUrl);
		movie.setBackgroundImageUrl(bgImageUrl);
		movie.setVideoUrl(videoUrl);
//https://github.com/corochann/AndroidTVappTutorial
		//https://github.com/corochann/AndroidTVappTutorial/blob/master/app/src/main/java/com/corochann/androidtvapptutorial/model/Movie.java
		//https://github.com/corochann/AndroidTVappTutorial/blob/2ace6518b8c67636d2ed50ca9541ce4480d23ee7/app/src/main/java/com/corochann/androidtvapptutorial/model/Movie.java

		return movie;
	}


	// workaround for partially pre-encoded sample data
	private static String getVideoSourceUrl(final JSONArray videos) throws JSONException {
		try {
			final String url = videos.getString(0);
			System.out.println("VideoProvider / url = " + url);

			// original
//			return (-1) == url.indexOf('%') ? url : URLDecoder.decode(url, "UTF-8");

			// test
			return (-1) == url.indexOf('%') ? url : url;
//		} catch (UnsupportedEncodingException e) {
		} catch (Exception e) {
			throw new JSONException("Broken VM: no UTF-8");
		}
	}

	protected static JSONObject parseUrl(String urlString) {
		Log.d(TAG, "Parse URL: " + urlString);
		BufferedReader reader = null;

		//sPrefixUrl = Uri.parse(sResources.getString(R.string.prefix_url));
		sPrefixUrl = Uri.parse(PREFIX_URL);

		try {
			java.net.URL url = new java.net.URL(urlString);
			URLConnection urlConnection = url.openConnection();
			reader = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
			//urlConnection.getInputStream(), "iso-8859-1"));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			String json = sb.toString();
			return new JSONObject(json);
		} catch (Exception e) {
			Log.d(TAG, "Failed to parse the json for media list", e);
			return null;
		} finally {
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					Log.d(TAG, "JSON feed closed", e);
				}
			}
		}
	}
}
