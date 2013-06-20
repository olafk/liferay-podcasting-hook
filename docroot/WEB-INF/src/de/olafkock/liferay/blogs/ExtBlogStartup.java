package de.olafkock.liferay.blogs;

import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.SimpleAction;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Company;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portlet.expando.model.ExpandoColumnConstants;
import com.liferay.portlet.expando.model.ExpandoTable;

import java.util.List;

public class ExtBlogStartup extends SimpleAction {
	/* (non-Java-doc)
	 * @see com.liferay.portal.kernel.events.SimpleAction#SimpleAction()
	 */
	public ExtBlogStartup() {
		super();
	}

	/* (non-Java-doc)
	 * @see com.liferay.portal.kernel.events.SimpleAction#run(String[] ids)
	 */
	public void run(String[] ids) throws ActionException {
		try {
			System.out.println("ExtBlog Startup - creating required expando attributes");
			List<Company> companies = CompanyLocalServiceUtil.getCompanies();
			for(Company company: companies) {
				ExpandoTable table = ExtBlogUtil.getBlogExpandoTable(company.getCompanyId());
				long tableId = table.getTableId();
				ExtBlogUtil.getColumn(tableId, PodcastingKeys.ENCLOSURE_URL, ExpandoColumnConstants.STRING);
				ExtBlogUtil.getColumn(tableId, PodcastingKeys.ENCLOSURE_LENGTH, ExpandoColumnConstants.LONG);
				ExtBlogUtil.getColumn(tableId, PodcastingKeys.ENCLOSURE_TYPE, ExpandoColumnConstants.STRING);
				ExtBlogUtil.getColumn(tableId, PodcastingKeys.ITUNES_DURATION, ExpandoColumnConstants.STRING);
			}
		} catch (SystemException e) {
			throw new ActionException(e);
		} catch (PortalException e) {
			throw new ActionException(e);
		}
	}

}