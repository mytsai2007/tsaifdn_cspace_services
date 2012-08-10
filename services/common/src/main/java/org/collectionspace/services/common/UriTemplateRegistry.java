/**
 * This document is a part of the source code and related artifacts for
 * CollectionSpace, an open source collections management system for museums and
 * related institutions:
 *
 * http://www.collectionspace.org http://wiki.collectionspace.org
 *
 * Copyright © 2009-2012 University of California, Berkeley
 *
 * Licensed under the Educational Community License (ECL), Version 2.0. You may
 * not use this file except in compliance with this License.
 *
 * You may obtain a copy of the ECL 2.0 License at
 *
 * https://source.collectionspace.org/collection-space/LICENSE.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.collectionspace.services.common;

import java.util.HashMap;
import java.util.Map;

/**
 * UriTemplateRegistry.java
 *
 * Maps document types to templates for building URIs, per tenant.
 */
public class UriTemplateRegistry extends HashMap<String, Map<String, StoredValuesUriTemplate>> {

    // For debugging
    public void dump() {
        for (String tenantId : this.keySet()) {
            System.out.println("###############################");
            System.out.println("Tenant ID = " + tenantId);
            System.out.println("###############################");
            for (Map.Entry<String, StoredValuesUriTemplate> uriTemplateEntry : this.get(tenantId).entrySet()) {
                System.out.println("Key = " + uriTemplateEntry.getKey()
                        + ", Value = " + uriTemplateEntry.getValue().getUriTemplateType()
                        + " : " + uriTemplateEntry.getValue().toString());
            }
        }
    }
}
