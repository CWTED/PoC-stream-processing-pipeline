/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.schemas;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class TruckKey extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -5199355149068930331L;


  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"TruckKey\",\"namespace\":\"com.schemas\",\"fields\":[{\"name\":\"customer\",\"type\":\"string\"},{\"name\":\"vehicle\",\"type\":\"string\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static final SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<TruckKey> ENCODER =
      new BinaryMessageEncoder<>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<TruckKey> DECODER =
      new BinaryMessageDecoder<>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<TruckKey> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<TruckKey> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<TruckKey> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this TruckKey to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a TruckKey from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a TruckKey instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static TruckKey fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  private java.lang.CharSequence customer;
  private java.lang.CharSequence vehicle;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public TruckKey() {}

  /**
   * All-args constructor.
   * @param customer The new value for customer
   * @param vehicle The new value for vehicle
   */
  public TruckKey(java.lang.CharSequence customer, java.lang.CharSequence vehicle) {
    this.customer = customer;
    this.vehicle = vehicle;
  }

  @Override
  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }

  @Override
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }

  // Used by DatumWriter.  Applications should not call.
  @Override
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return customer;
    case 1: return vehicle;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @Override
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: customer = (java.lang.CharSequence)value$; break;
    case 1: vehicle = (java.lang.CharSequence)value$; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'customer' field.
   * @return The value of the 'customer' field.
   */
  public java.lang.CharSequence getCustomer() {
    return customer;
  }


  /**
   * Sets the value of the 'customer' field.
   * @param value the value to set.
   */
  public void setCustomer(java.lang.CharSequence value) {
    this.customer = value;
  }

  /**
   * Gets the value of the 'vehicle' field.
   * @return The value of the 'vehicle' field.
   */
  public java.lang.CharSequence getVehicle() {
    return vehicle;
  }


  /**
   * Sets the value of the 'vehicle' field.
   * @param value the value to set.
   */
  public void setVehicle(java.lang.CharSequence value) {
    this.vehicle = value;
  }

  /**
   * Creates a new TruckKey RecordBuilder.
   * @return A new TruckKey RecordBuilder
   */
  public static com.schemas.TruckKey.Builder newBuilder() {
    return new com.schemas.TruckKey.Builder();
  }

  /**
   * Creates a new TruckKey RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new TruckKey RecordBuilder
   */
  public static com.schemas.TruckKey.Builder newBuilder(com.schemas.TruckKey.Builder other) {
    if (other == null) {
      return new com.schemas.TruckKey.Builder();
    } else {
      return new com.schemas.TruckKey.Builder(other);
    }
  }

  /**
   * Creates a new TruckKey RecordBuilder by copying an existing TruckKey instance.
   * @param other The existing instance to copy.
   * @return A new TruckKey RecordBuilder
   */
  public static com.schemas.TruckKey.Builder newBuilder(com.schemas.TruckKey other) {
    if (other == null) {
      return new com.schemas.TruckKey.Builder();
    } else {
      return new com.schemas.TruckKey.Builder(other);
    }
  }

  /**
   * RecordBuilder for TruckKey instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<TruckKey>
    implements org.apache.avro.data.RecordBuilder<TruckKey> {

    private java.lang.CharSequence customer;
    private java.lang.CharSequence vehicle;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$, MODEL$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(com.schemas.TruckKey.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.customer)) {
        this.customer = data().deepCopy(fields()[0].schema(), other.customer);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.vehicle)) {
        this.vehicle = data().deepCopy(fields()[1].schema(), other.vehicle);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
    }

    /**
     * Creates a Builder by copying an existing TruckKey instance
     * @param other The existing instance to copy.
     */
    private Builder(com.schemas.TruckKey other) {
      super(SCHEMA$, MODEL$);
      if (isValidValue(fields()[0], other.customer)) {
        this.customer = data().deepCopy(fields()[0].schema(), other.customer);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.vehicle)) {
        this.vehicle = data().deepCopy(fields()[1].schema(), other.vehicle);
        fieldSetFlags()[1] = true;
      }
    }

    /**
      * Gets the value of the 'customer' field.
      * @return The value.
      */
    public java.lang.CharSequence getCustomer() {
      return customer;
    }


    /**
      * Sets the value of the 'customer' field.
      * @param value The value of 'customer'.
      * @return This builder.
      */
    public com.schemas.TruckKey.Builder setCustomer(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.customer = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'customer' field has been set.
      * @return True if the 'customer' field has been set, false otherwise.
      */
    public boolean hasCustomer() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'customer' field.
      * @return This builder.
      */
    public com.schemas.TruckKey.Builder clearCustomer() {
      customer = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'vehicle' field.
      * @return The value.
      */
    public java.lang.CharSequence getVehicle() {
      return vehicle;
    }


    /**
      * Sets the value of the 'vehicle' field.
      * @param value The value of 'vehicle'.
      * @return This builder.
      */
    public com.schemas.TruckKey.Builder setVehicle(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.vehicle = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'vehicle' field has been set.
      * @return True if the 'vehicle' field has been set, false otherwise.
      */
    public boolean hasVehicle() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'vehicle' field.
      * @return This builder.
      */
    public com.schemas.TruckKey.Builder clearVehicle() {
      vehicle = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public TruckKey build() {
      try {
        TruckKey record = new TruckKey();
        record.customer = fieldSetFlags()[0] ? this.customer : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.vehicle = fieldSetFlags()[1] ? this.vehicle : (java.lang.CharSequence) defaultValue(fields()[1]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<TruckKey>
    WRITER$ = (org.apache.avro.io.DatumWriter<TruckKey>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<TruckKey>
    READER$ = (org.apache.avro.io.DatumReader<TruckKey>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeString(this.customer);

    out.writeString(this.vehicle);

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.customer = in.readString(this.customer instanceof Utf8 ? (Utf8)this.customer : null);

      this.vehicle = in.readString(this.vehicle instanceof Utf8 ? (Utf8)this.vehicle : null);

    } else {
      for (int i = 0; i < 2; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.customer = in.readString(this.customer instanceof Utf8 ? (Utf8)this.customer : null);
          break;

        case 1:
          this.vehicle = in.readString(this.vehicle instanceof Utf8 ? (Utf8)this.vehicle : null);
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}










