/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package de.olafkock.liferay.blogs;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CustomFieldEliminationTest {

	private String markupWrapStart = "<div class=\"taglib-custom-attributes-list\">";
	private String markupWrapEnd = "</div>";
	private String markupPodcastEnclosureFields = "<div class=\"aui-field aui-field-wrapper\">"
			+ "<div class=\"aui-field-wrapper-content\">"
			+ "<label class=\"aui-field-label\"> Enclosure Length </label>"
			+ "<span id=\"pqai_enclosure-length\">1000</span>"
			+ "</div>"
			+ "</div>"
			+ "<div class=\"aui-field aui-field-wrapper\">"
			+ "<div class=\"aui-field-wrapper-content\">"
			+ "<label class=\"aui-field-label\"> Enclosure Type </label>"
			+ "<span id=\"znux_enclosure-type\">application/mpeg</span>"
			+ "</div>"
			+ "</div>"
			+ "<div class=\"aui-field aui-field-wrapper\">"
			+ "<div class=\"aui-field-wrapper-content\">"
			+ "<label class=\"aui-field-label\"> Enclosure Url </label>"
			+ "<span id=\"eada_enclosure-url\">http://localhost/something.mp3</span>"
			+ "</div>"
			+ "</div>"
			+ "<div class=\"aui-field aui-field-wrapper\">"
			+ "<div class=\"aui-field-wrapper-content\">"
			+ "<label class=\"aui-field-label\"> Itunes Duration </label>"
			+ "<span id=\"scch_itunes-duration\">1:23:21</span>"
			+ "</div>"
			+ "</div>";
	private String markupExtraCustomField = "<div class=\"aui-field aui-field-wrapper\">"
			+ "<div class=\"aui-field-wrapper-content\">"
			+ "<label class=\"aui-field-label\"> Some extra field </label>"
			+ "<span id=\"bla_bla_extra_field\">some value</span>"
			+ "</div>"
			+ "</div>";

	@Test
	public void testCompleteEliminationWithOnlyPodcastFields() {
		String markup = markupWrapStart + markupPodcastEnclosureFields + markupWrapEnd;
		String cleaned = PseudoCodeForTest.cleanupCustomFields(markup);
		assertEquals("", cleaned);
	}

	@Test
	public void testEliminationOfOnlyPodcastFieldsFirst() {
		String markup = markupWrapStart + markupPodcastEnclosureFields + markupExtraCustomField + markupWrapEnd;
		String cleaned = PseudoCodeForTest.cleanupCustomFields(markup);
		assertEquals(markupWrapStart + markupExtraCustomField + markupWrapEnd, cleaned);
	}

	@Test
	public void testEliminationOfOnlyPodcastFieldsLast() {
		String markup = markupWrapStart + markupExtraCustomField + markupPodcastEnclosureFields + markupWrapEnd;
		String cleaned = PseudoCodeForTest.cleanupCustomFields(markup);
		assertEquals(markupWrapStart + markupExtraCustomField + markupWrapEnd, cleaned);
	}

	
	@Before
	public void setup() {

	}

}
