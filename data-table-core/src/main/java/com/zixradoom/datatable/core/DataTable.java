
package com.zixradoom.datatable.core;

import java.util.Objects;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Supplier;
import java.util.function.IntSupplier;

import java.nio.ByteBuffer;

public abstract class DataTable < E extends DataTable.Entry > {
  
  public static final int DEFAULT_ENTRY_MULTIPLIER = 32;
  
  protected ByteBuffer table;
  protected final int entrySize;
  protected int entryCount;
  protected final Factory factory;
  protected final List < E > entries;
  
  protected DataTable ( Factory < E > factory ) {
    this.entrySize = factory.getEntrySize ();
    this.entryCount = 0;
    this.factory = Objects.requireNonNull ( factory );
    this.table = ByteBuffer.allocate ( entrySize * DEFAULT_ENTRY_MULTIPLIER );
    this.entries = new ArrayList <> ();
  }
  
  public E newEntry () {
    throw new UnsupportedOperationException ( "not implemented" );
  }
  
  public int getEntrySize () {
    return entrySize;
  }
  
  public int getEntryCount () {
    return entryCount;
  }
  
  public ByteBuffer getEncoded () {
    return table.duplicate ().asReadOnlyBuffer ();
  }
  
  public int getEncodedSize () {
    return table.limit ();
  }
  
  private void updateBufferPositons () {
    table.limit ( entrySize * entryCount );
    table.position ( table.limit () );
  }
  
  private ByteBuffer buffer () {
    return table;
  }
  
  public interface Entry {
    int getIndex ();
  }
  
  public interface Factory < S extends Entry > {
    S createEntry ( int index, Supplier < ByteBuffer > bufferGetter );
    int getEntrySize ();
  }
}
