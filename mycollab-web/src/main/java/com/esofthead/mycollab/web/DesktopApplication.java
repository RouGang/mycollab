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
package com.esofthead.mycollab.web;

import static com.esofthead.mycollab.common.MyCollabSession.CURRENT_APP;
import static com.esofthead.mycollab.common.MyCollabSession.EVENT_BUS_VAL;

import java.util.Collection;

import javax.servlet.http.Cookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.dialogs.ConfirmDialog;

import com.esofthead.mycollab.common.MyCollabSession;
import com.esofthead.mycollab.common.i18n.GenericI18Enum;
import com.esofthead.mycollab.configuration.PasswordEncryptHelper;
import com.esofthead.mycollab.configuration.SiteConfiguration;
import com.esofthead.mycollab.core.ResourceNotFoundException;
import com.esofthead.mycollab.core.UnsupportedFeatureException;
import com.esofthead.mycollab.core.UserInvalidInputException;
import com.esofthead.mycollab.eventmanager.EventBusFactory;
import com.esofthead.mycollab.module.billing.SubDomainNotExistException;
import com.esofthead.mycollab.module.billing.UsageExceedBillingPlanException;
import com.esofthead.mycollab.module.user.view.LoginPresenter;
import com.esofthead.mycollab.module.user.view.LoginView;
import com.esofthead.mycollab.shell.ShellController;
import com.esofthead.mycollab.shell.events.ShellEvent;
import com.esofthead.mycollab.shell.view.MainWindowContainer;
import com.esofthead.mycollab.shell.view.NoSubDomainExistedWindow;
import com.esofthead.mycollab.shell.view.ShellUrlResolver;
import com.esofthead.mycollab.vaadin.AppContext;
import com.esofthead.mycollab.vaadin.MyCollabUI;
import com.esofthead.mycollab.vaadin.mvp.ControllerRegistry;
import com.esofthead.mycollab.vaadin.mvp.PresenterResolver;
import com.esofthead.mycollab.vaadin.mvp.ViewManager;
import com.esofthead.mycollab.vaadin.ui.ConfirmDialogExt;
import com.esofthead.mycollab.vaadin.ui.NotificationUtil;
import com.google.common.eventbus.Subscribe;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.Page.UriFragmentChangedListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

/**
 * 
 * @author MyCollab Ltd.
 * @since 1.0
 * 
 */
@Theme("mycollab")
@Widgetset("com.esofthead.mycollab.widgetset.MyCollabWidgetSet")
public class DesktopApplication extends MyCollabUI {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory
			.getLogger(DesktopApplication.class);

	private MainWindowContainer mainWindowContainer;

	public static final String NAME_COOKIE = "mycollab";

	public static ShellUrlResolver rootUrlResolver = new ShellUrlResolver();

	@Override
	protected void init(VaadinRequest request) {
		LOG.debug("Init mycollab application {} associate with session {}",
				this.toString(), VaadinSession.getCurrent());
		LOG.debug("Register default error handler");

		VaadinSession.getCurrent().setErrorHandler(new DefaultErrorHandler() {
			private static final long serialVersionUID = 1L;

			@Override
			public void error(com.vaadin.server.ErrorEvent event) {
				Throwable e = event.getThrowable();
				handleException(e);
			}
		});

		initialUrl = this.getPage().getUriFragment();
		MyCollabSession.putVariable(CURRENT_APP, this);
		currentContext = new AppContext(this);
		postSetupApp(request);
		try {
			currentContext.initDomain(initialSubDomain);
		} catch (SubDomainNotExistException e) {
			this.setContent(new NoSubDomainExistedWindow(initialSubDomain));
			return;
		}

		mainWindowContainer = new MainWindowContainer();
		this.setContent(mainWindowContainer);

		getPage().addUriFragmentChangedListener(
				new UriFragmentChangedListener() {
					private static final long serialVersionUID = 1L;

					@Override
					public void uriFragmentChanged(UriFragmentChangedEvent event) {
						enter(event.getUriFragment());
					}
				});

		EventBusFactory.getInstance().register(new ShellErrorHandler());
	}

