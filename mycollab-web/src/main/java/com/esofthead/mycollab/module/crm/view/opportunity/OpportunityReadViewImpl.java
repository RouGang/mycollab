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
package com.esofthead.mycollab.module.crm.view.opportunity;

import com.esofthead.mycollab.common.ModuleNameConstants;
import com.esofthead.mycollab.core.arguments.NumberSearchField;
import com.esofthead.mycollab.core.arguments.SearchField;
import com.esofthead.mycollab.core.arguments.StringSearchField;
import com.esofthead.mycollab.module.crm.CrmLinkGenerator;
import com.esofthead.mycollab.module.crm.CrmResources;
import com.esofthead.mycollab.module.crm.CrmTypeConstants;
import com.esofthead.mycollab.module.crm.domain.SimpleActivity;
import com.esofthead.mycollab.module.crm.domain.SimpleContactOpportunityRel;
import com.esofthead.mycollab.module.crm.domain.SimpleLead;
import com.esofthead.mycollab.module.crm.domain.SimpleOpportunity;
import com.esofthead.mycollab.module.crm.domain.criteria.ActivitySearchCriteria;
import com.esofthead.mycollab.module.crm.i18n.CrmCommonI18nEnum;
import com.esofthead.mycollab.module.crm.i18n.LeadI18nEnum;
import com.esofthead.mycollab.module.crm.service.LeadService;
import com.esofthead.mycollab.module.crm.ui.components.*;
import com.esofthead.mycollab.module.crm.view.activity.ActivityRelatedItemListComp;
import com.esofthead.mycollab.security.RolePermissionCollections;
import com.esofthead.mycollab.spring.ApplicationContextUtil;
import com.esofthead.mycollab.vaadin.AppContext;
import com.esofthead.mycollab.vaadin.events.HasPreviewFormHandlers;
import com.esofthead.mycollab.vaadin.mvp.ViewComponent;
import com.esofthead.mycollab.vaadin.ui.*;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 
 * @author MyCollab Ltd.
 * @since 2.0
 * 
 */
