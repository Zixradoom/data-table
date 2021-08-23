package com.zixradoom.datatable.core;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public abstract class AbstractDataTableEntry implements DataTable.Entry {
	
  private final int index;
  private final Supplier < ByteBuffer > bufferGetter;
  private final IntSupplier entrySizeGetter;
  
  protected AbstractDataTableEntry ( int index, Supplier < ByteBuffer > bufferGetter, IntSupplier entrySizeGetter ) {
    this.index = index;
    this.bufferGetter = Objects.requireNonNull ( bufferGetter, "bufferGetter is null" );
    this.entrySizeGetter = Objects.requireNonNull ( entrySizeGetter, "entrySizeGetter is null" );
    
    if ( index < 0 ) {
      throw new IllegalArgumentException ( String.format ( "index %d < 0", index ) );
    }
  }
  
  @Override
  public final int getIndex() {
    return index;
  }
  
  @Override
  public final int getEntrySize () {
    return entrySizeGetter.getAsInt ();
  }
  
  protected final ByteBuffer buffer () {
    return bufferGetter.get ();
  }
  
  protected final int start () {
    return entrySizeGetter.getAsInt () * index;
  }
}