	private void handleException(Throwable e) {
		UserInvalidInputException invalidException = (UserInvalidInputException) getExceptionType(
				e, UserInvalidInputException.class);
		if (invalidException != null) {
			NotificationUtil.showWarningNotification(AppContext.getMessage(
					GenericI18Enum.ERROR_USER_INPUT_MESSAGE,
					invalidException.getMessage()));
			return;
		}

		UnsupportedFeatureException unsupportedException = (UnsupportedFeatureException) getExceptionType(
				e, UnsupportedFeatureException.class);
		if (unsupportedException != null) {
			NotificationUtil.showFeatureNotPresentInSubscription();
			return;
		}

		ResourceNotFoundException resourceNotFoundException = (ResourceNotFoundException) getExceptionType(
				e, ResourceNotFoundException.class);
		if (resourceNotFoundException != null) {
			NotificationUtil.showWarningNotification("Can not found resource.");
			LOG.error("404", resourceNotFoundException);
		}

		UsageExceedBillingPlanException usageBillingException = (UsageExceedBillingPlanException) getExceptionType(
				e, UsageExceedBillingPlanException.class);
		if (usageBillingException != null) {
			if (AppContext.isAdmin()) {
				ConfirmDialogExt
						.show(UI.getCurrent(),
								AppContext.getMessage(
										GenericI18Enum.WINDOW_ATTENTION_TITLE,
										SiteConfiguration.getSiteName()),
								AppContext
										.getMessage(GenericI18Enum.EXCEED_BILLING_PLAN_MSG_FOR_ADMIN),
								AppContext
										.getMessage(GenericI18Enum.BUTTON_YES),
								AppContext
										.getMessage(GenericI18Enum.BUTTON_NO),
								new ConfirmDialog.Listener() {
									private static final long serialVersionUID = 1L;

									@Override
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											Collection<Window> windowsList = UI
													.getCurrent().getWindows();
											for (Window window : windowsList) {
												window.close();
											}
											EventBusFactory
													.getInstance()
													.post(new ShellEvent.GotoUserAccountModule(
															this,
															new String[] { "billing" }));
										}
									}
								});

			} else {
				NotificationUtil
						.showErrorNotification(AppContext
								.getMessage(GenericI18Enum.EXCEED_BILLING_PLAN_MSG_FOR_USER));
			}
		} else {
			LOG.error("Error", e);
			NotificationUtil
					.showErrorNotification(AppContext
							.getMessage(GenericI18Enum.ERROR_USER_NOTICE_INFORMATION_MESSAGE));

		}
	}

	private void enter(String uriFragement) {
		rootUrlResolver.navigateByFragement(uriFragement);
	}

	private void clearSession() {
		if (currentContext != null) {
			currentContext.clearSession();
			initialUrl = "";
			ViewManager.clearViewCaches();
			PresenterResolver.clearCaches();
			ControllerRegistry.reset();
			MyCollabSession.removeVariable(EVENT_BUS_VAL);
		}
	}

	public void redirectToLoginView() {
		clearSession();

		AppContext.addFragment("", "Login Page");
		// clear cookie remember username/password if any
		this.unsetRememberPassword();

		ControllerRegistry.addController(new ShellController(
				mainWindowContainer));
		LoginPresenter presenter = PresenterResolver
				.getPresenter(LoginPresenter.class);
		LoginView loginView = presenter.getView();

		mainWindowContainer.setStyleName("loginView");

		if (loginView.getParent() == null
				|| loginView.getParent() == mainWindowContainer) {
			mainWindowContainer.setAutoLogin(false);
			mainWindowContainer.setContent(loginView);
		} else {
			presenter.go(mainWindowContainer, null);
		}
	}

	public void rememberPassword(String username, String password) {

		Cookie cookie = getCookieByName(DesktopApplication.NAME_COOKIE);
		String storeVal = username + "$"
				+ PasswordEncryptHelper.encyptText(password);
		if (cookie == null) {
			cookie = new Cookie(DesktopApplication.NAME_COOKIE, storeVal);
		} else {
			cookie.setValue(storeVal);
		}
		cookie.setPath("/");
		cookie.setMaxAge(60 * 60 * 24 * 7);

		VaadinService.getCurrentResponse().addCookie(cookie);
	}

	public void unsetRememberPassword() {
		Cookie cookie = getCookieByName(DesktopApplication.NAME_COOKIE);

		if (cookie != null) {
			cookie.setValue("");
			cookie.setPath("/");
			cookie.setMaxAge(0);
			VaadinService.getCurrentResponse().addCookie(cookie);
		}
	}

	public Cookie getCookieByName(String name) {
		Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();

		if (cookies != null) {
			// Iterate to find cookie by its name
			for (Cookie cookie : cookies) {
				if (name.equals(cookie.getName())) {
					return cookie;
				}
			}
		}

		return null;
	}

	private static Throwable getExceptionType(Throwable e,
			Class<? extends Throwable> exceptionType) {
		if (exceptionType.isAssignableFrom(e.getClass())) {
			return e;
		} else if (e.getCause() != null) {
			return getExceptionType(e.getCause(), exceptionType);
		} else {
			return null;
		}
	}

	private class ShellErrorHandler {
		@Subscribe
		public void handle(ShellEvent.NotifyErrorEvent event) {
			Throwable e = (Throwable) event.getData();
			handleException(e);
		}
	}
}
