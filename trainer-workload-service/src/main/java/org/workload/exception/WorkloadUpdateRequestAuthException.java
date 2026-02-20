package org.workload.exception;

public class WorkloadUpdateRequestAuthException extends RuntimeException {
    public WorkloadUpdateRequestAuthException(String msg, Throwable cause) {
        super(msg, cause);
    }
    public WorkloadUpdateRequestAuthException(String msg) {
        super(msg);
    }

}
