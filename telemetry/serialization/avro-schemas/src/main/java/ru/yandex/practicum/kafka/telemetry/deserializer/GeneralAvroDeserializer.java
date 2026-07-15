package ru.yandex.practicum.kafka.telemetry.deserializer;

import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class GeneralAvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {

    private final DecoderFactory decoderFactory;
    private final Schema schema;

    public GeneralAvroDeserializer(Schema schema) {
        this(DecoderFactory.get(), schema);
    }

    public GeneralAvroDeserializer(DecoderFactory decoderFactory, Schema schema) {
        this.decoderFactory = decoderFactory;
        this.schema = schema;
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        try {
            BinaryDecoder decoder = decoderFactory.binaryDecoder(data, null);
            DatumReader<T> reader = new SpecificDatumReader<>(schema);
            return reader.read(null, decoder);
        } catch (IOException exception) {
            throw new SerializationException("Ошибка десериализации " + data.getClass().getSimpleName(), exception);
        }
    }
}
