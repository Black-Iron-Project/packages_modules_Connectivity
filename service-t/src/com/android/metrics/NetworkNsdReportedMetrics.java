/*
 * Copyright (C) 2023 The Android Open Source Project
 *
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
 */

package com.android.metrics;

import static com.android.metrics.NetworkNsdReported.Builder;

import android.stats.connectivity.MdnsQueryResult;
import android.stats.connectivity.NsdEventType;

import com.android.internal.annotations.VisibleForTesting;
import com.android.server.ConnectivityStatsLog;

/**
 * Class to record the NetworkNsdReported into statsd. Each client should create this class to
 * report its data.
 */
public class NetworkNsdReportedMetrics {
    // Whether this client is using legacy backend.
    private final boolean mIsLegacy;
    // The client id.
    private final int mClientId;
    private final Dependencies mDependencies;

    public NetworkNsdReportedMetrics(boolean isLegacy, int clientId) {
        this(isLegacy, clientId, new Dependencies());
    }

    @VisibleForTesting
    NetworkNsdReportedMetrics(boolean isLegacy, int clientId, Dependencies dependencies) {
        mIsLegacy = isLegacy;
        mClientId = clientId;
        mDependencies = dependencies;
    }

    /**
     * Dependencies of NetworkNsdReportedMetrics, for injection in tests.
     */
    public static class Dependencies {

        /**
         * @see ConnectivityStatsLog
         */
        public void statsWrite(NetworkNsdReported event) {
            ConnectivityStatsLog.write(ConnectivityStatsLog.NETWORK_NSD_REPORTED,
                    event.getIsLegacy(),
                    event.getClientId(),
                    event.getTransactionId(),
                    event.getIsKnownService(),
                    event.getType().getNumber(),
                    event.getEventDurationMillisec(),
                    event.getQueryResult().getNumber(),
                    event.getFoundServiceCount(),
                    event.getFoundCallbackCount(),
                    event.getLostCallbackCount(),
                    event.getRepliedRequestsCount());
        }
    }

    private Builder makeReportedBuilder() {
        final Builder builder = NetworkNsdReported.newBuilder();
        builder.setIsLegacy(mIsLegacy);
        builder.setClientId(mClientId);
        return builder;
    }

    /**
     * Report service registration succeeded metric data.
     *
     * @param transactionId The transaction id of service registration.
     * @param durationMs The duration of service registration success.
     */
    public void reportServiceRegistrationSucceeded(int transactionId, long durationMs) {
        final Builder builder = makeReportedBuilder();
        builder.setTransactionId(transactionId);
        builder.setType(NsdEventType.NET_REGISTER);
        builder.setQueryResult(MdnsQueryResult.MQR_SERVICE_REGISTERED);
        builder.setEventDurationMillisec(durationMs);
        mDependencies.statsWrite(builder.build());
    }

    /**
     * Report service registration failed metric data.
     *
     * @param transactionId The transaction id of service registration.
     * @param durationMs The duration of service registration failed.
     */
    public void reportServiceRegistrationFailed(int transactionId, long durationMs) {
        final Builder builder = makeReportedBuilder();
        builder.setTransactionId(transactionId);
        builder.setType(NsdEventType.NET_REGISTER);
        builder.setQueryResult(MdnsQueryResult.MQR_SERVICE_REGISTRATION_FAILED);
        builder.setEventDurationMillisec(durationMs);
        mDependencies.statsWrite(builder.build());
    }

    /**
     * Report service unregistration success metric data.
     *
     * @param transactionId The transaction id of service registration.
     * @param durationMs The duration of service stayed registered.
     */
    public void reportServiceUnregistration(int transactionId, long durationMs) {
        final Builder builder = makeReportedBuilder();
        builder.setTransactionId(transactionId);
        builder.setType(NsdEventType.NET_REGISTER);
        builder.setQueryResult(MdnsQueryResult.MQR_SERVICE_UNREGISTERED);
        builder.setEventDurationMillisec(durationMs);
        // TODO: Report repliedRequestsCount
        mDependencies.statsWrite(builder.build());
    }
}