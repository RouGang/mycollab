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

package com.esofthead.mycollab.module.user.accountsettings.team.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.esofthead.mycollab.core.persistence.service.ISearchableService;
import com.esofthead.mycollab.module.user.accountsettings.view.AccountSettingBreadcrumb;
import com.esofthead.mycollab.module.user.domain.Role;
import com.esofthead.mycollab.module.user.domain.SimpleRole;
import com.esofthead.mycollab.module.user.domain.criteria.RoleSearchCriteria;
import com.esofthead.mycollab.module.user.service.RoleService;
import com.esofthead.mycollab.security.AccessPermissionFlag;
import com.esofthead.mycollab.security.RolePermissionCollections;
import com.esofthead.mycollab.spring.ApplicationContextUtil;
import com.esofthead.mycollab.vaadin.AppContext;
import com.esofthead.mycollab.vaadin.desktop.ui.DefaultMassEditActionHandler;
import com.esofthead.mycollab.vaadin.desktop.ui.ListSelectionPresenter;
import com.esofthead.mycollab.vaadin.events.MassItemActionHandler;
import com.esofthead.mycollab.vaadin.mvp.ScreenData;
import com.esofthead.mycollab.vaadin.mvp.ViewManager;
import com.esofthead.mycollab.vaadin.mvp.ViewPermission;
import com.esofthead.mycollab.vaadin.ui.MailFormWindow;
import com.esofthead.mycollab.vaadin.ui.NotificationUtil;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.UI;

/**
 * 
 * @author MyCollab Ltd.
 * @since 1.0
 */
@ViewPermission(permissionId = RolePermissionCollections.ACCOUNT_ROLE, impliedPermissionVal = AccessPermissionFlag.READ_ONLY)
public class RoleListPresenter extends
		ListSelectionPresenter<RoleListView, RoleSearchCriteria, SimpleRole> {
	private static final long serialVersionUID = 1L;

	private RoleService roleService;

	public RoleListPresenter() {
		super(RoleListView.class);
		roleService = ApplicationContextUtil.getSpringBean(RoleService.class);
	}

	@Override
	protected void postInitView() {
		super.postInitView();

		view.getPopupActionHandlers().addMassItemActionHandler(
				new DefaultMassEditActionHandler(this) {

					@Override
					protected void onSelectExtra(String id) {
						if (MassItemActionHandler.MAIL_ACTION.equals(id)) {
							UI.getCurrent().addWindow(new MailFormWindow());
						}

					}

					@Override
					protected String getReportTitle() {
						return "Role List";
					}

					@Override
					protected Class<?> getReportModelClassType() {
						return SimpleRole.class;
					}
				});
	}

	@Override
	protected void deleteSelectedItems() {
		if (!isSelectAll) {
			Collection<SimpleRole> currentDataList = view.getPagedBeanTable()
					.getCurrentDataList();
			List<Integer> keyList = new ArrayList<Integer>();
			for (Role item : currentDataList) {
				if (item.isSelected()
						&& (item.getIssystemrole() == null || item
								.getIssystemrole() == Boolean.FALSE)) {
					keyList.add(item.getId());
				} else {
					NotificationUtil
							.showErrorNotification("Can not delete role "
									+ item.getRolename()
									+ " because it is the system role.");
				}
			}

			if (keyList.size() > 0) {
				roleService.massRemoveWithSession(keyList,
						AppContext.getUsername(), AppContext.getAccountId());
				doSearch(searchCriteria);
			}
		} else {
			roleService.removeByCriteria(searchCriteria,
					AppContext.getAccountId());
			doSearch(searchCriteria);
		}

	}

	@Override
	protected void onGo(ComponentContainer container, ScreenData<?> data) {
		if (AppContext.canRead(RolePermissionCollections.ACCOUNT_ROLE)) {
			RoleContainer roleContainer = (RoleContainer) container;
			roleContainer.removeAllComponents();
			roleContainer.addComponent(view.getWidget());
			searchCriteria = (RoleSearchCriteria) data.getParams();
			doSearch(searchCriteria);

			AccountSettingBreadcrumb breadcrumb = ViewManager
					.getCacheComponent(AccountSettingBreadcrumb.class);
			breadcrumb.gotoRoleList();
		} else {
			NotificationUtil.showMessagePermissionAlert();
		}
	}

	@Override
	public ISearchableService<RoleSearchCriteria> getSearchService() {
		return ApplicationContextUtil.getSpringBean(RoleService.class);
	}
}
