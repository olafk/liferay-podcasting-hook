package de.olafkock.liferay.blogs;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.blogs.model.BlogsEntry;
import com.liferay.portlet.expando.model.ExpandoColumn;
import com.liferay.portlet.expando.model.ExpandoColumnConstants;
import com.liferay.portlet.expando.model.ExpandoTable;
import com.liferay.portlet.expando.model.ExpandoValue;
import com.liferay.portlet.expando.service.ExpandoValueLocalServiceUtil;
import com.liferay.util.RSSUtil;
import com.sun.syndication.feed.module.itunes.EntryInformation;
import com.sun.syndication.feed.module.itunes.EntryInformationImpl;
import com.sun.syndication.feed.module.itunes.types.Duration;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEnclosureImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PodcastingUtil {

	/*
	 * This method is the only reason for overloading the blog service - as it's
	 * not in the interface (being protected) all the calling methods needed to
	 * be overridden as well.
	 * 
	 * Most of this code is from BlogsEntryServiceImpl.  
	 * It will generate the standard RSS content with added enclosures for the 
	 * entries that have them.
	 */
	public static String exportToRSS(
			String name, String description, String type, double version,
			String displayStyle, String feedURL, String entryURL,
			List<BlogsEntry> blogsEntries, ThemeDisplay themeDisplay)
		throws SystemException, PortalException {
	
		SyndFeed syndFeed = new SyndFeedImpl();
		
		syndFeed.setFeedType(RSSUtil.getFeedType(type, version));
		syndFeed.setTitle(name);
		syndFeed.setLink(feedURL);
		syndFeed.setDescription(description);

		List<SyndEntry> syndEntries = new ArrayList<SyndEntry>();
	
		syndFeed.setEntries(syndEntries);

		for (BlogsEntry entry : blogsEntries) {
			String author = HtmlUtil.escape(
				PortalUtil.getUserName(entry.getUserId(), entry.getUserName()));
	
			StringBundler link = new StringBundler(4);
	
			if (entryURL.endsWith("/blogs/rss")) {
				link.append(entryURL.substring(0, entryURL.length() - 3));
				link.append(entry.getUrlTitle());
			}
			else {
				link.append(entryURL);
	
				if (!entryURL.endsWith(StringPool.QUESTION)) {
					link.append(StringPool.AMPERSAND);
				}
	
				link.append("entryId=");
				link.append(entry.getEntryId());
			}
	
			String value = null;
	
			if (displayStyle.equals(RSSUtil.DISPLAY_STYLE_ABSTRACT)) {
				String summary = entry.getDescription();
	
				if (Validator.isNull(summary)) {
					summary = entry.getContent();
				}
	
				value = StringUtil.shorten(
					HtmlUtil.extractText(summary),
					200 /* PropsValues.BLOGS_RSS_ABSTRACT_LENGTH */, StringPool.BLANK);
			}
			else if (displayStyle.equals(RSSUtil.DISPLAY_STYLE_TITLE)) {
				value = StringPool.BLANK;
			}
			else {
				value = StringUtil.replace(
					entry.getContent(),
					new String[] {
						"href=\"/", "src=\"/"
					},
					new String[] {
						"href=\"" + themeDisplay.getURLPortal() + "/",
						"src=\"" + themeDisplay.getURLPortal() + "/"
					});
			}
	
			SyndEntry syndEntry = new SyndEntryImpl();
	
			syndEntry.setAuthor(author);
			syndEntry.setTitle(entry.getTitle());
			syndEntry.setLink(link.toString());
			syndEntry.setUri(syndEntry.getLink());
			syndEntry.setPublishedDate(entry.getCreateDate());
			syndEntry.setUpdatedDate(entry.getModifiedDate());
	
			SyndEnclosure enclosure = getEnclosure(entry.getCompanyId(), entry.getPrimaryKey());
			if(enclosure != null) {
				List<SyndEnclosure> enclosures = new LinkedList<SyndEnclosure>();
				enclosures.add(enclosure);
				syndEntry.setEnclosures(enclosures);
				EntryInformation info = getItunesInfo(entry);
				ArrayList<EntryInformation> modules = new ArrayList<EntryInformation>();
				modules.add(info);
				syndEntry.setModules(modules);
			}
			
			SyndContent syndContent = new SyndContentImpl();
	
			syndContent.setType(RSSUtil.ENTRY_TYPE_DEFAULT);
			syndContent.setValue(value);
	
			syndEntry.setDescription(syndContent);
	
			syndEntries.add(syndEntry);
		}
	
		try {
			return RSSUtil.export(syndFeed);
		}
		catch (FeedException fe) {
			throw new SystemException(fe);
		}
	}

	/***
	 * 
	 * @param companyId
	 * @param entryId Id for the blog entry
	 * @return Enclosure if exists, null otherwise.
	 * @throws SystemException 
	 * @throws PortalException 
	 */
	private static SyndEnclosure getEnclosure(long companyId, long entryId) throws PortalException, SystemException {
		// Needs some refactoring - e.g. remember the expando columns for the company in order to not fetch it over and over and over again.
		ExpandoTable table = ExtBlogUtil.getBlogExpandoTable(companyId);
		long tableId = table.getTableId();
	
		ExpandoColumn enclosureUrlColumn = ExtBlogUtil.getColumn(tableId, PodcastingKeys.ENCLOSURE_URL, ExpandoColumnConstants.STRING);
		ExpandoColumn enclosureLengthColumn = ExtBlogUtil.getColumn(tableId, PodcastingKeys.ENCLOSURE_LENGTH, ExpandoColumnConstants.LONG);
		ExpandoColumn enclosureTypeColumn = ExtBlogUtil.getColumn(tableId, PodcastingKeys.ENCLOSURE_TYPE, ExpandoColumnConstants.STRING);
	
		ExpandoValue enclosureUrlValue = ExpandoValueLocalServiceUtil.getValue(tableId, enclosureUrlColumn.getColumnId(), entryId);
		ExpandoValue enclosureLengthValue = ExpandoValueLocalServiceUtil.getValue(tableId, enclosureLengthColumn.getColumnId(), entryId);
		ExpandoValue enclosureTypeValue = ExpandoValueLocalServiceUtil.getValue(tableId, enclosureTypeColumn.getColumnId(), entryId);
	
		if(enclosureUrlValue != null) {
			SyndEnclosure enclosure = new SyndEnclosureImpl();
			enclosure.setUrl(enclosureUrlValue.getString());
			enclosure.setLength(enclosureLengthValue.getLong());
			enclosure.setType(enclosureTypeValue.getString());
			return enclosure;
		}
		return null;
	}

	/***
	 * 
	 * @param companyId
	 * @param entryId Id for the blog entry
	 * @return Enclosure if exists, null otherwise.
	 * @throws SystemException 
	 * @throws PortalException 
	 */
	private static EntryInformation getItunesInfo(BlogsEntry entry) throws PortalException, SystemException {
		// Needs some refactoring - e.g. remember the expando columns for the company in order to not fetch it over and over and over again.
		ExpandoTable table = ExtBlogUtil.getBlogExpandoTable(entry.getCompanyId());
		long tableId = table.getTableId();
	
		ExpandoColumn itunesDuration = ExtBlogUtil.getColumn(tableId, PodcastingKeys.ITUNES_DURATION, ExpandoColumnConstants.STRING);
		ExpandoValue itunesDurationValue = ExpandoValueLocalServiceUtil.getValue(tableId, itunesDuration.getColumnId(), entry.getEntryId());
	
		if(itunesDurationValue != null) {
			EntryInformation entryInformation = new EntryInformationImpl();
			entryInformation.setDuration(new Duration(itunesDurationValue.getString()));
			entryInformation.setAuthor(entry.getUserName());
			entryInformation.setSubtitle(entry.getTitle());
			return entryInformation;
		}
		return null;
	}

}
