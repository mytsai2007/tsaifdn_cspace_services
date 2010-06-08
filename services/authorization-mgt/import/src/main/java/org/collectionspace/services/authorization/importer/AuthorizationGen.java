/**
 *  This document is a part of the source code and related artifacts
 *  for CollectionSpace, an open source collections management system
 *  for museums and related institutions:

 *  http://www.collectionspace.org
 *  http://wiki.collectionspace.org

 *  Copyright 2009 University of California at Berkeley

 *  Licensed under the Educational Community License (ECL), Version 2.0.
 *  You may not use this file except in compliance with this License.

 *  You may obtain a copy of the ECL 2.0 License at

 *  https://source.collectionspace.org/collection-space/LICENSE.txt

 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.collectionspace.services.authorization.importer;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import org.collectionspace.services.authorization.ActionType;
import org.collectionspace.services.authorization.Permission;
import org.collectionspace.services.authorization.EffectType;
import org.collectionspace.services.authorization.PermissionAction;
import org.collectionspace.services.authorization.PermissionRole;
import org.collectionspace.services.authorization.PermissionValue;
import org.collectionspace.services.authorization.PermissionsList;
import org.collectionspace.services.authorization.PermissionsRolesList;
import org.collectionspace.services.authorization.Role;
import org.collectionspace.services.authorization.RoleValue;
import org.collectionspace.services.authorization.RolesList;
import org.collectionspace.services.authorization.SubjectType;
import org.collectionspace.services.common.config.TenantBindingConfigReaderImpl;
import org.collectionspace.services.common.service.ServiceBindingType;
import org.collectionspace.services.common.tenant.TenantBindingType;

/**
 * AuthorizationGen generates authorizations (permissions and roles)
 * for tenant services
 * @author 
 */
public class AuthorizationGen {

    final public static String ROLE_ADMINISTRATOR = "ROLE_ADMINISTRATOR";
    final public static String ROLE_TENANT_ADMINISTRATOR = "ROLE_TENANT_ADMINISTRATOR";
    final public static String ROLE_TENANT_READER = "ROLE_TENANT_READER";
    final public static String ROLE_ADMINISTRATOR_ID = "0";
    final Logger logger = LoggerFactory.getLogger(AuthorizationGen.class);
    private List<Permission> adminPermList = new ArrayList<Permission>();
    private List<PermissionRole> adminPermRoleList = new ArrayList<PermissionRole>();
    private List<Permission> readerPermList = new ArrayList<Permission>();
    private List<PermissionRole> readerPermRoleList = new ArrayList<PermissionRole>();
    private List<Role> adminRoles = new ArrayList<Role>();
    private List<Role> readerRoles = new ArrayList<Role>();
    private Role cspaceAdminRole;
    private Hashtable<String, TenantBindingType> tenantBindings =
            new Hashtable<String, TenantBindingType>();

    public void initialize(String tenantBindingFileName) throws Exception {
        TenantBindingConfigReaderImpl tenantBindingConfigReader =
                new TenantBindingConfigReaderImpl(null);
        tenantBindingConfigReader.read(tenantBindingFileName);
        tenantBindings = tenantBindingConfigReader.getTenantBindings();
        cspaceAdminRole = buildCSpaceAdminRole();

        if (logger.isDebugEnabled()) {
            logger.debug("initialized with tenant bindings from " + tenantBindingFileName);
        }
    }

    /**
     * createDefaultPermissions creates default admin and reader permissions
     * for each tenant found in the given tenant binding file
     * @see initialize
     * @return
     */
    public void createDefaultPermissions() {
        for (String tenantId : tenantBindings.keySet()) {
            List<Permission> adminPerms = createDefaultAdminPermissions(tenantId);
            adminPermList.addAll(adminPerms);

            List<Permission> readerPerms = createDefaultReaderPermissions(tenantId);
            readerPermList.addAll(readerPerms);
        }
    }

    /**
     * createDefaultAdminPermissions creates default admin permissions for all services
     * used by the given tenant
     * @param tenantId
     * @return
     */
    public List<Permission> createDefaultAdminPermissions(String tenantId) {
        ArrayList<Permission> apcList = new ArrayList<Permission>();
        TenantBindingType tbinding = tenantBindings.get(tenantId);
        for (ServiceBindingType sbinding : tbinding.getServiceBindings()) {

            //add permissions for the main path
            Permission perm = buildAdminPermission(tbinding.getId(),
                    sbinding.getName().toLowerCase());
            apcList.add(perm);

            //add permissions for alternate paths
            List<String> uriPaths = sbinding.getUriPath();
            for (String uriPath : uriPaths) {
                perm = buildAdminPermission(tbinding.getId(),
                        uriPath.toLowerCase());
                apcList.add(perm);
            }

        }
        return apcList;

    }

