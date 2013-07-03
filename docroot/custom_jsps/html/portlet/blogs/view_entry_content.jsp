<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@page import="com.liferay.portal.kernel.util.StringUtil"%>
<%@page import="com.liferay.portal.kernel.util.HtmlUtil"%>
<%@page import="com.liferay.portal.util.PortalUtil"%>
<%@page import="com.liferay.portlet.blogs.model.BlogsEntry"%>
<%@page import="com.liferay.portlet.expando.service.ExpandoTableLocalServiceUtil"%>
<%@page import="com.liferay.portlet.expando.service.ExpandoColumnLocalServiceUtil"%>
<%@page import="com.liferay.portlet.expando.service.ExpandoValueLocalServiceUtil"%>
<%@page import="com.liferay.portlet.expando.model.ExpandoValue"%>
<%@page import="com.liferay.portlet.expando.model.ExpandoTable"%>
<%@page import="com.liferay.portlet.expando.model.ExpandoColumn"%>
<%@ include file="/html/portlet/blogs/init.jsp" %>

<%
SearchContainer searchContainer = (SearchContainer)request.getAttribute("view_entry_content.jsp-searchContainer");

BlogsEntry entry = (BlogsEntry)request.getAttribute("view_entry_content.jsp-entry");

AssetEntry assetEntry = (AssetEntry)request.getAttribute("view_entry_content.jsp-assetEntry");
%>

