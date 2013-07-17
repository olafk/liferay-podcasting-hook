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

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portlet.blogs.model.BlogsEntry;
import com.liferay.portlet.expando.DuplicateColumnNameException;
import com.liferay.portlet.expando.DuplicateTableNameException;
import com.liferay.portlet.expando.model.ExpandoColumn;
import com.liferay.portlet.expando.model.ExpandoColumnConstants;
import com.liferay.portlet.expando.model.ExpandoTable;
import com.liferay.portlet.expando.service.ExpandoColumnLocalServiceUtil;
import com.liferay.portlet.expando.service.ExpandoTableLocalServiceUtil;

public class ExtBlogUtil {

	/***
	 *  Get a reference to the ExpandoTable (Blog class), 
	 *  create if not yet existing
	 */
	public static ExpandoTable getBlogExpandoTable(long companyId)
			throws PortalException, SystemException {
		ExpandoTable table = null;
	
		try {
		 	table = ExpandoTableLocalServiceUtil.addDefaultTable(
			 	companyId, BlogsEntry.class.getName());
		}
		catch(DuplicateTableNameException dtne) {
		 	table = ExpandoTableLocalServiceUtil.getDefaultTable(
			 	companyId, BlogsEntry.class.getName());
		}
		return table;
	}

	/*
	 * get a reference to the given expando column - create if it doesn't exist yet.
	 */
	public static ExpandoColumn getColumn(long tableId, String name, int type) throws PortalException, SystemException {
		try {
			ExpandoColumn column = ExpandoColumnLocalServiceUtil.addColumn(
				tableId, name, type);
	
			// Add Unicode Properties
	
			UnicodeProperties properties = new UnicodeProperties();
			properties.setProperty(
					ExpandoColumnConstants.INDEX_TYPE, Boolean.FALSE.toString());
			column.setTypeSettingsProperties(properties);
			ExpandoColumnLocalServiceUtil.updateExpandoColumn(column);
			return column;
		}
		catch(DuplicateColumnNameException dcne) {
			return ExpandoColumnLocalServiceUtil.getColumn(
					tableId, name);
		}
	}

}