    private Permission buildAdminPermission(String tenantId, String resourceName) {
        String id = UUID.randomUUID().toString();
        Permission perm = new Permission();
        perm.setCsid(id);
        perm.setDescription("generated admin permission");
        perm.setCreatedAtItem(new Date());
        perm.setResourceName(resourceName.toLowerCase());
        perm.setEffect(EffectType.PERMIT);
        perm.setTenantId(tenantId);
        ArrayList<PermissionAction> pas = new ArrayList<PermissionAction>();
        perm.setActions(pas);

        PermissionAction pa = new PermissionAction();
        pa.setName(ActionType.CREATE);
        pas.add(pa);
        PermissionAction pa1 = new PermissionAction();
        pa1.setName(ActionType.READ);
        pas.add(pa1);
        PermissionAction pa2 = new PermissionAction();
        pa2.setName(ActionType.UPDATE);
        pas.add(pa2);
        PermissionAction pa3 = new PermissionAction();
        pa3.setName(ActionType.DELETE);
        pas.add(pa3);
        PermissionAction pa4 = new PermissionAction();
        pa4.setName(ActionType.SEARCH);
        pas.add(pa4);
        return perm;
    }

    /**
     * createDefaultReaderPermissions creates read only permissions for all services
     * used by the given tenant
     * @param tenantId
     * @return
     */
    public List<Permission> createDefaultReaderPermissions(String tenantId) {
        ArrayList<Permission> apcList = new ArrayList<Permission>();
        TenantBindingType tbinding = tenantBindings.get(tenantId);
        for (ServiceBindingType sbinding : tbinding.getServiceBindings()) {

            //add permissions for the main path
            Permission perm = buildReaderPermission(tbinding.getId(),
                    sbinding.getName().toLowerCase());
            apcList.add(perm);

            //add permissions for alternate paths
            List<String> uriPaths = sbinding.getUriPath();
            for (String uriPath : uriPaths) {
                perm = buildReaderPermission(tbinding.getId(),
                        uriPath.toLowerCase());
                apcList.add(perm);
            }

        }
        return apcList;

    }

    private Permission buildReaderPermission(String tenantId, String resourceName) {
        String id = UUID.randomUUID().toString();
        Permission perm = new Permission();
        perm.setCsid(id);
        perm.setCreatedAtItem(new Date());
        perm.setDescription("generated readonly permission");
        perm.setResourceName(resourceName.toLowerCase());
        perm.setEffect(EffectType.PERMIT);
        perm.setTenantId(tenantId);
        ArrayList<PermissionAction> pas = new ArrayList<PermissionAction>();
        perm.setActions(pas);

        PermissionAction pa1 = new PermissionAction();
        pa1.setName(ActionType.READ);
        pas.add(pa1);

        PermissionAction pa4 = new PermissionAction();
        pa4.setName(ActionType.SEARCH);
        pas.add(pa4);
        return perm;
    }

    public List<Permission> getDefaultPermissions() {
        List<Permission> allPermList = new ArrayList<Permission>();
        allPermList.addAll(adminPermList);
        allPermList.addAll(readerPermList);
        return allPermList;
    }

    public List<Permission> getDefaultAdminPermissions() {
        return adminPermList;
    }

    public List<Permission> getDefaultReaderPermissions() {
        return readerPermList;
    }

    /**
     * createDefaultRoles creates default admin and reader roles
     * for each tenant found in the given tenant binding file
     */
    public void createDefaultRoles() {
        for (String tenantId : tenantBindings.keySet()) {

            Role arole = buildTenantAdminRole(tenantId);
            adminRoles.add(arole);

            Role rrole = buildTenantReaderRole(tenantId);
            readerRoles.add(rrole);

        }
    }

    private Role buildTenantAdminRole(String tenantId) {
        Role role = new Role();
        role.setCreatedAtItem(new Date());
        role.setRoleName(ROLE_TENANT_ADMINISTRATOR);
        String id = UUID.randomUUID().toString();
        role.setCsid(id);
        role.setDescription("generated tenant admin role");
        role.setTenantId(tenantId);
        return role;
    }

    private Role buildTenantReaderRole(String tenantId) {
        Role role = new Role();
        role.setCreatedAtItem(new Date());
        role.setRoleName(ROLE_TENANT_READER);
        String id = UUID.randomUUID().toString();
        role.setCsid(id);
        role.setDescription("generated tenant read only role");
        role.setTenantId(tenantId);
        return role;
    }