<c:choose>
	<c:when test="<%= BlogsEntryPermission.contains(permissionChecker, entry, ActionKeys.VIEW) && (entry.isVisible() || (entry.getUserId() == user.getUserId()) || BlogsEntryPermission.contains(permissionChecker, entry, ActionKeys.UPDATE)) %>">
		<div class="entry <%= WorkflowConstants.toLabel(entry.getStatus()) %>">
			<div class="entry-content">

				<%
				String strutsAction = ParamUtil.getString(request, "struts_action");
				%>

				<c:if test="<%= !entry.isApproved() %>">
					<h3>
						<liferay-ui:message key='<%= entry.isPending() ? "pending-approval" : WorkflowConstants.toLabel(entry.getStatus()) %>' />
					</h3>
				</c:if>

				<portlet:renderURL var="viewEntryURL">
					<portlet:param name="struts_action" value="/blogs/view_entry" />
					<portlet:param name="redirect" value="<%= currentURL %>" />
					<portlet:param name="urlTitle" value="<%= entry.getUrlTitle() %>" />
				</portlet:renderURL>

				<c:if test='<%= !strutsAction.equals("/blogs/view_entry") %>'>
					<div class="entry-title">
						<h2><aui:a href="<%= viewEntryURL %>"><%= HtmlUtil.escape(entry.getTitle()) %></aui:a></h2>
					</div>
				</c:if>

				<div class="entry-date">
					<%= dateFormatDateTime.format(entry.getDisplayDate()) %>
				</div>
			</div>

			<portlet:renderURL var="bookmarkURL" windowState="<%= WindowState.NORMAL.toString() %>">
				<portlet:param name="struts_action" value="/blogs/view_entry" />
				<portlet:param name="urlTitle" value="<%= entry.getUrlTitle() %>" />
			</portlet:renderURL>

			<c:if test='<%= enableSocialBookmarks && socialBookmarksDisplayPosition.equals("top") %>'>
				<liferay-ui:social-bookmarks
					displayStyle="<%= socialBookmarksDisplayStyle %>"
					target="_blank"
					title="<%= entry.getTitle() %>"
					url="<%= PortalUtil.getCanonicalURL(bookmarkURL.toString(), themeDisplay, layout) %>"
				/>
			</c:if>

			<c:if test="<%= BlogsEntryPermission.contains(permissionChecker, entry, ActionKeys.DELETE) || BlogsEntryPermission.contains(permissionChecker, entry, ActionKeys.PERMISSIONS) || BlogsEntryPermission.contains(permissionChecker, entry, ActionKeys.UPDATE) %>">
				<div class="lfr-meta-actions edit-actions entry">
					<table class="lfr-table">
					<tr>
						<c:if test="<%= BlogsEntryPermission.contains(permissionChecker, entry, ActionKeys.UPDATE) %>">
							<td>
								<portlet:renderURL var="editEntryURL">
									<portlet:param name="struts_action" value="/blogs/edit_entry" />
									<portlet:param name="redirect" value="<%= currentURL %>" />
									<portlet:param name="backURL" value="<%= currentURL %>" />
									<portlet:param name="entryId" value="<%= String.valueOf(entry.getEntryId()) %>" />
								</portlet:renderURL>

								<liferay-ui:icon
									image="edit"
									label="<%= true %>"
									url="<%= editEntryURL %>"
								/>
							</td>
						</c:if>

						<c:if test="<%= showEditEntryPermissions && BlogsEntryPermission.contains(permissionChecker, entry, ActionKeys.PERMISSIONS) %>">
							<td>
								<liferay-security:permissionsURL
									modelResource="<%= BlogsEntry.class.getName() %>"
									modelResourceDescription="<%= entry.getTitle() %>"
									resourcePrimKey="<%= String.valueOf(entry.getEntryId()) %>"
									var="permissionsEntryURL"
								/>

								<liferay-ui:icon
									image="permissions"
									label="<%= true %>"
									url="<%= permissionsEntryURL %>"
								/>
							</td>
						</c:if>

						<c:if test="<%= BlogsEntryPermission.contains(permissionChecker, entry, ActionKeys.DELETE) %>">
							<td>
								<portlet:renderURL var="viewURL">
									<portlet:param name="struts_action" value="/blogs/view" />
								</portlet:renderURL>

								<portlet:actionURL var="deleteEntryURL">
									<portlet:param name="struts_action" value="/blogs/edit_entry" />
									<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.DELETE %>" />
									<portlet:param name="redirect" value="<%= viewURL %>" />
									<portlet:param name="entryId" value="<%= String.valueOf(entry.getEntryId()) %>" />
								</portlet:actionURL>

								<liferay-ui:icon-delete
									label="<%= true %>"
									url="<%= deleteEntryURL %>"
								/>
							</td>
						</c:if>
					</tr>
					</table>
				</div>
			</c:if>

			<div class="entry-body">
				<c:choose>
					<c:when test='<%= pageDisplayStyle.equals(RSSUtil.DISPLAY_STYLE_ABSTRACT) && !strutsAction.equals("/blogs/view_entry") %>'>
						<c:if test="<%= entry.isSmallImage() %>">

							<%
							String src = StringPool.BLANK;

							if (Validator.isNotNull(entry.getSmallImageURL())) {
								src = entry.getSmallImageURL();
							}
							else {
								src = themeDisplay.getPathImage() + "/blogs/article?img_id=" + entry.getSmallImageId() + "&t=" + WebServerServletTokenUtil.getToken(entry.getSmallImageId());
							}
							%>

							<div class="asset-small-image">
								<img alt="" class="asset-small-image" src="<%= HtmlUtil.escape(src) %>" width="150" />
							</div>
						</c:if>

						<%
						String summary = entry.getDescription();

						if (Validator.isNull(summary)) {
							summary = entry.getContent();
						}
						%>

						<%= StringUtil.shorten(HtmlUtil.stripHtml(summary), pageAbstractLength) %>

						<br />

						 <aui:a href="<%= viewEntryURL %>"><liferay-ui:message arguments='<%= new Object[] {"aui-helper-hidden-accessible", entry.getTitle()} %>' key="read-more-x-about-x" /> &raquo;</aui:a>
					</c:when>
					<c:when test='<%= pageDisplayStyle.equals(RSSUtil.DISPLAY_STYLE_FULL_CONTENT) || strutsAction.equals("/blogs/view_entry") %>'>
						<%= entry.getContent() %>
						<!-- showing just the non-podcasting related custom fields. -->
						<!-- this can be solved a lot more elegant after LPS-33455 has been fixed (see comment) -->
						<!-- e.g. starting with 6.2 -->
						<liferay-ui:custom-attributes-available className="<%= BlogsEntry.class.getName() %>"> <!-- ignoreAttributeNames="enclosure-url,enclosure-length,enclosure-type" -->
						<liferay-util:buffer var="tempCustomFieldMarkup">
							<liferay-ui:custom-attribute-list
								className="<%= BlogsEntry.class.getName() %>"
								classPK="<%= entry.getEntryId() %>"
								editable="<%= false %>"
								label="<%= true %>"
							/>
						</liferay-util:buffer>
						<%=cleanupCustomFields(tempCustomFieldMarkup) %>
						<!-- However, we want to show an icon for the content... -->
						<%=getIconMarkup(request, company.getPrimaryKey(), entry.getEntryId()) %>
						</liferay-ui:custom-attributes-available>
					</c:when>
					<c:when test='<%= pageDisplayStyle.equals(RSSUtil.DISPLAY_STYLE_TITLE) && !strutsAction.equals("/blogs/view_entry") %>'>
						<aui:a href="<%= viewEntryURL %>"><liferay-ui:message arguments='<%= new Object[] {"aui-helper-hidden-accessible", entry.getTitle()} %>' key="read-more-x-about-x" /> &raquo;</aui:a>
					</c:when>
				</c:choose>
			</div>

			<div class="entry-footer">
				<div class="entry-author">
					<liferay-ui:message key="written-by" /> <%= HtmlUtil.escape(PortalUtil.getUserName(entry.getUserId(), entry.getUserName())) %>
				</div>

				<div class="stats">
					<c:if test="<%= assetEntry != null %>">
						<span class="view-count">
							<c:choose>
								<c:when test="<%= assetEntry.getViewCount() == 1 %>">
									<%= assetEntry.getViewCount() %> <liferay-ui:message key="view" />,
								</c:when>
								<c:when test="<%= assetEntry.getViewCount() > 1 %>">
									<%= assetEntry.getViewCount() %> <liferay-ui:message key="views" />,
								</c:when>
							</c:choose>
						</span>
					</c:if>

					<c:if test="<%= enableComments %>">
						<span class="comments">

							<%
							long classNameId = PortalUtil.getClassNameId(BlogsEntry.class.getName());

							int messagesCount = MBMessageLocalServiceUtil.getDiscussionMessagesCount(classNameId, entry.getEntryId(), WorkflowConstants.STATUS_APPROVED);
							%>

							<c:choose>
								<c:when test='<%= strutsAction.equals("/blogs/view_entry") %>'>
									<%= messagesCount %> <liferay-ui:message key='<%= (messagesCount == 1) ? "comment" : "comments" %>' />
								</c:when>
								<c:otherwise>
									<aui:a href='<%= PropsValues.PORTLET_URL_ANCHOR_ENABLE ? viewEntryURL : viewEntryURL + StringPool.POUND + "blogsCommentsPanelContainer" %>'><%= messagesCount %> <liferay-ui:message key='<%= (messagesCount == 1) ? "comment" : "comments" %>' /></aui:a>
								</c:otherwise>
							</c:choose>
						</span>
					</c:if>
				</div>

				<c:if test="<%= enableFlags %>">
					<liferay-ui:flags
						className="<%= BlogsEntry.class.getName() %>"
						classPK="<%= entry.getEntryId() %>"
						contentTitle="<%= entry.getTitle() %>"
						reportedUserId="<%= entry.getUserId() %>"
					/>
				</c:if>

				<span class="entry-categories">
					<liferay-ui:asset-categories-summary
						className="<%= BlogsEntry.class.getName() %>"
						classPK="<%= entry.getEntryId() %>"
						portletURL="<%= renderResponse.createRenderURL() %>"
					/>
				</span>

				<span class="entry-tags">
					<liferay-ui:asset-tags-summary
						className="<%= BlogsEntry.class.getName() %>"
						classPK="<%= entry.getEntryId() %>"
						portletURL="<%= renderResponse.createRenderURL() %>"
					/>
				</span>

				<c:if test='<%= pageDisplayStyle.equals(RSSUtil.DISPLAY_STYLE_FULL_CONTENT) || strutsAction.equals("/blogs/view_entry") %>'>
					<c:if test="<%= enableRelatedAssets %>">
						<div class="entry-links">
							<liferay-ui:asset-links
								assetEntryId="<%= (assetEntry != null) ? assetEntry.getEntryId() : 0 %>"
								className="<%= BlogsEntry.class.getName() %>"
								classPK="<%= entry.getEntryId() %>"
							/>
						</div>
					</c:if>

					<c:if test='<%= enableSocialBookmarks && socialBookmarksDisplayPosition.equals("bottom") %>'>
						<liferay-ui:social-bookmarks
							displayStyle="<%= socialBookmarksDisplayStyle %>"
							target="_blank"
							title="<%= entry.getTitle() %>"
							url="<%= PortalUtil.getCanonicalURL(bookmarkURL.toString(), themeDisplay, layout) %>"
						/>
					</c:if>

					<c:if test="<%= enableRatings %>">
						<liferay-ui:ratings
							className="<%= BlogsEntry.class.getName() %>"
							classPK="<%= entry.getEntryId() %>"
						/>
					</c:if>
				</c:if>
			</div>
		</div>

		<div class="separator"><!-- --></div>
	</c:when>
	<c:otherwise>

		<%
		if (searchContainer != null) {
			searchContainer.setTotal(searchContainer.getTotal() - 1);
		}
		%>

	</c:otherwise>
