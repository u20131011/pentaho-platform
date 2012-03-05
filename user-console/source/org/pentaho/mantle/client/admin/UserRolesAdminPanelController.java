/*
 * This program is free software; you can redistribute it and/or modify it under the 
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software 
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this 
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html 
 * or from the Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright 2008 Pentaho Corporation.  All rights reserved.
 *
 * Created Mar 03, 2012
 * @author Ezequiel Cuellar
 */

package org.pentaho.mantle.client.admin;

import org.pentaho.gwt.widgets.client.utils.string.StringUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class UserRolesAdminPanelController extends UserRolesAdminPanel implements ISysAdminPanel {

	public UserRolesAdminPanelController() {
		super();

		usersListBox.addChangeHandler(new UsersListChangeListener());
		rolesListBox.addChangeHandler(new RolesListChangeListener());
		addRoleButton.addClickHandler(new AddRoleListener());
		removeRoleButton.addClickHandler(new RemoveRoleListener());
		addAllRolesButton.addClickHandler(new AddAllRolesListener());
		removeAllRolesButton.addClickHandler(new RemoveAllRolesListener());
		addUserButton.addClickHandler(new AddUserListener());
		removeUserButton.addClickHandler(new RemoveUserListener());
		addAllUsersButton.addClickHandler(new AddAllUsersListener());
		removeAllUsersButton.addClickHandler(new RemoveAllUsersListener());

		initializeAvailableUsers();
		initializeAvailableRoles();
	}

	// -- Remote Calls.

	private void initializeAvailableRoles() {
		final String url = GWT.getHostPageBaseURL() + "api/roles";
		RequestBuilder executableTypesRequestBuilder = new RequestBuilder(RequestBuilder.GET, url);
		executableTypesRequestBuilder.setHeader("accept", "application/xml");
		try {
			executableTypesRequestBuilder.sendRequest(null, new RequestCallback() {

				public void onError(Request request, Throwable exception) {
				}

				public void onResponseReceived(Request request, Response response) {
					rolesListBox.clear();
					String txt = response.getText();
					Document doc = XMLParser.parse(txt);
					NodeList roles = doc.getElementsByTagName("role");
					for (int i = 0; i < roles.getLength(); i++) {
						Node roleNode = roles.item(i);
						String role = roleNode.getFirstChild().getNodeValue();
						rolesListBox.addItem(role);
					}
				}
			});
		} catch (RequestException e) {
		}
	}

	private void initializeAvailableUsers() {
		final String url = GWT.getHostPageBaseURL() + "api/users";
		RequestBuilder executableTypesRequestBuilder = new RequestBuilder(RequestBuilder.GET, url);
		executableTypesRequestBuilder.setHeader("accept", "application/xml");
		try {
			executableTypesRequestBuilder.sendRequest(null, new RequestCallback() {

				public void onError(Request request, Throwable exception) {
				}

				public void onResponseReceived(Request request, Response response) {
					usersListBox.clear();
					String txt = response.getText();
					Document doc = XMLParser.parse(txt);
					NodeList users = doc.getElementsByTagName("user");
					for (int i = 0; i < users.getLength(); i++) {
						Node userNode = users.item(i);
						String user = userNode.getFirstChild().getNodeValue();
						usersListBox.addItem(user);
					}
				}
			});
		} catch (RequestException e) {
		}
	}

	private void getRolesForUser(String user) {
		final String url = GWT.getHostPageBaseURL() + "api/userrole/getRolesForUser?user=" + user;
		RequestBuilder executableTypesRequestBuilder = new RequestBuilder(RequestBuilder.GET, url);
		executableTypesRequestBuilder.setHeader("accept", "application/xml");
		try {
			executableTypesRequestBuilder.sendRequest(null, new RequestCallback() {

				public void onError(Request request, Throwable exception) {
				}

				public void onResponseReceived(Request request, Response response) {
					selectedRolesListBox.clear();
					String txt = response.getText();
					Document doc = XMLParser.parse(txt);
					NodeList roles = doc.getElementsByTagName("role");
					for (int i = 0; i < roles.getLength(); i++) {
						Node roleNode = roles.item(i);
						String role = roleNode.getFirstChild().getNodeValue();
						selectedRolesListBox.addItem(role);
					}

					// availableRolesListBox =
					// rolesListBox - selectedRolesListBox
					availableRolesListBox.clear();
					for (int i = 0; i < rolesListBox.getItemCount(); i++) {
						String role = rolesListBox.getValue(i);
						boolean isSelected = false;
						for (int j = 0; j < selectedRolesListBox.getItemCount(); j++) {
							if (selectedRolesListBox.getValue(j).equals(role)) {
								isSelected = true;
							}
						}
						if (!isSelected) {
							availableRolesListBox.addItem(role);
						}
					}
				}
			});
		} catch (RequestException e) {
		}
	}

	private void getUsersInRole(String role) {
		final String url = GWT.getHostPageBaseURL() + "api/userrole/getUsersInRole?role=" + role;
		RequestBuilder executableTypesRequestBuilder = new RequestBuilder(RequestBuilder.GET, url);
		executableTypesRequestBuilder.setHeader("accept", "application/xml");
		try {
			executableTypesRequestBuilder.sendRequest(null, new RequestCallback() {

				public void onError(Request request, Throwable exception) {
				}

				public void onResponseReceived(Request request, Response response) {
					selectedMembersListBox.clear();
					String txt = response.getText();
					Document doc = XMLParser.parse(txt);
					NodeList users = doc.getElementsByTagName("user");
					for (int i = 0; i < users.getLength(); i++) {
						Node userNode = users.item(i);
						String user = userNode.getFirstChild().getNodeValue();
						selectedMembersListBox.addItem(user);
					}

					// availableMembersListBox =
					// usersListBox - selectedMembersListBox
					availableMembersListBox.clear();
					for (int i = 0; i < usersListBox.getItemCount(); i++) {
						String user = usersListBox.getValue(i);
						boolean isSelected = false;
						for (int j = 0; j < selectedMembersListBox.getItemCount(); j++) {
							if (selectedMembersListBox.getValue(j).equals(user)) {
								isSelected = true;
							}
						}
						if (!isSelected) {
							availableMembersListBox.addItem(user);
						}
					}
				}
			});
		} catch (RequestException e) {
		}
	}
	
	private void modifyUserRoles(final String userName, String serviceUrl) {
		RequestBuilder executableTypesRequestBuilder = new RequestBuilder(RequestBuilder.PUT, serviceUrl);
		try {
			executableTypesRequestBuilder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
				}

				public void onResponseReceived(Request request, Response response) {
					getRolesForUser(userName);
				}
			});
		} catch (RequestException e) {
		}
	}
	
	private void modifyRoleUsers(final String roleName, String serviceUrl) {
		RequestBuilder executableTypesRequestBuilder = new RequestBuilder(RequestBuilder.PUT, serviceUrl);
		try {
			executableTypesRequestBuilder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
				}

				public void onResponseReceived(Request request, Response response) {
					getUsersInRole(roleName);
				}
			});
		} catch (RequestException e) {
		}
	}	
	
	// -- ISysAdminPanel implementation.

	public void activate() {
		initializeAvailableUsers();
		initializeAvailableRoles();
	}

	public String getId() {
		return "userRolesAdminPanel";
	}

	public void passivate(final AsyncCallback<Boolean> callback) {
		callback.onSuccess(true);
	}

	// -- Event Listeners.

	class UsersListChangeListener implements ChangeHandler {
		public void onChange(ChangeEvent evt) {
			String user = usersListBox.getValue(usersListBox.getSelectedIndex());
			if (!StringUtils.isEmpty(user)) {
				getRolesForUser(user);
				userNameTextBox.setText(user);
			}
		}
	}

	class RolesListChangeListener implements ChangeHandler {
		public void onChange(ChangeEvent evt) {
			String role = rolesListBox.getValue(rolesListBox.getSelectedIndex());
			if (!StringUtils.isEmpty(role)) {
				getUsersInRole(role);
				roleNameTextBox.setText(role);
			}
		}
	}

	class AddRoleListener implements ClickHandler {
		public void onClick(ClickEvent event) {
			String userName = userNameTextBox.getText();
			String roleName = availableRolesListBox.getValue(availableRolesListBox.getSelectedIndex());
			String serviceUrl = GWT.getHostPageBaseURL() + "api/userrole/assignRoleToUser?userName=" + userName + "&roleName=" + roleName;
			modifyUserRoles(userName, serviceUrl);
		}
	}
	
	class RemoveRoleListener implements ClickHandler {
		public void onClick(ClickEvent event) {
			String userName = userNameTextBox.getText();
			String roleName = selectedRolesListBox.getValue(selectedRolesListBox.getSelectedIndex());
			String serviceUrl = GWT.getHostPageBaseURL() + "api/userrole/removeRoleFromUser?userName=" + userName + "&roleName=" + roleName;
			modifyUserRoles(userName, serviceUrl);
		}
	}
	
	class AddAllRolesListener implements ClickHandler {
		public void onClick(ClickEvent event) {
			String userName = userNameTextBox.getText();
			String serviceUrl = GWT.getHostPageBaseURL() + "api/userrole/assignAllRolesToUser?userName=" + userName;
			modifyUserRoles(userName, serviceUrl);
		}
	}
	
	class RemoveAllRolesListener implements ClickHandler {
		public void onClick(ClickEvent event) {
			String userName = userNameTextBox.getText();
			String serviceUrl = GWT.getHostPageBaseURL() + "api/userrole/removeAllRolesFromUser?userName=" + userName;
			modifyUserRoles(userName, serviceUrl);
		}
	}
	
	class AddUserListener implements ClickHandler {
		public void onClick(ClickEvent event) {
			String roleName = roleNameTextBox.getText();
			String userName = availableMembersListBox.getValue(availableMembersListBox.getSelectedIndex());
			String serviceUrl = GWT.getHostPageBaseURL() + "api/userrole/assignUserToRole?userName=" + userName + "&roleName=" + roleName;
			modifyRoleUsers(roleName, serviceUrl);
		}
	}
	
	class RemoveUserListener implements ClickHandler {
		public void onClick(ClickEvent event) {
			String roleName = roleNameTextBox.getText();
			String userName = selectedMembersListBox.getValue(selectedMembersListBox.getSelectedIndex());
			String serviceUrl = GWT.getHostPageBaseURL() + "api/userrole/removeUserFromRole?userName=" + userName + "&roleName=" + roleName;
			modifyRoleUsers(roleName, serviceUrl);
		}
	}
	
	class AddAllUsersListener implements ClickHandler {
		public void onClick(ClickEvent event) {
			String roleName = roleNameTextBox.getText();
			String serviceUrl = GWT.getHostPageBaseURL() + "api/userrole/assignAllUsersToRole?roleName=" + roleName;
			modifyRoleUsers(roleName, serviceUrl);
		}
	}
	
	class RemoveAllUsersListener implements ClickHandler {
		public void onClick(ClickEvent event) {
			String roleName = roleNameTextBox.getText();
			String serviceUrl = GWT.getHostPageBaseURL() + "api/userrole/removeAllUsersFromRole?roleName=" + roleName;
			modifyRoleUsers(roleName, serviceUrl);
		}
	}	
}
