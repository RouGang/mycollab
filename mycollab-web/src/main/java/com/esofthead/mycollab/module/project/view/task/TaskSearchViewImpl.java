/**
 * This file is part of mycollab-web.
 *
 * mycollab-web is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mycollab-web is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mycollab-web.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.esofthead.mycollab.module.project.view.task;

import java.util.Arrays;

import org.vaadin.maddon.layouts.MHorizontalLayout;
import org.vaadin.maddon.layouts.MVerticalLayout;

import com.esofthead.mycollab.eventmanager.EventBusFactory;
import com.esofthead.mycollab.module.project.domain.SimpleTask;
import com.esofthead.mycollab.module.project.domain.criteria.TaskSearchCriteria;
import com.esofthead.mycollab.module.project.events.TaskEvent;
import com.esofthead.mycollab.module.project.events.TaskListEvent;
import com.esofthead.mycollab.module.project.i18n.TaskI18nEnum;
import com.esofthead.mycollab.vaadin.AppContext;
import com.esofthead.mycollab.vaadin.events.HasSearchHandlers;
import com.esofthead.mycollab.vaadin.mvp.AbstractPageView;
import com.esofthead.mycollab.vaadin.mvp.ViewComponent;
import com.esofthead.mycollab.vaadin.mvp.ViewScope;
import com.esofthead.mycollab.vaadin.ui.MyCollabResource;
import com.esofthead.mycollab.vaadin.ui.UIConstants;
import com.esofthead.mycollab.vaadin.ui.WebResourceIds;
import com.esofthead.mycollab.vaadin.ui.table.IPagedBeanTable;
import com.esofthead.mycollab.vaadin.ui.table.IPagedBeanTable.TableClickEvent;
import com.esofthead.mycollab.vaadin.ui.table.IPagedBeanTable.TableClickListener;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;

/**
 * 
 * @author MyCollab Ltd.
 * @since 4.0
 * 
 */
@ViewComponent(scope = ViewScope.PROTOTYPE)
public class TaskSearchViewImpl extends AbstractPageView implements
		TaskSearchView {

	private static final long serialVersionUID = 1L;
	
	private TaskSearchPanel taskSearchPanel;
	private TaskSearchTableDisplay tableItem;
	private MVerticalLayout taskListLayout;
	private Label headerText;

	public void setSearchInputValue(String value) {
		taskSearchPanel.setTextField(value);
	}

	public TaskSearchViewImpl() {
		this.taskListLayout = new MVerticalLayout().withMargin(true);
		with(taskListLayout);

		this.taskSearchPanel = new TaskSearchPanel();
		this.generateDisplayTable();

		final MHorizontalLayout header = new MHorizontalLayout()
				.withSpacing(true)
				.withMargin(new MarginInfo(true, false, true, false))
				.withStyleName(UIConstants.HEADER_VIEW).withWidth("100%");
		
		Image titleIcon = new Image(null,
				MyCollabResource.newResource(WebResourceIds._24_project_task));

		headerText = new Label();
		headerText.setSizeUndefined();
		headerText.setStyleName(UIConstants.HEADER_TEXT);

		Button backtoTaskListBtn = new Button(
				AppContext.getMessage(TaskI18nEnum.BUTTON_BACK_TO_DASHBOARD),
				new Button.ClickListener() {
					private static final long serialVersionUID = 1L;

					@Override
					public void buttonClick(ClickEvent event) {
						EventBusFactory.getInstance()
								.post(new TaskListEvent.GotoTaskListScreen(
										this, null));

					}
				});
		backtoTaskListBtn.setStyleName(UIConstants.THEME_GREEN_LINK);
		backtoTaskListBtn.setIcon(MyCollabResource
				.newResource(WebResourceIds._16_back));

		header.with(titleIcon, headerText, backtoTaskListBtn)
				.withAlign(titleIcon, Alignment.TOP_LEFT)
				.withAlign(headerText, Alignment.MIDDLE_LEFT)
				.withAlign(backtoTaskListBtn, Alignment.MIDDLE_RIGHT)
				.expand(headerText);

		taskListLayout.with(header, taskSearchPanel, tableItem);
	}

	private void generateDisplayTable() {
		this.tableItem = new TaskSearchTableDisplay(TaskTableFieldDef.id,
				Arrays.asList(TaskTableFieldDef.taskname,
						TaskTableFieldDef.startdate, TaskTableFieldDef.duedate,
						TaskTableFieldDef.assignee,
						TaskTableFieldDef.percentagecomplete));

		this.tableItem.addTableListener(new TableClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void itemClick(final TableClickEvent event) {
				final SimpleTask task = (SimpleTask) event.getData();
				if ("taskname".equals(event.getFieldName())) {
					EventBusFactory.getInstance().post(
							new TaskEvent.GotoRead(TaskSearchViewImpl.this,
									task.getId()));
				}
			}
		});
	}

	@Override
	public HasSearchHandlers<TaskSearchCriteria> getSearchHandlers() {
		return this.taskSearchPanel;
	}

	@Override
	public IPagedBeanTable<TaskSearchCriteria, SimpleTask> getPagedBeanTable() {
		return this.tableItem;
	}

	@Override
	public void setTitle(String title) {
		headerText.setValue(title);

	}

	@Override
	public void moveToAdvanceSearch() {
		taskSearchPanel.getAdvanceSearch();
	}

	@Override
	public void moveToBasicSearch() {
		taskSearchPanel.getBasicSearch();
	}

}
