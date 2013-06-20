package de.olafkock.liferay.blogs;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Organization;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.blogs.model.BlogsEntry;
import com.liferay.portlet.blogs.service.BlogsEntryService;
import com.liferay.portlet.blogs.service.BlogsEntryServiceWrapper;

import java.util.Date;
import java.util.List;

public class ExtBlogsEntryServiceImpl extends BlogsEntryServiceWrapper {

	public ExtBlogsEntryServiceImpl(BlogsEntryService blogsEntryService) {
		super(blogsEntryService);
	}

	public String getOrganizationEntriesRSS(
			long organizationId, Date displayDate, int status, int max,
			String type, double version, String displayStyle, String feedURL,
			String entryURL, ThemeDisplay themeDisplay)
		throws PortalException, SystemException {
		
		Organization organization = OrganizationLocalServiceUtil.getOrganization(organizationId);

		String name = organization.getName();
		String description = name;
		List<BlogsEntry> blogsEntries = getOrganizationEntries(
			organizationId, displayDate, status, max);

		return PodcastingUtil.exportToRSS(
			name, description, type, version, displayStyle, feedURL, entryURL,
			blogsEntries, themeDisplay);
	}
	

	public String getGroupEntriesRSS(
			long groupId, Date displayDate, int status, int max, String type,
			double version, String displayStyle, String feedURL,
			String entryURL, ThemeDisplay themeDisplay)
		throws PortalException, SystemException {

		Group group = GroupLocalServiceUtil.getGroup(groupId);

		String name = HtmlUtil.escape(group.getDescriptiveName());
		String description = name;
		List<BlogsEntry> blogsEntries = getGroupEntries(
			groupId, displayDate, status, max);

		return PodcastingUtil.exportToRSS(
			name, description, type, version, displayStyle, feedURL, entryURL,
			blogsEntries, themeDisplay);
	}

	public String getCompanyEntriesRSS(
			long companyId, Date displayDate, int status, int max, String type,
			double version, String displayStyle, String feedURL,
			String entryURL, ThemeDisplay themeDisplay)
		throws PortalException, SystemException {

		Company company = CompanyLocalServiceUtil.getCompany(companyId);

		String name = company.getName();
		String description = name;
		List<BlogsEntry> blogsEntries = getCompanyEntries(
			companyId, displayDate, status, max);

		return PodcastingUtil.exportToRSS(
			name, description, type, version, displayStyle, feedURL, entryURL,
			blogsEntries, themeDisplay);
	}

}