package com.overseaslabs.examples.ureg;

public interface MessagePublisher {

    /**
     * Publish a message
     * @param message The message to publish
     */
    void publish(Object message);
}