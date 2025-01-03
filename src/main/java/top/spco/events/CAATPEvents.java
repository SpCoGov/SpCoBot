/*
 * Copyright 2025 SpCo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.spco.events;

import top.spco.core.event.Event;
import top.spco.core.event.EventFactory;

/**
 * CAATP相关事件
 *
 * @author SpCo
 * @version 0.1.2
 * @since 0.1.0
 */
public class CAATPEvents {
    private CAATPEvents() {
    }

    /**
     * Called when a CAATP message is received.
     */
    public static final Event<Receive> RECEIVE = EventFactory.createArrayBacked(Receive.class, callbacks -> (message) -> {
        for (Receive event : callbacks) {
            event.onReceive(message);
        }
    });

    @FunctionalInterface
    public interface Receive {
        void onReceive(String message);
    }

    /**
     * Called when disconnected from CAATP.
     */
    public static final Event<Disconnect> DISCONNECT = EventFactory.createArrayBacked(Disconnect.class, callbacks -> () -> {
        for (Disconnect event : callbacks) {
            event.onDisconnect();
        }
    });

    @FunctionalInterface
    public interface Disconnect {
        void onDisconnect();
    }

    /**
     * Called when disconnected from CAATP.
     */
    public static final Event<Connect> CONNECT = EventFactory.createArrayBacked(Connect.class, callbacks -> () -> {
        for (Connect event : callbacks) {
            event.onConnect();
        }
    });

    @FunctionalInterface
    public interface Connect {
        void onConnect();
    }
}