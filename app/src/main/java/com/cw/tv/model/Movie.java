package com.cw.tv.model;

/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */


import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *  Modified from AOSP sample source code, by corochann on 2/7/2015.
 *  Movie class represents video entity with title, description, image thumbs and video url.
 */
public class Movie  implements Serializable {

	private static final String TAG = Movie.class.getSimpleName();

	static final long serialVersionUID = 727566175075960653L;
	private long id;
	private String title;
	private String studio;
	private String description;
	private String cardImageUrl;
	private String videoUrl;
	private String category;
	private String bgImageUrl;

	public Movie() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStudio() {
		return studio;
	}

	public void setStudio(String studio) {
		this.studio = studio;
	}

	@Override
	public String toString() {
		return "Movie{" +
				"id=" + id +
				", title='" + title + '\'' +
				'}';
	}

	public String getCardImageUrl() {
		return cardImageUrl;
	}

	public void setCardImageUrl(String cardImageUrl) {
		this.cardImageUrl = cardImageUrl;
	}

	public URI getCardImageURI() {
		try {
			return new URI(getCardImageUrl());
		} catch (URISyntaxException e) {
			return null;
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public String getCategory() { return category; }

	public void setCategory(String category) {
		this.category = category;
	}

	public String getBackgroundImageUrl() {
		return bgImageUrl;
	}

	public void setBackgroundImageUrl(String bgImageUrl) {
		this.bgImageUrl = bgImageUrl;
	}
}