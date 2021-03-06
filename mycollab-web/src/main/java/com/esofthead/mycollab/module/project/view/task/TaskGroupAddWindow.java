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

import com.esofthead.mycollab.common.i18n.GenericI18Enum;
import com.esofthead.mycollab.common.i18n.OptionI18nEnum;
import com.esofthead.mycollab.eventmanager.EventBusFactory;
import com.esofthead.mycollab.module.project.CurrentProjectVariables;
import com.esofthead.mycollab.module.project.domain.SimpleProjectMember;
import com.esofthead.mycollab.module.project.domain.SimpleTaskList;
import com.esofthead.mycollab.module.project.domain.TaskList;
import com.esofthead.mycollab.module.project.events.TaskListEvent;
import com.esofthead.mycollab.module.project.i18n.TaskGroupI18nEnum;
import com.esofthead.mycollab.module.project.i18n.TaskI18nEnum;
import com.esofthead.mycollab.module.project.service.ProjectTaskListService;
import com.esofthead.mycollab.module.project.view.milestone.MilestoneComboBox;
import com.esofthead.mycollab.module.project.view.settings.component.ProjectMemberSelectionBox;
import com.esofthead.mycollab.module.project.view.settings.component.ProjectMemberSelectionField;
import com.esofthead.mycollab.spring.ApplicationContextUtil;
import com.esofthead.mycollab.vaadin.AppContext;
import com.esofthead.mycollab.vaadin.ui.*;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import org.vaadin.maddon.layouts.MHorizontalLayout;

import java.util.GregorianCalendar;

/**
 * 
 * @author MyCollab Ltd.
 * @since 1.0
 */
public class TaskGroupAddWindow extends Window {
	private static final long serialVersionUID = 1L;

	private ProjectMemberSelectionField projectSelectionField;

	private final TaskGroupDisplayView taskView;
	private SimpleTaskList taskList;
	private TaskListForm taskListForm;

	public TaskGroupAddWindow(final TaskGroupDisplayView taskView) {
		this(taskView, new SimpleTaskList());
	}

	public TaskGroupAddWindow(final TaskGroupDisplayView taskView,
			final SimpleTaskList taskList) {
		super(AppContext.getMessage(TaskI18nEnum.DIALOG_NEW_TASKGROUP_TITLE));
		this.setModal(true);
		this.setResizable(false);
		this.taskView = taskView;
		this.taskList = taskList;
		this.initUI();
	}

	private void initUI() {
		this.setWidth("950px");
		this.taskListForm = new TaskListForm();
		this.taskListForm.setBean(this.taskList);
		this.setContent(this.taskListForm);
		this.center();
	}

	private void notifyToReloadTaskList() {
		if (this.taskView != null) {
			this.taskView.insertTaskList(this.taskList);
		} else {
			EventBusFactory.getInstance().post(
					new TaskListEvent.GotoTaskListScreen(this, null));
		}
	}

	private class TaskListForm extends AdvancedEditBeanForm<TaskList> {
		private static final long serialVersionUID = 1L;

		@Override
		public void setBean(final TaskList newDataSource) {
			this.setFormLayoutFactory(new TaskListFormLayoutFactory());
			this.setBeanFormFieldFactory(new TaskListEditFormFieldFactory(
					TaskListForm.this));
			super.setBean(newDataSource);
		}

		private class TaskListFormLayoutFactory implements IFormLayoutFactory {
			private static final long serialVersionUID = 1L;
			private GridFormLayoutHelper informationLayout;

			@Override
			public ComponentContainer getLayout() {
				final VerticalLayout taskListAddLayout = new VerticalLayout();
				taskListAddLayout.setMargin(false);
				taskListAddLayout.setWidth("100%");

				this.informationLayout = new GridFormLayoutHelper(2, 3, "100%",
						"167px", Alignment.TOP_LEFT);

				final VerticalLayout bodyLayout = new VerticalLayout();
				this.informationLayout.getLayout().setMargin(false);
				this.informationLayout.getLayout().setWidth("100%");
				this.informationLayout.getLayout().addStyleName(
						"colored-gridlayout");
				bodyLayout.addComponent(this.informationLayout.getLayout());

				taskListAddLayout.addComponent(bodyLayout);
				final Layout bottomPanel = this.createBottomPanel();
				taskListAddLayout.addComponent(bottomPanel);
				taskListAddLayout.setComponentAlignment(bottomPanel,
						Alignment.MIDDLE_RIGHT);
				return taskListAddLayout;
			}

