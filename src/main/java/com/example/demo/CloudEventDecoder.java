package com.example.demo;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.provider.EventFormatProvider;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractDataBufferDecoder;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

import java.util.Map;

@Component
@SuppressWarnings("ConstantConditions")
public class CloudEventDecoder extends AbstractDataBufferDecoder<CloudEvent> {

    public CloudEventDecoder() {
        super(MimeType.valueOf("application/cloudevents+json"));
    }

    @Override
    public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
        Class<?> clazz = elementType.toClass();
        return super.canDecode(elementType, mimeType)
                && CloudEvent.class.isAssignableFrom(clazz) && EventFormatProvider
                .getInstance().resolveFormat(mimeType.toString()) != null;
    }

    @Override
    public CloudEvent decode(DataBuffer buffer, ResolvableType targetType,
                             MimeType mimeType, Map<String, Object> hints) throws DecodingException {
        EventFormat format = EventFormatProvider.getInstance()
                .resolveFormat(mimeType.toString());
        byte[] result = new byte[buffer.readableByteCount()];
        buffer.read(result);
        DataBufferUtils.release(buffer);
        return format.deserialize(result);
    }

}