    public List<Role> getDefaultRoles() {
        List<Role> allRoleList = new ArrayList<Role>();
        allRoleList.addAll(adminRoles);
        allRoleList.addAll(readerRoles);
        return allRoleList;
    }

    public void associateDefaultPermissionsRoles() {
        List<Role> roles = new ArrayList<Role>();
        roles.add(cspaceAdminRole);
        for (Permission p : adminPermList) {
            PermissionRole permAdmRole = associatePermissionRoles(p, adminRoles);
            adminPermRoleList.add(permAdmRole);

            //CSpace Administrator has all access
            PermissionRole permCAdmRole = associatePermissionRoles(p, roles);
            adminPermRoleList.add(permCAdmRole);
        }

        for (Permission p : readerPermList) {
            PermissionRole permRdrRole = associatePermissionRoles(p, readerRoles);
            readerPermRoleList.add(permRdrRole);
        }
    }

    public List<PermissionRole> associatePermissionsRoles(List<Permission> perms, List<Role> roles) {
        List<PermissionRole> permRoles = new ArrayList<PermissionRole>();
        for (Permission perm : perms) {
            PermissionRole permRole = associatePermissionRoles(perm, roles);
            permRoles.add(permRole);
        }
        return permRoles;
    }

    private PermissionRole associatePermissionRoles(Permission perm,
            List<Role> roles) {

        PermissionRole pr = new PermissionRole();
        pr.setSubject(SubjectType.ROLE);
        List<PermissionValue> permValues = new ArrayList<PermissionValue>();
        pr.setPermissions(permValues);
        PermissionValue permValue = new PermissionValue();
        permValue.setPermissionId(perm.getCsid());
        permValue.setResourceName(perm.getResourceName().toLowerCase());
        permValues.add(permValue);

        List<RoleValue> roleValues = new ArrayList<RoleValue>();
        for (Role role : roles) {
            RoleValue rv = new RoleValue();
            rv.setRoleName(role.getRoleName().toUpperCase());
            rv.setRoleId(role.getCsid());
            roleValues.add(rv);
        }
        pr.setRoles(roleValues);

        return pr;
    }

    public List<PermissionRole> getDefaultPermissionRoles() {
        List<PermissionRole> allPermRoleList = new ArrayList<PermissionRole>();
        allPermRoleList.addAll(adminPermRoleList);
        allPermRoleList.addAll(readerPermRoleList);
        return allPermRoleList;
    }

    public List<PermissionRole> getDefaultAdminPermissionRoles() {
        return adminPermRoleList;
    }

    public List<PermissionRole> getDefaultReaderPermissionRoles() {
        return readerPermRoleList;
    }

    private Role buildCSpaceAdminRole() {
        Role role = new Role();
        role.setRoleName(ROLE_ADMINISTRATOR);
        role.setCsid(ROLE_ADMINISTRATOR_ID);
        return role;
    }

    public void exportDefaultRoles(String fileName) {
        RolesList rList = new RolesList();
        List<Role> allRoleList = new ArrayList<Role>();
        allRoleList.addAll(adminRoles);
        allRoleList.addAll(readerRoles);
        rList.setRoles(allRoleList);
        toFile(rList, RolesList.class,
                fileName);
        if (logger.isDebugEnabled()) {
            logger.debug("exported roles to " + fileName);
        }
    }

    public void exportDefaultPermissions(String fileName) {
        PermissionsList pcList = new PermissionsList();
        List<Permission> allPermList = new ArrayList<Permission>();
        allPermList.addAll(adminPermList);
        allPermList.addAll(readerPermList);
        pcList.setPermissions(allPermList);
        toFile(pcList, PermissionsList.class,
                fileName);
        if (logger.isDebugEnabled()) {
            logger.debug("exported permissions to " + fileName);
        }
    }

    public void exportDefaultPermissionRoles(String fileName) {
        PermissionsRolesList psrsl = new PermissionsRolesList();
        List<PermissionRole> allPermRoleList = new ArrayList<PermissionRole>();
        allPermRoleList.addAll(adminPermRoleList);
        allPermRoleList.addAll(readerPermRoleList);
        psrsl.setPermissionRoles(allPermRoleList);
        toFile(psrsl, PermissionsRolesList.class,
                fileName);
        if (logger.isDebugEnabled()) {
            logger.debug("exported permissions-roles to " + fileName);
        }
    }

    private void toFile(Object o, Class jaxbClass, String fileName) {
        File f = new File(fileName);
        try {
            JAXBContext jc = JAXBContext.newInstance(jaxbClass);
            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                    Boolean.TRUE);
            m.marshal(o, f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}