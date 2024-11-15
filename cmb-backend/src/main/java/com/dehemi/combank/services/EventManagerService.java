package com.dehemi.combank.services;

import com.dehemi.combank.dao.TransactionScanLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Slf4j
@Service
public class EventManagerService {
    Sinks.Many<TransactionScanLog> scanLogSink = Sinks.many().multicast().onBackpressureBuffer(32);

    public Mono<Void> sendTransactionScanLogEvent(TransactionScanLog transactionScanLog) {
        Sinks.EmitResult emitResult = scanLogSink.tryEmitNext(transactionScanLog);
        if (emitResult.isFailure()) {
            log.info("Transaction scan log event failed");
            return Mono.empty();
        }

        return Mono.empty();
    }

    public Flux<TransactionScanLog> getTransactionScanLogFlux(String userId) {
        return Flux.create(transactionScanLogFluxSink -> {
            scanLogSink.asFlux().filter(transactionScanLog -> transactionScanLog.getUserId().equals(userId)).subscribe(transactionScanLogFluxSink::next);
        });
    }
}
