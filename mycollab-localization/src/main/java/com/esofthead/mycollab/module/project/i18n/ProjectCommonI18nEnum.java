/**
 * This file is part of mycollab-localization.
 *
 * mycollab-localization is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mycollab-localization is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mycollab-localization.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.esofthead.mycollab.module.project.i18n;

import ch.qos.cal10n.BaseName;
import ch.qos.cal10n.Locale;
import ch.qos.cal10n.LocaleData;

@BaseName("localization/project-common")
@LocaleData(value = { @Locale("en-US"), @Locale("ja-JP") }, defaultCharset = "UTF-8")
public enum ProjectCommonI18nEnum {
	WIDGET_ACTIVE_PROJECTS_TITLE,
	WIDGET_ARCHIVE_PROJECTS_TITLE,
	WIDGET_ALL_PROJECTS_TITLE,
	WIDGET_MEMBERS_TITLE,
	WIDGET_OPEN_ASSIGNMENTS_TITLE,

	TASKS_TITLE,

	FEED_PROJECT_MESSAGE_TITLE,
	FEED_USER_ACTIVITY_CREATE_ACTION_TITLE,
	FEED_USER_ACTIVITY_UPDATE_ACTION_TITLE,
	FEED_USER_ACTIVITY_COMMENT_ACTION_TITLE,
	FEED_PROJECT_USER_ACTIVITY_CREATE_ACTION_TITLE,
	FEED_PROJECT_USER_ACTIVITY_UPDATE_ACTION_TITLE,
	FEED_PROJECT_USER_ACTIVITY_COMMENT_ACTION_TITLE,

	TOOLTIP_GANTT_CHART_TITLE,

	BUTTON_NEW_PROJECT,
	BUTTON_EDIT_PROJECT,
	BUTTON_DELETE_PROJECT,
	BUTTON_ACTIVE_PROJECT,
	BUTTON_ARCHIVE_PROJECT,
	BUTTON_ACTIVE_PROJECTS,
	BUTTON_ARCHIVE_PROJECTS,
	BUTTON_ALL_PROJECTS,

	DIALOG_CONFIRM_PROJECT_DELETE_MESSAGE,
	DIALOG_CONFIRM_PROJECT_ARCHIVE_MESSAGE,

	VIEW_DASHBOARD,
	VIEW_MESSAGE,
	VIEW_MILESTONE,
	VIEW_TASK,
	VIEW_BUG,
	VIEW_FILE,
	VIEW_RISK,
	VIEW_PROBLEM,
	VIEW_TIME,
	VIEW_PAGE,
	VIEW_STANDAUP,
	VIEW_MEMBER,
	VIEW_USERS,
	VIEW_ROLES,
	VIEW_SETTINGS,

	TAB_COMMENT,
	TAB_HISTORY,

	SUB_INFO_PEOPLE,
	ITEM_CREATED_DATE,
	ITEM_UPDATED_DATE,
	SUB_INFO_DATES,
	ITEM_CREATED_PEOPLE,
	ITEM_ASSIGN_PEOPLE,

	M_VIEW_PROJECT_LIST,
	M_VIEW_PROJECT_ACTIVITIES,
	M_VIEW_PROJECT_FOLLOWING_TICKETS,
	M_FEED_USER_ACTIVITY_CREATE_ACTION_TITLE,
	M_FEED_USER_ACTIVITY_UPDATE_ACTION_TITLE,
	M_FEED_PROJECT_ACTIVITY_PREFIX
}