@ViewComponent
public class OpportunityReadViewImpl extends
		AbstractPreviewItemComp<SimpleOpportunity> implements
		OpportunityReadView {

	private static final long serialVersionUID = 1L;

	protected OpportunityContactListComp associateContactList;
	protected OpportunityLeadListComp associateLeadList;
	protected NoteListItems noteListItems;
	protected ActivityRelatedItemListComp associateActivityList;

	private PeopleInfoComp peopleInfoComp;
	private DateInfoComp dateInfoComp;
	private CrmFollowersComp<SimpleOpportunity> followersComp;

	public OpportunityReadViewImpl() {
		super(MyCollabResource.newResource(WebResourceIds._22_crm_opportunity));
	}

	@Override
	protected AdvancedPreviewBeanForm<SimpleOpportunity> initPreviewForm() {
		return new AdvancedPreviewBeanForm<SimpleOpportunity>() {
			private static final long serialVersionUID = 1L;

			@Override
			public void showHistory() {
				OpportunityHistoryLogWindow historyLog = new OpportunityHistoryLogWindow(
						ModuleNameConstants.CRM, CrmTypeConstants.OPPORTUNITY);
				historyLog.loadHistory(beanItem.getId());
				UI.getCurrent().addWindow(historyLog);
			}
		};
	}

	@Override
	protected ComponentContainer createButtonControls() {
		return new CrmPreviewFormControlsGenerator<>(
				previewForm)
				.createButtonControls(RolePermissionCollections.CRM_OPPORTUNITY);
	}

	@Override
	protected ComponentContainer createBottomPanel() {
		return noteListItems;
	}

	@Override
	protected void onPreviewItem() {
		displayNotes();
		displayActivities();
		displayContacts();
		displayLeads();

		dateInfoComp.displayEntryDateTime(beanItem);
		peopleInfoComp.displayEntryPeople(beanItem);
		followersComp.displayFollowers(beanItem);

		previewItemContainer.selectTab("about");
		
		previewLayout.resetTitleStyle();

		String saleState = this.beanItem.getSalesstage();
		Date closeDate = this.beanItem.getExpectedcloseddate();
		if ((!"Closed Won".equals(saleState) && !"Closed Lost"
				.equals(saleState))
				&& closeDate != null
				&& (closeDate.before(new GregorianCalendar().getTime()))) {
			previewLayout.setTitleStyleName("hdr-text-overdue");
		}
	}

	@Override
	protected String initFormTitle() {
		// check if there is converted lead associates with this account
		LeadService leadService = ApplicationContextUtil
				.getSpringBean(LeadService.class);
		SimpleLead lead = leadService.findConvertedLeadOfOpportunity(
				beanItem.getId(), AppContext.getAccountId());
		if (lead != null) {
			return "<h2>"
					+ beanItem.getOpportunityname()
					+ AppContext
							.getMessage(
									LeadI18nEnum.CONVERT_FROM_LEAD_TITLE,
									CrmResources
											.getResourceLink(CrmTypeConstants.LEAD),
									CrmLinkGenerator.generateCrmItemLink(
											CrmTypeConstants.LEAD, lead.getId()),
									lead.getLeadName()) + "</h2>";
		} else {
			return beanItem.getOpportunityname();
		}
	}

	@Override
	protected void initRelatedComponents() {
		associateContactList = new OpportunityContactListComp();
		associateLeadList = new OpportunityLeadListComp();
		associateActivityList = new ActivityRelatedItemListComp(true);
		noteListItems = new NoteListItems(
				AppContext.getMessage(CrmCommonI18nEnum.TAB_NOTE));

		CssLayout navigatorWrapper = previewItemContainer.getNavigatorWrapper();
		VerticalLayout basicInfo = new VerticalLayout();
		basicInfo.setWidth("100%");
		basicInfo.setMargin(true);
		basicInfo.setSpacing(true);
		basicInfo.setStyleName("basic-info");

		dateInfoComp = new DateInfoComp();
		basicInfo.addComponent(dateInfoComp);

		peopleInfoComp = new PeopleInfoComp();
		basicInfo.addComponent(peopleInfoComp);

		followersComp = new CrmFollowersComp<>(
				CrmTypeConstants.OPPORTUNITY,
				RolePermissionCollections.CRM_OPPORTUNITY);
		basicInfo.addComponent(followersComp);

		navigatorWrapper.addComponentAsFirst(basicInfo);

		previewItemContainer.addTab(previewContent, "about",
				AppContext.getMessage(CrmCommonI18nEnum.TAB_ABOUT));
		previewItemContainer.addTab(associateContactList, "contact",
				AppContext.getMessage(CrmCommonI18nEnum.TAB_CONTACT));
		previewItemContainer.addTab(associateLeadList, "lead",
				AppContext.getMessage(CrmCommonI18nEnum.TAB_LEAD));
		previewItemContainer.addTab(associateActivityList, "activity",
				AppContext.getMessage(CrmCommonI18nEnum.TAB_ACTIVITY));
	}

	@Override
	protected IFormLayoutFactory initFormLayoutFactory() {
		return new DynaFormLayout(CrmTypeConstants.OPPORTUNITY,
				OpportunityDefaultDynaFormLayoutFactory.getForm());
	}

	@Override
	protected AbstractBeanFieldGroupViewFieldFactory<SimpleOpportunity> initBeanFormFieldFactory() {
		return new OpportunityReadFormFieldFactory(previewForm);
	}

	protected void displayNotes() {
		noteListItems.showNotes(CrmTypeConstants.OPPORTUNITY, beanItem.getId());
	}

	public SimpleOpportunity getOpportunity() {
		return beanItem;
	}

	protected void displayActivities() {
		ActivitySearchCriteria criteria = new ActivitySearchCriteria();
		criteria.setSaccountid(new NumberSearchField(AppContext.getAccountId()));
		criteria.setType(new StringSearchField(SearchField.AND,
				CrmTypeConstants.OPPORTUNITY));
		criteria.setTypeid(new NumberSearchField(beanItem.getId()));
		associateActivityList.setSearchCriteria(criteria);
	}

	protected void displayContacts() {
		associateContactList.displayContacts(beanItem);
	}

	protected void displayLeads() {
		associateLeadList.displayLeads(beanItem);
	}

	@Override
	public SimpleOpportunity getItem() {
		return beanItem;
	}

	@Override
	public HasPreviewFormHandlers<SimpleOpportunity> getPreviewFormHandlers() {
		return previewForm;
	}

	@Override
	public IRelatedListHandlers<SimpleActivity> getRelatedActivityHandlers() {
		return associateActivityList;
	}

	@Override
	public IRelatedListHandlers<SimpleContactOpportunityRel> getRelatedContactHandlers() {
		return associateContactList;
	}

	@Override
	public IRelatedListHandlers<SimpleLead> getRelatedLeadHandlers() {
		return associateLeadList;
	}
}
