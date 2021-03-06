/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.client.dataframe;

import org.elasticsearch.client.Validatable;
import org.elasticsearch.client.ValidationException;
import org.elasticsearch.common.unit.TimeValue;

import java.util.Objects;
import java.util.Optional;

public class StopDataFrameTransformRequest implements Validatable {

    private final String id;
    private Boolean waitForCompletion;
    private TimeValue timeout;
    private Boolean allowNoMatch;

    public StopDataFrameTransformRequest(String id) {
        this.id = id;
        waitForCompletion = null;
        timeout = null;
    }

    public StopDataFrameTransformRequest(String id, Boolean waitForCompletion, TimeValue timeout) {
        this.id = id;
        this.waitForCompletion = waitForCompletion;
        this.timeout = timeout;
    }

    public String getId() {
        return id;
    }

    public void setWaitForCompletion(Boolean waitForCompletion) {
        this.waitForCompletion = waitForCompletion;
    }

    public Boolean getWaitForCompletion() {
        return waitForCompletion;
    }

    public void setTimeout(TimeValue timeout) {
        this.timeout = timeout;
    }

    public TimeValue getTimeout() {
        return timeout;
    }

    public Boolean getAllowNoMatch() {
        return allowNoMatch;
    }

    public void setAllowNoMatch(Boolean allowNoMatch) {
        this.allowNoMatch = allowNoMatch;
    }

    @Override
    public Optional<ValidationException> validate() {
        if (id == null) {
            ValidationException validationException = new ValidationException();
            validationException.addValidationError("data frame transform id must not be null");
            return Optional.of(validationException);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, waitForCompletion, timeout, allowNoMatch);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        StopDataFrameTransformRequest other = (StopDataFrameTransformRequest) obj;
        return Objects.equals(this.id, other.id)
                && Objects.equals(this.waitForCompletion, other.waitForCompletion)
                && Objects.equals(this.timeout, other.timeout)
                && Objects.equals(this.allowNoMatch, other.allowNoMatch);
    }

}
