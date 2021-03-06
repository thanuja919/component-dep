/*******************************************************************************
 * Copyright  (c) 2015-2016, WSO2.Telco Inc. (http://www.wso2telco.com) All Rights Reserved.
 * 
 * WSO2.Telco Inc. licences this file to you under  the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.wso2telco.dep.oneapivalidation.service.impl.ussd;

import com.wso2telco.dep.oneapivalidation.exceptions.CustomException;
import com.wso2telco.dep.oneapivalidation.service.IServiceValidate;
import com.wso2telco.dep.oneapivalidation.util.UrlValidator;
import com.wso2telco.dep.oneapivalidation.util.Validation;
import com.wso2telco.dep.oneapivalidation.util.ValidationRule;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class ValidateUssdSubscription.
 */
public class ValidateUssdSubscription implements IServiceValidate {

    /** The validation rules. */
    private final String[] validationRules = {"inbound", "subscriptions"};

    /* (non-Javadoc)
     * @see com.wso2telco.oneapivalidation.service.IServiceValidate#validate(java.lang.String)
     */
    public void validate(String json) throws CustomException {


        String callbackData = null;
        String notifyUrl = null;
        String clientCorrelator = null;

        try {
            JSONObject objJSONObject = new JSONObject(json);
            JSONObject requestData = objJSONObject.getJSONObject("subscription");

            if (requestData.has("clientCorrelator")) {
                clientCorrelator = nullOrTrimmed(requestData.getString("clientCorrelator"));
            }

            if (requestData.has("callbackReference")) {
                JSONObject callbackReference = requestData.getJSONObject("callbackReference");

                if (callbackReference.has("callbackData")) {
                    callbackData = nullOrTrimmed(callbackReference.getString("callbackData"));
                }
                if (callbackReference.has("notifyURL")) {
                    notifyUrl = nullOrTrimmed(callbackReference.getString("notifyURL"));
                }
            }

            ValidationRule[] rules = null;

            rules = new ValidationRule[]{
                    new ValidationRule(ValidationRule.VALIDATION_TYPE_OPTIONAL, "clientCorrelator", clientCorrelator),
                    new ValidationRule(ValidationRule.VALIDATION_TYPE_MANDATORY, "notifyURL", notifyUrl),
                    new ValidationRule(ValidationRule.VALIDATION_TYPE_OPTIONAL, "callbackData", callbackData),};

            Validation.checkRequestParams(rules);

            if (requestData.has("shortCodes")) {
                JSONArray shortCodesArray = requestData.getJSONArray("shortCodes");

                if (shortCodesArray.length() != 0) {

                    List<ValidationRule> shortCodeListRules = new ArrayList<ValidationRule>();

                    for (int i=0; i<shortCodesArray.length(); i++) {
                        JSONObject shortCode = shortCodesArray.getJSONObject(i);
                        String sCode = nullOrTrimmed(shortCode.getString("shortCode"));
                        String oCode = nullOrTrimmed(shortCode.getString("operatorCode"));

                        shortCodeListRules.add(new ValidationRule(ValidationRule.VALIDATION_TYPE_MANDATORY, "shortCode"+i, sCode));
                        shortCodeListRules.add(new ValidationRule(ValidationRule.VALIDATION_TYPE_MANDATORY, "operatorCode"+i, oCode));
                    }

                    Validation.checkRequestParams(shortCodeListRules.toArray(new ValidationRule[shortCodesArray.length()]));
                } else {
                    throw new CustomException("SVC0002", "Invalid input value for message part %1", new String[]{"Empty sortcodes list"});
                }
            } else if (requestData.has("shortCode")) {
                ValidationRule[] shortCodeRules = new ValidationRule[2];
                shortCodeRules[0] = new ValidationRule(ValidationRule.VALIDATION_TYPE_MANDATORY, "shortCode", nullOrTrimmed(requestData.getString("shortCode")));
                shortCodeRules[1] = new ValidationRule(ValidationRule.VALIDATION_TYPE_OPTIONAL, "keyword", nullOrTrimmed(requestData.getString("keyword")));

                Validation.checkRequestParams(shortCodeRules);
            } else {
                throw new CustomException("SVC0002", "Invalid input value for message part %1", new String[]{"Missing mandatory parameter: shortCode"});
            }

        } catch (Exception e) {

            System.out.println("Manipulating received JSON Object: " + e);

            if (e instanceof CustomException)
                throw new CustomException(((CustomException) e).getErrcode(), ((CustomException) e).getErrmsg(), ((CustomException) e).getErrvar());
            else
                throw new CustomException("POL0299", "Unexpected Error", new String[]{""});
        }
    }

    /* (non-Javadoc)
     * @see com.wso2telco.oneapivalidation.service.IServiceValidate#validateUrl(java.lang.String)
     */
    public void validateUrl(String pathInfo) throws CustomException {
        String[] requestParts = null;
        if (pathInfo != null) {
            if (pathInfo.startsWith("/")) {
                pathInfo = pathInfo.substring(1);
            }
            requestParts = pathInfo.split("/");
        }

        UrlValidator.validateRequest(requestParts, validationRules);

    }

    /* (non-Javadoc)
     * @see com.wso2telco.oneapivalidation.service.IServiceValidate#validate(java.lang.String[])
     */
    public void validate(String[] params) throws CustomException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Null or trimmed.
     *
     * @param s the s
     * @return the string
     */
    private static String nullOrTrimmed(String s) {
        String rv = null;
        if (s != null && s.trim().length() > 0) {
            rv = s.trim();
        }
        return rv;
    }
}
