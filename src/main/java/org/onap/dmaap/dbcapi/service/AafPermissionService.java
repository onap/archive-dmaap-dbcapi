/*-
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2019 Nokia Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.dmaap.dbcapi.service;

import org.onap.dmaap.dbcapi.aaf.AafService;
import org.onap.dmaap.dbcapi.aaf.AafUserRole;
import org.onap.dmaap.dbcapi.aaf.DmaapGrant;
import org.onap.dmaap.dbcapi.aaf.DmaapPerm;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.DmaapObject.DmaapObject_Status;
import org.onap.dmaap.dbcapi.model.MR_Client;

public class AafPermissionService extends BaseLoggingClass {

    private final AafService aafService;
    private final DmaapService dmaapService;

    public AafPermissionService() {
        this(new AafService(AafService.ServiceType.AAF_TopicMgr), new DmaapService());
    }

    AafPermissionService(AafService aafService, DmaapService dmaapService) {
        this.aafService = aafService;
        this.dmaapService = dmaapService;
    }

    void assignIdentityToRole(MR_Client client, String role, ApiError err) {
        okStatus(err);
        AafUserRole ur = new AafUserRole(client.getClientIdentity(), role);
        client.setStatus(DmaapObject_Status.VALID);
        int rc = aafService.addUserRole(ur);
        if (rc != 201 && rc != 409) {
            client.setStatus(DmaapObject_Status.INVALID);
            assignClientToRoleError(err, rc, client.getClientIdentity(), role);
        }
    }

    void grantClientRolePerms(MR_Client client, ApiError err) {

        okStatus(err);
        String instance = ":topic." + client.getFqtn();
        client.setStatus(DmaapObject_Status.VALID);

        for (String action : client.getAction()) {
            if (client.getClientRole() != null) {
                int rc = grantPermForClientRole(client.getClientRole(), instance, action);
                if (rc != 201 && rc != 409) {
                    client.setStatus(DmaapObject_Status.INVALID);
                    grantPermsError(err, rc, dmaapService.getTopicPerm(), instance, action, client.getClientRole());
                }

            } else {
                logger.warn("No Grant of " + permissionFullName(dmaapService.getTopicPerm(), instance, action) + " because role is null ");
            }
        }
    }

    void revokeClientPerms(MR_Client client, ApiError err) {
        okStatus(err);
        String instance = ":topic." + client.getFqtn();
        client.setStatus(DmaapObject_Status.VALID);

        for (String action : client.getAction()) {

            int rc = revokePermForClientRole(client.getClientRole(), instance, action);

            if (rc != 200 && rc != 404) {
                client.setStatus(DmaapObject_Status.INVALID);
                revokePermsError(err, rc, dmaapService.getTopicPerm(), instance, action, client.getClientRole());
            }
        }

    }

    private int grantPermForClientRole(String clientRole, String instance, String action) {
        DmaapPerm perm = new DmaapPerm(dmaapService.getTopicPerm(), instance, action);
        DmaapGrant g = new DmaapGrant(perm, clientRole);
        return aafService.addGrant(g);
    }

    private int revokePermForClientRole(String clientRole, String instance, String action) {
        DmaapPerm perm = new DmaapPerm(dmaapService.getTopicPerm(), instance, action);
        DmaapGrant g = new DmaapGrant(perm, clientRole);
        return aafService.delGrant(g);
    }

    private void assignClientToRoleError(ApiError err, int code, String clientIdentity, String role) {
        err.setCode(code);
        err.setMessage("Failed to add user " + clientIdentity + "  to " + role);
        logger.warn(err.getMessage());
    }

    private void grantPermsError(ApiError err, int code, String permission, String instance, String action, String role) {
        err.setCode(code);
        err.setMessage("Grant of " + permissionFullName(permission, instance, action) + " failed for " + role);
        logger.warn(err.getMessage());
    }

    private void revokePermsError(ApiError err, int code, String permission, String instance, String action, String role) {
        err.setCode(code);
        err.setMessage("Revoke of " + permissionFullName(permission, instance, action) + " failed for " + role);
        logger.warn(err.getMessage());
    }

    private String permissionFullName(String permission, String instance, String action) {
        return permission + "|" + instance + "|" + action;
    }

    private void okStatus(ApiError err) {
        err.setCode(200);
        err.setMessage("OK");
    }

}
