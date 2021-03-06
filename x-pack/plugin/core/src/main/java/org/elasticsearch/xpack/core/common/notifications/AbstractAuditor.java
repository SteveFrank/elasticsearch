/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.xpack.core.common.notifications;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.Objects;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.xpack.core.ClientHelper.executeAsyncWithOrigin;

public abstract class AbstractAuditor<T extends AbstractAuditMessage> {

    private static final Logger logger = LogManager.getLogger(AbstractAuditor.class);
    private final Client client;
    private final String nodeName;
    private final String auditIndex;
    private final String executionOrigin;
    private final AbstractAuditMessage.AbstractBuilder<T> messageBuilder;

    public AbstractAuditor(Client client,
                           String nodeName,
                           String auditIndex,
                           String executionOrigin,
                           AbstractAuditMessage.AbstractBuilder<T> messageBuilder) {
        this.client = Objects.requireNonNull(client);
        this.nodeName = Objects.requireNonNull(nodeName);
        this.auditIndex = auditIndex;
        this.executionOrigin = executionOrigin;
        this.messageBuilder = Objects.requireNonNull(messageBuilder);
    }

    public void info(String resourceId, String message) {
        indexDoc(messageBuilder.info(resourceId, message, nodeName));
    }

    public void warning(String resourceId, String message) {
        indexDoc(messageBuilder.warning(resourceId, message, nodeName));
    }

    public void error(String resourceId, String message) {
        indexDoc(messageBuilder.error(resourceId, message, nodeName));
    }

    protected void onIndexResponse(IndexResponse response) {
        logger.trace("Successfully wrote audit message");
    }

    protected void onIndexFailure(Exception exception) {
        logger.debug("Failed to write audit message", exception);
    }

    private void indexDoc(ToXContent toXContent) {
        IndexRequest indexRequest = new IndexRequest(auditIndex);
        indexRequest.source(toXContentBuilder(toXContent));
        indexRequest.timeout(TimeValue.timeValueSeconds(5));
        executeAsyncWithOrigin(client.threadPool().getThreadContext(),
            executionOrigin,
            indexRequest,
            ActionListener.wrap(
                this::onIndexResponse,
                this::onIndexFailure
            ), client::index);
    }

    private XContentBuilder toXContentBuilder(ToXContent toXContent) {
        try (XContentBuilder jsonBuilder = jsonBuilder()) {
            return toXContent.toXContent(jsonBuilder, ToXContent.EMPTY_PARAMS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