</c:choose>

<%!
	/**
	 * Stupid way to display an icon - assume this is contained in the portal.
	 * This can be achieved by deploying it - like a jsp - with a hook. 
	 * The location for icons is: "/html/portlet/blogs/enclosure/", 
	 * followed by enclosure type (e.g. "audio/mpeg"), then ".png"
	 * In fact, with release 1, audio/mpeg.png is the only icon contained, but it
	 * can be extended with individual hooks introducing more icons.
	 */
	 
	protected static String getIconMarkup(HttpServletRequest request, long companyId, long entryId) {
		try {
			ExpandoValue urlValue = ExpandoValueLocalServiceUtil.getValue(companyId, BlogsEntry.class.getName(), "CUSTOM_FIELDS", "enclosure-url", entryId);
			ExpandoValue typeValue = ExpandoValueLocalServiceUtil.getValue(companyId, BlogsEntry.class.getName(), "CUSTOM_FIELDS", "enclosure-type", entryId);
			ExpandoValue lengthValue = ExpandoValueLocalServiceUtil.getValue(companyId, BlogsEntry.class.getName(), "CUSTOM_FIELDS", "enclosure-length", entryId);
			
			if(urlValue != null && typeValue != null && !urlValue.getString().isEmpty() && !typeValue.getString().isEmpty()) {
				String url = HtmlUtil.escapeAttribute(urlValue.getString());
				String type = HtmlUtil.escapeAttribute(typeValue.getString());
				// I know, incorrect localization. I didn't want to introduce more language keys.
				String length = LanguageUtil.get(request.getLocale(), "download") +
						" " + lengthValue.getLong() + " " +
						LanguageUtil.get(request.getLocale(), "bytes");

				String iconUrl = PortalUtil.getPortalURL(request) + 
						"/html/portlet/blogs/enclosure/" + type + ".png";
				String result = "<a href=\"" + url + "\">" +
						"<img src=\"" + iconUrl + "\" " +
						"alt=\"" + type + "\" " + 
						"title=\"" + length +"\" /></a>";
				return result;
			}
		} catch(Exception ignore) {
			ignore.printStackTrace();
			return "<!-- " + ignore.toString() + " -->";
		}
		return "";
	}

	private static final String token = "<div class=\"aui-field aui-field-wrapper\">";

	/**
	 * This code is manually copied from the class PseudoCodeForTest after the 
	 * appropriate JUnit tests have turned green. See the remarks at that class
	 * for reasons.
	 */
	public static String cleanupCustomFields(String markup) {
		String[] tokens = markup.trim().split(token);
		String result = "";
		System.out.println(tokens.length);
		// element 0 is an outer <div> element, ignore here
		// last element contains closing </div> for outer element: Eliminate
		int last = tokens.length-1;
		tokens[last] = tokens[last].substring(0, tokens[last].length()-"</div>".length());

		for(int i = 1; i < tokens.length; i++) {
			if(tokens[i].contains("enclosure-type") || tokens[i].contains("enclosure-length") || tokens[i].contains("enclosure-url") || tokens[i].contains("itunes-duration") ) {
				continue;
			}
			result += token + tokens[i];
		}
		if(result.length() > 0) {
			result = "<div class=\"taglib-custom-attributes-list\">" + result + "</div>";
		}
		return result;
	}



%>
