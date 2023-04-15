package com.example.demo;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.provider.EventFormatProvider;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractSingleValueEncoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;

import java.util.Map;

@Component
@SuppressWarnings("ConstantConditions")
public class CloudEventEncoder extends AbstractSingleValueEncoder<CloudEvent> {

    public CloudEventEncoder() {
        super(MimeType.valueOf("application/cloudevents+json"));
    }

    @Override
    public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
        Class<?> clazz = elementType.toClass();
        return super.canEncode(elementType, mimeType)
                && CloudEvent.class.isAssignableFrom(clazz) && EventFormatProvider
                .getInstance().resolveFormat(mimeType.toString()) != null;
    }

    @Override
    protected Flux<DataBuffer> encode(CloudEvent event, DataBufferFactory bufferFactory,
                                      ResolvableType type, @Nullable MimeType mimeType,
                                      @Nullable Map<String, Object> hints) {
        return Flux.just(encodeValue(event, bufferFactory, type, mimeType, hints));
    }

    @Override
    public DataBuffer encodeValue(CloudEvent event, DataBufferFactory bufferFactory,
                                  ResolvableType valueType, MimeType mimeType, Map<String, Object> hints) {
        EventFormat format = EventFormatProvider.getInstance()
                .resolveFormat(mimeType.toString());
        return bufferFactory.wrap(format.serialize(event));
    }

}