			private Layout createBottomPanel() {
				final MHorizontalLayout layout = new MHorizontalLayout().withSpacing(true).withMargin(true);

				final Button saveBtn = new Button(
						AppContext.getMessage(GenericI18Enum.BUTTON_SAVE),
						new Button.ClickListener() {
							private static final long serialVersionUID = 1L;

							@Override
							public void buttonClick(final ClickEvent event) {
								if (TaskGroupAddWindow.TaskListForm.this
										.validateForm()) {
									TaskListForm.this.fieldFactory.commit();
									TaskListFormLayoutFactory.this
											.saveTaskList();
									TaskGroupAddWindow.this.close();
								}
							}
						});
				saveBtn.setStyleName(UIConstants.THEME_GREEN_LINK);
				saveBtn.setIcon(MyCollabResource
						.newResource(WebResourceIds._16_save));
				layout.addComponent(saveBtn);

				final Button saveAndNewBtn = new Button(
						AppContext
								.getMessage(GenericI18Enum.BUTTON_SAVE_NEW),
						new Button.ClickListener() {
							private static final long serialVersionUID = 1L;

							@Override
							public void buttonClick(final ClickEvent event) {
								if (TaskGroupAddWindow.TaskListForm.this
										.validateForm()) {
									TaskListForm.this.fieldFactory.commit();
									TaskListFormLayoutFactory.this
											.saveTaskList();
									TaskGroupAddWindow.this.taskList = new SimpleTaskList();
								}
							}
						});
				saveAndNewBtn.setStyleName(UIConstants.THEME_GREEN_LINK);
				saveAndNewBtn.setIcon(MyCollabResource
						.newResource(WebResourceIds._16_save_new));
				layout.addComponent(saveAndNewBtn);

				final Button cancelBtn = new Button(
						AppContext
								.getMessage(GenericI18Enum.BUTTON_CANCEL),
						new Button.ClickListener() {
							private static final long serialVersionUID = 1L;

							@Override
							public void buttonClick(final ClickEvent event) {
								TaskGroupAddWindow.this.close();
							}
						});
				cancelBtn.setStyleName(UIConstants.THEME_GRAY_LINK);
				layout.addComponent(cancelBtn);

				return layout;
			}

			private void saveTaskList() {
				final ProjectTaskListService taskListService = ApplicationContextUtil
						.getSpringBean(ProjectTaskListService.class);
				TaskGroupAddWindow.this.taskList.setSaccountid(AppContext
						.getAccountId());
				TaskGroupAddWindow.this.taskList.setCreateduser(AppContext
						.getUsername());
				TaskGroupAddWindow.this.taskList
						.setCreatedtime(new GregorianCalendar().getTime());
				TaskGroupAddWindow.this.taskList.setStatus(OptionI18nEnum.StatusI18nEnum.Open.name());
				TaskGroupAddWindow.this.taskList
						.setProjectid(CurrentProjectVariables.getProjectId());

				ProjectMemberSelectionBox prjMemberSelectionBox = projectSelectionField
						.getWrappedComponent();
				Object memberVal = prjMemberSelectionBox.getValue();
				if (memberVal != null) {
					SimpleProjectMember member = (SimpleProjectMember) memberVal;
					TaskGroupAddWindow.this.taskList.setOwner(member
							.getUsername());
					TaskGroupAddWindow.this.taskList.setOwnerAvatarId(member
							.getMemberAvatarId());
					TaskGroupAddWindow.this.taskList.setOwnerFullName(member
							.getDisplayName());
				}

				taskListService.saveWithSession(
						TaskGroupAddWindow.this.taskList,
						AppContext.getUsername());
				TaskGroupAddWindow.this.notifyToReloadTaskList();
				TaskGroupAddWindow.this.close();
			}

			@Override
			public void attachField(final Object propertyId,
					final Field<?> field) {
				if (propertyId.equals("name")) {
					this.informationLayout.addComponent(field, AppContext
							.getMessage(TaskGroupI18nEnum.FORM_NAME_FIELD), 0,
							0, 2, "100%");
				} else if (propertyId.equals("description")) {
					this.informationLayout
							.addComponent(
									field,
									AppContext
											.getMessage(TaskGroupI18nEnum.FORM_DESCRIPTION_FIELD),
									0, 1, 2, "100%");
				} else if (propertyId.equals("owner")) {
					this.informationLayout
							.addComponent(field, AppContext
									.getMessage(GenericI18Enum.FORM_ASSIGNEE),
									0, 2);
				} else if (propertyId.equals("milestoneid")) {
					this.informationLayout.addComponent(field, AppContext
							.getMessage(TaskGroupI18nEnum.FORM_PHASE_FIELD), 1,
							2);
				}
			}
		}

		private class TaskListEditFormFieldFactory extends
				AbstractBeanFieldGroupEditFieldFactory<TaskList> {

			private static final long serialVersionUID = 1L;

			public TaskListEditFormFieldFactory(GenericBeanForm<TaskList> form) {
				super(form);
			}

			@Override
			protected Field<?> onCreateField(final Object propertyId) {
				if (propertyId.equals("description")) {
					final RichTextArea area = new RichTextArea();
					area.setNullRepresentation("");
					return area;
				} else if (propertyId.equals("owner")) {
					projectSelectionField = new ProjectMemberSelectionField();
					return projectSelectionField;
				} else if (propertyId.equals("milestoneid")) {
					return new MilestoneComboBox();
				} else if (propertyId.equals("name")) {
					final TextField tf = new TextField();
					tf.setNullRepresentation("");
					tf.setRequired(true);
					tf.setRequiredError("Please enter task group name");
					return tf;
				}

				return null;
			}
		}
	}
}
